<<<<<<< HEAD
# BadJokes Platform

> 🎭 Plataforma web moderna para compartir, votar y comentar chistes malos con arquitectura profesional y mejores prácticas de seguridad.

## 📋 Tabla de Contenidos

- [Características](#características)
- [Stack Tecnológico](#stack-tecnológico)
- [Requisitos Previos](#requisitos-previos)
- [Instalación y Configuración](#instalación-y-configuración)
- [Estructura del Proyecto](#estructura-del-proyecto)
- [API REST](#api-rest)
- [Seguridad](#seguridad)
- [Documentación](#documentación)
- [Despliegue](#despliegue)
- [Contribución](#contribución)

## ✨ Características

### 👥 Gestión de Usuarios
- Registro e inicio de sesión con validación completa
- Contraseñas cifradas con BCrypt
- Rol de usuario (USER, ADMIN, MODERATOR)
- Autenticación JWT con tokens de acceso y refresh
- Perfiles de usuario personalizables

### 🎯 Gestión de Chistes
- Crear, leer, actualizar y eliminar chistes
- Organización por categorías
- Sistema de votación (upvote/downvote)
- Contador de vistas
- Búsqueda de chistes del día y peores chistes
- Publicación programada (borrador/publicado)

### 💬 Sistema de Comentarios
- Comentarios en chistes
- Votación en comentarios
- Respuestas a comentarios
- Eliminación segura (soft delete)

### 🔐 Seguridad
- Protección contra SQL Injection (JPA/Consultas Preparadas)
- Prevención de XSS (Sanitización de inputs)
- Protección CSRF (Spring Security)
- Rate Limiting para prevenir abuso
- Validación de datos en cliente y servidor
- JWT con expiración configurable
- CORS configurado

### 📊 Características Administrativas
- Panel de administración (en desarrollo)
- Moderación de contenido
- Estadísticas del sistema
- Logs de actividad

## 🛠️ Stack Tecnológico

### Backend
- **Java 17** - Lenguaje de programación
- **Spring Boot 3.3.0** - Framework
- **Spring Security** - Autenticación y autorización
- **Spring Data JPA** - ORM y persistencia
- **MySQL 8.0+** - Base de datos relacional
- **Flyway** - Migraciones de base de datos
- **JWT (JJWT)** - Autenticación basada en tokens
- **Bucket4j** - Rate Limiting
- **Lombok** - Reducción de boilerplate
- **MapStruct** - Mapeo de objetos
- **Springdoc OpenAPI** - Documentación Swagger

### Frontend
- **HTML5** - Estructura semántica
- **CSS3** - Estilos responsive
- **JavaScript (ES6+)** - Lógica del cliente (vanilla, sin frameworks)
- **Axios** - Cliente HTTP
- **Font Awesome 6** - Iconografía

### Herramientas
- **Maven 3.9+** - Gestor de dependencias
- **Git** - Control de versiones

## 📦 Requisitos Previos

- Java JDK 17 o superior
- MySQL 8.0 o superior
- Maven 3.9 o superior
- Node.js 16+ (opcional, para desarrollo avanzado)
- Git

## 🚀 Instalación y Configuración

### 1. Clonar el Repositorio

\`\`\`bash
git clone https://github.com/tuusuario/chistes-malos.git
cd chistes-malos
\`\`\`

### 2. Configurar Base de Datos

\`\`\`bash
# Crear base de datos
mysql -u root -p
> CREATE DATABASE chistes_malos_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
> EXIT;
\`\`\`

### 3. Configurar Variables de Entorno

Crear archivo `.env` en la raíz del proyecto:

\`\`\`env
# Base de Datos
DB_URL=jdbc:mysql://localhost:3306/chistes_malos_db
DB_USERNAME=root
DB_PASSWORD=tu_contraseña

# JWT
JWT_SECRET=tu_secreto_jwt_muy_seguro_y_largo_de_al_menos_32_caracteres
JWT_EXPIRATION=86400000
JWT_REFRESH_EXPIRATION=604800000

# Server
SERVER_PORT=8080
API_URL=http://localhost:8080/api
\`\`\`

### 4. Configurar application.yml

Editar `src/main/resources/application.yml`:

\`\`\`yaml
spring:
  datasource:
    url: \${DB_URL:jdbc:mysql://localhost:3306/chistes_malos_db}
    username: \${DB_USERNAME:root}
    password: \${DB_PASSWORD:password}

jwt:
  secret: \${JWT_SECRET:mySecretKeyForChistesMalosApplicationPleaseChangeInProduction1234567890}
  expiration: \${JWT_EXPIRATION:86400000}
  refresh-expiration: \${JWT_REFRESH_EXPIRATION:604800000}
\`\`\`

### 5. Compilar y Ejecutar Backend

\`\`\`bash
# Compilar
mvn clean compile

# Ejecutar pruebas
mvn test

# Construir JAR
mvn clean package

# Ejecutar aplicación
mvn spring-boot:run
# o
java -jar target/chistes-malos-1.0.0.jar
\`\`\`

La aplicación estará disponible en `http://localhost:8080`

### 6. Acceder al Frontend

Abrir en navegador:
- **Sitio Web**: `http://localhost:8080`
- **API Swagger**: `http://localhost:8080/swagger-ui.html`

## 📁 Estructura del Proyecto

\`\`\`
ChistesMalos/
├── src/main/java/com/chistesmalos/
│   ├── config/                 # Configuración (Security, Swagger, etc)
│   ├── controller/             # Controladores REST
│   ├── service/                # Lógica de negocio
│   ├── repository/             # Acceso a datos
│   ├── entity/                 # Entidades JPA
│   ├── dto/                    # Data Transfer Objects
│   ├── exception/              # Excepciones personalizadas
│   ├── security/               # JWT, Autenticación, Autorización
│   └── ChistesMalosApplication.java
├── src/main/resources/
│   ├── application.yml         # Configuración principal
│   └── db/migration/           # Scripts SQL Flyway
├── src/test/                   # Tests unitarios e integración
├── frontend/
│   ├── index.html              # Punto de entrada HTML
│   ├── css/
│   │   ├── variables.css       # Variables de diseño
│   │   ├── styles.css          # Estilos principales
│   │   ├── responsive.css      # Estilos responsivos
│   │   └── animations.css      # Animaciones
│   └── js/
│       ├── api/client.js       # Cliente HTTP con Axios
│       ├── utils/              # Utilidades (auth, validator, etc)
│       ├── components/         # Componentes reutilizables
│       ├── pages/              # Páginas de la aplicación
│       ├── router.js           # Enrutador SPA
│       └── main.js             # Punto de entrada
├── pom.xml                     # Dependencias Maven
└── README.md                   # Este archivo
\`\`\`

## 🔌 API REST

### Autenticación

\`\`\`
POST /auth/register
POST /auth/login
\`\`\`

### Chistes

\`\`\`
GET    /jokes                      # Listar chistes (paginado)
GET    /jokes/{id}                 # Obtener detalle
POST   /jokes                      # Crear chiste
PUT    /jokes/{id}                 # Actualizar
DELETE /jokes/{id}                 # Eliminar
GET    /jokes/category/{categoryId} # Por categoría
GET    /jokes/trending/worst       # Peores chistes
\`\`\`

### Comentarios

\`\`\`
GET    /comments/joke/{jokeId}     # Listar comentarios
POST   /comments                   # Crear comentario
PUT    /comments/{id}              # Actualizar
DELETE /comments/{id}              # Eliminar
\`\`\`

### Votos

\`\`\`
POST   /votes/jokes/{jokeId}       # Votar chiste
POST   /votes/comments/{commentId} # Votar comentario
\`\`\`

### Categorías

\`\`\`
GET    /categories                 # Listar categorías
GET    /categories/{id}            # Obtener detalle
GET    /categories/name/{name}     # Por nombre
\`\`\`

### Usuarios

\`\`\`
GET    /users/{id}                 # Obtener usuario
GET    /users/username/{username}  # Por username
\`\`\`

## 🔐 Seguridad

### Protecciones Implementadas

1. **SQL Injection**
   - JPA con consultas preparadas
   - Validación de entrada

2. **XSS (Cross-Site Scripting)**
   - Sanitización de HTML en backend
   - Escaping en frontend con `textContent`
   - CSP headers

3. **CSRF (Cross-Site Request Forgery)**
   - Spring Security CSRF tokens
   - SameSite cookies

4. **Authentication**
   - JWT con tokens de acceso/refresh
   - BCrypt para contraseñas (10 rounds)

5. **Rate Limiting**
   - Bucket4j: 100 requests/minuto por IP
   - Previene fuerza bruta

6. **Validación**
   - Jakarta Validation (anotaciones)
   - Validación en cliente y servidor

## 📚 Documentación

### Swagger/OpenAPI

Acceder en: `http://localhost:8080/swagger-ui.html`

Todos los endpoints están documentados con:
- Descripción clara
- Parámetros requeridos/opcionales
- Ejemplos de request/response
- Códigos de respuesta HTTP

### Archivos de Documentación

- `ARQUITECTURA.md` - Diseño técnico detallado
- `GUIA_IMPLEMENTACION.md` - Guía paso a paso
- `GUIA_FRONTEND.md` - Documentación del frontend
- `SETUP_DESARROLLO.md` - Configuración local
- `RESUMEN_EJECUTIVO.md` - Resumen para stakeholders

## 🌐 Despliegue

### Docker

\`\`\`dockerfile
FROM openjdk:17-jdk-slim
COPY target/chistes-malos-1.0.0.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
\`\`\`

\`\`\`bash
docker build -t chistes-malos .
docker run -p 8080:8080 -e DB_URL=... chistes-malos
\`\`\`

### Azure App Service (Recomendado)

\`\`\`bash
az webapp create --resource-group myGroup --plan myPlan --name chistes-malos
az webapp up --name chistes-malos --runtime java:17:JAVA17
\`\`\`

### Heroku (Deprecated, pero documentado)

\`\`\`bash
heroku create chistes-malos
git push heroku main
\`\`\`

## 🧪 Testing

\`\`\`bash
# Ejecutar todos los tests
mvn test

# Tests específicos
mvn test -Dtest=JokeServiceTest

# Con cobertura
mvn clean test jacoco:report
\`\`\`

## 📝 Ejemplos de Uso

### Registrarse

\`\`\`bash
curl -X POST http://localhost:8080/api/auth/register \\
  -H "Content-Type: application/json" \\
  -d '{
    "username": "usuario123",
    "email": "usuario@example.com",
    "password": "MiPassword123!",
    "passwordConfirm": "MiPassword123!"
  }'
\`\`\`

### Iniciar Sesión

\`\`\`bash
curl -X POST http://localhost:8080/api/auth/login \\
  -H "Content-Type: application/json" \\
  -d '{
    "usernameOrEmail": "usuario123",
    "password": "MiPassword123!"
  }'
\`\`\`

### Crear Chiste

\`\`\`bash
curl -X POST http://localhost:8080/api/jokes \\
  -H "Content-Type: application/json" \\
  -H "Authorization: Bearer {token}" \\
  -d '{
    "title": "Chiste de Programadores",
    "content": "¿Por qué los programadores prefieren el dark mode?",
    "categoryId": 1,
    "isPublished": true
  }'
\`\`\`

## 🤝 Contribución

1. Fork el repositorio
2. Crear rama feature (`git checkout -b feature/AmazingFeature`)
3. Commit cambios (`git commit -m 'Add AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abrir Pull Request

## 📄 Licencia

Este proyecto está bajo la Licencia Apache 2.0. Ver [LICENSE](LICENSE) para más detalles.

## 👥 Autores

- **BadJokes Team** - Desarrollo principal

## 📧 Contacto

- Email: support@badjokes.com
- Website: https://badjokes.com
- Issues: https://github.com/badjokes/issues

### 5. **DIAGRAMAS.md** 📊 VISUALIZACIÓN DE ARQUITECTURA
**Propósito**: Diagramas ASCII de la arquitectura y flujos de datos  
**Audiencia**: Todos (Arquitectos, Developers, Stakeholders)  
**Contenido**:
- Diagrama de componentes general (completo)
- Flujo de autenticación JWT (detallado)
- Flujo de crear chiste (paso a paso)
- Flujo de votar en chiste (UPDATE detallado)
- Flujo de obtener trending (con caché)
- Modelo físico de relaciones entre tablas
- Escalabilidad horizontal (4 fases)
  - MVP monolítico
  - Escalabilidad manual con load balancer
  - Kubernetes con autoscaling
  - Microservicios con event-driven
- Stack tecnológico visual (capas)
- Diagramas de flujo en ASCII

**Tamaño**: ~35KB | **Tiempo de lectura**: 50 minutos

---

### 6. **RESUMEN_EJECUTIVO.md** 📊 OVERVIEW ESTRATÉGICO
**Propósito**: Resumen de alto nivel para stakeholders y decision makers  
**Audiencia**: CTOs, Product Managers, Executives, Stakeholders  
**Contenido**:
- Overview ejecutivo
- Arquitectura a alto nivel
- Componentes principales
- Modelo de datos resumen
- Flujos de datos principales (3 flujos clave)
- Tecnologías seleccionadas
- Escalabilidad (4 fases detalladas)
- Seguridad implementada
- Monitoreo y observabilidad
- Testing strategy
- Plan de implementación (16 semanas)
- Métricas de éxito
- Costos estimados (AWS/Azure)
- Documentación entregable
- Ventajas de la arquitectura
- Desafíos y soluciones
- Referencias
- Conclusión

**Tamaño**: ~20KB | **Tiempo de lectura**: 40 minutos

---

## 🎯 CÓMO USAR ESTOS DOCUMENTOS

### Para Entender la Arquitectura
1. **Lee primero**: RESUMEN_EJECUTIVO.md (20 min)
2. **Luego**: ARQUITECTURA.md (45 min)
3. **Visualiza**: DIAGRAMAS.md (50 min)
4. **Total**: 2 horas para entender completamente el sistema

### Para Implementar el Backend
1. **Setup**: SETUP_DESARROLLO.md → "4. CONFIGURAR BACKEND" (30 min)
2. **Código**: GUIA_IMPLEMENTACION.md (90 min)
3. **Compilar**: `mvn clean compile`
4. **Ejecutar**: `mvn spring-boot:run`

### Para Implementar el Frontend
1. **Setup**: SETUP_DESARROLLO.md → "5. CONFIGURAR FRONTEND" (20 min)
2. **Código**: GUIA_FRONTEND.md (100 min)
3. **Servir**: `python -m http.server 8000`
4. **Acceder**: http://localhost:8000

### Para Configurar Desarrollo Local Completo
1. **Seguir**: SETUP_DESARROLLO.md (inicio a fin)
2. **Ejecutar**: Docker Compose para bases de datos
3. **Levantar**: Backend + Frontend
4. **Testear**: API con Postman
5. **Usar**: DBeaver para ver BD

### Para Escalar el Sistema
1. **Leer**: ARQUITECTURA.md → "9. ESCALABILIDAD"
2. **Leer**: DIAGRAMAS.md → "8. ESCALABILIDAD - HORIZONTAL SCALING"
3. **Implementar**: Fase por fase según crecimiento

---

## 📊 ESTADÍSTICAS

| Documento | Tamaño | Código | Diagramas | Líneas |
|-----------|--------|--------|-----------|--------|
| ARQUITECTURA.md | 15KB | 50 | 3 | 600+ |
| GUIA_IMPLEMENTACION.md | 40KB | 600+ | 2 | 900+ |
| GUIA_FRONTEND.md | 50KB | 800+ | 1 | 1200+ |
| SETUP_DESARROLLO.md | 30KB | 100+ | 0 | 700+ |
| DIAGRAMAS.md | 35KB | 0 | 10+ | 800+ |
| RESUMEN_EJECUTIVO.md | 20KB | 0 | 1 | 500+ |
| **TOTAL** | **190KB** | **1550+** | **17** | **4700+** |

---

## 🔍 TABLA DE CONTENIDO RÁPIDA

### Backend (Java/Spring Boot)
- Modelos JPA: **GUIA_IMPLEMENTACION.md** (Sección 4)
- Repositorios: **GUIA_IMPLEMENTACION.md** (Sección 5)
- Servicios: **GUIA_IMPLEMENTACION.md** (Sección 6)
- Controladores: **GUIA_IMPLEMENTACION.md** (Sección 7)
- Seguridad: **ARQUITECTURA.md** (Sección 6)
- Configuration: **SETUP_DESARROLLO.md** (Sección 4)

### Frontend (HTML/CSS/JavaScript)
- Estructura HTML: **GUIA_FRONTEND.md** (Sección 2)
- Estilos CSS: **GUIA_FRONTEND.md** (Sección 3)
- Variables CSS: **GUIA_FRONTEND.md** (Sección 3.1)
- JavaScript API: **GUIA_FRONTEND.md** (Sección 4)
- Componentes: **GUIA_FRONTEND.md** (Sección 5)
- Páginas: **GUIA_FRONTEND.md** (Sección 6)
- Router: **GUIA_FRONTEND.md** (Sección 7)

### Base de Datos
- Schema SQL: **GUIA_IMPLEMENTACION.md** (Sección 9.1)
- Modelo Físico: **DIAGRAMAS.md** (Sección 6)
- Índices: **ARQUITECTURA.md** (Sección 4)
- Relaciones: **DIAGRAMAS.md** (Sección 6)

### DevOps & Deployment
- Docker Compose: **SETUP_DESARROLLO.md** (Sección 3)
- CI/CD: **ARQUITECTURA.md** (Sección 11)
- Monitoreo: **ARQUITECTURA.md** (Sección 10)
- Scaling: **DIAGRAMAS.md** (Sección 8)

### Flujos de Datos
- Autenticación: **DIAGRAMAS.md** (Sección 2)
- Crear Chiste: **DIAGRAMAS.md** (Sección 3)
- Votar: **DIAGRAMAS.md** (Sección 4)
- Trending: **DIAGRAMAS.md** (Sección 5)

---

## 🚀 TIMELINE DE IMPLEMENTACIÓN

### Semana 1-2: Setup Inicial
- [ ] Leer ARQUITECTURA.md y RESUMEN_EJECUTIVO.md
- [ ] Seguir SETUP_DESARROLLO.md
- [ ] Levantar Docker Compose (MySQL, Redis)
- [ ] Crear proyecto Maven
- [ ] Implementar modelos JPA (de GUIA_IMPLEMENTACION.md)

### Semana 3-4: MVP Backend
- [ ] Implementar Repositorios
- [ ] Implementar Servicios
- [ ] Implementar Controladores
- [ ] Setup JWT Authentication
- [ ] Tests unitarios (80% coverage)

### Semana 5-6: Frontend Básico
- [ ] Crear estructura HTML (de GUIA_FRONTEND.md)
- [ ] Implementar CSS (variables + estilos)
- [ ] Implementar JavaScript (api, componentes, páginas)
- [ ] Conectar con backend
- [ ] Tests E2E

### Semana 7-8: Escalabilidad
- [ ] Implementar Redis caching
- [ ] Full-text search (Elasticsearch)
- [ ] Rate limiting
- [ ] Load balancer
- [ ] Performance testing

### Semana 9-12: Producción
- [ ] Monitoreo (Prometheus + Grafana)
- [ ] Logging (ELK)
- [ ] CI/CD (GitHub Actions)
- [ ] Docker build & push
- [ ] Security audit

### Semana 13+: Mejoras
- [ ] Microservicios
- [ ] Event-driven architecture
- [ ] Notificaciones
- [ ] Analytics
- [ ] Recomendaciones

---

## 💡 CONSEJOS IMPORTANTES

✅ **DO's:**
- Leer ARQUITECTURA.md antes de codificar
- Seguir el plan de fases de RESUMEN_EJECUTIVO.md
- Usar Docker Compose para desarrollo (SETUP_DESARROLLO.md)
- Tests desde el inicio
- Code review antes de merge
- Documentar cambios arquitectónicos

❌ **DON'Ts:**
- No saltarse security.yml setup
- No usar contraseñas hardcodeadas
- No ignorar índices de BD
- No hacer cambios arquitectónicos sin revisar ARQUITECTURA.md
- No desplegar sin CI/CD
- No olvidar HTTPS en producción

---

## 📞 SOPORTE

Si tienes dudas sobre:
- **Arquitectura general**: Leer ARQUITECTURA.md sección correspondiente
- **Implementación Java**: Leer GUIA_IMPLEMENTACION.md
- **Implementación JavaScript**: Leer GUIA_FRONTEND.md
- **Setup local**: Leer SETUP_DESARROLLO.md
- **Visualizaciones**: Leer DIAGRAMAS.md
- **Decisiones estratégicas**: Leer RESUMEN_EJECUTIVO.md

---

## 📈 MÉTRICAS DE ÉXITO

| Métrica | Target | Documento |
|---------|--------|-----------|
| Test coverage | > 80% | ARQUITECTURA.md |
| Latencia p95 | < 200ms | RESUMEN_EJECUTIVO.md |
| Uptime | 99.9% | ARQUITECTURA.md |
| Usuarios concurrentes | 100K+ | RESUMEN_EJECUTIVO.md |
| Requests/minuto | 10K+ | ARQUITECTURA.md |

---

## 🎓 RECURSOS ADICIONALES

### Libros Recomendados
- "Clean Architecture" - Robert C. Martin
- "Microservices Patterns" - Chris Richardson
- "Domain-Driven Design" - Eric Evans
- "12 Factor App" - https://12factor.net

### Documentación Oficial
- Spring Boot: https://spring.io/projects/spring-boot
- MySQL: https://dev.mysql.com/doc/
- Redis: https://redis.io/docs/
- Docker: https://docs.docker.com/
- Kubernetes: https://kubernetes.io/docs/

### Cursos Online
- Spring Boot Masterclass
- Complete JavaScript Course
- Docker & Kubernetes Complete Guide
- Microservices Architecture

---

## 📝 VERSIONADO

**Versión**: 1.0  
**Fecha**: 28 de abril de 2026  
**Arquitecto**: Senior Software Architect (5+ años)  
**Status**: ✅ LISTO PARA IMPLEMENTACIÓN

### Cambios Futuros
- Versión 1.1: Microservicios (Semana 13)
- Versión 1.2: Kubernetes deployment (Semana 15)
- Versión 2.0: AI-driven recommendations (Semana 20+)

---

## 🙏 CONCLUSIÓN

Esta documentación proporciona **TODO** lo necesario para:
1. **Entender** la arquitectura completa
2. **Implementar** el sistema de forma escalable
3. **Escalar** de 1K a 1M+ usuarios
4. **Mantener** código limpio y profesional
5. **Monitorear** sistema en producción

**¡Estás completamente equipado para empezar!**

---

**Documentación creada por**: GitHub Copilot (Claude Haiku 4.5)  
**Para**: ChistesMalos - Plataforma de chistes malos  
**Todos los derechos reservados** © 2026

=======
# ChistesMalos
>>>>>>> fa849df6807663f1b3305264603d049e37ca6400
