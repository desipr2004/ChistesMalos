// Página: Login

function pageLogin(params) {
    const appContainer = document.getElementById('app');

    if (AUTH.isAuthenticated()) {
        navigateTo('/');
        return;
    }

    appContainer.innerHTML = `
        <div class="container" style="max-width: 400px; margin: 50px auto;">
            <div class="card">
                <div class="card-header">
                    <h2><i class="fas fa-sign-in-alt"></i> Iniciar Sesión</h2>
                </div>
                <form id="loginForm">
                    <div class="form-group">
                        <label for="username">Usuario o Email</label>
                        <input type="text" id="username" name="username" required 
                               placeholder="tu_usuario o tu@email.com">
                        <div class="form-error" id="usernameError"></div>
                    </div>

                    <div class="form-group">
                        <label for="password">Contraseña</label>
                        <input type="password" id="password" name="password" required 
                               placeholder="••••••••">
                        <div class="form-error" id="passwordError"></div>
                    </div>

                    <button type="submit" class="btn btn-primary" style="width: 100%;">
                        <i class="fas fa-sign-in-alt"></i> Iniciar Sesión
                    </button>

                    <div id="formMessage" class="mt-3" style="display: none;"></div>
                </form>

                <div class="mt-3" style="text-align: center;">
                    <p>¿No tienes cuenta? <a href="/register">Regístrate aquí</a></p>
                </div>
            </div>
        </div>
    `;

    // Event listener para submit
    document.getElementById('loginForm').addEventListener('submit', (e) => {
        e.preventDefault();

        const username = document.getElementById('username').value;
        const password = document.getElementById('password').value;

        API.login({ usernameOrEmail: username, password })
            .then(response => {
                const { accessToken, refreshToken, user } = response.data;
                AUTH.setToken(accessToken, refreshToken, user);
                showToast('Sesión iniciada correctamente', 'success');
                updateAuthUI();
                navigateTo('/');
            })
            .catch(error => {
                const message = getErrorMessage(error);
                const messageDiv = document.getElementById('formMessage');
                messageDiv.className = 'error-message mt-3';
                messageDiv.textContent = message;
                messageDiv.style.display = 'block';
            });
    });
}
