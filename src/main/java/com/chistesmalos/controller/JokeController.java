package com.chistesmalos.controller;

import com.chistesmalos.dto.JokeRequest;
import com.chistesmalos.dto.JokeResponse;
import com.chistesmalos.service.JokeService;
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
 * Controlador REST para gestión de chistes.
 */
@Slf4j
@RestController
@RequestMapping("/jokes")
@RequiredArgsConstructor
public class JokeController {

    private final JokeService jokeService;

    /**
     * Obtener todos los chistes (paginados).
     * GET /jokes?page=0&size=10&sort=createdAt,desc
     */
    @GetMapping
    public ResponseEntity<Page<JokeResponse>> getAllJokes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "DESC") Sort.Direction direction) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort));
        Page<JokeResponse> jokes = jokeService.getAllJokes(pageable);
        return ResponseEntity.ok(jokes);
    }

    /**
     * Obtener chiste por ID.
     * GET /jokes/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<JokeResponse> getJokeById(@PathVariable Long id) {
        JokeResponse joke = jokeService.getJokeById(id);
        return ResponseEntity.ok(joke);
    }

    /**
     * Obtener chistes por categoría.
     * GET /jokes/category/{categoryId}
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Page<JokeResponse>> getJokesByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<JokeResponse> jokes = jokeService.getJokesByCategory(categoryId, pageable);
        return ResponseEntity.ok(jokes);
    }

    /**
     * Crear un nuevo chiste.
     * POST /jokes
     */
    @PostMapping
    public ResponseEntity<JokeResponse> createJoke(
            @Valid @RequestBody JokeRequest request,
            Authentication authentication) {
        log.info("Creando chiste del usuario: {}", authentication.getName());
        JokeResponse joke = jokeService.createJoke(request, authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(joke);
    }

    /**
     * Actualizar un chiste.
     * PUT /jokes/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<JokeResponse> updateJoke(
            @PathVariable Long id,
            @Valid @RequestBody JokeRequest request,
            Authentication authentication) {
        log.info("Actualizando chiste: {} del usuario: {}", id, authentication.getName());
        JokeResponse joke = jokeService.updateJoke(id, request, authentication);
        return ResponseEntity.ok(joke);
    }

    /**
     * Eliminar un chiste.
     * DELETE /jokes/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJoke(
            @PathVariable Long id,
            Authentication authentication) {
        log.info("Eliminando chiste: {} del usuario: {}", id, authentication.getName());
        jokeService.deleteJoke(id, authentication);
        return ResponseEntity.noContent().build();
    }

    /**
     * Obtener el chiste del día.
     * GET /jokes/daily/joke-of-day
     */
    @GetMapping({"/special/joke-of-day", "/daily/joke-of-day"})
    public ResponseEntity<JokeResponse> getJokeOfDay() {
        JokeResponse joke = jokeService.getJokeOfDay();
        return ResponseEntity.ok(joke);
    }

    /**
     * Obtener los peores chistes (ranking).
     * GET /jokes/trending/worst
     */
    @GetMapping({"/special/worst", "/trending/worst"})
    public ResponseEntity<Page<JokeResponse>> getWorstJokes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<JokeResponse> jokes = jokeService.getWorstJokes(pageable);
        return ResponseEntity.ok(jokes);
    }
}
