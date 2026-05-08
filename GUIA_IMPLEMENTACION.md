# Guía Técnica de Implementación - ChistesMalos

## 1. SETUP INICIAL DEL PROYECTO

### 1.1 Crear Estructura Base con Spring Boot

```bash
# Opción 1: Usar Spring Boot CLI
spring boot new --from https://start.spring.io chistesmalos \
  --dependencies=web,data-jpa,security,mysql,validation,lombok,redis

# Opción 2: Descargar desde https://start.spring.io con:
# - Project: Maven
# - Language: Java
# - Spring Boot: 3.2.x (LTS)
# - Dependencies: 
#   * Spring Web
#   * Spring Data JPA
#   * Spring Security
#   * MySQL Driver
#   * Spring Data Redis
#   * Validation
#   * Lombok
#   * Dev Tools
```

### 1.2 Estructura pom.xml (Dependencias Principales)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.chistesmalos</groupId>
    <artifactId>chistesmalos</artifactId>
    <version>1.0.0</version>
    <name>ChistesMalos</name>
    <description>Plataforma para compartir chistes malos</description>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
    </parent>
    
    <properties>
        <java.version>21</java.version>
        <maven.compiler.source>21</maven.compiler.source>
        <maven.compiler.target>21</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>
    
    <dependencies>
        <!-- Spring Boot Starters -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
        
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>
        
        <!-- Database -->
        <dependency>
            <groupId>com.mysql</groupId>
            <artifactId>mysql-connector-j</artifactId>
            <version>8.0.33</version>
            <scope>runtime</scope>
        </dependency>
        
        <!-- JWT -->
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-api</artifactId>
            <version>0.12.3</version>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-impl</artifactId>
            <version>0.12.3</version>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-jackson</artifactId>
            <version>0.12.3</version>
            <scope>runtime</scope>
        </dependency>
        
        <!-- Lombok -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        
        <!-- MapStruct para DTOs -->
        <dependency>
            <groupId>org.mapstruct</groupId>
            <artifactId>mapstruct</artifactId>
            <version>1.5.5.Final</version>
        </dependency>
        
        <!-- Testing -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        
        <dependency>
            <groupId>org.springframework.security</groupId>
            <artifactId>spring-security-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
            
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <configuration>
                    <source>21</source>
                    <target>21</target>
                    <annotationProcessorPaths>
                        <path>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                            <version>1.18.30</version>
                        </path>
                        <path>
                            <groupId>org.mapstruct</groupId>
                            <artifactId>mapstruct-processor</artifactId>
                            <version>1.5.5.Final</version>
                        </path>
                    </annotationProcessorPaths>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

---

## 2. CONFIGURACIÓN APPLICATION.YML

### 2.1 application.yml (Desarrollo)

```yaml
spring:
  application:
    name: chistesmalos-api
  
  datasource:
    url: jdbc:mysql://localhost:3306/chistesmalos
    username: root
    password: password123
    driver-class-name: com.mysql.cj.jdbc.Driver
  
  jpa:
    hibernate:
      ddl-auto: validate  # use 'update' in dev, 'validate' in prod
    show-sql: true
    properties:
      hibernate:
        format_sql: true
        dialect: org.hibernate.dialect.MySQL8Dialect
        jdbc:
          batch_size: 20
          fetch_size: 50
        order_inserts: true
        order_updates: true
  
  redis:
    host: localhost
    port: 6379
    timeout: 2000ms
    jedis:
      pool:
        max-active: 8
        max-idle: 8
        min-idle: 0
  
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 10MB

server:
  port: 8080
  servlet:
    context-path: /api/v1
  compression:
    enabled: true
    mime-types: application/json,application/xml,text/html,text/xml,text/plain

jwt:
  secret: your-secret-key-min-256-bits-very-secure-key-here-change-in-production
  expiration: 86400000  # 24 horas en milisegundos
  refresh-expiration: 604800000  # 7 días

logging:
  level:
    root: INFO
    com.chistesmalos: DEBUG
    org.springframework.web: DEBUG
    org.hibernate.SQL: DEBUG
```

### 2.2 application-prod.yml (Producción)

```yaml
spring:
  datasource:
    url: jdbc:mysql://prod-db.example.com:3306/chistesmalos
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      idle-timeout: 600000
      max-lifetime: 1800000
  
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
    properties:
      hibernate:
        format_sql: false
        jdbc:
          batch_size: 20
          fetch_size: 100
  
  redis:
    host: ${REDIS_HOST}
    port: ${REDIS_PORT}
    password: ${REDIS_PASSWORD}
    ssl: true

server:
  port: 8080
  compression:
    enabled: true
    min-response-size: 1024

jwt:
  secret: ${JWT_SECRET}
  expiration: 86400000

logging:
  level:
    root: WARN
    com.chistesmalos: INFO
```

---

## 3. CLASE PRINCIPAL Y CONFIGURACIÓN

### 3.1 ChistesMalosApplication.java

```java
package com.chistesmalos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableCaching
@EnableScheduling
public class ChistesMalosApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(ChistesMalosApplication.class, args);
    }
}
```

### 3.2 SecurityConfig.java

```java
package com.chistesmalos.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.chistesmalos.security.JwtAuthenticationFilter;
import com.chistesmalos.security.JwtAuthenticationEntryPoint;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .build();
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .exceptionHandling(exception -> 
                exception.authenticationEntryPoint(jwtAuthenticationEntryPoint)
            )
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/jokes").permitAll()
                .requestMatchers("/jokes/{id}").permitAll()
                .requestMatchers("/jokes/{id}/comments").permitAll()
                .requestMatchers("/users/{id}").permitAll()
                .requestMatchers("/categories").permitAll()
                .requestMatchers("/search").permitAll()
                .anyRequest().authenticated()
            );
        
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}
```

---

## 4. MODELOS JPA

### 4.1 User.java

```java
package com.chistesmalos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.annotation.CreationTimestamp;
import org.springframework.data.annotation.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_email", columnList = "email"),
    @Index(name = "idx_username", columnList = "username"),
    @Index(name = "idx_created_at", columnList = "created_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50)
    @Column(unique = true, nullable = false)
    private String username;
    
    @NotBlank(message = "Email is required")
    @Email
    @Column(unique = true, nullable = false)
    private String email;
    
    @NotBlank
    @Column(nullable = false)
    private String passwordHash;
    
    @Size(max = 100)
    private String displayName;
    
    @Column(columnDefinition = "TEXT")
    private String bio;
    
    @Size(max = 500)
    private String avatarUrl;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    @Builder.Default
    private Boolean isActive = true;
    
    @Builder.Default
    private Boolean isVerified = false;
    
    private LocalDateTime lastLogin;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Joke> jokes = new HashSet<>();
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Comment> comments = new HashSet<>();
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Vote> votes = new HashSet<>();
    
    @ManyToMany
    @JoinTable(
        name = "follows",
        joinColumns = @JoinColumn(name = "follower_id"),
        inverseJoinColumns = @JoinColumn(name = "following_id")
    )
    private Set<User> following = new HashSet<>();
    
    @ManyToMany(mappedBy = "following")
    private Set<User> followers = new HashSet<>();
}
```

### 4.2 Joke.java

```java
package com.chistesmalos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.annotation.CreationTimestamp;
import org.springframework.data.annotation.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "jokes", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_category_id", columnList = "category_id"),
    @Index(name = "idx_created_at", columnList = "created_at"),
    @Index(name = "idx_upvotes", columnList = "upvotes"),
    @Index(name = "idx_is_published", columnList = "is_published")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Joke {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @NotBlank(message = "Title is required")
    @Size(min = 5, max = 200)
    @Column(nullable = false)
    private String title;
    
    @NotBlank(message = "Content is required")
    @Size(min = 10, max = 5000)
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    
    @Builder.Default
    @Column(nullable = false)
    private Integer upvotes = 0;
    
    @Builder.Default
    @Column(nullable = false)
    private Integer downvotes = 0;
    
    @Builder.Default
    @Column(nullable = false)
    private Integer commentCount = 0;
    
    @Builder.Default
    @Column(nullable = false)
    private Integer views = 0;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    @Builder.Default
    private Boolean isPublished = true;
    
    @Builder.Default
    private Boolean isDeleted = false;
    
    @OneToMany(mappedBy = "joke", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Comment> comments = new HashSet<>();
    
    @OneToMany(mappedBy = "joke", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Vote> votes = new HashSet<>();
    
    // Métodos helper
    public void incrementUpvotes() {
        this.upvotes++;
    }
    
    public void decrementUpvotes() {
        if (this.upvotes > 0) this.upvotes--;
    }
    
    public void incrementDownvotes() {
        this.downvotes++;
    }
    
    public void decrementDownvotes() {
        if (this.downvotes > 0) this.downvotes--;
    }
    
    public Integer getNetVotes() {
        return this.upvotes - this.downvotes;
    }
}
```

### 4.3 Comment.java

```java
package com.chistesmalos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.annotation.CreationTimestamp;
import org.springframework.data.annotation.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "comments", indexes = {
    @Index(name = "idx_joke_id", columnList = "joke_id"),
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_created_at", columnList = "created_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "joke_id", nullable = false)
    private Joke joke;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @NotBlank
    @Size(min = 1, max = 1000)
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;
    
    @Builder.Default
    private Integer upvotes = 0;
    
    @Builder.Default
    private Integer downvotes = 0;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    private Comment parentComment;
    
    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Comment> replies = new HashSet<>();
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    @Builder.Default
    private Boolean isDeleted = false;
    
    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Vote> votes = new HashSet<>();
}
```

### 4.4 Vote.java

```java
package com.chistesmalos.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreationTimestamp;
import org.springframework.data.annotation.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "votes", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "voteable_id", "voteable_type"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vote {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false)
    private Long voteableId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VoteableType voteableType;
    
    @Column(nullable = false)
    private Byte voteValue;  // -1 (downvote), 0 (remove), 1 (upvote)
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    @ManyToOne
    @JoinColumn(name = "joke_id")
    private Joke joke;
    
    @ManyToOne
    @JoinColumn(name = "comment_id")
    private Comment comment;
    
    public enum VoteableType {
        JOKE, COMMENT
    }
}
```

### 4.5 Category.java

```java
package com.chistesmalos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.annotation.CreationTimestamp;

@Entity
@Table(name = "categories", indexes = {
    @Index(name = "idx_slug", columnList = "slug")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @NotBlank
    @Size(min = 3, max = 50)
    @Column(unique = true, nullable = false)
    private String name;
    
    @Size(max = 255)
    private String description;
    
    @NotBlank
    @Column(unique = true, nullable = false)
    private String slug;
    
    @CreationTimestamp
    private java.time.LocalDateTime createdAt;
}
```

---

## 5. REPOSITORIOS

### 5.1 JokeRepository.java

```java
package com.chistesmalos.repository;

import com.chistesmalos.model.Joke;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface JokeRepository extends JpaRepository<Joke, Long> {
    
    Page<Joke> findByIsPublishedTrueAndIsDeletedFalse(Pageable pageable);
    
    Page<Joke> findByUserIdAndIsDeletedFalse(Long userId, Pageable pageable);
    
    Page<Joke> findByCategoryIdAndIsPublishedTrueAndIsDeletedFalse(Integer categoryId, Pageable pageable);
    
    @Query("SELECT j FROM Joke j WHERE j.isPublished = true AND j.isDeleted = false " +
           "ORDER BY j.upvotes DESC LIMIT 50")
    List<Joke> findTrendingJokes();
    
    @Query("SELECT j FROM Joke j WHERE j.isPublished = true AND j.isDeleted = false " +
           "AND j.createdAt >= :since ORDER BY j.upvotes DESC")
    List<Joke> findTrendingJokesSince(@Param("since") LocalDateTime since);
    
    @Query(value = "SELECT * FROM jokes WHERE MATCH(title, content) " +
           "AGAINST(:searchTerm IN BOOLEAN MODE) AND is_published = true " +
           "AND is_deleted = false", nativeQuery = true)
    Page<Joke> searchByFullText(@Param("searchTerm") String searchTerm, Pageable pageable);
}
```

### 5.2 UserRepository.java

```java
package com.chistesmalos.repository;

import com.chistesmalos.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
}
```

### 5.3 CommentRepository.java

```java
package com.chistesmalos.repository;

import com.chistesmalos.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    
    Page<Comment> findByJokeIdAndIsDeletedFalse(Long jokeId, Pageable pageable);
    
    Page<Comment> findByUserIdAndIsDeletedFalse(Long userId, Pageable pageable);
}
```

### 5.4 VoteRepository.java

```java
package com.chistesmalos.repository;

import com.chistesmalos.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    
    Optional<Vote> findByUserIdAndVoteableIdAndVoteableType(
        Long userId, Long voteableId, Vote.VoteableType voteableType
    );
    
    boolean existsByUserIdAndVoteableIdAndVoteableType(
        Long userId, Long voteableId, Vote.VoteableType voteableType
    );
}
```

---

## 6. SERVICIOS

### 6.1 JokeService.java

```java
package com.chistesmalos.service;

import com.chistesmalos.dto.JokeDTO;
import com.chistesmalos.model.Joke;
import com.chistesmalos.model.User;
import com.chistesmalos.repository.JokeRepository;
import com.chistesmalos.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class JokeService {
    
    private final JokeRepository jokeRepository;
    private final UserRepository userRepository;
    
    public Page<Joke> getAllJokes(Pageable pageable) {
        log.info("Fetching all jokes with pagination: {}", pageable);
        return jokeRepository.findByIsPublishedTrueAndIsDeletedFalse(pageable);
    }
    
    public Page<Joke> getJokesByUser(Long userId, Pageable pageable) {
        log.info("Fetching jokes for user: {}", userId);
        return jokeRepository.findByUserIdAndIsDeletedFalse(userId, pageable);
    }
    
    public Page<Joke> getJokesByCategory(Integer categoryId, Pageable pageable) {
        log.info("Fetching jokes for category: {}", categoryId);
        return jokeRepository.findByCategoryIdAndIsPublishedTrueAndIsDeletedFalse(categoryId, pageable);
    }
    
    @Transactional(readOnly = true)
    public Joke getJokeById(Long id) {
        log.info("Fetching joke with id: {}", id);
        Joke joke = jokeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Joke not found"));
        
        // Incrementar views
        joke.setViews(joke.getViews() + 1);
        jokeRepository.save(joke);
        
        return joke;
    }
    
    @CacheEvict(value = "trending-jokes", allEntries = true)
    public Joke createJoke(Joke joke, Long userId) {
        log.info("Creating joke for user: {}", userId);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        joke.setUser(user);
        joke.setIsPublished(true);
        joke.setIsDeleted(false);
        
        return jokeRepository.save(joke);
    }
    
    @CacheEvict(value = "trending-jokes", allEntries = true)
    public Joke updateJoke(Long id, Joke jokeDetails, Long userId) {
        log.info("Updating joke: {} by user: {}", id, userId);
        
        Joke joke = jokeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Joke not found"));
        
        if (!joke.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }
        
        joke.setTitle(jokeDetails.getTitle());
        joke.setContent(jokeDetails.getContent());
        joke.setCategory(jokeDetails.getCategory());
        
        return jokeRepository.save(joke);
    }
    
    @CacheEvict(value = "trending-jokes", allEntries = true)
    public void deleteJoke(Long id, Long userId) {
        log.info("Deleting joke: {} by user: {}", id, userId);
        
        Joke joke = jokeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Joke not found"));
        
        if (!joke.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }
        
        joke.setIsDeleted(true);
        jokeRepository.save(joke);
    }
    
    @Transactional(readOnly = true)
    @Cacheable(value = "trending-jokes", unless = "#result == null")
    public List<Joke> getTrendingJokes() {
        log.info("Fetching trending jokes");
        LocalDateTime since = LocalDateTime.now().minusHours(24);
        return jokeRepository.findTrendingJokesSince(since);
    }
    
    public Page<Joke> searchJokes(String searchTerm, Pageable pageable) {
        log.info("Searching jokes with term: {}", searchTerm);
        return jokeRepository.searchByFullText(searchTerm, pageable);
    }
}
```

### 6.2 UserService.java

```java
package com.chistesmalos.service;

import com.chistesmalos.model.User;
import com.chistesmalos.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public User registerUser(String username, String email, String password) {
        log.info("Registering new user: {}", username);
        
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists");
        }
        
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }
        
        User user = User.builder()
            .username(username)
            .email(email)
            .passwordHash(passwordEncoder.encode(password))
            .isActive(true)
            .isVerified(false)
            .build();
        
        return userRepository.save(user);
    }
    
    @Transactional(readOnly = true)
    public User getUserByUsername(String username) {
        log.info("Fetching user by username: {}", username);
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }
    
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        log.info("Fetching user by id: {}", id);
        return userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }
    
    public User updateUserProfile(Long userId, String displayName, String bio, String avatarUrl) {
        log.info("Updating profile for user: {}", userId);
        
        User user = getUserById(userId);
        user.setDisplayName(displayName);
        user.setBio(bio);
        user.setAvatarUrl(avatarUrl);
        
        return userRepository.save(user);
    }
    
    public void updateLastLogin(Long userId) {
        User user = getUserById(userId);
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
    }
    
    public void followUser(Long followerId, Long followingId) {
        log.info("User {} following user {}", followerId, followingId);
        
        User follower = getUserById(followerId);
        User following = getUserById(followingId);
        
        follower.getFollowing().add(following);
        userRepository.save(follower);
    }
    
    public void unfollowUser(Long followerId, Long followingId) {
        log.info("User {} unfollowing user {}", followerId, followingId);
        
        User follower = getUserById(followerId);
        User following = getUserById(followingId);
        
        follower.getFollowing().remove(following);
        userRepository.save(follower);
    }
}
```

---

## 7. CONTROLADORES REST

### 7.1 JokeController.java

```java
package com.chistesmalos.controller;

import com.chistesmalos.dto.JokeDTO;
import com.chistesmalos.model.Joke;
import com.chistesmalos.service.JokeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/jokes")
@RequiredArgsConstructor
public class JokeController {
    
    private final JokeService jokeService;
    
    @GetMapping
    public ResponseEntity<Page<Joke>> getAllJokes(Pageable pageable) {
        log.info("GET /jokes - Get all jokes");
        return ResponseEntity.ok(jokeService.getAllJokes(pageable));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Joke> getJokeById(@PathVariable Long id) {
        log.info("GET /jokes/{} - Get joke by id", id);
        return ResponseEntity.ok(jokeService.getJokeById(id));
    }
    
    @PostMapping
    public ResponseEntity<Joke> createJoke(@Valid @RequestBody Joke joke, Authentication authentication) {
        log.info("POST /jokes - Create new joke");
        Long userId = Long.parseLong(authentication.getName());
        Joke createdJoke = jokeService.createJoke(joke, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdJoke);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Joke> updateJoke(
            @PathVariable Long id,
            @Valid @RequestBody Joke jokeDetails,
            Authentication authentication) {
        log.info("PUT /jokes/{} - Update joke", id);
        Long userId = Long.parseLong(authentication.getName());
        Joke updatedJoke = jokeService.updateJoke(id, jokeDetails, userId);
        return ResponseEntity.ok(updatedJoke);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJoke(@PathVariable Long id, Authentication authentication) {
        log.info("DELETE /jokes/{} - Delete joke", id);
        Long userId = Long.parseLong(authentication.getName());
        jokeService.deleteJoke(id, userId);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/trending")
    public ResponseEntity<List<Joke>> getTrendingJokes() {
        log.info("GET /jokes/trending - Get trending jokes");
        return ResponseEntity.ok(jokeService.getTrendingJokes());
    }
    
    @GetMapping("/search")
    public ResponseEntity<Page<Joke>> searchJokes(
            @RequestParam String q,
            Pageable pageable) {
        log.info("GET /jokes/search - Search jokes with term: {}", q);
        return ResponseEntity.ok(jokeService.searchJokes(q, pageable));
    }
    
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Page<Joke>> getJokesByCategory(
            @PathVariable Integer categoryId,
            Pageable pageable) {
        log.info("GET /jokes/category/{} - Get jokes by category", categoryId);
        return ResponseEntity.ok(jokeService.getJokesByCategory(categoryId, pageable));
    }
}
```

### 7.2 AuthController.java

```java
package com.chistesmalos.controller;

import com.chistesmalos.dto.AuthRequest;
import com.chistesmalos.dto.AuthResponse;
import com.chistesmalos.model.User;
import com.chistesmalos.security.JwtTokenProvider;
import com.chistesmalos.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final UserService userService;
    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;
    
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody AuthRequest authRequest) {
        log.info("POST /auth/register - Register new user");
        
        User user = userService.registerUser(
            authRequest.getUsername(),
            authRequest.getEmail(),
            authRequest.getPassword()
        );
        
        String token = tokenProvider.generateToken(user.getId());
        
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .email(user.getEmail())
                .build());
    }
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest authRequest) {
        log.info("POST /auth/login - User login");
        
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                authRequest.getUsername(),
                authRequest.getPassword()
            )
        );
        
        User user = (User) authentication.getPrincipal();
        userService.updateLastLogin(user.getId());
        
        String token = tokenProvider.generateToken(user.getId());
        
        return ResponseEntity.ok(AuthResponse.builder()
            .token(token)
            .username(user.getUsername())
            .email(user.getEmail())
            .build());
    }
}
```

---

## 8. DTOs

### 8.1 JokeDTO.java

```java
package com.chistesmalos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JokeDTO {
    private Long id;
    private String title;
    private String content;
    private String category;
    private Integer upvotes;
    private Integer downvotes;
    private Integer netVotes;
    private Integer commentCount;
    private Integer views;
    private String authorUsername;
    private String authorAvatar;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

### 8.2 AuthRequest.java & AuthResponse.java

```java
package com.chistesmalos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequest {
    private String username;
    private String email;
    private String password;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String username;
    private String email;
}
```

---

## 9. SCRIPT DE INICIALIZACIÓN DE BASE DE DATOS

### 9.1 V1__Initial_Schema.sql

```sql
-- Ejecutar este archivo con Flyway o manualmente

CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    display_name VARCHAR(100),
    bio TEXT,
    avatar_url VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    is_verified BOOLEAN DEFAULT FALSE,
    last_login TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_username (username),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS categories (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) UNIQUE NOT NULL,
    description VARCHAR(255),
    slug VARCHAR(50) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_slug (slug)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS jokes (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    category_id INT NOT NULL,
    upvotes INT DEFAULT 0,
    downvotes INT DEFAULT 0,
    comment_count INT DEFAULT 0,
    views INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_published BOOLEAN DEFAULT TRUE,
    is_deleted BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories(id),
    INDEX idx_user_id (user_id),
    INDEX idx_category_id (category_id),
    INDEX idx_created_at (created_at),
    INDEX idx_upvotes (upvotes),
    INDEX idx_is_published (is_published),
    FULLTEXT INDEX ftx_content (title, content)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS comments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    joke_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    upvotes INT DEFAULT 0,
    downvotes INT DEFAULT 0,
    parent_comment_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (joke_id) REFERENCES jokes(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (parent_comment_id) REFERENCES comments(id) ON DELETE CASCADE,
    INDEX idx_joke_id (joke_id),
    INDEX idx_user_id (user_id),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS votes (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    voteable_id BIGINT NOT NULL,
    voteable_type ENUM('JOKE', 'COMMENT') NOT NULL,
    vote_value TINYINT NOT NULL CHECK (vote_value IN (-1, 0, 1)),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY unique_vote (user_id, voteable_id, voteable_type),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_voteable (voteable_id, voteable_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS follows (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    follower_id BIGINT NOT NULL,
    following_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_follow (follower_id, following_id),
    FOREIGN KEY (follower_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (following_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_follower_id (follower_id),
    INDEX idx_following_id (following_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insertar categorías iniciales
INSERT INTO categories (name, slug, description) VALUES
('Programación', 'programming', 'Chistes sobre programación'),
('Ciencia', 'science', 'Chistes científicos'),
('Humor Negro', 'dark-humor', 'Humor oscuro'),
('Puns', 'puns', 'Juegos de palabras'),
('Profesiones', 'professions', 'Chistes sobre profesiones'),
('Actualidad', 'current', 'Chistes sobre actualidad')
ON DUPLICATE KEY UPDATE id=id;
```

---

Este documento proporciona la base técnica completa para iniciar el desarrollo de ChistesMalos. 

**Próximos pasos:**
1. Crear el proyecto Maven con Spring Boot
2. Configurar MySQL local
3. Ejecutar las migraciones SQL
4. Implementar los servicios y controladores
5. Crear tests unitarios
6. Desarrollar el frontend HTML/CSS/JS

