package com.chistesmalos.service;

import com.chistesmalos.dto.CategoryResponse;
import com.chistesmalos.entity.Category;
import com.chistesmalos.exception.ResourceNotFoundException;
import com.chistesmalos.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para gestión de categorías de chistes.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    /**
     * Obtener todas las categorías.
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "categories", unless = "#result == null")
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(categoryMapper::toCategoryResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtener categorías con paginación.
     */
    @Transactional(readOnly = true)
    public Page<CategoryResponse> getCategoriesPage(Pageable pageable) {
        return categoryRepository.findAll(pageable)
                .map(categoryMapper::toCategoryResponse);
    }

    /**
     * Obtener una categoría por ID.
     */
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));
        return categoryMapper.toCategoryResponse(category);
    }

    /**
     * Obtener una categoría por nombre.
     */
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryByName(String name) {
        Category category = categoryRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));
        return categoryMapper.toCategoryResponse(category);
    }

    /**
     * Crear una nueva categoría (solo admin).
     */
    @Transactional
    public CategoryResponse createCategory(String name, String description) {
        if (categoryRepository.findByName(name).isPresent()) {
            throw new IllegalArgumentException("La categoría ya existe");
        }

        Category category = Category.builder()
                .name(name)
                .description(description)
                .build();

        Category saved = categoryRepository.save(category);
        log.info("Categoría creada: {}", name);

        return categoryMapper.toCategoryResponse(saved);
    }
}
