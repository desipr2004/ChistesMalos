package com.chistesmalos.service;

import com.chistesmalos.dto.LoginRequest;
import com.chistesmalos.dto.AuthResponse;
import com.chistesmalos.dto.RegisterRequest;
import com.chistesmalos.dto.TokenRefreshRequest;
import com.chistesmalos.dto.UserResponse;
import com.chistesmalos.entity.Role;
import com.chistesmalos.entity.RefreshToken;
import com.chistesmalos.entity.User;
import com.chistesmalos.exception.AuthenticationException;
import com.chistesmalos.exception.ValidationException;
import com.chistesmalos.repository.UserRepository;
import com.chistesmalos.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.regex.Pattern;

/**
 * Servicio para gestión de autenticación y usuarios.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final RefreshTokenService refreshTokenService;

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    /**
     * Registrar un nuevo usuario.
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        validateRegistration(request);

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ValidationException("El nombre de usuario ya está en uso");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ValidationException("El email ya está registrado");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .isActive(true)
                .build();

        User savedUser = userRepository.save(user);
        log.info("Usuario registrado exitosamente: {}", savedUser.getUsername());

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(savedUser);
        String token = tokenProvider.generateTokenFromUsername(savedUser.getUsername());

        return AuthResponse.builder()
                .accessToken(token)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .expiresIn(tokenProvider.getTokenExpirationTime())
                .refreshExpiresIn(tokenProvider.getRefreshTokenExpirationTime())
                .user(userMapper.toUserResponse(savedUser))
                .build();
    }

    /**
     * Iniciar sesión.
     */
    public AuthResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsernameOrEmail(),
                            request.getPassword()
                    )
            );

            User user = userRepository.findByUsernameOrEmail(
                    request.getUsernameOrEmail(),
                    request.getUsernameOrEmail()
            ).orElseThrow(() -> new AuthenticationException("Usuario no encontrado"));

            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
            String token = tokenProvider.generateToken(authentication);

            log.info("Usuario inició sesión: {}", user.getUsername());

            return AuthResponse.builder()
                    .accessToken(token)
                    .refreshToken(refreshToken.getToken())
                    .tokenType("Bearer")
                    .expiresIn(tokenProvider.getTokenExpirationTime())
                    .refreshExpiresIn(tokenProvider.getRefreshTokenExpirationTime())
                    .user(userMapper.toUserResponse(user))
                    .build();
        } catch (Exception e) {
            throw new AuthenticationException("Credenciales inválidas", e);
        }
    }

    /**
     * Refrescar token de acceso usando refresh token.
     */
    public AuthResponse refreshAccessToken(TokenRefreshRequest request) {
        RefreshToken refreshToken = refreshTokenService.verifyRefreshToken(request.getRefreshToken());
        User user = refreshToken.getUser();

        String accessToken = tokenProvider.generateTokenFromUsername(user.getUsername());
        RefreshToken rotatedToken = refreshTokenService.rotateRefreshToken(refreshToken);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(rotatedToken.getToken())
                .tokenType("Bearer")
                .expiresIn(tokenProvider.getTokenExpirationTime())
                .refreshExpiresIn(tokenProvider.getRefreshTokenExpirationTime())
                .user(userMapper.toUserResponse(user))
                .build();
    }

    public void revokeRefreshToken(String refreshToken) {
        refreshTokenService.revokeToken(refreshToken);
    }

    /**
     * Obtener usuario por ID.
     */
    public UserResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AuthenticationException("Usuario no encontrado"));
        return userMapper.toUserResponse(user);
    }

    /**
     * Obtener usuario por username.
     */
    public UserResponse getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AuthenticationException("Usuario no encontrado"));
        return userMapper.toUserResponse(user);
    }

    /**
     * Validar datos de registro.
     */
    private void validateRegistration(RegisterRequest request) {
        if (request.getUsername() == null || request.getUsername().length() < 3) {
            throw new ValidationException("El nombre de usuario debe tener al menos 3 caracteres");
        }

        if (!EMAIL_PATTERN.matcher(request.getEmail()).matches()) {
            throw new ValidationException("El email no tiene un formato válido");
        }

        if (request.getPassword() == null || request.getPassword().length() < 8) {
            throw new ValidationException("La contraseña debe tener al menos 8 caracteres");
        }

        if (!request.getPassword().equals(request.getPasswordConfirm())) {
            throw new ValidationException("Las contraseñas no coinciden");
        }
    }
}
