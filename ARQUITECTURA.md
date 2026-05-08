# Arquitectura Técnica - ChistesMalos.com

## 1. VISIÓN GENERAL DEL SISTEMA

### Descripción del Proyecto
Plataforma web colaborativa para compartir, valorar y descubrir chistes malos. Los usuarios pueden:
- Registrarse y crear perfiles
- Publicar chistes malos con categorías
- Votar en chistes (upvote/downvote)
- Comentar en chistes
- Seguir otros usuarios
- Ver trending de chistes
- Filtrar por categoría y fecha

### Objetivos de Escalabilidad
- Soportar 100K+ usuarios concurrentes
- Procesar 10K+ publicaciones diarias
- Tiempo de respuesta < 200ms para lecturas
- Alta disponibilidad (99.9% uptime)
- Facilidad de mantenimiento y extensión

---

## 2. ARQUITECTURA DE CAPAS (3-TIER + API)

```
┌─────────────────────────────────────────────┐
│        CAPA DE PRESENTACIÓN (Frontend)      │
│     HTML5 | CSS3 | JavaScript (ES6+)        │
└─────────────────────────────────────────────┘
                      ↓ HTTP/REST
┌─────────────────────────────────────────────┐
│      CAPA DE API (API Gateway/REST)         │
│  Spring Boot 3.x + Spring MVC/WebFlux       │
│  - Autenticación (JWT/OAuth2)               │
│  - Rate Limiting                            │
│  - CORS & Seguridad                         │
└─────────────────────────────────────────────┘
                      ↓
┌─────────────────────────────────────────────┐
│    CAPA DE LÓGICA DE NEGOCIO (Service)      │
│  - UserService                              │
│  - JokeService                              │
│  - CommentService                           │
│  - VoteService                              │
│  - CategoryService                          │
│  - NotificationService                      │
└─────────────────────────────────────────────┘
                      ↓
┌─────────────────────────────────────────────┐
│  CAPA DE ACCESO A DATOS (Repository/DAO)    │
│  Spring Data JPA + Hibernate                │
│  - UserRepository                           │
│  - JokeRepository                           │
│  - CommentRepository                        │
│  - VoteRepository                           │
└─────────────────────────────────────────────┘
                      ↓
┌─────────────────────────────────────────────┐
│    CAPA DE PERSISTENCIA (BASE DE DATOS)     │
│  MySQL 8.0+ (Master-Slave Replication)      │
│  Redis (Caché y Sesiones)                   │
│  Elasticsearch (Búsqueda Full-Text)         │
└─────────────────────────────────────────────┘
```

---

## 3. COMPONENTES DEL SISTEMA

### 3.1 Backend - Spring Boot 3.x
```
Estructura del Proyecto:
chistesmalos-backend/
├── src/main/java/com/chistesmalos/
│   ├── ChistesMalosApplication.java
│   ├── config/
│   │   ├── SecurityConfig.java
│   │   ├── DatabaseConfig.java
│   │   ├── CacheConfig.java
│   │   └── CorsConfig.java
│   ├── controller/
│   │   ├── JokeController.java
│   │   ├── UserController.java
│   │   ├── CommentController.java
│   │   ├── VoteController.java
│   │   └── CategoryController.java
│   ├── service/
│   │   ├── JokeService.java
│   │   ├── UserService.java
│   │   ├── CommentService.java
│   │   ├── VoteService.java
│   │   └── CategoryService.java
│   ├── repository/
│   │   ├── JokeRepository.java
│   │   ├── UserRepository.java
│   │   ├── CommentRepository.java
│   │   └── VoteRepository.java
│   ├── model/
│   │   ├── User.java
│   │   ├── Joke.java
│   │   ├── Comment.java
│   │   ├── Vote.java
│   │   └── Category.java
│   ├── dto/
│   │   ├── JokeDTO.java
│   │   ├── UserDTO.java
│   │   └── CommentDTO.java
│   ├── exception/
│   │   ├── GlobalExceptionHandler.java
│   │   └── CustomExceptions.java
│   ├── security/
│   │   ├── JwtTokenProvider.java
│   │   ├── JwtAuthenticationFilter.java
│   │   └── UserDetailsServiceImpl.java
│   └── util/
│       └── ValidationUtil.java
├── resources/
│   ├── application.yml
│   ├── application-dev.yml
│   ├── application-prod.yml
│   └── db/migration/
│       └── V1__Initial_Schema.sql
└── pom.xml
```

### 3.2 Frontend - HTML/CSS/JavaScript
```
Estructura del Proyecto:
chistesmalos-frontend/
├── index.html
├── css/
│   ├── styles.css
│   ├── responsive.css
│   └── animations.css
├── js/
│   ├── main.js
│   ├── api/
│   │   ├── jokeApi.js
│   │   ├── userApi.js
│   │   └── commentApi.js
│   ├── components/
│   │   ├── jokeCard.js
│   │   ├── navbar.js
│   │   ├── modal.js
│   │   └── pagination.js
│   ├── utils/
│   │   ├── auth.js
│   │   ├── validator.js
│   │   └── storage.js
│   └── pages/
│       ├── home.js
│       ├── profile.js
│       ├── createJoke.js
│       └── trending.js
└── assets/
    └── images/
```

---

## 4. MODELOS DE DATOS

### 4.1 Entidad: User (Usuario)
```sql
CREATE TABLE users (
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
);
```

### 4.2 Entidad: Joke (Chiste)
```sql
CREATE TABLE jokes (
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
);
```

### 4.3 Entidad: Comment (Comentario)
```sql
CREATE TABLE comments (
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
);
```

### 4.4 Entidad: Vote (Voto)
```sql
CREATE TABLE votes (
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
);
```

### 4.5 Entidad: Category (Categoría)
```sql
CREATE TABLE categories (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) UNIQUE NOT NULL,
    description VARCHAR(255),
    slug VARCHAR(50) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_slug (slug)
);
```

### 4.6 Entidad: Follow (Seguimiento)
```sql
CREATE TABLE follows (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    follower_id BIGINT NOT NULL,
    following_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_follow (follower_id, following_id),
    FOREIGN KEY (follower_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (following_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_follower_id (follower_id),
    INDEX idx_following_id (following_id)
);
```

---

## 5. PATRONES DE DISEÑO Y ARQUITECTURA

### 5.1 MVC (Model-View-Controller)
- **Model**: Entidades JPA + DTOs para transferencia de datos
- **View**: Templates HTML + JavaScript dinámico
- **Controller**: Controladores REST que manejan las peticiones HTTP

### 5.2 DAO (Data Access Object)
- Spring Data JPA Repository para abstraer la capa de persistencia
- Queries personalizadas cuando sea necesario
- Uso de Specifications para consultas dinámicas

### 5.3 Service Layer
- Encapsulación de lógica de negocio
- Transacciones (@Transactional)
- Manejo de excepciones personalizadas

### 5.4 API RESTful
```
POST   /api/v1/auth/register         - Registrar usuario
POST   /api/v1/auth/login            - Iniciar sesión (JWT)
POST   /api/v1/auth/refresh          - Refrescar token
GET    /api/v1/jokes                 - Listar chistes (paginado)
POST   /api/v1/jokes                 - Crear chiste
GET    /api/v1/jokes/{id}            - Obtener chiste
PUT    /api/v1/jokes/{id}            - Actualizar chiste
DELETE /api/v1/jokes/{id}            - Eliminar chiste
GET    /api/v1/jokes/{id}/comments   - Comentarios del chiste
POST   /api/v1/jokes/{id}/comments   - Agregar comentario
POST   /api/v1/jokes/{id}/vote       - Votar en chiste
GET    /api/v1/users/{id}            - Perfil usuario
GET    /api/v1/users/{id}/jokes      - Chistes del usuario
POST   /api/v1/users/{id}/follow     - Seguir usuario
GET    /api/v1/categories            - Listar categorías
GET    /api/v1/trending              - Chistes trending
GET    /api/v1/search                - Búsqueda full-text
```

### 5.5 Inyección de Dependencias
- Spring Dependency Injection
- Configuración con @Configuration y @Bean
- Autowiring con @Autowired

---

## 6. SEGURIDAD

### 6.1 Autenticación y Autorización
- **JWT (JSON Web Tokens)** para autenticación stateless
- **Spring Security** para control de acceso
- **OAuth2** como opción futura (Google, GitHub login)

### 6.2 Protección de Datos
- **Bcrypt** para hash de contraseñas
- **HTTPS/TLS** obligatorio en producción
- **CORS** configurado correctamente
- **CSRF Protection** para formularios

### 6.3 Rate Limiting
- Limitar requests por IP (prevenir abuse)
- Implementado con filtros personalizados o RateLimiter de Spring

### 6.4 Validación
- Validación en frontend (UX)
- Validación en backend (seguridad)
- HTML5 + JavaScript para frontend
- Bean Validation (@Valid) en backend

---

## 7. CACHING Y OPTIMIZACIÓN

### 7.1 Redis para Caché
```
- Sesiones de usuario (JWT + metadata)
- Trending diario (top 50 chistes)
- Contadores (votos, vistas)
- Resultados de búsqueda frecuentes
- TTL: 1 hora para trending, 24 horas para perfiles
```

### 7.2 Caché en Base de Datos
- Índices en campos frecuentemente consultados
- Índices full-text para búsquedas
- Particionamiento por fecha si es necesario

### 7.3 Optimizaciones Frontend
- Lazy loading de imágenes
- Minificación de CSS/JS
- Compresión Gzip
- Service Workers para modo offline

---

## 8. MANEJO DE TRANSACCIONES

### 8.1 ACID Compliance
```java
@Service
@Transactional
public class JokeService {
    // Operaciones atómicas
    // Rollback automático en excepciones
    // Aislamiento de lecturas sucias
}
```

### 8.2 Bloqueo Optimista
- Versionamiento con @Version
- Prevenir actualizaciones concurrentes
- Útil para contadores de votos

---

## 9. ESCALABILIDAD

### 9.1 Horizontal Scaling
```
Load Balancer (Nginx/HAProxy)
        ↓
┌──────────────────────────┐
│  Backend Pod 1 (Java)    │
├──────────────────────────┤
│  Backend Pod 2 (Java)    │
├──────────────────────────┤
│  Backend Pod 3 (Java)    │
└──────────────────────────┘
        ↓
┌──────────────────────────┐
│  MySQL (Master)          │
│  MySQL (Slave Read)      │
│  MySQL (Slave Read)      │
└──────────────────────────┘
        ↓
┌──────────────────────────┐
│  Redis Cluster           │
└──────────────────────────┘
```

### 9.2 Database Sharding (Futuro)
- Sharding por usuario_id para tabla votes
- Sharding por category_id para tabla jokes
- Geo-distributed si es necesario

### 9.3 Microservicios (Fase 2)
- Separar en: Auth Service, Joke Service, Comment Service, Search Service
- Comunicación con RabbitMQ/Kafka
- Event-Driven Architecture

---

## 10. MONITOREO Y LOGGING

### 10.1 Logging
```
- SLF4J + Logback
- Niveles: DEBUG, INFO, WARN, ERROR
- Logs centralizados en ELK (Elasticsearch, Logstash, Kibana)
- Correlation ID para rastreo de requests
```

### 10.2 Métricas
```
- Micrometer + Prometheus
- Métricas custom: tiempo de respuesta, errores, usuarios activos
- Dashboards en Grafana
```

### 10.3 Alertas
```
- Alert en errores > 5% de requests
- Alert en tiempo respuesta > 500ms
- Alert en CPU > 80%
- Alert en storage MySQL > 80%
```

---

## 11. CONTINUOUS INTEGRATION/DEPLOYMENT

### 11.1 CI/CD Pipeline
```
Git Push
  ↓
Build (Maven)
  ↓
Tests Unitarios (JUnit 5)
  ↓
Tests Integración
  ↓
SonarQube (Code Quality)
  ↓
Build Docker Image
  ↓
Push a Docker Registry
  ↓
Deploy a Staging
  ↓
Smoke Tests
  ↓
Deploy a Producción
```

### 11.2 Docker
```dockerfile
# Backend
FROM openjdk:21-jdk-slim
COPY target/chistesmalos-1.0.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
EXPOSE 8080

# Frontend (nginx)
FROM nginx:alpine
COPY dist/ /usr/share/nginx/html/
COPY nginx.conf /etc/nginx/nginx.conf
EXPOSE 80
```

---

## 12. STACK TECNOLÓGICO RECOMENDADO

| Capa | Tecnología | Versión |
|------|-----------|---------|
| **Backend** | Spring Boot | 3.x LTS |
| | Spring Data JPA | 3.x |
| | Spring Security | 6.x |
| | Maven | 3.9+ |
| **Frontend** | HTML5 | - |
| | CSS3 | - |
| | JavaScript | ES6+ |
| | Axios (HTTP Client) | 1.6+ |
| **Base de Datos** | MySQL | 8.0+ |
| | Redis | 7.x |
| **Testing** | JUnit 5 | 5.x |
| | Mockito | 5.x |
| | Testcontainers | 1.x |
| **DevOps** | Docker | Latest |
| | Docker Compose | Latest |
| | Jenkins/GitHub Actions | - |
| **Monitoreo** | Prometheus | Latest |
| | Grafana | Latest |
| | ELK Stack | 8.x |

---

## 13. FLUJOS PRINCIPALES

### 13.1 Crear Chiste
```
Frontend
  ↓ POST /api/v1/jokes (con JWT)
Backend (Controller)
  ↓ Validación
  ↓ @Transactional JokeService.createJoke()
  ↓ Guardar en MySQL
  ↓ Invalidar caché de trending
  ↓ Guardar evento en queue (futuro)
  ↓ Response: JokeDTO con ID
Frontend
  ↓ Redirigir a detail page
  ↓ Mostrar notificación éxito
```

### 13.2 Votar en Chiste
```
Frontend (POST /api/v1/jokes/{id}/vote)
  ↓
Backend (VoteController)
  ↓ Validar usuario autenticado
  ↓ VoteService.vote(jokeId, userId, voteValue)
  ↓ Buscar voto anterior en MySQL
  ↓ Si existe: actualizar (UPDATE)
  ↓ Si no existe: crear (INSERT)
  ↓ Actualizar counters en tabla jokes
  ↓ Invalidar caché del trending
  ↓ Response: actualizar UI sin reload
```

### 13.3 Obtener Trending
```
Frontend (GET /api/v1/trending?limit=50)
  ↓
Backend (JokeController)
  ↓ Buscar en Redis caché
  ↓ Si no existe:
     ↓ Consultar MySQL (jokes ordenados por upvotes, últimas 24h)
     ↓ Guardar en Redis (TTL: 1 hora)
  ↓ Mapear a DTOs
  ↓ Response: JSON con top 50 chistes
```

---

## 14. CONSIDERACIONES DE RENDIMIENTO

### 14.1 Paginación
```
GET /api/v1/jokes?page=0&size=20&sort=created_at,desc
- Offset-based pagination para simplicidad
- Cursor-based para escalabilidad extrema (futura)
```

### 14.2 N+1 Query Problem
```
- Usar @EntityGraph para eager loading
- Usar @Fetch para lazy loading estratégico
- Proyecciones con DTOs para campos específicos
```

### 14.3 Índices
```sql
- idx_user_id: Para buscar chistes por usuario
- idx_category_id: Para filtrar por categoría
- idx_created_at: Para ordenar por fecha
- idx_upvotes: Para trending
- ftx_content: Full-text search en título y contenido
```

---

## 15. PLAN DE IMPLEMENTACIÓN FASES

### **Fase 1 (Semanas 1-4): MVP**
- [ ] Setup Spring Boot + MySQL
- [ ] Modelos básicos (User, Joke, Comment, Vote)
- [ ] Autenticación JWT
- [ ] CRUD completo de chistes
- [ ] Frontend básico HTML/CSS/JS
- [ ] Votos simples

### **Fase 2 (Semanas 5-8): Mejoras**
- [ ] Comentarios en chistes
- [ ] Sistema de seguimiento (follow)
- [ ] Categorías
- [ ] Búsqueda basic (LIKE en SQL)
- [ ] Perfil de usuario
- [ ] Paginación

### **Fase 3 (Semanas 9-12): Escalabilidad**
- [ ] Redis caché
- [ ] Trending diario
- [ ] Full-text search (Elasticsearch)
- [ ] Rate limiting
- [ ] Compresión frontend
- [ ] Docker + Docker Compose

### **Fase 4 (Semanas 13+): Producción**
- [ ] CI/CD (GitHub Actions)
- [ ] Monitoreo (Prometheus + Grafana)
- [ ] Logging centralizado (ELK)
- [ ] MySQL replication (Master-Slave)
- [ ] Load balancing
- [ ] HTTPS + DNS

---

## 16. REFERENCIAS Y MEJORES PRÁCTICAS

- **Clean Code**: Robert C. Martin
- **Domain Driven Design**: Eric Evans
- **Microservices Patterns**: Chris Richardson
- **12 Factor App**: methodology.12factor.net
- **REST API Best Practices**: restfulapi.net

---

**Documento creado**: 28 de abril de 2026
**Versión**: 1.0
**Status**: Arquitectura Propuesta
