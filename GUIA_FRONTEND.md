# Frontend - Arquitectura HTML, CSS, JavaScript

## 1. ESTRUCTURA DEL PROYECTO FRONTEND

```
chistesmalos-frontend/
├── index.html                 # Página principal
├── profile.html              # Página de perfil
├── create-joke.html          # Crear nuevo chiste
├── details.html              # Detalles del chiste
├── trending.html             # Chistes trending
├── search.html               # Resultados búsqueda
│
├── css/
│   ├── styles.css           # Estilos principales
│   ├── responsive.css       # Media queries (mobile)
│   ├── animations.css       # Transiciones y animaciones
│   └── variables.css        # Variables CSS (colores, fuentes)
│
├── js/
│   ├── main.js              # Punto de entrada principal
│   ├── router.js            # SPA routing sin librería
│   │
│   ├── api/
│   │   ├── client.js        # Interceptor HTTP (Axios)
│   │   ├── jokeApi.js       # Endpoints de chistes
│   │   ├── userApi.js       # Endpoints de usuarios
│   │   └── commentApi.js    # Endpoints de comentarios
│   │
│   ├── components/
│   │   ├── jokeCard.js      # Componente tarjeta chiste
│   │   ├── navbar.js        # Barra de navegación
│   │   ├── modal.js         # Modal genérico
│   │   ├── pagination.js    # Paginación
│   │   ├── commentsList.js  # Lista comentarios
│   │   └── voteButtons.js   # Botones votar
│   │
│   ├── utils/
│   │   ├── auth.js          # Gestión JWT/localStorage
│   │   ├── validator.js     # Validaciones de formularios
│   │   ├── formatter.js     # Formateo de fechas/números
│   │   ├── storage.js       # localStorage wrapper
│   │   └── debounce.js      # Funciones debounce/throttle
│   │
│   └── pages/
│       ├── home.js          # Lógica página home
│       ├── profile.js       # Lógica página perfil
│       ├── createJoke.js    # Lógica crear chiste
│       ├── details.js       # Lógica detalles chiste
│       ├── trending.js      # Lógica trending
│       └── search.js        # Lógica búsqueda
│
├── assets/
│   ├── images/
│   │   ├── logo.svg
│   │   ├── default-avatar.svg
│   │   └── icons/
│   └── fonts/
│       └── fonts.css
│
├── package.json             # Dependencias (si usas bundler)
└── .gitignore
```

---

## 2. INDEX.HTML - ESTRUCTURA BASE

```html
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="ChistesMalos - Comparte tus chistes más malos">
    <meta name="keywords" content="chistes, humor, compartir, comunidad">
    <meta name="author" content="ChistesMalos Team">
    
    <!-- Favicon -->
    <link rel="icon" type="image/svg+xml" href="assets/images/logo.svg">
    
    <!-- Estilos CSS -->
    <link rel="stylesheet" href="css/variables.css">
    <link rel="stylesheet" href="css/styles.css">
    <link rel="stylesheet" href="css/responsive.css">
    <link rel="stylesheet" href="css/animations.css">
    
    <!-- Font: Poppins + Inter -->
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;600;700&family=Inter:wght@400;500;600&display=swap" rel="stylesheet">
    
    <title>ChistesMalos - Comparte tus chistes</title>
</head>
<body>
    <div id="app">
        <!-- Navbar -->
        <nav id="navbar" class="navbar">
            <div class="container">
                <div class="navbar-brand">
                    <a href="/" class="logo">
                        <img src="assets/images/logo.svg" alt="ChistesMalos Logo">
                        <span>ChistesMalos</span>
                    </a>
                </div>
                
                <div class="navbar-menu">
                    <ul class="navbar-nav">
                        <li><a href="/" class="nav-link">Home</a></li>
                        <li><a href="/trending" class="nav-link">Trending</a></li>
                        <li><a href="/categories" class="nav-link">Categorías</a></li>
                    </ul>
                </div>
                
                <div class="navbar-actions">
                    <input type="text" id="searchInput" class="search-box" placeholder="Buscar chistes...">
                    
                    <div id="authButtons" class="auth-buttons">
                        <button id="loginBtn" class="btn btn-outline">Iniciar Sesión</button>
                        <button id="registerBtn" class="btn btn-primary">Registrarse</button>
                    </div>
                    
                    <div id="userMenu" class="user-menu" style="display: none;">
                        <button id="createJokeBtn" class="btn btn-primary">+ Nuevo Chiste</button>
                        <div class="user-dropdown">
                            <img id="userAvatar" class="avatar-small" alt="Avatar">
                            <div class="dropdown-menu">
                                <a href="/profile" class="dropdown-item">Mi Perfil</a>
                                <a href="/my-jokes" class="dropdown-item">Mis Chistes</a>
                                <hr>
                                <button id="logoutBtn" class="dropdown-item">Cerrar Sesión</button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </nav>
        
        <!-- Contenido Principal -->
        <main id="main-content" class="main-content">
            <!-- Las páginas se cargarán aquí dinámicamente -->
        </main>
        
        <!-- Footer -->
        <footer class="footer">
            <div class="container">
                <p>&copy; 2024 ChistesMalos. Todos los derechos reservados.</p>
                <ul class="footer-links">
                    <li><a href="#">Sobre Nosotros</a></li>
                    <li><a href="#">Privacidad</a></li>
                    <li><a href="#">Términos</a></li>
                    <li><a href="#">Contacto</a></li>
                </ul>
            </div>
        </footer>
    </div>
    
    <!-- Modales -->
    <div id="modalContainer"></div>
    
    <!-- Scripts -->
    <script src="https://cdn.jsdelivr.net/npm/axios/dist/axios.min.js"></script>
    <script src="js/utils/storage.js"></script>
    <script src="js/utils/auth.js"></script>
    <script src="js/utils/validator.js"></script>
    <script src="js/utils/formatter.js"></script>
    <script src="js/utils/debounce.js"></script>
    <script src="js/api/client.js"></script>
    <script src="js/api/jokeApi.js"></script>
    <script src="js/api/userApi.js"></script>
    <script src="js/api/commentApi.js"></script>
    <script src="js/components/navbar.js"></script>
    <script src="js/components/jokeCard.js"></script>
    <script src="js/components/modal.js"></script>
    <script src="js/components/pagination.js"></script>
    <script src="js/components/commentsList.js"></script>
    <script src="js/components/voteButtons.js"></script>
    <script src="js/pages/home.js"></script>
    <script src="js/pages/trending.js"></script>
    <script src="js/pages/details.js"></script>
    <script src="js/pages/profile.js"></script>
    <script src="js/pages/createJoke.js"></script>
    <script src="js/pages/search.js"></script>
    <script src="js/router.js"></script>
    <script src="js/main.js"></script>
</body>
</html>
```

---

## 3. CSS - ESTILOS PRINCIPALES

### 3.1 variables.css

```css
:root {
    /* Colores */
    --primary: #FF6B6B;
    --primary-dark: #E63946;
    --primary-light: #FFB3B3;
    
    --secondary: #4ECDC4;
    --secondary-dark: #45B7AA;
    --secondary-light: #A7E9E6;
    
    --accent: #FFE66D;
    --accent-dark: #F7D947;
    
    --neutral-900: #1A1A1A;
    --neutral-800: #2D2D2D;
    --neutral-700: #404040;
    --neutral-600: #606060;
    --neutral-500: #808080;
    --neutral-400: #A0A0A0;
    --neutral-300: #D0D0D0;
    --neutral-200: #E8E8E8;
    --neutral-100: #F5F5F5;
    --neutral-50: #FAFAFA;
    
    --success: #06D6A0;
    --warning: #FFB703;
    --danger: #E63946;
    
    --bg-primary: #FFFFFF;
    --bg-secondary: #F5F5F5;
    --text-primary: #1A1A1A;
    --text-secondary: #606060;
    
    /* Tipografía */
    --font-primary: 'Poppins', sans-serif;
    --font-secondary: 'Inter', sans-serif;
    --font-size-xs: 0.75rem;
    --font-size-sm: 0.875rem;
    --font-size-base: 1rem;
    --font-size-lg: 1.125rem;
    --font-size-xl: 1.25rem;
    --font-size-2xl: 1.5rem;
    --font-size-3xl: 1.875rem;
    --font-size-4xl: 2.25rem;
    
    /* Espaciado */
    --spacing-xs: 0.25rem;
    --spacing-sm: 0.5rem;
    --spacing-md: 1rem;
    --spacing-lg: 1.5rem;
    --spacing-xl: 2rem;
    --spacing-2xl: 3rem;
    
    /* Bordes */
    --border-radius-sm: 0.25rem;
    --border-radius-md: 0.5rem;
    --border-radius-lg: 1rem;
    --border-radius-full: 9999px;
    
    /* Sombras */
    --shadow-sm: 0 1px 2px rgba(0, 0, 0, 0.05);
    --shadow-md: 0 4px 6px rgba(0, 0, 0, 0.1);
    --shadow-lg: 0 10px 15px rgba(0, 0, 0, 0.1);
    --shadow-xl: 0 20px 25px rgba(0, 0, 0, 0.15);
    
    /* Transiciones */
    --transition-fast: 150ms ease-in-out;
    --transition-base: 250ms ease-in-out;
    --transition-slow: 350ms ease-in-out;
    
    /* Z-index */
    --z-dropdown: 1000;
    --z-sticky: 1020;
    --z-fixed: 1030;
    --z-modal: 1040;
    --z-tooltip: 1050;
}

/* Dark mode (opcional) */
@media (prefers-color-scheme: dark) {
    :root {
        --bg-primary: #1A1A1A;
        --bg-secondary: #2D2D2D;
        --text-primary: #FFFFFF;
        --text-secondary: #B0B0B0;
    }
}
```

### 3.2 styles.css (Extracto)

```css
/* Reset y base */
*,
*::before,
*::after {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
}

html {
    scroll-behavior: smooth;
    font-size: 16px;
}

body {
    font-family: var(--font-secondary);
    color: var(--text-primary);
    background-color: var(--bg-primary);
    line-height: 1.6;
}

/* Contenedor */
.container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 var(--spacing-md);
}

/* Navbar */
.navbar {
    position: sticky;
    top: 0;
    background: white;
    border-bottom: 1px solid var(--neutral-200);
    z-index: var(--z-sticky);
    box-shadow: var(--shadow-sm);
}

.navbar .container {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: var(--spacing-lg);
    padding: var(--spacing-md);
}

.navbar-brand {
    flex-shrink: 0;
}

.logo {
    display: flex;
    align-items: center;
    gap: var(--spacing-sm);
    font-weight: 700;
    color: var(--primary);
    text-decoration: none;
    font-size: var(--font-size-xl);
}

.logo img {
    height: 32px;
    width: auto;
}

.navbar-menu {
    flex: 1;
}

.navbar-nav {
    display: flex;
    list-style: none;
    gap: var(--spacing-lg);
}

.nav-link {
    color: var(--text-primary);
    text-decoration: none;
    font-weight: 500;
    transition: color var(--transition-fast);
}

.nav-link:hover {
    color: var(--primary);
}

.navbar-actions {
    display: flex;
    align-items: center;
    gap: var(--spacing-md);
}

.search-box {
    padding: var(--spacing-sm) var(--spacing-md);
    border: 1px solid var(--neutral-300);
    border-radius: var(--border-radius-full);
    font-size: var(--font-size-sm);
    min-width: 200px;
}

.search-box:focus {
    outline: none;
    border-color: var(--primary);
    box-shadow: 0 0 0 3px rgba(255, 107, 107, 0.1);
}

/* Botones */
.btn {
    padding: var(--spacing-sm) var(--spacing-lg);
    border: none;
    border-radius: var(--border-radius-md);
    font-weight: 600;
    cursor: pointer;
    transition: all var(--transition-base);
    font-size: var(--font-size-base);
    display: inline-flex;
    align-items: center;
    gap: var(--spacing-sm);
}

.btn-primary {
    background-color: var(--primary);
    color: white;
}

.btn-primary:hover {
    background-color: var(--primary-dark);
    transform: translateY(-2px);
    box-shadow: var(--shadow-md);
}

.btn-outline {
    border: 2px solid var(--primary);
    color: var(--primary);
    background-color: transparent;
}

.btn-outline:hover {
    background-color: var(--primary);
    color: white;
}

/* Tarjeta de chiste */
.joke-card {
    background: white;
    border: 1px solid var(--neutral-200);
    border-radius: var(--border-radius-lg);
    padding: var(--spacing-lg);
    margin-bottom: var(--spacing-lg);
    transition: all var(--transition-base);
    cursor: pointer;
}

.joke-card:hover {
    box-shadow: var(--shadow-md);
    transform: translateY(-4px);
}

.joke-header {
    display: flex;
    align-items: center;
    gap: var(--spacing-md);
    margin-bottom: var(--spacing-md);
}

.avatar {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    background: var(--neutral-300);
}

.joke-meta {
    display: flex;
    flex-direction: column;
    gap: var(--spacing-xs);
}

.author-name {
    font-weight: 600;
    color: var(--text-primary);
}

.created-at {
    font-size: var(--font-size-sm);
    color: var(--text-secondary);
}

.joke-title {
    font-size: var(--font-size-lg);
    font-weight: 700;
    margin-bottom: var(--spacing-md);
    color: var(--text-primary);
}

.joke-content {
    font-size: var(--font-size-base);
    color: var(--text-primary);
    margin-bottom: var(--spacing-lg);
    line-height: 1.8;
}

.joke-category {
    display: inline-block;
    background: var(--secondary-light);
    color: var(--secondary-dark);
    padding: var(--spacing-xs) var(--spacing-md);
    border-radius: var(--border-radius-full);
    font-size: var(--font-size-sm);
    font-weight: 600;
    margin-bottom: var(--spacing-md);
}

.joke-footer {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding-top: var(--spacing-md);
    border-top: 1px solid var(--neutral-200);
}

.joke-stats {
    display: flex;
    gap: var(--spacing-xl);
    font-size: var(--font-size-sm);
    color: var(--text-secondary);
}

.stat {
    display: flex;
    align-items: center;
    gap: var(--spacing-xs);
}

.vote-buttons {
    display: flex;
    gap: var(--spacing-sm);
}

.vote-btn {
    padding: var(--spacing-xs) var(--spacing-sm);
    background: transparent;
    border: 1px solid var(--neutral-300);
    border-radius: var(--border-radius-md);
    cursor: pointer;
    transition: all var(--transition-fast);
    font-size: var(--font-size-lg);
}

.vote-btn:hover {
    border-color: var(--primary);
    color: var(--primary);
}

.vote-btn.active {
    background: var(--primary);
    border-color: var(--primary);
    color: white;
}

/* Main content */
.main-content {
    min-height: calc(100vh - 200px);
    padding: var(--spacing-2xl) var(--spacing-md);
}

/* Footer */
.footer {
    background: var(--neutral-900);
    color: white;
    padding: var(--spacing-2xl) var(--spacing-md);
    text-align: center;
    margin-top: var(--spacing-2xl);
}

.footer-links {
    display: flex;
    justify-content: center;
    list-style: none;
    gap: var(--spacing-lg);
    margin-top: var(--spacing-lg);
}

.footer-links a {
    color: white;
    text-decoration: none;
    transition: color var(--transition-fast);
}

.footer-links a:hover {
    color: var(--primary);
}

/* Paginación */
.pagination {
    display: flex;
    justify-content: center;
    gap: var(--spacing-sm);
    margin: var(--spacing-2xl) 0;
}

.pagination-item {
    padding: var(--spacing-sm) var(--spacing-md);
    border: 1px solid var(--neutral-300);
    border-radius: var(--border-radius-md);
    background: white;
    cursor: pointer;
    transition: all var(--transition-base);
}

.pagination-item:hover {
    border-color: var(--primary);
    color: var(--primary);
}

.pagination-item.active {
    background: var(--primary);
    color: white;
    border-color: var(--primary);
}

/* Modal */
.modal-overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(0, 0, 0, 0.5);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: var(--z-modal);
}

.modal {
    background: white;
    border-radius: var(--border-radius-lg);
    padding: var(--spacing-2xl);
    max-width: 500px;
    width: 90%;
    box-shadow: var(--shadow-xl);
}

.modal-header {
    font-size: var(--font-size-xl);
    font-weight: 700;
    margin-bottom: var(--spacing-lg);
}

.modal-close {
    float: right;
    font-size: var(--font-size-xl);
    cursor: pointer;
    background: none;
    border: none;
    color: var(--text-secondary);
}

.modal-close:hover {
    color: var(--text-primary);
}
```

### 3.3 responsive.css

```css
/* Tablet (768px+) */
@media (max-width: 768px) {
    .navbar .container {
        flex-wrap: wrap;
    }
    
    .navbar-menu {
        display: none;
    }
    
    .search-box {
        min-width: 150px;
    }
    
    .joke-footer {
        flex-direction: column;
        align-items: flex-start;
        gap: var(--spacing-md);
    }
    
    .modal {
        width: 95%;
    }
}

/* Mobile (480px+) */
@media (max-width: 480px) {
    :root {
        --spacing-md: 0.75rem;
        --spacing-lg: 1rem;
        --font-size-2xl: 1.25rem;
    }
    
    .navbar-actions {
        flex-wrap: wrap;
        width: 100%;
    }
    
    .search-box {
        width: 100%;
        min-width: unset;
    }
    
    .joke-card {
        padding: var(--spacing-md);
    }
    
    .joke-stats {
        flex-wrap: wrap;
        gap: var(--spacing-md);
    }
    
    .btn {
        padding: var(--spacing-xs) var(--spacing-md);
        font-size: var(--font-size-sm);
    }
}
```

### 3.4 animations.css

```css
@keyframes fadeIn {
    from {
        opacity: 0;
    }
    to {
        opacity: 1;
    }
}

@keyframes slideInUp {
    from {
        opacity: 0;
        transform: translateY(20px);
    }
    to {
        opacity: 1;
        transform: translateY(0);
    }
}

@keyframes pulse {
    0%, 100% {
        opacity: 1;
    }
    50% {
        opacity: 0.5;
    }
}

.fade-in {
    animation: fadeIn var(--transition-base);
}

.slide-in-up {
    animation: slideInUp var(--transition-base);
}

.loading {
    animation: pulse 2s infinite;
}

/* Transición de página */
.page-enter {
    animation: slideInUp var(--transition-base);
}

.page-exit {
    animation: fadeIn var(--transition-fast) reverse;
}
```

---

## 4. JAVASCRIPT - UTILIDADES

### 4.1 api/client.js - Interceptor Axios

```javascript
// Configurar cliente HTTP base
const API_BASE_URL = 'http://localhost:8080/api/v1';

const axiosClient = axios.create({
    baseURL: API_BASE_URL,
    timeout: 10000,
    headers: {
        'Content-Type': 'application/json'
    }
});

// Interceptor de request para agregar JWT
axiosClient.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('auth_token');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => Promise.reject(error)
);

// Interceptor de response para manejo de errores
axiosClient.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response?.status === 401) {
            // Token expirado, limpiar y redirigir a login
            localStorage.removeItem('auth_token');
            window.location.href = '/login';
        }
        return Promise.reject(error);
    }
);

// Exportar cliente
window.apiClient = axiosClient;
```

### 4.2 utils/auth.js - Gestión de autenticación

```javascript
const AuthService = {
    // Guardar token
    setToken: (token) => {
        localStorage.setItem('auth_token', token);
    },
    
    // Obtener token
    getToken: () => {
        return localStorage.getItem('auth_token');
    },
    
    // Verificar si está autenticado
    isAuthenticated: () => {
        return !!localStorage.getItem('auth_token');
    },
    
    // Limpiar autenticación
    logout: () => {
        localStorage.removeItem('auth_token');
        localStorage.removeItem('user_info');
    },
    
    // Guardar información del usuario
    setUser: (user) => {
        localStorage.setItem('user_info', JSON.stringify(user));
    },
    
    // Obtener información del usuario
    getUser: () => {
        const user = localStorage.getItem('user_info');
        return user ? JSON.parse(user) : null;
    },
    
    // Registrar usuario
    register: async (username, email, password) => {
        try {
            const response = await apiClient.post('/auth/register', {
                username,
                email,
                password
            });
            AuthService.setToken(response.data.token);
            AuthService.setUser({
                username: response.data.username,
                email: response.data.email
            });
            return response.data;
        } catch (error) {
            throw error.response?.data?.message || 'Error en registro';
        }
    },
    
    // Iniciar sesión
    login: async (username, password) => {
        try {
            const response = await apiClient.post('/auth/login', {
                username,
                password
            });
            AuthService.setToken(response.data.token);
            AuthService.setUser({
                username: response.data.username,
                email: response.data.email
            });
            return response.data;
        } catch (error) {
            throw error.response?.data?.message || 'Error en login';
        }
    }
};

window.AuthService = AuthService;
```

### 4.3 utils/validator.js - Validaciones

```javascript
const Validator = {
    // Email
    isValidEmail: (email) => {
        const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return regex.test(email);
    },
    
    // Username (3-50 caracteres, sin espacios)
    isValidUsername: (username) => {
        return username.length >= 3 && username.length <= 50 && !/\s/.test(username);
    },
    
    // Password (min 8 caracteres)
    isValidPassword: (password) => {
        return password.length >= 8;
    },
    
    // Validar formulario de login
    validateLoginForm: (username, password) => {
        const errors = {};
        if (!username) errors.username = 'Username requerido';
        if (!password) errors.password = 'Password requerido';
        return errors;
    },
    
    // Validar formulario de registro
    validateRegisterForm: (username, email, password, confirmPassword) => {
        const errors = {};
        
        if (!Validator.isValidUsername(username)) {
            errors.username = 'Username debe tener 3-50 caracteres';
        }
        if (!Validator.isValidEmail(email)) {
            errors.email = 'Email inválido';
        }
        if (!Validator.isValidPassword(password)) {
            errors.password = 'Password debe tener al menos 8 caracteres';
        }
        if (password !== confirmPassword) {
            errors.confirmPassword = 'Las contraseñas no coinciden';
        }
        
        return errors;
    },
    
    // Validar chiste
    validateJoke: (title, content) => {
        const errors = {};
        if (!title || title.length < 5) {
            errors.title = 'Título debe tener al menos 5 caracteres';
        }
        if (!content || content.length < 10) {
            errors.content = 'El chiste debe tener al menos 10 caracteres';
        }
        return errors;
    }
};

window.Validator = Validator;
```

### 4.4 utils/formatter.js - Formateo

```javascript
const Formatter = {
    // Formatear fecha relativa (ej: "hace 2 horas")
    formatRelativeDate: (dateString) => {
        const date = new Date(dateString);
        const now = new Date();
        const seconds = Math.floor((now - date) / 1000);
        
        if (seconds < 60) return 'hace unos segundos';
        if (seconds < 3600) return `hace ${Math.floor(seconds / 60)} minutos`;
        if (seconds < 86400) return `hace ${Math.floor(seconds / 3600)} horas`;
        if (seconds < 604800) return `hace ${Math.floor(seconds / 86400)} días`;
        
        return date.toLocaleDateString('es-ES');
    },
    
    // Formatear número (ej: 1000 -> 1K)
    formatNumber: (num) => {
        if (num >= 1000000) return (num / 1000000).toFixed(1) + 'M';
        if (num >= 1000) return (num / 1000).toFixed(1) + 'K';
        return num.toString();
    },
    
    // Truncar texto
    truncateText: (text, length = 100) => {
        return text.length > length ? text.substring(0, length) + '...' : text;
    }
};

window.Formatter = Formatter;
```

---

## 5. COMPONENTES

### 5.1 components/jokeCard.js

```javascript
const JokeCardComponent = {
    // Crear tarjeta de chiste
    create: (joke) => {
        const div = document.createElement('div');
        div.className = 'joke-card slide-in-up';
        div.innerHTML = `
            <div class="joke-header">
                <img src="${joke.authorAvatar || 'assets/images/default-avatar.svg'}" 
                     class="avatar" alt="${joke.authorUsername}">
                <div class="joke-meta">
                    <a href="/profile/${joke.authorUsername}" class="author-name">
                        ${joke.authorUsername}
                    </a>
                    <time class="created-at">${Formatter.formatRelativeDate(joke.createdAt)}</time>
                </div>
            </div>
            
            <a href="/joke/${joke.id}" class="joke-link" style="text-decoration: none; color: inherit;">
                <span class="joke-category">${joke.category}</span>
                <h3 class="joke-title">${joke.title}</h3>
                <p class="joke-content">${Formatter.truncateText(joke.content, 200)}</p>
            </a>
            
            <div class="joke-footer">
                <div class="joke-stats">
                    <div class="stat">
                        👍 <span class="upvotes">${Formatter.formatNumber(joke.upvotes)}</span>
                    </div>
                    <div class="stat">
                        👎 <span class="downvotes">${Formatter.formatNumber(joke.downvotes)}</span>
                    </div>
                    <div class="stat">
                        💬 <span class="comments">${Formatter.formatNumber(joke.commentCount)}</span>
                    </div>
                    <div class="stat">
                        👁️ <span class="views">${Formatter.formatNumber(joke.views)}</span>
                    </div>
                </div>
                <div class="vote-buttons">
                    <button class="vote-btn upvote-btn" data-joke-id="${joke.id}">👍</button>
                    <button class="vote-btn downvote-btn" data-joke-id="${joke.id}">👎</button>
                </div>
            </div>
        `;
        
        return div;
    }
};

window.JokeCardComponent = JokeCardComponent;
```

### 5.2 components/modal.js

```javascript
const Modal = {
    show: (title, content, buttons = []) => {
        const overlay = document.createElement('div');
        overlay.className = 'modal-overlay';
        overlay.id = 'modalOverlay';
        
        const modal = document.createElement('div');
        modal.className = 'modal';
        
        let buttonsHTML = '';
        if (buttons.length > 0) {
            buttonsHTML = buttons.map(btn => 
                `<button class="btn ${btn.class}" data-action="${btn.action}">${btn.text}</button>`
            ).join('');
        }
        
        modal.innerHTML = `
            <button class="modal-close" id="modalClose">&times;</button>
            <h2 class="modal-header">${title}</h2>
            <div class="modal-body">${content}</div>
            ${buttonsHTML ? `<div class="modal-footer" style="margin-top: 20px; display: flex; gap: 10px;">${buttonsHTML}</div>` : ''}
        `;
        
        overlay.appendChild(modal);
        document.getElementById('modalContainer').appendChild(overlay);
        
        // Cerrar modal
        document.getElementById('modalClose').addEventListener('click', () => {
            overlay.remove();
        });
        
        overlay.addEventListener('click', (e) => {
            if (e.target === overlay) overlay.remove();
        });
        
        // Eventos de botones
        modal.querySelectorAll('[data-action]').forEach(btn => {
            btn.addEventListener('click', (e) => {
                const action = btn.dataset.action;
                modal.dispatchEvent(new CustomEvent('action', { detail: { action } }));
            });
        });
        
        return modal;
    }
};

window.Modal = Modal;
```

---

## 6. PÁGINAS - Lógica Principal

### 6.1 pages/home.js

```javascript
const HomePage = {
    init: async () => {
        const mainContent = document.getElementById('main-content');
        mainContent.innerHTML = `
            <div class="container">
                <section class="hero" style="text-align: center; margin-bottom: 50px;">
                    <h1 style="font-size: 3rem; margin-bottom: 10px;">Bienvenido a ChistesMalos</h1>
                    <p style="font-size: 1.25rem; color: #606060;">Comparte los chistes más malos y pásala bien</p>
                </section>
                
                <section class="filter-bar" style="margin-bottom: 30px; display: flex; gap: 10px;">
                    <select id="categoryFilter" class="search-box" style="min-width: auto;">
                        <option value="">Todas las categorías</option>
                    </select>
                    <select id="sortFilter" class="search-box" style="min-width: auto;">
                        <option value="recent">Más recientes</option>
                        <option value="trending">Trending</option>
                        <option value="mostVoted">Más votados</option>
                    </select>
                </section>
                
                <div id="jokesList" class="jokes-list"></div>
                <div id="pagination" class="pagination"></div>
                <div id="loading" class="loading" style="text-align: center; display: none;">
                    Cargando chistes...
                </div>
            </div>
        `;
        
        let currentPage = 0;
        const pageSize = 10;
        
        const loadJokes = async (page = 0) => {
            try {
                document.getElementById('loading').style.display = 'block';
                
                const response = await apiClient.get('/jokes', {
                    params: { page, size: pageSize }
                });
                
                const jokesList = document.getElementById('jokesList');
                jokesList.innerHTML = '';
                
                response.data.content.forEach(joke => {
                    jokesList.appendChild(JokeCardComponent.create(joke));
                });
                
                // Inicializar botones de voto
                HomePage.initVoteButtons();
                
                document.getElementById('loading').style.display = 'none';
            } catch (error) {
                console.error('Error loading jokes:', error);
                document.getElementById('loading').innerHTML = 'Error cargando chistes';
            }
        };
        
        HomePage.initVoteButtons = () => {
            document.querySelectorAll('.upvote-btn').forEach(btn => {
                btn.addEventListener('click', async (e) => {
                    e.preventDefault();
                    if (!AuthService.isAuthenticated()) {
                        Router.goTo('/login');
                        return;
                    }
                    
                    const jokeId = btn.dataset.jokeId;
                    try {
                        await apiClient.post(`/jokes/${jokeId}/vote`, { voteValue: 1 });
                        loadJokes(currentPage);
                    } catch (error) {
                        alert('Error al votar');
                    }
                });
            });
        };
        
        // Cargar primera página
        await loadJokes(0);
    }
};

window.HomePage = HomePage;
```

---

## 7. ROUTER SPA

### 7.1 router.js - Enrutador simple

```javascript
const Router = {
    routes: {
        '/': HomePage,
        '/trending': TrendingPage,
        '/profile/:id': ProfilePage,
        '/joke/:id': DetailPage,
        '/create': CreateJokePage,
        '/search': SearchPage,
        '/login': LoginPage,
        '/register': RegisterPage
    },
    
    goTo: (path) => {
        window.history.pushState({}, '', path);
        Router.navigate();
    },
    
    navigate: () => {
        const path = window.location.pathname.replace('/api/v1', '');
        const route = Object.keys(Router.routes).find(r => {
            const regex = new RegExp('^' + r.replace(/:[^/]/g, '[^/]+') + '$');
            return regex.test(path);
        });
        
        if (route && Router.routes[route]) {
            Router.routes[route].init();
        }
    }
};

// Manejar botones atrás/adelante
window.addEventListener('popstate', () => Router.navigate());

window.Router = Router;
```

---

## 8. MAIN.JS - Punto de entrada

```javascript
document.addEventListener('DOMContentLoaded', () => {
    // Inicializar navbar
    Navbar.init();
    
    // Iniciar router
    Router.navigate();
    
    // Manejar búsqueda global
    document.getElementById('searchInput')?.addEventListener('keydown', (e) => {
        if (e.key === 'Enter') {
            Router.goTo(`/search?q=${encodeURIComponent(e.target.value)}`);
        }
    });
});
```

---

## SIGUIENTES PASOS PARA FRONTEND

1. Implementar todas las páginas (home, trending, profile, create-joke, details, search)
2. Crear componentes reutilizables (navbar, footer, comentarios)
3. Integrar con API REST del backend
4. Implementar validación de formularios
5. Agregar modo responsive para mobile
6. Agregar soporte para dark mode
7. Testing con Jest/Testing Library
8. Optimización de performance (lazy loading, code splitting)
9. PWA capabilities (service workers)
10. SEO optimization

