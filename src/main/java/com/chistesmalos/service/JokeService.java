package com.chistesmalos.service;

import com.chistesmalos.dto.JokeRequest;
import com.chistesmalos.dto.JokeResponse;
import com.chistesmalos.entity.Category;
import com.chistesmalos.entity.Joke;
import com.chistesmalos.entity.User;
import com.chistesmalos.exception.ResourceNotFoundException;
import com.chistesmalos.exception.UnauthorizedException;
import com.chistesmalos.exception.ValidationException;
import com.chistesmalos.repository.CategoryRepository;
import com.chistesmalos.repository.JokeRepository;
import com.chistesmalos.repository.UserRepository;
import com.chistesmalos.security.InputSanitizer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio para gestión de chistes.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JokeService {

    private final JokeRepository jokeRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final InputSanitizer inputSanitizer;
    private final JokeMapper jokeMapper;

    /**
     * Crear un nuevo chiste.
     */
    @Transactional
    @CacheEvict(value = "jokes", allEntries = true)
    public JokeResponse createJoke(JokeRequest request, Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        // Validar y sanitizar entrada
        if (inputSanitizer.isSuspicious(request.getTitle()) || 
            inputSanitizer.isSuspicious(request.getContent())) {
            throw new ValidationException("El contenido contiene caracteres no permitidos");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));

        Joke joke = Joke.builder()
                .title(inputSanitizer.sanitize(request.getTitle()))
                .content(inputSanitizer.sanitize(request.getContent()))
                .user(user)
                .category(category)
                .isPublished(request.getIsPublished())
                .build();

        Joke saved = jokeRepository.save(joke);
        log.info("Chiste creado por usuario {}: {}", user.getUsername(), saved.getId());

        return jokeMapper.toJokeResponse(saved);
    }

    /**
     * Obtener todos los chistes publicados (con paginación).
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "jokes", unless = "#result == null")
    public Page<JokeResponse> getAllJokes(Pageable pageable) {
        return jokeRepository.findByIsPublishedTrueAndIsDeletedFalse(pageable)
                .map(jokeMapper::toJokeResponse);
    }

    /**
     * Obtener chiste por ID.
     */
    @Transactional
    public JokeResponse getJokeById(Long jokeId) {
        Joke joke = jokeRepository.findById(jokeId)
                .orElseThrow(() -> new ResourceNotFoundException("Chiste no encontrado"));

        if (!joke.getIsPublished()) {
            throw new ResourceNotFoundException("Chiste no disponible");
        }

        // Incrementar vista
        joke.setViews(joke.getViews() + 1);
        jokeRepository.save(joke);

        return jokeMapper.toJokeResponse(joke);
    }

    /**
     * Obtener chistes por categoría.
     */
    @Transactional(readOnly = true)
    public Page<JokeResponse> getJokesByCategory(Long categoryId, Pageable pageable) {
        categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));

        return jokeRepository.findByCategoryIdAndIsPublishedTrueAndIsDeletedFalse(categoryId, pageable)
                .map(jokeMapper::toJokeResponse);
    }

    /**
     * Actualizar chiste.
     */
    @Transactional
    @CacheEvict(value = "jokes", allEntries = true)
    public JokeResponse updateJoke(Long jokeId, JokeRequest request, Authentication authentication) {
        Joke joke = jokeRepository.findById(jokeId)
                .orElseThrow(() -> new ResourceNotFoundException("Chiste no encontrado"));

        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        if (!joke.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("No tienes permiso para actualizar este chiste");
        }

        joke.setTitle(inputSanitizer.sanitize(request.getTitle()));
        joke.setContent(inputSanitizer.sanitize(request.getContent()));
        joke.setIsPublished(request.getIsPublished());

        Joke updated = jokeRepository.save(joke);
        log.info("Chiste actualizado: {}", jokeId);

        return jokeMapper.toJokeResponse(updated);
    }

    /**
     * Eliminar chiste.
     */
    @Transactional
    @CacheEvict(value = "jokes", allEntries = true)
    public void deleteJoke(Long jokeId, Authentication authentication) {
        Joke joke = jokeRepository.findById(jokeId)
                .orElseThrow(() -> new ResourceNotFoundException("Chiste no encontrado"));

        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        if (!joke.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("No tienes permiso para eliminar este chiste");
        }

        joke.setIsDeleted(true);
        jokeRepository.save(joke);
        log.info("Chiste eliminado: {}", jokeId);
    }

    /**
     * Obtener el chiste del día.
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "jokeOfDay", unless = "#result == null")
    public JokeResponse getJokeOfDay() {
        Joke joke = jokeRepository.findJokeOfDay()
                .orElseThrow(() -> new ResourceNotFoundException("No hay chistes disponibles"));
        return jokeMapper.toJokeResponse(joke);
    }

    /**
     * Obtener los peores chistes.
     */
    @Transactional(readOnly = true)
    public Page<JokeResponse> getWorstJokes(Pageable pageable) {
        return jokeRepository.findWorstJokes(pageable)
                .map(jokeMapper::toJokeResponse);
    }
}
