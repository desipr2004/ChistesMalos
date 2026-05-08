package com.chistesmalos.controller;

import com.chistesmalos.dto.CategoryResponse;
import com.chistesmalos.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestión de categorías.
 */
@Slf4j
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "API para gestión de categorías de chistes")
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * Obtener todas las categorías.
     * GET /categories
     */
    @GetMapping
    @Operation(summary = "Obtener todas las categorías", description = "Retorna una lista de todas las categorías disponibles")
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        List<CategoryResponse> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    /**
     * Obtener categorías con paginación.
     * GET /categories/page?page=0&size=10
     */
    @GetMapping("/page")
    @Operation(summary = "Obtener categorías paginadas")
    public ResponseEntity<Page<CategoryResponse>> getCategoriesPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<CategoryResponse> categories = categoryService.getCategoriesPage(pageable);
        return ResponseEntity.ok(categories);
    }

    /**
     * Obtener una categoría por ID.
     * GET /categories/{id}
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obtener categoría por ID")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable Long id) {
        CategoryResponse category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(category);
    }

    /**
     * Obtener una categoría por nombre.
     * GET /categories/name/{name}
     */
    @GetMapping("/name/{name}")
    @Operation(summary = "Obtener categoría por nombre")
    public ResponseEntity<CategoryResponse> getCategoryByName(@PathVariable String name) {
        CategoryResponse category = categoryService.getCategoryByName(name);
        return ResponseEntity.ok(category);
    }
}
