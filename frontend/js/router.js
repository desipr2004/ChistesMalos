// Router SPA - Single Page Application

const ROUTES = {
    '/': 'home',
    '/login': 'login',
    '/register': 'register',
    '/create': 'create',
    '/jokes/:id': 'detail',
    '/categories': 'categories',
    '/categories/:id': 'categoryDetail',
    '/trending': 'trending',
    '/profile': 'profile',
    '/my-jokes': 'myJokes',
    '/404': 'notFound'
};

let currentPage = null;
let currentParams = {};

function navigateTo(path) {
    window.history.pushState(null, null, path);
    router();
}

function router() {
    const path = window.location.pathname;
    const appContainer = document.getElementById('app');

    // Actualizar active link
    document.querySelectorAll('.nav-link').forEach(link => {
        link.classList.remove('active');
    });
    document.querySelector(`.nav-link[href="${path}"]`)?.classList.add('active');

    // Resolver ruta
    let route = null;
    let params = {};

    if (path in ROUTES) {
        route = ROUTES[path];
    } else {
        // Buscar rutas con parámetros
        for (const [routePath, routeName] of Object.entries(ROUTES)) {
            const regex = routePath.replace(/:[^/]+/g, '([^/]+)').replace(/\//g, '\\/');
            const match = path.match(new RegExp(`^${regex}$`));
            if (match) {
                route = routeName;
                // Extraer parámetros
                const paramNames = routePath.match(/:[^/]+/g) || [];
                paramNames.forEach((name, index) => {
                    params[name.substring(1)] = match[index + 1];
                });
                break;
            }
        }
    }

    if (!route) {
        route = 'notFound';
    }

    currentParams = params;

    // Renderizar página
    appContainer.innerHTML = '<div class="loading"><div class="spinner"></div></div>';
    
    const pageFunction = window[`page${route.charAt(0).toUpperCase() + route.slice(1)}`];
    if (pageFunction) {
        pageFunction(params);
    } else {
        appContainer.innerHTML = '<div class="error-message">Página no encontrada</div>';
    }
}

// Event listeners para navegación
window.addEventListener('popstate', router);
document.addEventListener('DOMContentLoaded', router);

document.addEventListener('click', handleInternalNavigation);

// Actualizar auth section en navbar
function updateAuthUI() {
    const authSection = document.getElementById('authSection');
    const userSection = document.getElementById('userSection');
    const usernameDisplay = document.getElementById('usernameDisplay');

    if (AUTH.isAuthenticated()) {
        authSection.style.display = 'none';
        userSection.style.display = 'flex';
        const user = AUTH.getUser();
        usernameDisplay.textContent = user?.username || 'Usuario';
    } else {
        authSection.style.display = 'flex';
        userSection.style.display = 'none';
    }
}

function handleInternalNavigation(event) {
    const anchor = event.target.closest('a[href^="/"]');
    if (!anchor || anchor.target === '_blank') return;

    const href = anchor.getAttribute('href');
    if (href && href.startsWith('/')) {
        event.preventDefault();
        navigateTo(href);
    }
}

// Dropdown toggle
document.addEventListener('click', (e) => {
    const dropdown = e.target.closest('.dropdown');
    if (dropdown) {
        const menu = dropdown.querySelector('.dropdown-menu');
        menu.classList.toggle('active');
    } else {
        document.querySelectorAll('.dropdown-menu.active').forEach(menu => {
            menu.classList.remove('active');
        });
    }
});

// Hamburger menu
document.addEventListener('click', (e) => {
    const hamburger = e.target.closest('.hamburger');
    if (hamburger) {
        hamburger.classList.toggle('active');
        document.querySelector('.nav-menu').classList.toggle('active');
    } else if (!e.target.closest('.nav-menu')) {
        document.querySelector('.hamburger')?.classList.remove('active');
        document.querySelector('.nav-menu')?.classList.remove('active');
    }
});

// Logout button
document.addEventListener('click', (e) => {
    if (e.target.id === 'logoutBtn') {
        e.preventDefault();
        AUTH.logout();
        showToast('Sesión cerrada', 'success');
        updateAuthUI();
        navigateTo('/');
    }
});

// Inicializar UI
document.addEventListener('DOMContentLoaded', updateAuthUI);
