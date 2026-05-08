package com.chistesmalos.service;

import com.chistesmalos.dto.JokeResponse;
import com.chistesmalos.entity.Joke;
import com.chistesmalos.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Mapper para convertir Joke a JokeResponse.
 */
@Component
@RequiredArgsConstructor
public class JokeMapper {

    private final UserMapper userMapper;
    private final CommentRepository commentRepository;

    public JokeResponse toJokeResponse(Joke joke) {
        if (joke == null) {
            return null;
        }

        Long commentCount = commentRepository.countByJokeId(joke.getId());

        return JokeResponse.builder()
                .id(joke.getId())
                .title(joke.getTitle())
                .content(joke.getContent())
                .upvotes(joke.getUpvotes())
                .downvotes(joke.getDownvotes())
                .views(joke.getViews())
                .isPublished(joke.getIsPublished())
                .categoryName(joke.getCategory().getName())
                .categoryId(joke.getCategory().getId())
                .author(userMapper.toUserResponse(joke.getUser()))
                .commentCount(commentCount)
                .createdAt(joke.getCreatedAt())
                .updatedAt(joke.getUpdatedAt())
                .build();
    }
}
