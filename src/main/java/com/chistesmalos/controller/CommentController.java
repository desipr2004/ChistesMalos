package com.chistesmalos.controller;

import com.chistesmalos.dto.CommentRequest;
import com.chistesmalos.dto.CommentResponse;
import com.chistesmalos.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para gestión de comentarios.
 */
@Slf4j
@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    /**
     * Obtener comentarios de un chiste.
     * GET /comments/joke/{jokeId}
     */
    @GetMapping("/joke/{jokeId}")
    public ResponseEntity<Page<CommentResponse>> getCommentsByJoke(
            @PathVariable Long jokeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<CommentResponse> comments = commentService.getCommentsByJoke(jokeId, pageable);
        return ResponseEntity.ok(comments);
    }

    /**
     * Crear un nuevo comentario.
     * POST /comments
     */
    @PostMapping
    public ResponseEntity<CommentResponse> createComment(
            @Valid @RequestBody CommentRequest request,
            Authentication authentication) {
        log.info("Creando comentario del usuario: {}", authentication.getName());
        CommentResponse comment = commentService.createComment(request, authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(comment);
    }

    /**
     * Actualizar un comentario.
     * PUT /comments/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable Long id,
            @Valid @RequestBody CommentRequest request,
            Authentication authentication) {
        log.info("Actualizando comentario: {} del usuario: {}", id, authentication.getName());
        CommentResponse comment = commentService.updateComment(id, request, authentication);
        return ResponseEntity.ok(comment);
    }

    /**
     * Eliminar un comentario.
     * DELETE /comments/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long id,
            Authentication authentication) {
        log.info("Eliminando comentario: {} del usuario: {}", id, authentication.getName());
        commentService.deleteComment(id, authentication);
        return ResponseEntity.noContent().build();
    }
}
