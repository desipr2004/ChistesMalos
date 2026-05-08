package com.chistesmalos.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entidad Vote - Votos en chistes y comentarios.
 */
@Entity
@Table(name = "votes", uniqueConstraints = {
    @UniqueConstraint(name = "uk_vote", columnNames = {"user_id", "voteable_id", "voteable_type"})
}, indexes = {
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_voteable", columnList = "voteable_id", unique = false)
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer voteValue; // 1 (upvote), -1 (downvote), 0 (neutral)

    @Column(nullable = false, length = 50)
    private String voteableType; // 'JOKE', 'COMMENT'

    @Column(nullable = false)
    private Long voteableId;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Relaciones
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "joke_id")
    @ToString.Exclude
    private Joke joke;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    @ToString.Exclude
    private Comment comment;

}
