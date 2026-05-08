package com.chistesmalos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para respuesta de voto.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoteResponse {

    private Long id;
    private String voteType;  // UPVOTE, DOWNVOTE
    private Long jokeId;
    private Long commentId;
}
