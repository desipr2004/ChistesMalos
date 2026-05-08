package com.chistesmalos.repository;

import com.chistesmalos.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para la entidad Comment.
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByJokeId(Long jokeId, Pageable pageable);
    Page<Comment> findByUserId(Long userId, Pageable pageable);
    Long countByJokeId(Long jokeId);
}
