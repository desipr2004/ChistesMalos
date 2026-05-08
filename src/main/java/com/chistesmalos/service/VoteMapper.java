package com.chistesmalos.service;

import com.chistesmalos.dto.VoteResponse;
import com.chistesmalos.entity.Vote;
import org.springframework.stereotype.Component;

/**
 * Mapper para convertir Vote a VoteResponse.
 */
@Component
public class VoteMapper {

    public VoteResponse toVoteResponse(Vote vote) {
        if (vote == null) {
            return null;
        }

        String voteType = switch (vote.getVoteValue()) {
            case 1 -> "UPVOTE";
            case -1 -> "DOWNVOTE";
            case 0 -> "NEUTRAL";
            default -> "UNKNOWN";
        };

        return VoteResponse.builder()
                .id(vote.getId())
                .voteType(voteType)
                .jokeId(vote.getJoke() != null ? vote.getJoke().getId() : null)
                .commentId(vote.getComment() != null ? vote.getComment().getId() : null)
                .build();
    }
}
