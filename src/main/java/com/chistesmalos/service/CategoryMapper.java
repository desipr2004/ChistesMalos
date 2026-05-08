package com.chistesmalos.service;

import com.chistesmalos.dto.CategoryResponse;
import com.chistesmalos.entity.Category;
import com.chistesmalos.repository.JokeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Mapper para convertir Category a CategoryResponse.
 */
@Component
@RequiredArgsConstructor
public class CategoryMapper {

    private final JokeRepository jokeRepository;

    public CategoryResponse toCategoryResponse(Category category) {
        if (category == null) {
            return null;
        }

        Long jokeCount = jokeRepository.countByCategoryIdAndIsPublishedTrueAndIsDeletedFalse(category.getId());

        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .jokeCount(jokeCount)
                .build();
    }
}
