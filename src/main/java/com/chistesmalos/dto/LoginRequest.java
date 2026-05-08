package com.chistesmalos.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para login de usuario.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {

    @NotBlank(message = "El nombre de usuario o email es requerido")
    private String usernameOrEmail;

    @NotBlank(message = "La contraseña es requerida")
    private String password;
}
