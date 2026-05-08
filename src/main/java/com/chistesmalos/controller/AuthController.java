package com.chistesmalos.controller;

import com.chistesmalos.dto.*;
import com.chistesmalos.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para autenticación y registro de usuarios.
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    /**
     * Registrar un nuevo usuario.
     * POST /auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Solicitud de registro para usuario: {}", request.getUsername());
        AuthResponse response = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Iniciar sesión.
     * POST /auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Solicitud de login para: {}", request.getUsernameOrEmail());
        AuthResponse response = userService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Refrescar token de acceso usando refresh token.
     * POST /auth/refresh
     */
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        log.info("Solicitud de refresh token");
        AuthResponse response = userService.refreshAccessToken(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Cerrar sesión y revocar refresh token.
     * POST /auth/logout
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody TokenRefreshRequest request, Authentication authentication) {
        log.info("Solicitud de logout para usuario: {}", authentication.getName());
        userService.revokeRefreshToken(request.getRefreshToken());
        return ResponseEntity.noContent().build();
    }
}
