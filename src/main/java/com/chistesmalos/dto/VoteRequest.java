package com.chistesmalos.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para votar en un chiste o comentario.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoteRequest {

    @NotNull(message = "El tipo de voto es requerido")
    private VoteType voteType;

    public enum VoteType {
        UPVOTE, DOWNVOTE, REMOVE
    }
}
