package com.chistesmalos.controller;

import com.chistesmalos.dto.VoteRequest;
import com.chistesmalos.service.VoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para gestión de votos.
 */
@Slf4j
@RestController
@RequestMapping("/votes")
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;

    /**
     * Votar en un chiste.
     * POST /votes/jokes/{jokeId}
     */
    @PostMapping("/jokes/{jokeId}")
    public ResponseEntity<Void> voteOnJoke(
            @PathVariable Long jokeId,
            @Valid @RequestBody VoteRequest request,
            Authentication authentication) {
        log.info("Usuario {} votando en chiste {}", authentication.getName(), jokeId);
        voteService.voteOnJoke(jokeId, request, authentication.getName());
        return ResponseEntity.ok().build();
    }

    /**
     * Votar en un comentario.
     * POST /votes/comments/{commentId}
     */
    @PostMapping("/comments/{commentId}")
    public ResponseEntity<Void> voteOnComment(
            @PathVariable Long commentId,
            @Valid @RequestBody VoteRequest request,
            Authentication authentication) {
        log.info("Usuario {} votando en comentario {}", authentication.getName(), commentId);
        voteService.voteOnComment(commentId, request, authentication.getName());
        return ResponseEntity.ok().build();
    }
}
