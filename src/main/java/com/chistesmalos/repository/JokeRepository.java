package com.chistesmalos.repository;

import com.chistesmalos.entity.Joke;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la entidad Joke.
 */
@Repository
public interface JokeRepository extends JpaRepository<Joke, Long> {
    Page<Joke> findByIsPublishedTrueAndIsDeletedFalse(Pageable pageable);
    Page<Joke> findByCategoryIdAndIsPublishedTrueAndIsDeletedFalse(Long categoryId, Pageable pageable);
    Page<Joke> findByUserIdAndIsDeletedFalse(Long userId, Pageable pageable);
    
    long countByCategoryIdAndIsPublishedTrueAndIsDeletedFalse(Long categoryId);

    Optional<Joke> findFirstByIsPublishedTrueAndIsDeletedFalseOrderByUpvotesDesc();

    @Query("SELECT j FROM Joke j WHERE j.isPublished = true AND j.isDeleted = false ORDER BY FUNCTION('RAND') LIMIT 1")
    Optional<Joke> findJokeOfDay();

    @Query("SELECT j FROM Joke j WHERE j.isPublished = true AND j.isDeleted = false ORDER BY (j.downvotes - j.upvotes) DESC")
    Page<Joke> findWorstJokes(Pageable pageable);
}
