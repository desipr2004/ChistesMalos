package com.chistesmalos.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entidad Joke - Representa un chiste en la plataforma.
 */
@Entity
@Table(name = "jokes", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_category_id", columnList = "category_id"),
    @Index(name = "idx_created_at", columnList = "created_at"),
    @Index(name = "idx_upvotes", columnList = "upvotes"),
    @Index(name = "idx_published", columnList = "is_published")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Joke {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El título es requerido")
    @Size(min = 5, max = 200, message = "El título debe tener entre 5 y 200 caracteres")
    @Column(nullable = false, length = 200)
    private String title;

    @NotBlank(message = "El contenido es requerido")
    @Size(min = 10, max = 5000, message = "El contenido debe tener entre 10 y 5000 caracteres")
    @Column(nullable = false, columnDefinition = "TEXT", length = 5000)
    private String content;

    @Column(nullable = false)
    @Builder.Default
    private Long upvotes = 0L;

    @Column(nullable = false)
    @Builder.Default
    private Long downvotes = 0L;

    @Column(nullable = false)
    @Builder.Default
    private Long views = 0L;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isPublished = true;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;

    @Version
    private Long version;

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
    @JoinColumn(name = "category_id", nullable = false)
    @ToString.Exclude
    private Category category;

    @OneToMany(mappedBy = "joke", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @Builder.Default
    private Set<Comment> comments = new HashSet<>();

    @OneToMany(mappedBy = "joke", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @Builder.Default
    private Set<Vote> votes = new HashSet<>();

}
