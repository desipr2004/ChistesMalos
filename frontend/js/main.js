// Main - Inicializar la aplicación

document.addEventListener('DOMContentLoaded', () => {
    console.log('BadJokes Platform iniciada');
    
    // Verificar autenticación
    if (AUTH.isAuthenticated()) {
        console.log('Usuario autenticado:', AUTH.getUser().username);
    }

    // Inicializar router
    router();
});

// Páginas placeholder
function pageMyJokes(params) {
    document.getElementById('app').innerHTML = '<div class="container"><p class="mt-3">Mis Chistes - En desarrollo</p></div>';
}

function pageNotFound(params) {
    document.getElementById('app').innerHTML = `
        <div class="container" style="text-align: center; margin-top: 50px;">
            <h1>404</h1>
            <p>Página no encontrada</p>
            <a href="/" class="btn btn-primary">Volver al Inicio</a>
        </div>
    `;
}
