package com.chistesmalos.service;

import com.chistesmalos.dto.CommentRequest;
import com.chistesmalos.dto.CommentResponse;
import com.chistesmalos.entity.Comment;
import com.chistesmalos.entity.Joke;
import com.chistesmalos.entity.User;
import com.chistesmalos.exception.ResourceNotFoundException;
import com.chistesmalos.exception.UnauthorizedException;
import com.chistesmalos.exception.ValidationException;
import com.chistesmalos.repository.CommentRepository;
import com.chistesmalos.repository.JokeRepository;
import com.chistesmalos.repository.UserRepository;
import com.chistesmalos.security.InputSanitizer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio para gestión de comentarios.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final JokeRepository jokeRepository;
    private final UserRepository userRepository;
    private final InputSanitizer inputSanitizer;
    private final CommentMapper commentMapper;

    /**
     * Crear un nuevo comentario.
     */
    @Transactional
    @CacheEvict(value = "comments", allEntries = true)
    public CommentResponse createComment(CommentRequest request, Authentication authentication) {
        if (inputSanitizer.isSuspicious(request.getContent())) {
            throw new ValidationException("El contenido contiene caracteres no permitidos");
        }

        Joke joke = jokeRepository.findById(request.getJokeId())
                .orElseThrow(() -> new ResourceNotFoundException("Chiste no encontrado"));

        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Comment comment = Comment.builder()
                .content(inputSanitizer.sanitize(request.getContent()))
                .joke(joke)
                .user(user)
                .build();

        Comment saved = commentRepository.save(comment);
        log.info("Comentario creado por usuario {}: {}", user.getUsername(), saved.getId());

        return commentMapper.toCommentResponse(saved);
    }

    /**
     * Obtener comentarios de un chiste.
     */
    @Transactional(readOnly = true)
    public Page<CommentResponse> getCommentsByJoke(Long jokeId, Pageable pageable) {
        jokeRepository.findById(jokeId)
                .orElseThrow(() -> new ResourceNotFoundException("Chiste no encontrado"));

        return commentRepository.findByJokeId(jokeId, pageable)
                .map(commentMapper::toCommentResponse);
    }

    /**
     * Actualizar comentario.
     */
    @Transactional
    @CacheEvict(value = "comments", allEntries = true)
    public CommentResponse updateComment(Long commentId, CommentRequest request, Authentication authentication) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comentario no encontrado"));

        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        if (!comment.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("No tienes permiso para actualizar este comentario");
        }

        comment.setContent(inputSanitizer.sanitize(request.getContent()));
        Comment updated = commentRepository.save(comment);

        return commentMapper.toCommentResponse(updated);
    }

    /**
     * Eliminar comentario.
     */
    @Transactional
    @CacheEvict(value = "comments", allEntries = true)
    public void deleteComment(Long commentId, Authentication authentication) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comentario no encontrado"));

        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        if (!comment.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("No tienes permiso para eliminar este comentario");
        }

        commentRepository.delete(comment);
        log.info("Comentario eliminado: {}", commentId);
    }
}
