let currentCategoryId = null;

// Página: Categories

function pageCategories(params) {
    const appContainer = document.getElementById('app');
    appContainer.innerHTML = '<div class="loading"><div class="spinner"></div></div>';

    API.getAllCategories()
        .then(response => {
            const categories = response.data;

            let html = `
                <div class="container">
                    <div class="page-header">
                        <div>
                            <h1>Categorías</h1>
                            <p>Explora chistes por temas y encuentra el humor que más te gusta.</p>
                        </div>
                    </div>
                    <div class="grid grid-3">
            `;

            if (!Array.isArray(categories) || categories.length === 0) {
                html += '<div class="empty-state"><p>No hay categorías disponibles</p></div>';
            } else {
                html += categories.map(cat => `
                    <div class="card category-card">
                        <div class="card-body">
                            <h3>${cat.name}</h3>
                            <p class="text-muted">${cat.description || 'Sin descripción disponible'}</p>
                            <div class="category-meta">
                                <span><i class="fas fa-laugh"></i> ${cat.jokeCount || 0} chistes</span>
                            </div>
                        </div>
                        <button class="btn btn-primary btn-sm" onclick="navigateTo('/categories/${cat.id}')">
                            Ver chistes
                        </button>
                    </div>
                `).join('');
            }

            html += '</div></div>';
            appContainer.innerHTML = html;
        })
        .catch(error => {
            appContainer.innerHTML = `<div class="container"><div class="error-message">${getErrorMessage(error)}</div></div>`;
        });
}

function pageCategoryDetail(params) {
    const categoryId = params.id;
    currentCategoryId = categoryId;
    const page = parseInt(new URLSearchParams(window.location.search).get('page') || '0', 10);
    const appContainer = document.getElementById('app');
    appContainer.innerHTML = '<div class="loading"><div class="spinner"></div></div>';

    Promise.all([API.getCategoryById(categoryId), API.getJokesByCategory(categoryId, page, 10)])
        .then(([categoryResponse, jokesResponse]) => {
            const category = categoryResponse.data;
            const { content, totalPages, empty } = jokesResponse.data;

            let html = `
                <div class="container" style="max-width: 1024px; margin: 0 auto;">
                    <div class="page-header">
                        <div>
                            <h1>${category.name}</h1>
                            <p>${category.description || 'Chistes de esta categoría.'}</p>
                        </div>
                        <button class="btn btn-outline btn-sm" onclick="navigateTo('/categories')">
                            <i class="fas fa-arrow-left"></i> Volver a Categorías
                        </button>
                    </div>
                    <div id="categoryJokesList" class="jokes-list"></div>
                </div>
            `;

            appContainer.innerHTML = html;

            const jokesContainer = document.getElementById('categoryJokesList');
            if (empty || !Array.isArray(content) || content.length === 0) {
                jokesContainer.innerHTML = `
                    <div class="empty-state">
                        <div class="empty-state-icon"><i class="fas fa-smile-beam"></i></div>
                        <h3>No hay chistes en esta categoría</h3>
                        <p>Intenta otra categoría o vuelve al inicio.</p>
                    </div>
                `;
            } else {
                jokesContainer.innerHTML = content.map(createJokeCard).join('') + createPagination(page, totalPages, 'loadCategoryPage');
            }
        })
        .catch(error => {
            appContainer.innerHTML = `<div class="container"><div class="error-message">${getErrorMessage(error)}</div></div>`;
        });
}

function loadCategoryPage(page) {
    if (!currentCategoryId) return;
    navigateTo(`/categories/${currentCategoryId}?page=${page}`);
}
