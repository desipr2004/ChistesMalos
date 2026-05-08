# Resumen Ejecutivo - Arquitectura ChistesMalos

## OVERVIEW EJECUTIVO

**Proyecto:** ChistesMalos - Plataforma de compartición de chistes malos  
**Tipo:** Aplicación Web Full-Stack  
**Stack:** Java (Backend) + HTML/CSS/JavaScript (Frontend) + MySQL (Base de Datos)  
**Escala:** 100K+ usuarios, 10K+ posts diarios  
**Timeline Estimado:** 16 semanas

---

## 1. ARQUITECTURA A ALTO NIVEL

```
┌─────────────────────────────────────────────────────────────────┐
│                    CAPA DE PRESENTACIÓN                         │
│            HTML5 + CSS3 + JavaScript Vanilla (ES6+)             │
│  - SPA (Single Page Application) sin frameworks externos        │
│  - Responsive design (Mobile-first)                             │
│  - PWA Ready (Service Workers, Manifest)                        │
└──────────────────────┬──────────────────────────────────────────┘
                       │ HTTP/REST/JSON
┌──────────────────────▼──────────────────────────────────────────┐
│                    CAPA API (REST)                              │
│             Spring Boot 3.x + Spring Security                   │
│  - 15+ Endpoints REST  (GET, POST, PUT, DELETE)                 │
│  - Autenticación JWT  (Stateless)                               │
│  - Rate Limiting + CORS + Validación                            │
│  - Documentación OpenAPI/Swagger                                │
└──────────────────────┬──────────────────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────────────────┐
│                  CAPA DE NEGOCIO                                │
│        Service Layer + Repository Pattern (DAO)                 │
│  - JokeService, UserService, CommentService, VoteService       │
│  - Transacciones ACID (@Transactional)                          │
│  - Validaciones + Excepciones Personalizadas                    │
└──────────────────────┬──────────────────────────────────────────┘
                       │
         ┌─────────────┼─────────────┐
         │             │             │
┌────────▼───┐  ┌──────▼────┐  ┌───▼────────┐
│   MySQL    │  │   Redis   │  │ Elasticsearch
│  (OLTP)    │  │  (Cache)  │  │  (Search)
│            │  │           │  │
│ - Users    │  │ Sessions  │  │ Full-text
│ - Jokes    │  │ Trending  │  │ Indexing
│ - Comments │  │ Counters  │  │
│ - Votes    │  │           │  │
└────────────┘  └───────────┘  └────────────┘
```

---

## 2. COMPONENTES PRINCIPALES

### Backend (Java/Spring Boot)
- **Controllers**: 5 controladores REST
- **Services**: 6 servicios de negocio
- **Repositories**: 4 interfaces JPA Repository
- **Models**: 6 entidades JPA
- **Security**: JWT + BCrypt + Spring Security
- **Caching**: Redis para datos frecuentes
- **Persistencia**: MySQL 8.0+ con índices optimizados

### Frontend (HTML/CSS/JavaScript)
- **Páginas**: Home, Trending, Profile, Create, Details, Search
- **Componentes**: JokeCard, Modal, Navbar, Pagination, Comments
- **Utilidades**: Auth, Validator, Formatter, Storage, Debounce
- **APIs**: Cliente HTTP con Axios + Interceptores
- **Estilos**: CSS3 variables, Responsive, Animaciones suaves

---

## 3. MODELO DE DATOS

**6 Tablas Principales:**

| Tabla | Registros (Inicial) | Crecimiento | Índices |
|-------|-------------------|-------------|---------|
| users | 100K | +10K/mes | 3 (email, username, created_at) |
| jokes | 300K | +10K/día | 5 (user, category, created_at, upvotes, published) |
| comments | 1M | +50K/día | 3 (joke, user, created_at) |
| votes | 2M | +100K/día | 2 (voteable, unique constraint) |
| categories | 10 | - | 1 (slug) |
| follows | 500K | +5K/día | 2 (follower, following) |

**Relaciones principales:**
- User (1) → (N) Jokes
- Joke (1) → (N) Comments
- Joke (1) → (N) Votes
- User (1) → (N) Follows
- User (N) ↔ (N) Users (follows)

---

## 4. FLUJOS DE DATOS PRINCIPALES

### Flujo 1: Publicar Chiste
```
Usuario Frontend
    ↓ (Validación local)
Form Create Joke
    ↓ (POST /api/v1/jokes con JWT)
JokeController
    ↓ (Valida usuario autenticado)
JokeService.createJoke()
    ↓ (Transacción ACID)
JokeRepository.save()
    ↓
MySQL INSERT
    ↓ (Invalida caché trending)
Redis DELETE
    ↓
Response: JokeDTO
    ↓
Frontend: Redirigir a detalles
```

### Flujo 2: Obtener Trending
```
Usuario Frontend
    ↓ (GET /api/v1/trending)
JokeController
    ↓
JokeService.getTrendingJokes()
    ↓ (Busca en Redis con @Cacheable)
Redis HIT → Retorna datos
(Si miss → Consulta MySQL, cachea resultado con TTL 1h)
    ↓
Response: List<JokeDTO>
    ↓
Frontend: Renderiza tarjetas
```

### Flujo 3: Sistema de Votos
```
Usuario Frontend
    ↓ (POST /api/v1/jokes/{id}/vote?value=1)
VoteController
    ↓ (Valida usuario logueado)
VoteService.vote()
    ↓ (Busca voto anterior)
Si existe → UPDATE vote SET value=:value
Si no existe → INSERT voto nuevo
    ↓ (Actualiza counters de jokes)
JokeRepository.save()
    ↓ (Invalida caché trending)
Redis DELETE
    ↓
Response: UpdatedJokeDTO
```

---

## 5. TECNOLOGÍAS SELECCIONADAS

### Backend
```
Spring Boot 3.2 (LTS)
├── Spring Web (REST Controllers)
├── Spring Data JPA (ORM)
├── Spring Security (Autenticación)
├── Spring Cache (Caching)
├── Lombok (Boilerplate reduction)
├── MapStruct (DTO mapping)
├── JUnit 5 (Testing)
└── Mockito (Test mocking)

Java 21 (LTS)
Maven 3.9+
MySQL 8.0+
Redis 7.x
```

### Frontend
```
HTML5
CSS3 (Variables, Grid, Flexbox)
JavaScript ES6+

Librerías externas (CDN):
├── Axios 1.6+ (HTTP Client)
└── Google Fonts (Tipografía)

SPA arquitectura propia (sin frameworks)
```

### DevOps
```
Docker (Containerización)
Docker Compose (Orquestación local)
GitHub Actions (CI/CD)
```

---

## 6. ESCALABILIDAD

### Fase 1 (Semanas 1-4): MVP Monolítico
- Single backend instance
- MySQL con usuario único
- Redis single node
- 1K usuarios activos

### Fase 2 (Semanas 5-8): Crecimiento
- Load balancer (Nginx)
- 3 instancias backend (Java)
- MySQL Master-Slave replication
- Redis Sentinel
- Elasticsearch para búsqueda
- 10K usuarios activos

### Fase 3 (Semanas 9-12): Escalabilidad
- Kubernetes (EKS/AKS/GKE)
- Horizontal autoscaling
- MySQL sharding (usuarios, votos)
- Redis Cluster
- CDN para assets frontend
- 100K+ usuarios activos

### Fase 4 (Semanas 13+): Microservicios
- Auth Service (separado)
- Joke Service
- Comment Service
- Search Service
- Notification Service
- Message Broker (RabbitMQ/Kafka)
- Event-Driven Architecture

---

## 7. SEGURIDAD IMPLEMENTADA

### Autenticación
- ✅ JWT (JSON Web Tokens)
- ✅ BCrypt para hashing de contraseñas
- ✅ Refresh tokens (opcional)
- ✅ Token expiration (24 horas)

### Autorización
- ✅ Spring Security roles
- ✅ Endpoint level @PreAuthorize
- ✅ Ownership validation (solo autor puede editar)

### Protección
- ✅ CORS configurado
- ✅ CSRF protection (Spring Security)
- ✅ SQL Injection prevention (JPA parametrizado)
- ✅ XSS protection (Content-Type headers)
- ✅ Rate limiting por IP
- ✅ Validación en frontend + backend

### Data
- ✅ HTTPS/TLS en producción
- ✅ Database encryption en reposo
- ✅ Password hashing con Bcrypt
- ✅ Sensitive data logging exclusion

---

## 8. MONITOREO Y OBSERVABILIDAD

### Logging
```
SLF4J + Logback
  ├── Logs en archivo (app.log)
  ├── Rotating files diarios
  ├── Stack traces completos
  └── Correlation IDs para requests
```

### Métricas
```
Micrometer + Prometheus
  ├── Latencia de requests
  ├── Tasa de errores
  ├── Usuarios activos
  ├── Throughput (req/sec)
  └── Uso de heap/GC
```

### Alertas
```
Grafana + AlertManager
  ├── Error rate > 5%
  ├── Latencia p95 > 500ms
  ├── CPU > 80%
  ├── Memoria > 80%
  └── Disk > 80%
```

---

## 9. TESTING STRATEGY

### Unitarios (JUnit 5)
- Servicios: 100% coverage
- Validadores: 100% coverage
- Utilidades: 100% coverage
- Target: 80%+ código general

### Integración (TestContainers)
- MySQL en contenedor
- Redis en contenedor
- APIs REST end-to-end
- Transacciones ACID

### E2E (Selenium/Playwright)
- Flujos críticos de usuario
- Registro → Login → Crear chiste
- Voting + Comments
- Búsqueda y trending

---

## 10. PLAN DE IMPLEMENTACIÓN

### **Semana 1-2: Setup + Seguridad**
- [ ] Configurar Spring Boot + Maven
- [ ] Database schema + migrations
- [ ] JWT authentication
- [ ] User registration/login
- [ ] Setup Redis
- [ ] Docker files

### **Semana 3-4: Core MVP**
- [ ] CRUD completo Jokes
- [ ] Votos en chistes
- [ ] Paginación
- [ ] Frontend básico
- [ ] Tests unitarios
- [ ] CI/CD básico

### **Semana 5-6: Mejoras**
- [ ] Comentarios
- [ ] Sistema de follow
- [ ] Categorías
- [ ] Perfil de usuario
- [ ] Frontend mejorado

### **Semana 7-8: Escalabilidad**
- [ ] Caching con Redis
- [ ] Full-text search
- [ ] Rate limiting
- [ ] Load balancing
- [ ] Optimizaciones DB

### **Semana 9-12: Producción**
- [ ] Monitoring completo
- [ ] Disaster recovery
- [ ] Performance testing
- [ ] Security audit
- [ ] Deployment automation

### **Semana 13+: Mejoras**
- [ ] Microservicios
- [ ] Message broker
- [ ] Analytics
- [ ] Recomendaciones
- [ ] Notificaciones

---

## 11. MÉTRICAS DE ÉXITO

| Métrica | Target | Semana |
|---------|--------|--------|
| Usuarios registrados | 1K | 4 |
| DAU (Daily Active Users) | 500 | 8 |
| Posts/día | 500 | 8 |
| Uptime | 99.9% | 12 |
| Latencia p95 | < 200ms | 8 |
| Error rate | < 0.1% | 12 |
| Test coverage | > 80% | 6 |
| Load capacity | 10K req/min | 12 |

---

## 12. COSTOS ESTIMADOS (AWS/Azure)

### Infraestructura
- **Compute**: t3.medium (1 vCPU) × 2 = ~$60/mes (escalable)
- **Database**: RDS MySQL db.t3.small = ~$30/mes
- **Cache**: ElastiCache Redis cache.t3.micro = ~$15/mes
- **Storage**: S3 (assets) = ~$5/mes
- **CDN**: CloudFront = ~$20/mes (10GB)
- **Total Fase 1**: ~$130/mes

### Escalabilidad
- **Kubernetes**: EKS = ~$73/mes + nodos (t3.small) ~$20/mes c/u
- **Load Balancer**: NLB = ~$16/mes
- **Auto Scaling**: Incluido
- **Total Fase 2**: ~$250/mes (3-5 nodos)

---

## 13. DOCUMENTACIÓN ENTREGABLES

### Técnica
- ✅ ARQUITECTURA.md (este documento)
- ✅ GUIA_IMPLEMENTACION.md (código + setup)
- ✅ GUIA_FRONTEND.md (HTML/CSS/JS)
- ✅ API_DOCUMENTATION.md (Swagger/OpenAPI)
- ✅ DATABASE_SCHEMA.md (SQL scripts)
- ✅ DEPLOYMENT_GUIDE.md (Docker + CI/CD)

### Operacional
- ✅ RUNBOOK.md (cómo ejecutar)
- ✅ TROUBLESHOOTING.md (resolver problemas)
- ✅ PERFORMANCE_TUNING.md (optimizaciones)
- ✅ SECURITY_CHECKLIST.md (auditoría)
- ✅ SCALING_GUIDE.md (crecer a 100K+ usuarios)

---

## 14. VENTAJAS DE ESTA ARQUITECTURA

✅ **Escalable**: De 1K a 1M+ usuarios sin cambios críticos  
✅ **Mantenible**: Código limpio, bien estructurado, documentado  
✅ **Seguro**: JWT + BCrypt + validaciones en múltiples capas  
✅ **Performante**: Caching, índices, queries optimizadas  
✅ **Testeable**: 80%+ coverage, tests integración y E2E  
✅ **Flexible**: Fácil agregar features (comentarios, follow, etc)  
✅ **Moderno**: Java 21, Spring 3.x, JavaScript ES6+  
✅ **DevOps-friendly**: Docker, CI/CD ready, monitoreable  

---

## 15. DESAFÍOS Y SOLUCIONES

| Desafío | Solución |
|---------|----------|
| N+1 queries | @EntityGraph, proyecciones DTO |
| Caché inconsistencia | Invalidación selectiva, TTL |
| Transacciones concurrentes | Bloqueo optimista, versionamiento |
| Búsqueda full-text | Elasticsearch, índices MySQL |
| Rate limiting | Filter personalizado, Redis counters |
| Escalabilidad BD | Sharding por usuario_id, read replicas |
| Latencia frontend | Lazy loading, CDN, minificación |
| CORS en producción | Configurar por dominio, HTTPS |

---

## 16. REFERENCIAS

### Arquitectura
- Clean Architecture - Robert C. Martin
- Microservices Patterns - Chris Richardson
- Domain-Driven Design - Eric Evans
- 12 Factor App - https://12factor.net

### Spring Boot
- Spring Documentation - https://spring.io/projects/spring-boot
- Spring Security - https://spring.io/projects/spring-security
- Spring Data JPA - https://spring.io/projects/spring-data-jpa

### Databases
- MySQL 8.0 Official Docs
- Redis Best Practices
- Elasticsearch for Java Apps

### Frontend
- MDN Web Docs
- JavaScript.info
- Web.dev (Google)

### DevOps
- Docker Documentation
- Kubernetes best practices
- CI/CD with GitHub Actions

---

## CONCLUSIÓN

Esta arquitectura proporciona una base **sólida, escalable y moderna** para ChistesMalos.

La estructura separada en capas permite:
- **Desarrollo paralelo**: Frontend y backend pueden trabaje independientemente
- **Testing exhaustivo**: Cada capa se prueba de forma aislada
- **Mantenimiento a largo plazo**: Código limpio y documentado
- **Escalabilidad horizontal**: Agregar más servidores sin cambios arquitectónicos
- **Observabilidad**: Monitoreo completo y alertas proactivas

**Estimación final:** 16-20 semanas para un producto MVP en producción con 100K usuarios soportados.

---

**Documento creado**: 28 de abril de 2026  
**Versión**: 1.0  
**Autor**: Senior Software Architect  
**Status**: Listo para implementación

