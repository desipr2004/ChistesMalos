package com.chistesmalos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para crear/actualizar un comentario.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentRequest {

    private Long jokeId;

    @NotBlank(message = "El contenido del comentario es requerido")
    @Size(min = 1, max = 1000, message = "El comentario debe tener entre 1 y 1000 caracteres")
    private String content;
}
