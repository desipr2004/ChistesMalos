package com.chistesmalos.repository;

import com.chistesmalos.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la entidad Vote.
 */
@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    Optional<Vote> findByUserIdAndJokeId(Long userId, Long jokeId);
    Optional<Vote> findByUserIdAndCommentId(Long userId, Long commentId);
    Boolean existsByUserIdAndJokeId(Long userId, Long jokeId);
    Boolean existsByUserIdAndCommentId(Long userId, Long commentId);
}
