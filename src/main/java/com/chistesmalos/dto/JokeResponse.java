package com.chistesmalos.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para respuesta de chiste.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JokeResponse {

    private Long id;
    private String title;
    private String content;
    private Long upvotes;
    private Long downvotes;
    private Long views;
    private Boolean isPublished;
    private String categoryName;
    private Long categoryId;
    private UserResponse author;
    private Long commentCount;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
}
