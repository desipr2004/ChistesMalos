package com.chistesmalos.service;

import com.chistesmalos.dto.VoteRequest;
import com.chistesmalos.entity.Comment;
import com.chistesmalos.entity.Joke;
import com.chistesmalos.entity.User;
import com.chistesmalos.entity.Vote;
import com.chistesmalos.exception.ResourceNotFoundException;
import com.chistesmalos.repository.CommentRepository;
import com.chistesmalos.repository.JokeRepository;
import com.chistesmalos.repository.UserRepository;
import com.chistesmalos.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio para gestión de votos.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;
    private final JokeRepository jokeRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    /**
     * Votar en un chiste.
     */
    @Transactional
    @CacheEvict(value = "jokes", allEntries = true)
    public void voteOnJoke(Long jokeId, VoteRequest request, String username) {
        Joke joke = jokeRepository.findById(jokeId)
                .orElseThrow(() -> new ResourceNotFoundException("Chiste no encontrado"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        // Determinar el valor del voto
        int voteValue = request.getVoteType() == VoteRequest.VoteType.UPVOTE ? 1 : -1;

        // Buscar voto existente usando repositorio personalizado
        // Para esto usamos una consulta simple
        var existingVote = voteRepository.findByUserIdAndJokeId(user.getId(), jokeId);

        if (existingVote.isPresent()) {
            Vote vote = existingVote.get();
            int oldVoteValue = vote.getVoteValue();

            if (request.getVoteType() == VoteRequest.VoteType.REMOVE) {
                decrementJokeVotes(joke, oldVoteValue);
                voteRepository.delete(vote);
            } else if (oldVoteValue != voteValue) {
                adjustJokeVotes(joke, oldVoteValue, voteValue);
                vote.setVoteValue(voteValue);
                voteRepository.save(vote);
            }
        } else if (request.getVoteType() != VoteRequest.VoteType.REMOVE) {
            Vote newVote = Vote.builder()
                    .user(user)
                    .voteValue(voteValue)
                    .voteableType("JOKE")
                    .voteableId(jokeId)
                    .build();
            incrementJokeVotes(joke, voteValue);
            voteRepository.save(newVote);
        }

        jokeRepository.save(joke);
        log.info("Usuario {} votó en chiste {}", username, jokeId);
    }

    /**
     * Votar en un comentario.
     */
    @Transactional
    @CacheEvict(value = "comments", allEntries = true)
    public void voteOnComment(Long commentId, VoteRequest request, String username) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comentario no encontrado"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        int voteValue = request.getVoteType() == VoteRequest.VoteType.UPVOTE ? 1 : -1;

        var existingVote = voteRepository.findByUserIdAndCommentId(user.getId(), commentId);

        if (existingVote.isPresent()) {
            Vote vote = existingVote.get();
            int oldVoteValue = vote.getVoteValue();

            if (request.getVoteType() == VoteRequest.VoteType.REMOVE) {
                decrementCommentVotes(comment, oldVoteValue);
                voteRepository.delete(vote);
            } else if (oldVoteValue != voteValue) {
                adjustCommentVotes(comment, oldVoteValue, voteValue);
                vote.setVoteValue(voteValue);
                voteRepository.save(vote);
            }
        } else if (request.getVoteType() != VoteRequest.VoteType.REMOVE) {
            Vote newVote = Vote.builder()
                    .user(user)
                    .voteValue(voteValue)
                    .voteableType("COMMENT")
                    .voteableId(commentId)
                    .build();
            incrementCommentVotes(comment, voteValue);
            voteRepository.save(newVote);
        }

        commentRepository.save(comment);
        log.info("Usuario {} votó en comentario {}", username, commentId);
    }

    private void incrementJokeVotes(Joke joke, int voteValue) {
        if (voteValue == 1) {
            joke.setUpvotes(joke.getUpvotes() + 1);
        } else {
            joke.setDownvotes(joke.getDownvotes() + 1);
        }
    }

    private void decrementJokeVotes(Joke joke, int oldVoteValue) {
        if (oldVoteValue == 1) {
            joke.setUpvotes(Math.max(0, joke.getUpvotes() - 1));
        } else {
            joke.setDownvotes(Math.max(0, joke.getDownvotes() - 1));
        }
    }

    private void adjustJokeVotes(Joke joke, int oldVoteValue, int newVoteValue) {
        decrementJokeVotes(joke, oldVoteValue);
        incrementJokeVotes(joke, newVoteValue);
    }

    private void incrementCommentVotes(Comment comment, int voteValue) {
        if (voteValue == 1) {
            comment.setUpvotes(comment.getUpvotes() + 1);
        } else {
            comment.setDownvotes(comment.getDownvotes() + 1);
        }
    }

    private void decrementCommentVotes(Comment comment, int oldVoteValue) {
        if (oldVoteValue == 1) {
            comment.setUpvotes(Math.max(0, comment.getUpvotes() - 1));
        } else {
            comment.setDownvotes(Math.max(0, comment.getDownvotes() - 1));
        }
    }

    private void adjustCommentVotes(Comment comment, int oldVoteValue, int newVoteValue) {
        decrementCommentVotes(comment, oldVoteValue);
        incrementCommentVotes(comment, newVoteValue);
    }
}

