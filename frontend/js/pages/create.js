// Página: Create Joke

function pageCreate(params) {
    const appContainer = document.getElementById('app');

    if (!AUTH.isAuthenticated()) {
        navigateTo('/login');
        return;
    }

    // Cargar categorías
    API.getAllCategories()
        .then(response => {
            const categories = response.data;

            appContainer.innerHTML = `
                <div class="container" style="max-width: 600px; margin: 50px auto;">
                    <div class="card">
                        <div class="card-header">
                            <h2><i class="fas fa-lightbulb"></i> Crear Nuevo Chiste</h2>
                        </div>
                        <form id="createJokeForm">
                            <div class="form-group">
                                <label for="title">Título</label>
                                <input type="text" id="title" name="title" required 
                                       placeholder="El título de tu chiste"
                                       minlength="5" maxlength="200">
                                <small class="text-muted">Entre 5 y 200 caracteres</small>
                            </div>

                            <div class="form-group">
                                <label for="categoryId">Categoría</label>
                                <select id="categoryId" name="categoryId" required>
                                    <option value="">-- Selecciona una categoría --</option>
                                    ${categories.map(cat => `<option value="${cat.id}">${cat.name}</option>`).join('')}
                                </select>
                            </div>

                            <div class="form-group">
                                <label for="content">Contenido</label>
                                <textarea id="content" name="content" required 
                                          placeholder="Cuenta tu chiste aquí..."
                                          minlength="10" maxlength="5000"></textarea>
                                <small class="text-muted">Entre 10 y 5000 caracteres</small>
                            </div>

                            <div class="form-group">
                                <label>
                                    <input type="checkbox" id="isPublished" name="isPublished" checked>
                                    Publicar inmediatamente
                                </label>
                            </div>

                            <button type="submit" class="btn btn-primary" style="width: 100%;">
                                <i class="fas fa-paper-plane"></i> Publicar Chiste
                            </button>

                            <div id="formMessage" class="mt-3" style="display: none;"></div>
                        </form>
                    </div>
                </div>
            `;

            // Event listener para submit
            document.getElementById('createJokeForm').addEventListener('submit', (e) => {
                e.preventDefault();

                const title = document.getElementById('title').value;
                const content = document.getElementById('content').value;
                const categoryId = parseInt(document.getElementById('categoryId').value);
                const isPublished = document.getElementById('isPublished').checked;

                API.createJoke({ title, content, categoryId, isPublished })
                    .then(response => {
                        showToast('Chiste creado exitosamente', 'success');
                        navigateTo(`/jokes/${response.data.id}`);
                    })
                    .catch(error => {
                        const message = getErrorMessage(error);
                        const messageDiv = document.getElementById('formMessage');
                        messageDiv.className = 'error-message mt-3';
                        messageDiv.textContent = message;
                        messageDiv.style.display = 'block';
                    });
            });
        })
        .catch(error => {
            appContainer.innerHTML = `<div class="container"><div class="error-message">${getErrorMessage(error)}</div></div>`;
        });
}
