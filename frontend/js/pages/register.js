// Página: Register

function pageRegister(params) {
    const appContainer = document.getElementById('app');

    if (AUTH.isAuthenticated()) {
        navigateTo('/');
        return;
    }

    appContainer.innerHTML = `
        <div class="container" style="max-width: 400px; margin: 50px auto;">
            <div class="card">
                <div class="card-header">
                    <h2><i class="fas fa-user-plus"></i> Crear Cuenta</h2>
                </div>
                <form id="registerForm">
                    <div class="form-group">
                        <label for="username">Usuario</label>
                        <input type="text" id="username" name="username" required 
                               placeholder="tu_usuario"
                               minlength="3" maxlength="50">
                        <div class="form-error" id="usernameError"></div>
                    </div>

                    <div class="form-group">
                        <label for="email">Email</label>
                        <input type="email" id="email" name="email" required 
                               placeholder="tu@email.com">
                        <div class="form-error" id="emailError"></div>
                    </div>

                    <div class="form-group">
                        <label for="password">Contraseña</label>
                        <input type="password" id="password" name="password" required 
                               placeholder="Mínimo 8 caracteres"
                               minlength="8">
                        <div class="form-error" id="passwordError"></div>
                    </div>

                    <div class="form-group">
                        <label for="passwordConfirm">Confirmar Contraseña</label>
                        <input type="password" id="passwordConfirm" name="passwordConfirm" required 
                               placeholder="Confirma tu contraseña"
                               minlength="8">
                        <div class="form-error" id="passwordConfirmError"></div>
                    </div>

                    <button type="submit" class="btn btn-primary" style="width: 100%;">
                        <i class="fas fa-user-plus"></i> Crear Cuenta
                    </button>

                    <div id="formMessage" class="mt-3" style="display: none;"></div>
                </form>

                <div class="mt-3" style="text-align: center;">
                    <p>¿Ya tienes cuenta? <a href="/login">Inicia sesión aquí</a></p>
                </div>
            </div>
        </div>
    `;

    // Event listener para submit
    document.getElementById('registerForm').addEventListener('submit', (e) => {
        e.preventDefault();

        const username = document.getElementById('username').value;
        const email = document.getElementById('email').value;
        const password = document.getElementById('password').value;
        const passwordConfirm = document.getElementById('passwordConfirm').value;

        // Validaciones
        if (password !== passwordConfirm) {
            showToast('Las contraseñas no coinciden', 'error');
            return;
        }

        API.register({ username, email, password, passwordConfirm })
            .then(response => {
                const { accessToken, refreshToken, user } = response.data;
                AUTH.setToken(accessToken, refreshToken, user);
                showToast('Cuenta creada correctamente', 'success');
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
