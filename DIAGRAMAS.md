# Diagramas de Arquitectura - ChistesMalos

## 1. DIAGRAMA DE COMPONENTES GENERAL

```
┌─────────────────────────────────────────────────────────────────────────┐
│                        CLIENTE / NAVEGADOR                             │
│                      (HTML5, CSS3, JavaScript)                         │
│  ┌──────────────────────────────────────────────────────────────────┐  │
│  │  - SPA (Single Page Application)                                │  │
│  │  - Responsive Design (Mobile/Tablet/Desktop)                   │  │
│  │  - Service Workers (PWA)                                        │  │
│  │  - Local Storage (JWT, User Info)                              │  │
│  └──────────────────────────────────────────────────────────────────┘  │
└────────────────────────────┬─────────────────────────────────────────────┘
                             │ HTTP/REST JSON
         ┌───────────────────┴───────────────────┐
         │                                       │
    ┌────▼─────────────────────────────────────────┐
    │      API GATEWAY / LOAD BALANCER            │
    │            (Nginx / HAProxy)                │
    │  - Rate Limiting                            │
    │  - SSL/TLS Termination                      │
    │  - Request Logging                          │
    └────┬─────────────────────────────────────────┘
         │
    ┌────▼──────────────────────────────────────────────────────┐
    │         SPRING BOOT APPLICATION SERVERS                  │
    │       (Escalable horizontalmente: 1-N instancias)        │
    │                                                           │
    │  ┌──────────────────────────────────────────────────┐    │
    │  │  Controllers (REST Endpoints)                   │    │
    │  │  ├─ AuthController                             │    │
    │  │  ├─ JokeController                             │    │
    │  │  ├─ UserController                             │    │
    │  │  ├─ CommentController                          │    │
    │  │  └─ VoteController                             │    │
    │  └──────────────────────────────────────────────────┘    │
    │                      ↓                                    │
    │  ┌──────────────────────────────────────────────────┐    │
    │  │  Security Filters                               │    │
    │  │  ├─ JwtAuthenticationFilter                    │    │
    │  │  ├─ RateLimitingFilter                         │    │
    │  │  ├─ CorsFilter                                 │    │
    │  │  └─ ExceptionHandler                           │    │
    │  └──────────────────────────────────────────────────┘    │
    │                      ↓                                    │
    │  ┌──────────────────────────────────────────────────┐    │
    │  │  Service Layer (Business Logic)                 │    │
    │  │  ├─ JokeService                                │    │
    │  │  ├─ UserService                                │    │
    │  │  ├─ CommentService                             │    │
    │  │  ├─ VoteService                                │    │
    │  │  └─ CategoryService                            │    │
    │  └──────────────────────────────────────────────────┘    │
    │                      ↓                                    │
    │  ┌──────────────────────────────────────────────────┐    │
    │  │  Repository Layer (Data Access)                 │    │
    │  │  ├─ UserRepository (JPA)                       │    │
    │  │  ├─ JokeRepository (JPA)                       │    │
    │  │  ├─ CommentRepository (JPA)                    │    │
    │  │  └─ VoteRepository (JPA)                       │    │
    │  └──────────────────────────────────────────────────┘    │
    └────┬──────────────────────────────────────────────────────┘
         │
    ┌────┼──────────────────────────────────────────────────────┐
    │    │         DATA LAYER (PERSISTENCE)                    │
    │    │                                                      │
    │    ├──────────────────────────────────────────────────┐  │
    │    │ MySQL (OLTP - Online Transaction Processing)   │  │
    │    │ ├─ Master (Write)                               │  │
    │    │ ├─ Slave Read #1                                │  │
    │    │ └─ Slave Read #2                                │  │
    │    │                                                  │  │
    │    │ Tables:                                         │  │
    │    │ ├─ users (100K records)                        │  │
    │    │ ├─ jokes (300K records)                        │  │
    │    │ ├─ comments (1M records)                       │  │
    │    │ ├─ votes (2M records)                          │  │
    │    │ ├─ categories (10 records)                     │  │
    │    │ └─ follows (500K records)                      │  │
    │    └──────────────────────────────────────────────────┘  │
    │                                                      │  │
    │    ├──────────────────────────────────────────────────┐  │
    │    │ Redis (In-Memory Cache & Session Store)       │  │
    │    │ ├─ Cached Trending (TTL: 1h)                  │  │
    │    │ ├─ User Sessions                              │  │
    │    │ ├─ Vote Counters                              │  │
    │    │ └─ Rate Limit Counters                        │  │
    │    └──────────────────────────────────────────────────┘  │
    │                                                      │  │
    │    └──────────────────────────────────────────────────┐  │
    │      Elasticsearch (Full-Text Search)              │  │
    │      └─ Indexed: title + content (Jokes)           │  │
    │    └──────────────────────────────────────────────────┘  │
    │                                                      │
    └────────────────────────────────────────────────────────┘
```

---

## 2. FLUJO DE AUTENTICACIÓN (JWT)

```
USUARIO FRONTEND
    │
    ├─ Input: username, password
    │
    ▼
┌─────────────────────────────┐
│ Validar input localmente    │
│ (Validator.validateLogin)   │
└──────────┬──────────────────┘
           │
           ▼
      ┌────────────────────────────────────┐
      │ POST /auth/login                   │
      │ Body: {username, password}         │
      └──────────┬─────────────────────────┘
                 │
    ┌────────────▼─────────────┐
    │  AuthController          │
    │  - Recibe request        │
    │  - Valida no nulo        │
    └──────────┬────────────────┘
               │
    ┌──────────▼──────────────────────────────┐
    │ AuthenticationManager.authenticate()    │
    │ - Busca usuario en BD                  │
    │ - Verifica password con BCrypt         │
    │ - Si falla: throw AuthException        │
    └──────────┬───────────────────────────────┘
               │
    ┌──────────▼──────────────────────────────┐
    │ JwtTokenProvider.generateToken(userId) │
    │ - Crea JWT: header.payload.signature   │
    │ - Expiration: 24 horas                │
    │ - Secret: guardado en config          │
    └──────────┬───────────────────────────────┘
               │
    ┌──────────▼──────────────────────────────┐
    │ Response: {token, username, email}    │
    │ HTTP 200 OK                           │
    └──────────┬───────────────────────────────┘
               │
               ▼
    ┌──────────────────────────┐
    │ Frontend:                │
    │ - Guarda token en       │
    │   localStorage           │
    │ - Redirige a home       │
    └──────────────────────────┘

═════════════════════════════════════════════════════════════════

SIGUIENTES REQUESTS (Autenticados)

    ┌──────────────────────────────────┐
    │ Frontend agrega header           │
    │ Authorization: Bearer <token>    │
    └──────────┬───────────────────────┘
               │
    ┌──────────▼────────────────────────────────┐
    │ JwtAuthenticationFilter                  │
    │ - Extrae token del header               │
    │ - Valida firma (Secret key)            │
    │ - Valida expiración                    │
    │ - Si válido: SecurityContext.auth       │
    │ - Si inválido: throw JwtException       │
    └──────────┬─────────────────────────────────┘
               │
    ┌──────────▼────────────────────────────────┐
    │ Controller recibe request autenticado    │
    │ Authentication.getPrincipal()           │
    │ → userId para operaciones               │
    └─────────────────────────────────────────────┘
```

---

## 3. FLUJO DE CREAR CHISTE (CREAR)

```
USUARIO FRONTEND (Autenticado)
    │
    ├─ Llenar form: título, contenido, categoría
    │
    ▼
┌─────────────────────────────┐
│ Validación LOCAL (JS)       │
│ ├─ Título: min 5 chars     │
│ ├─ Contenido: min 10 chars │
│ ├─ Categoría: seleccionada │
│ └─ Mostrar errores al user │
└──────────┬──────────────────┘
           │ (Si OK)
           ▼
    ┌────────────────────────────────┐
    │ POST /api/v1/jokes             │
    │ Header: Authorization: Bearer  │
    │ Body: JokeDTO {title, content, │
    │                 categoryId}     │
    └──────────┬─────────────────────┘
               │
    ┌──────────▼───────────────────────────────┐
    │ JwtAuthenticationFilter                 │
    │ - Valida JWT                           │
    │ - Establece SecurityContext            │
    └──────────┬────────────────────────────────┘
               │
    ┌──────────▼───────────────────────────────┐
    │ JokeController.createJoke()             │
    │ - Extrae userId de Authentication      │
    │ - Recibe JokeDTO en body              │
    └──────────┬────────────────────────────────┘
               │
    ┌──────────▼───────────────────────────────┐
    │ @Valid Validator                        │
    │ - @NotBlank title                      │
    │ - @Size(min=5, max=200) title         │
    │ - @NotBlank content                   │
    │ - @NotNull categoryId                 │
    │ Si error: HTTP 400 Bad Request        │
    └──────────┬────────────────────────────────┘
               │
    ┌──────────▼────────────────────────────────┐
    │ JokeService.createJoke()                 │
    │ @Transactional                          │
    │ @CacheEvict("trending-jokes")          │
    │                                         │
    │ 1. jokeRepository.findById(userId)     │
    │ 2. categoryRepository.findById(catId)  │
    │ 3. Crear Joke entity:                  │
    │    ├─ user_id = current user           │
    │    ├─ title = input                    │
    │    ├─ content = input                  │
    │    ├─ category_id = input              │
    │    ├─ upvotes = 0                      │
    │    ├─ downvotes = 0                    │
    │    ├─ views = 0                        │
    │    ├─ created_at = NOW()               │
    │    └─ is_published = true              │
    └──────────┬────────────────────────────────┘
               │
    ┌──────────▼────────────────────────────────┐
    │ JokeRepository.save(joke)               │
    │ - Hibernate convierte a INSERT SQL     │
    │ - Ejecuta en transacción ACID          │
    │ - Si éxito: retorna joke con ID       │
    │ - Si error: ROLLBACK automático        │
    └──────────┬────────────────────────────────┘
               │
    ┌──────────▼────────────────────────────────┐
    │ Invalidar caché:                        │
    │ redis.delete("trending-jokes")         │
    │ - Próxima llamada a trending          │
    │   regenerará el caché                  │
    └──────────┬────────────────────────────────┘
               │
    ┌──────────▼────────────────────────────────┐
    │ MapStruct mapea Joke → JokeDTO         │
    │ - id, title, content, category        │
    │ - author.username, author.avatar      │
    │ - upvotes=0, downvotes=0              │
    │ - createdAt = timestamp              │
    └──────────┬────────────────────────────────┘
               │
    ┌──────────▼────────────────────────────────┐
    │ HTTP 201 CREATED                        │
    │ Body: JokeDTO con ID                   │
    │ Location: /api/v1/jokes/{id}          │
    └──────────┬────────────────────────────────┘
               │
               ▼
    ┌──────────────────────────────┐
    │ Frontend:                    │
    │ 1. Mostrar "Chiste creado"  │
    │ 2. Guardar ID del chiste    │
    │ 3. Redirigir a detalles     │
    └──────────────────────────────┘
```

---

## 4. FLUJO DE VOTAR (UPDATE)

```
USUARIO FRONTEND (Autenticado)
    │
    ├─ Click en botón upvote/downvote
    │ en JokeCard
    │
    ▼
┌────────────────────────────────────────┐
│ POST /api/v1/jokes/{jokeId}/vote      │
│ Header: Authorization: Bearer          │
│ Body: {voteValue: 1 or -1}            │
└──────────┬─────────────────────────────┘
           │
    ┌──────▼──────────────────────────────┐
    │ VoteController.voteOnJoke()         │
    │ - jokeId: 123                      │
    │ - userId: from JWT                 │
    │ - voteValue: 1 (upvote)            │
    └──────┬───────────────────────────────┘
           │
    ┌──────▼──────────────────────────────┐
    │ VoteService.vote()                 │
    │ @Transactional                     │
    │ @CacheEvict("trending-jokes")      │
    └──────┬───────────────────────────────┘
           │
    ┌──────▼──────────────────────────────────────────────┐
    │ Buscar voto anterior                               │
    │ voteRepository.findByUserIdAndVoteableIdAndType()   │
    │                                                    │
    │ ├─ Si EXISTE:                                     │
    │ │  └─ Actualizar: vote.voteValue = 1             │
    │ │     (cambiar de -1 a 1)                        │
    │ │     vote.updatedAt = NOW()                     │
    │ │                                                 │
    │ └─ Si NO EXISTE:                                  │
    │    └─ Crear Vote nuevo:                          │
    │       ├─ user_id = current user                  │
    │       ├─ voteable_id = 123                       │
    │       ├─ voteable_type = 'JOKE'                 │
    │       ├─ vote_value = 1                         │
    │       └─ created_at = NOW()                     │
    └──────┬──────────────────────────────────────────────┘
           │
    ┌──────▼──────────────────────────────┐
    │ Guardar/Actualizar voto            │
    │ voteRepository.save(vote)           │
    │ - INSERT o UPDATE en MySQL         │
    │ - UNIQUE constraint válido        │
    └──────┬───────────────────────────────┘
           │
    ┌──────▼──────────────────────────────┐
    │ Actualizar contador de joke        │
    │                                    │
    │ Si anterior era NULL:              │
    │  upvotes: 0 → 1                   │
    │                                    │
    │ Si anterior era -1:                │
    │  downvotes: 1 → 0                 │
    │  upvotes: 0 → 1                   │
    │                                    │
    │ Si anterior era 1:                 │
    │  upvotes: 1 → 0 (remover voto)    │
    │                                    │
    │ jokeRepository.save(joke)          │
    └──────┬───────────────────────────────┘
           │
    ┌──────▼───────────────────────────────┐
    │ Invalidar caché trending            │
    │ redis.delete("trending-jokes")      │
    │ - Se regenerará en próxima llamada │
    └──────┬────────────────────────────────┘
           │
    ┌──────▼────────────────────────────────┐
    │ HTTP 200 OK                          │
    │ Body: Updated JokeDTO               │
    │ {id: 123, upvotes: 5, downvotes: 2}│
    └──────┬─────────────────────────────────┘
           │
           ▼
    ┌────────────────────────────────┐
    │ Frontend:                      │
    │ 1. Actualizar contadores en  │
    │    la UI sin recargar         │
    │ 2. Cambiar color del botón   │
    │ 3. Mostrar animación         │
    └────────────────────────────────┘
```

---

## 5. FLUJO DE OBTENER TRENDING

```
USUARIO FRONTEND (Puede ser anónimo)
    │
    ├─ Click en "Trending"
    │ o carga página /trending
    │
    ▼
┌────────────────────────────────────┐
│ GET /api/v1/trending               │
│ (sin Authorization header)         │
└──────────┬───────────────────────────┘
           │
    ┌──────▼────────────────────────────┐
    │ JokeController.getTrendingJokes()  │
    │ @Cacheable("trending-jokes")      │
    └──────┬─────────────────────────────┘
           │
    ┌──────▼────────────────────────────────────┐
    │ Buscar en Redis                          │
    │ redis.get("trending-jokes")              │
    │                                          │
    │ ├─ CACHE HIT (existe):                  │
    │ │  └─ Retorna lista en caché           │
    │ │     (completamente en RAM)           │
    │ │     ⚡ Latencia: ~5ms                 │
    │ │                                       │
    │ └─ CACHE MISS (no existe):              │
    │    └─ Continuar a BD...                │
    └──────┬────────────────────────────────────┘
           │
    ┌──────▼────────────────────────────────────┐
    │ JokeService.getTrendingJokes()           │
    │ (si miss en caché)                       │
    │                                          │
    │ SELECT * FROM jokes                     │
    │ WHERE is_published=true                 │
    │   AND is_deleted=false               │
    │   AND created_at >= NOW() - INTERVAL   │
    │       '24 HOURS'                        │
    │ ORDER BY upvotes DESC                   │
    │ LIMIT 50                                │
    │                                         │
    │ ⚡ Índices en:                          │
    │ - created_at                           │
    │ - upvotes                              │
    │ - is_published                         │
    └──────┬────────────────────────────────────┘
           │
    ┌──────▼────────────────────────────────────┐
    │ Mapear Joke entities → JokeDTOs         │
    │ Incluir:                                 │
    │ - id, title, content                    │
    │ - author.username, author.avatar        │
    │ - category.name                         │
    │ - upvotes, downvotes, views            │
    │ - createdAt                            │
    └──────┬────────────────────────────────────┘
           │
    ┌──────▼────────────────────────────────────┐
    │ Guardar en Redis                        │
    │ redis.set(                              │
    │   "trending-jokes",                     │
    │   JSON.stringify(list),                 │
    │   Duration.ofHours(1)  // TTL          │
    │ )                                       │
    │ - Próximas 1 hora: caché               │
    │ - Después: se regenera                 │
    └──────┬────────────────────────────────────┘
           │
    ┌──────▼────────────────────────────────────┐
    │ HTTP 200 OK                             │
    │ Body: List<JokeDTO> (max 50)           │
    │ [                                       │
    │   {id:1, title:"...", upvotes:1000},   │
    │   {id:2, title:"...", upvotes:950},    │
    │   ...                                   │
    │ ]                                       │
    └──────┬────────────────────────────────────┘
           │
           ▼
    ┌────────────────────────────────┐
    │ Frontend:                      │
    │ 1. Recibe array de chistes    │
    │ 2. Itera y crea JokeCard por  │
    │    cada elemento               │
    │ 3. Renderiza en página        │
    │ 4. Agrega event listeners     │
    │    para votos                  │
    └────────────────────────────────┘
```

---

## 6. MODELO FÍSICO - RELACIONES TABLAS

```
USERS (100K records)
┌─────────────┐
│ id (PK)     │◄────┐
│ username    │     │
│ email       │     │ Relación: 1 - N
│ password    │     │
│ avatar_url  │     │
│ created_at  │     │
└─────────────┘     │
                    │
      JOKES (300K records)
      ┌──────────────┐
      │ id (PK)      │
      │ user_id (FK)├────┘
      │ title        │
      │ content      │
      │ category_id  │
      │ upvotes      │
      │ downvotes    │
      │ created_at   │
      └──────────────┘
            │
            │ Relación: 1 - N
            │
      COMMENTS (1M records)
      ┌──────────────┐
      │ id (PK)      │
      │ joke_id (FK) ├────┘
      │ user_id (FK) ├──────────┐
      │ content      │          │
      │ created_at   │          │
      └──────────────┘          │
                        USERS◄──┘


      VOTES (2M records)
      ┌──────────────────┐
      │ id (PK)          │
      │ user_id (FK)     ├───────┐
      │ voteable_id      │       │
      │ voteable_type    │   USERS
      │ vote_value (-1,0,1)     │
      │ UNIQUE(user_id,  │       │
      │ voteable_id,type)        │
      └──────────────────┘       │
                            ◄────┘


FOLLOWS (500K records)
┌──────────────────┐
│ follower_id (FK) ├────┐
│ following_id (FK)├─┐  │
└──────────────────┘ │  │
                 USERS◄─┘
         (Self-referencing)
```

---

## 7. ESCALABILIDAD - HORIZONTAL SCALING

```
FASE 1: MVP Monolítico (Semanas 1-4)
┌──────────────────────────────────────────┐
│  1 Java App Instance                     │
│  1 MySQL Instance                        │
│  1 Redis Instance                        │
│  Soporta: 1K usuarios concurrentes       │
└──────────────────────────────────────────┘

═══════════════════════════════════════════════════════════════

FASE 2: Escalabilidad Manual (Semanas 5-8)
                │
                ▼
    ┌───────────────────────┐
    │   Load Balancer       │
    │   (Nginx / HAProxy)   │
    └───────────┬───────────┘
                │
        ┌───────┼───────┐
        │       │       │
        ▼       ▼       ▼
    ┌──────┐ ┌──────┐ ┌──────┐
    │ Java │ │ Java │ │ Java │
    │ App1 │ │ App2 │ │ App3 │
    └──────┘ └──────┘ └──────┘
        │       │       │
        └───────┼───────┘
                │
        ┌───────▼───────┐
        │ MySQL Master  │
        │  (writes)     │
        └───┬───────┬───┘
            │       │
        ┌───▼──┐ ┌──▼───┐
        │Slave │ │Slave │
        │Read1 │ │Read2 │
        └──────┘ └──────┘
        
        ┌──────────────┐
        │Redis Sentinel│
        │High Avail    │
        └──────────────┘

Soporta: 10K usuarios concurrentes

═══════════════════════════════════════════════════════════════

FASE 3: Kubernetes (Semanas 9-12)
                │
                ▼
    ┌──────────────────────┐
    │  Kubernetes Cluster  │
    │  (EKS / AKS / GKE)   │
    └──────────────────────┘
            │
    ┌───────▼────────┐
    │ Ingress        │
    │ (Load Balancing)
    └───────┬────────┘
            │
    ┌───────▼─────────────────┐
    │ Horizontal Pod Autoscaler
    │ (0-100 pods based on CPU)
    └───────┬──────────────────┘
            │
        ┌───▼─────────────────────────┐
        │ Deployment: Spring Boot Pods│
        │ ├─ Pod 1 (Java App)         │
        │ ├─ Pod 2 (Java App)         │
        │ ├─ Pod 3 (Java App)         │
        │ ├─ Pod N...                 │
        │ └─ Rolling updates          │
        └───┬─────────────────────────┘
            │
        ┌───┼──────────────┐
        │   │              │
        ▼   ▼              ▼
    ┌──────────────────────────┐
    │ StatefulSet: MySQL       │
    │ ├─ Master                │
    │ ├─ Slave-1               │
    │ └─ Slave-2               │
    └──────────────────────────┘
    
    ┌──────────────────────────┐
    │ StatefulSet: Redis       │
    │ ├─ Master                │
    │ └─ Sentinel (3 nodes)    │
    └──────────────────────────┘

Soporta: 100K+ usuarios concurrentes

═══════════════════════════════════════════════════════════════

FASE 4: Microservicios (Semanas 13+)
            │
            ▼
    ┌──────────────────────────┐
    │  API Gateway / Kong       │
    │  - Rate Limiting         │
    │  - Request Routing       │
    │  - Logging               │
    └──────────┬───────────────┘
               │
    ┌──────────┼──────────┬────────────┐
    │          │          │            │
    ▼          ▼          ▼            ▼
┌────────┐ ┌─────────┐ ┌──────────┐ ┌──────────┐
│Auth    │ │Joke     │ │Comment   │ │Search    │
│Service │ │Service  │ │Service   │ │Service   │
└────────┘ └─────────┘ └──────────┘ └──────────┘
    │          │          │            │
    └──────────┼──────────┼────────────┘
               │
    ┌──────────▼──────────┐
    │ Message Broker      │
    │ (RabbitMQ/Kafka)    │
    │ - Event Streaming   │
    │ - Async Processing  │
    │ - Service Decoupling│
    └─────────────────────┘

Soporta: 1M+ usuarios concurrentes
```

---

## 8. STACK TECNOLÓGICO VISUAL

```
┌─────────────────────────────────────────────────────────────┐
│                   CAPA PRESENTACIÓN                        │
│  ┌────────────┐  ┌────────────┐  ┌────────────────────┐   │
│  │   HTML5    │  │    CSS3    │  │  JavaScript ES6+   │   │
│  │ Estructura │  │   Estilos  │  │   Interactividad   │   │
│  └────────────┘  └────────────┘  └────────────────────┘   │
│                                                            │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  SPA (Single Page Application)                       │  │
│  │  - Axios para HTTP requests                         │  │
│  │  - localStorage para tokens JWT                     │  │
│  │  - Service Workers (PWA)                           │  │
│  └──────────────────────────────────────────────────────┘  │
└────────────────────┬────────────────────────────────────────┘
                     │
                     │ HTTP/REST/JSON
                     │
┌────────────────────▼────────────────────────────────────────┐
│                   CAPA API                                  │
│  ┌────────────────────────────────────────────────────┐    │
│  │  Spring Boot 3.2 (LTS)                             │    │
│  │  ├─ Spring Web (REST Controllers)                  │    │
│  │  ├─ Spring Security (JWT + BCrypt)                │    │
│  │  ├─ Spring Data JPA (Hibernate ORM)               │    │
│  │  ├─ Spring Data Redis (Caching)                   │    │
│  │  ├─ Lombok (Reducir boilerplate)                 │    │
│  │  ├─ MapStruct (DTO mapping)                       │    │
│  │  ├─ Validation (Bean Validation)                  │    │
│  │  └─ Logging (SLF4J + Logback)                     │    │
│  └────────────────────────────────────────────────────┘    │
│                                                             │
│  ┌────────────────────────────────────────────────────┐    │
│  │  Java 21 (LTS) - Características modernas:         │    │
│  │  ├─ Records (Immutable data carriers)            │    │
│  │  ├─ Text Blocks (Multi-line strings)             │    │
│  │  ├─ Virtual Threads (Lightweight concurrency)    │    │
│  │  └─ Pattern Matching (Switch expressions)        │    │
│  └────────────────────────────────────────────────────┘    │
│                                                             │
│  ┌────────────────────────────────────────────────────┐    │
│  │  Maven 3.9+ (Build Tool)                           │    │
│  │  ├─ Dependency Management                         │    │
│  │  ├─ Plugins (compiler, surefire, shade)          │    │
│  │  └─ Multi-module projects                        │    │
│  └────────────────────────────────────────────────────┘    │
└────────────────────┬────────────────────────────────────────┘
                     │
                     │
        ┌────────────┼────────────┐
        │            │            │
        ▼            ▼            ▼
┌──────────────┐ ┌───────────┐ ┌─────────────┐
│   MySQL 8.0+ │ │ Redis 7.x │ │Elasticsearch
│   (OLTP)     │ │(In-Memory)│ │ (Search)
│              │ │           │ │
│ Relational   │ │ Key-Value │ │ Full-Text
│ Database     │ │ Store     │ │ Indexing
│              │ │           │ │
│ Master-Slave │ │ Sentinel  │ │ Cluster
│ Replication  │ │ (HA)      │ │ Mode
└──────────────┘ └───────────┘ └─────────────┘

═══════════════════════════════════════════════════════════════

DEVOPS & MONITOREO

┌──────────────────────────────────────────────────────────┐
│  Containerización                                        │
│  ├─ Docker (Containerize apps)                         │
│  └─ Docker Compose (Orquestación local)               │
└────────────────────┬─────────────────────────────────────┘
                     │
┌────────────────────▼─────────────────────────────────────┐
│  CI/CD Pipeline                                          │
│  ├─ GitHub Actions                                      │
│  ├─ SonarQube (Code Quality)                           │
│  ├─ JUnit 5 (Unit Testing)                            │
│  └─ TestContainers (Integration Testing)             │
└────────────────────┬─────────────────────────────────────┘
                     │
┌────────────────────▼─────────────────────────────────────┐
│  Monitoreo & Observabilidad                              │
│  ├─ Prometheus (Métricas)                               │
│  ├─ Grafana (Dashboards)                               │
│  ├─ ELK Stack (Logging)                                │
│  └─ Alerts & Notifications                             │
└──────────────────────────────────────────────────────────┘
```

---

**Documentos relacionados:**
- ARQUITECTURA.md (Detalles completos)
- GUIA_IMPLEMENTACION.md (Código fuente)
- GUIA_FRONTEND.md (Interfaz de usuario)
- SETUP_DESARROLLO.md (Configuración local)

**Versión**: 1.0 | **Fecha**: 28 de abril de 2026

