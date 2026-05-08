package com.chistesmalos.service;

import com.chistesmalos.dto.CommentResponse;
import com.chistesmalos.entity.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Mapper para convertir Comment a CommentResponse.
 */
@Component
@RequiredArgsConstructor
public class CommentMapper {

    private final UserMapper userMapper;

    public CommentResponse toCommentResponse(Comment comment) {
        if (comment == null) {
            return null;
        }

        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .author(userMapper.toUserResponse(comment.getUser()))
                .jokeId(comment.getJoke().getId())
                .upvotes(comment.getUpvotes())
                .downvotes(comment.getDownvotes())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}
