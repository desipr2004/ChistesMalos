package com.chistesmalos.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Proveedor de JWT tokens para autenticación.
 */
@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    @Value("${jwt.refresh-expiration}")
    private long jwtRefreshExpirationMs;

    /**
     * Generar JWT token desde Authentication.
     */
    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        return createToken(username, jwtExpirationMs);
    }

    /**
     * Generar JWT token desde username.
     */
    public String generateTokenFromUsername(String username) {
        return createToken(username, jwtExpirationMs);
    }

    /**
     * Generar refresh token.
     */
    public String generateRefreshToken(String username) {
        return createToken(username, jwtRefreshExpirationMs);
    }

    /**
     * Crear token JWT con duración especificada.
     */
    private String createToken(String subject, long expirationTime) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationTime);

        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());

        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key)
                .compact();
    }

    /**
     * Obtener username desde JWT token.
     */
    public String getUsernameFromToken(String token) {
        Claims claims = parseClaims(token);
        return claims.getSubject();
    }

    /**
     * Validar JWT token.
     */
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (MalformedJwtException ex) {
            log.error("Token JWT inválido: {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            log.error("Token JWT expirado: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            log.error("Token JWT no soportado: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.error("String JWT claim vacío: {}", ex.getMessage());
        }
        return false;
    }

    private Claims parseClaims(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        return Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Obtener tiempo de expiración del token.
     */
    public long getTokenExpirationTime() {
        return jwtExpirationMs;
    }

    /**
     * Obtener tiempo de expiración del refresh token.
     */
    public long getRefreshTokenExpirationTime() {
        return jwtRefreshExpirationMs;
    }
}
