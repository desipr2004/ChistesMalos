# Configuración de Desarrollo Local - ChistesMalos

## 1. REQUISITOS PREVIOS

### Windows 10/11

**Software Requerido:**
- [ ] Git (https://git-scm.com/)
- [ ] Java 21 JDK (https://www.oracle.com/java/technologies/downloads/#java21)
- [ ] Maven 3.9+ (https://maven.apache.org/download.cgi)
- [ ] Docker Desktop (https://www.docker.com/products/docker-desktop)
- [ ] Node.js 18+ (opcional, para usar npm si necesitas dependencias)
- [ ] Visual Studio Code (recomendado)

**VS Code Extensions Recomendadas:**
- Extension Pack for Java
- Spring Boot Extension Pack
- MySQL (wenji1993.mysql)
- REST Client (humao.rest-client)
- Prettier - Code formatter

---

## 2. SETUP INICIAL DEL PROYECTO

### Paso 1: Crear estructura del proyecto

```bash
# Navegar a la carpeta del proyecto
cd c:\Users\desir\OneDrive\Escritorio\VisualStudioCode\ChistesMalos

# Crear carpetas
mkdir chistesmalos-backend
mkdir chistesmalos-frontend
mkdir docker-compose
cd chistesmalos-backend
```

### Paso 2: Crear proyecto Maven

```bash
mvn archetype:generate ^
  -DgroupId=com.chistesmalos ^
  -DartifactId=chistesmalos ^
  -DarchetypeArtifactId=maven-archetype-quickstart ^
  -DinteractiveMode=false
```

O usar Spring Boot CLI:

```bash
spring boot new --from https://start.spring.io chistesmalos
```

### Paso 3: Copiar pom.xml

Reemplazar el contenido del `pom.xml` con la configuración de la GUIA_IMPLEMENTACION.md (sección 1.2).

---

## 3. DOCKER COMPOSE PARA DESARROLLO LOCAL

### Paso 1: Crear docker-compose.yml

```yaml
# Archivo: docker-compose/docker-compose.yml

version: '3.8'

services:
  mysql:
    image: mysql:8.0
    container_name: chistesmalos-mysql
    environment:
      MYSQL_ROOT_PASSWORD: rootpass123
      MYSQL_DATABASE: chistesmalos
      MYSQL_USER: chistesmalos_user
      MYSQL_PASSWORD: chistesmalos_pass123
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
      - ./init.sql:/docker-entrypoint-initdb.d/init.sql
    networks:
      - chistesmalos_network
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      interval: 10s
      timeout: 5s
      retries: 5

  redis:
    image: redis:7-alpine
    container_name: chistesmalos-redis
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data
    networks:
      - chistesmalos_network
    healthcheck:
      test: ["CMD", "redis-cli", "ping"]
      interval: 10s
      timeout: 5s
      retries: 5

  phpmyadmin:
    image: phpmyadmin:latest
    container_name: chistesmalos-phpmyadmin
    environment:
      PMA_HOST: mysql
      PMA_USER: root
      PMA_PASSWORD: rootpass123
    ports:
      - "8081:80"
    depends_on:
      - mysql
    networks:
      - chistesmalos_network

volumes:
  mysql_data:
  redis_data:

networks:
  chistesmalos_network:
    driver: bridge
```

### Paso 2: Script de inicialización SQL

```sql
# Archivo: docker-compose/init.sql

-- Ya está en GUIA_IMPLEMENTACION.md (sección 9.1)
-- Copiar todo el contenido del V1__Initial_Schema.sql
```

### Paso 3: Levantar servicios

```bash
cd docker-compose
docker-compose up -d

# Verificar que todo está en marcha
docker-compose ps

# Ver logs
docker-compose logs -f mysql
docker-compose logs -f redis

# Acceder a PhpMyAdmin: http://localhost:8081
# usuario: root
# contraseña: rootpass123
```

---

## 4. CONFIGURAR BACKEND SPRING BOOT

### Paso 1: Estructura de carpetas

```
chistesmalos/
├── src/
│   ├── main/
│   │   ├── java/com/chistesmalos/
│   │   │   ├── ChistesMalosApplication.java
│   │   │   ├── config/
│   │   │   │   ├── SecurityConfig.java
│   │   │   │   ├── CacheConfig.java
│   │   │   │   └── CorsConfig.java
│   │   │   ├── controller/
│   │   │   ├── service/
│   │   │   ├── repository/
│   │   │   ├── model/
│   │   │   ├── dto/
│   │   │   ├── exception/
│   │   │   ├── security/
│   │   │   └── util/
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-dev.yml
│   │       ├── application-prod.yml
│   │       ├── logback.xml
│   │       └── db/
│   │           └── migration/
│   │               └── V1__Initial_Schema.sql
│   └── test/
│       └── java/com/chistesmalos/
├── target/
├── .gitignore
├── pom.xml
└── README.md
```

### Paso 2: Crear application.yml

```yaml
# Archivo: src/main/resources/application.yml

spring:
  application:
    name: chistesmalos-api
    version: 1.0.0
  
  datasource:
    url: jdbc:mysql://localhost:3306/chistesmalos
    username: chistesmalos_user
    password: chistesmalos_pass123
    driver-class-name: com.mysql.cj.jdbc.Driver
    hikari:
      maximum-pool-size: 10
      minimum-idle: 2
      connection-timeout: 20000
      idle-timeout: 300000
      max-lifetime: 1200000
  
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect
        format_sql: true
        use_sql_comments: true
        jdbc:
          batch_size: 20
          fetch_size: 50
        order_inserts: true
        order_updates: true
  
  redis:
    host: localhost
    port: 6379
    timeout: 2000ms
    database: 0
    jedis:
      pool:
        max-active: 8
        max-idle: 8
        min-idle: 0
        max-wait: -1ms
  
  cache:
    type: redis
    redis:
      time-to-live: 3600000

server:
  port: 8080
  servlet:
    context-path: /api/v1
  compression:
    enabled: true
    mime-types: application/json,application/xml,text/html,text/xml,text/plain
    min-response-size: 1024
  error:
    include-stacktrace: always
    include-message: always

jwt:
  secret: dev-secret-key-change-in-production-min-256-bits-very-secure-key-here
  expiration: 86400000
  refresh-expiration: 604800000

logging:
  level:
    root: INFO
    com.chistesmalos: DEBUG
    org.springframework.web: DEBUG
    org.springframework.security: DEBUG
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
  file:
    name: logs/app.log
    max-size: 10MB
    max-history: 30
    total-size-cap: 1GB

# Actuator para monitoreo
management:
  endpoints:
    web:
      exposure:
        include: health,metrics,prometheus
  endpoint:
    health:
      show-details: always
```

### Paso 3: Crear CacheConfig.java

```java
// Archivo: src/main/java/com/chistesmalos/config/CacheConfig.java

package com.chistesmalos.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.time.Duration;

@Configuration
@EnableCaching
public class CacheConfig {
    
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofHours(1));
        
        return RedisCacheManager.create(connectionFactory);
    }
}
```

### Paso 4: Crear CorsConfig.java

```java
// Archivo: src/main/java/com/chistesmalos/config/CorsConfig.java

package com.chistesmalos.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/v1/**")
            .allowedOrigins("http://localhost:3000", "http://localhost:8000")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true)
            .maxAge(3600);
    }
}
```

### Paso 5: Compilar y ejecutar

```bash
cd chistesmalos-backend

# Compilar
mvn clean compile

# Ejecutar tests
mvn test

# Ejecutar aplicación
mvn spring-boot:run

# La aplicación estará en: http://localhost:8080/api/v1
```

---

## 5. CONFIGURAR FRONTEND

### Paso 1: Crear estructura

```bash
cd ..\chistesmalos-frontend

# Crear archivos básicos
mkdir -p css js/{api,components,pages,utils} assets/images assets/fonts

# Crear archivos HTML/CSS/JS (basados en GUIA_FRONTEND.md)
```

### Paso 2: Crear index.html

Copiar todo el contenido de GUIA_FRONTEND.md (sección 2: INDEX.HTML)

```bash
# Archivo: index.html
```

### Paso 3: Crear estilos CSS

```bash
# Archivo: css/variables.css
# Archivo: css/styles.css
# Archivo: css/responsive.css
# Archivo: css/animations.css
```

Copiar contenido de GUIA_FRONTEND.md (sección 3)

### Paso 4: Crear JavaScript

```bash
# Archivo: js/main.js
# Archivo: js/router.js
# Archivo: js/api/client.js
# ... etc
```

Copiar contenido de GUIA_FRONTEND.md (secciones 4-7)

### Paso 5: Servir localmente

**Opción 1: Python SimpleHTTPServer**
```bash
cd chistesmalos-frontend
python -m http.server 8000

# Acceder a: http://localhost:8000
```

**Opción 2: Node.js http-server**
```bash
npm install -g http-server
http-server . -p 8000

# Acceder a: http://localhost:8000
```

**Opción 3: VS Code Live Server**
- Instalar extensión "Live Server"
- Right-click en index.html → "Open with Live Server"

---

## 6. VERIFICAR CONEXIÓN BACKEND-FRONTEND

### Paso 1: Test de API

```bash
# En PowerShell, probar endpoint

# Registrar usuario
Invoke-WebRequest -Method POST `
  -Uri "http://localhost:8080/api/v1/auth/register" `
  -ContentType "application/json" `
  -Body '{"username":"test","email":"test@test.com","password":"password123"}'

# Respuesta esperada:
# {"token":"eyJhbGciOiJIUzI1NiJ9...","username":"test","email":"test@test.com"}
```

### Paso 2: Test de CORS

```javascript
// En console del navegador (http://localhost:8000)

fetch('http://localhost:8080/api/v1/jokes')
  .then(r => r.json())
  .then(data => console.log(data))
  .catch(e => console.error('CORS error:', e));

// Debe traer lista vacía de chistes: {"content":[],"totalElements":0,...}
```

---

## 7. CONFIGURAR IDE (VS CODE)

### Extensiones necesarias

```bash
# Instalar extensiones recomendadas
code --install-extension vscjava.extension-pack-for-java
code --install-extension vscjava.vscode-spring-boot-dashboard
code --install-extension vscjava.vscode-spring-initializr
code --install-extension mysql.mysql-shell-for-vs-code
code --install-extension humao.rest-client
code --install-extension esbenp.prettier-vscode
code --install-extension ms-azuretools.vscode-docker
```

### Configurar Java en VS Code

1. Abrir `settings.json` (Ctrl+Shift+P → "Preferences: Open Settings (JSON)")
2. Agregar:

```json
{
  "java.jdt.ls.vmargs": "-XX:+UseZGC -XX:ZUncommitDelay=30 -XX:+DisableExplicitGC -Xmx1g",
  "java.project.outputPath": "bin",
  "java.dependency.packagePresentation": "hierarchical",
  "[java]": {
    "editor.defaultFormatter": "esbenp.prettier-vscode",
    "editor.formatOnSave": true,
    "editor.formatOnPaste": true
  }
}
```

### Debugging

1. En VS Code, crear `.vscode/launch.json`:

```json
{
  "version": "0.2.0",
  "configurations": [
    {
      "name": "Spring Boot App",
      "type": "java",
      "name": "Spring Boot App",
      "request": "launch",
      "cwd": "${workspaceFolder}",
      "mainClass": "com.chistesmalos.ChistesMalosApplication",
      "projectName": "chistesmalos",
      "preLaunchTask": "maven: compile",
      "args": ""
    }
  ]
}
```

2. Presionar F5 para iniciar debug

---

## 8. SETUP DE HERRAMIENTAS DE DESARROLLO

### Postman/Insomnia para testing de API

```bash
# Importar colección de API
# Crear requests GET/POST/PUT/DELETE para cada endpoint

# Ejemplo: Crear chiste
POST http://localhost:8080/api/v1/jokes
Authorization: Bearer <token_jwt>
Content-Type: application/json

{
  "title": "¿Por qué no te gusta el JavaScript?",
  "content": "Porque tiene muchas funciones pero ninguna tiene sentido",
  "categoryId": 1
}
```

### DBeaver para gestionar MySQL

```bash
# Descargar: https://dbeaver.io/
# Conectar:
# - Host: localhost
# - Port: 3306
# - Database: chistesmalos
# - User: chistesmalos_user
# - Password: chistesmalos_pass123
```

---

## 9. COMANDOS ÚTILES

### Maven

```bash
# Compilar proyecto
mvn clean compile

# Ejecutar tests
mvn test

# Ejecutar aplicación
mvn spring-boot:run

# Crear JAR
mvn clean package -DskipTests

# Limpiar artifacts
mvn clean

# Ver dependencias
mvn dependency:tree
```

### Docker

```bash
# Levantar servicios
docker-compose -f docker-compose/docker-compose.yml up -d

# Ver logs
docker-compose -f docker-compose/docker-compose.yml logs -f

# Detener servicios
docker-compose -f docker-compose/docker-compose.yml down

# Limpiar volumes
docker-compose -f docker-compose/docker-compose.yml down -v

# Ejecutar comando en contenedor
docker exec chistesmalos-mysql mysql -u root -prootpass123 chistesmalos
```

### Git

```bash
# Inicializar repositorio
git init
git add .
git commit -m "Initial commit: Arquitectura ChistesMalos"

# Crear rama de desarrollo
git checkout -b develop
git push origin develop

# Feature branch
git checkout -b feature/auth-jwt
# ... hacer cambios ...
git commit -am "Implement JWT authentication"
git push origin feature/auth-jwt
# Crear PR en GitHub
```

---

## 10. SOLUCIÓN DE PROBLEMAS

### Puerto 8080 en uso

```bash
# Encontrar proceso usando puerto 8080
netstat -ano | findstr :8080

# Matar proceso
taskkill /PID <PID> /F

# O cambiar puerto en application.yml:
server:
  port: 8081
```

### MySQL no conecta

```bash
# Verificar que contenedor está en marcha
docker ps

# Ver logs de MySQL
docker-compose logs mysql

# Reconectar
docker-compose restart mysql

# Esperar a que sea saludable
docker-compose ps
```

### Redis no funciona

```bash
# Verificar conexión
docker exec chistesmalos-redis redis-cli ping

# Limpiar caché
docker exec chistesmalos-redis redis-cli FLUSHALL
```

### Errores en compilación Java

```bash
# Limpiar caché Maven
mvn clean

# Reinstalar dependencias
rmdir /s %userprofile%\.m2\repository
mvn clean install

# Verificar versión Java
java -version
javac -version
```

---

## 11. CHECKLIST DE SETUP COMPLETO

- [ ] Java 21 instalado: `java -version`
- [ ] Maven 3.9+ instalado: `mvn -v`
- [ ] Docker Desktop corriendo
- [ ] Clonar/descargar código
- [ ] `docker-compose up -d` en carpeta docker-compose
- [ ] Verificar MySQL conecta: PhpMyAdmin en localhost:8081
- [ ] Verificar Redis conecta: `docker exec chistesmalos-redis redis-cli ping`
- [ ] Compilar backend: `mvn clean compile`
- [ ] Ejecutar backend: `mvn spring-boot:run`
- [ ] Backend en http://localhost:8080/api/v1
- [ ] Servir frontend en puerto 8000
- [ ] Frontend en http://localhost:8000
- [ ] Test registro: POST a /auth/register
- [ ] Test login: POST a /auth/login
- [ ] Verificar CORS funciona desde frontend

---

## 12. PRÓXIMOS PASOS RECOMENDADOS

1. **Leer documentación:**
   - ARQUITECTURA.md → Entender diseño general
   - GUIA_IMPLEMENTACION.md → Detalles Java
   - GUIA_FRONTEND.md → Detalles JavaScript

2. **Implementar gradualmente:**
   - Semana 1: Modelos + Repositorios
   - Semana 2: Servicios + Controladores
   - Semana 3: Autenticación JWT
   - Semana 4: Frontend básico

3. **Testing:**
   - Unit tests para servicios
   - Integration tests con TestContainers
   - E2E tests con Selenium

4. **DevOps:**
   - CI/CD con GitHub Actions
   - SonarQube para code quality
   - Prometheus + Grafana para monitoreo

---

**¡Estás listo para comenzar el desarrollo!**

Para preguntas o problemas, revisa los documentos arquitectónicos.

**Creado**: 28 de abril de 2026  
**Versión**: 1.0

