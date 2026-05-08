// Página: Home - Lista de chistes
let homeJokes = [];
let homeCurrentPage = 0;
let homeTotalPages = 0;

function pageHome(params) {
    homeCurrentPage = parseInt(new URLSearchParams(window.location.search).get('page') || '0', 10);
    const appContainer = document.getElementById('app');
    appContainer.innerHTML = '<div class="loading"><div class="spinner"></div></div>';

    Promise.all([API.getAllJokes(homeCurrentPage, 10), API.getAllCategories()])
        .then(([jokesResponse, categoriesResponse]) => {
            const { content, totalPages, empty } = jokesResponse.data;
            homeJokes = content;
            homeTotalPages = totalPages;
            const categories = categoriesResponse.data || [];

            let html = `
                <div class="container">
                    <div class="hero home-hero">
                        <div>
                            <h1>Bienvenido a BadJokes</h1>
                            <p>La plataforma para compartir tus peores y más divertidos chistes.</p>
                        </div>
                        <div class="hero-actions">
                            <button class="btn btn-secondary" id="btnDailyJoke">
                                <i class="fas fa-star"></i> Chiste del Día
                            </button>
                            <input id="homeSearchInput" class="input-search" type="search" placeholder="Buscar chistes, categorías o autor..." aria-label="Buscar chistes">
                        </div>
                    </div>

                    <div class="category-tags" id="homeCategoryTags">
                        ${categories.map(cat => `
                            <button class="btn btn-outline btn-sm category-tag" data-category-id="${cat.id}">
                                ${cat.name}
                            </button>
                        `).join('')}
                    </div>

                    <div id="homeJokesContainer" class="jokes-list"></div>
                </div>
            `;

            appContainer.innerHTML = html;
            renderHomeJokes(homeJokes, empty);
            attachHomeListeners();
        })
        .catch(error => {
            appContainer.innerHTML = `<div class="container"><div class="error-message">${getErrorMessage(error)}</div></div>`;
        });
}

function renderHomeJokes(jokes, empty) {
    const container = document.getElementById('homeJokesContainer');
    if (!container) return;

    if (empty || !jokes || jokes.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <div class="empty-state-icon"><i class="fas fa-inbox"></i></div>
                <h3>No hay chistes disponibles</h3>
                <p>Prueba otra categoría, crea uno nuevo o vuelve más tarde.</p>
            </div>
        `;
        return;
    }

    container.innerHTML = `
        ${jokes.map(joke => createJokeCard(joke)).join('')}
        ${createPagination(homeCurrentPage, homeTotalPages, 'loadHomePage')}
    `;
}

function attachHomeListeners() {
    document.getElementById('btnDailyJoke')?.addEventListener('click', loadDailyJoke);
    document.getElementById('homeSearchInput')?.addEventListener('input', (event) => {
        filterHomeJokes(event.target.value.trim());
    });

    document.querySelectorAll('.category-tag').forEach(button => {
        button.addEventListener('click', () => {
            navigateTo(`/categories/${button.dataset.categoryId}`);
        });
    });
}

function filterHomeJokes(query) {
    const normalizedQuery = (query || '').toLowerCase();
    const filtered = normalizedQuery === ''
        ? homeJokes
        : homeJokes.filter(joke => {
            const title = joke.title?.toLowerCase() || '';
            const content = joke.content?.toLowerCase() || '';
            const author = joke.author?.username?.toLowerCase() || '';
            const category = joke.categoryName?.toLowerCase() || '';
            return title.includes(normalizedQuery)
                || content.includes(normalizedQuery)
                || author.includes(normalizedQuery)
                || category.includes(normalizedQuery);
        });

    renderHomeJokes(filtered, filtered.length === 0);
}

function loadDailyJoke() {
    API.getJokeOfDay()
        .then(response => {
            const joke = response.data;
            createModal('Chiste del Día', `
                <div class="modal-joke-card">${createJokeCard(joke)}</div>
            `, [
                { text: 'Ver detalle', class: 'btn-primary', onClick: `navigateTo('/jokes/${joke.id}')` },
                { text: 'Cerrar', class: 'btn-outline', onClick: 'closeModal()' }
            ]);
        })
        .catch(error => {
            showToast(getErrorMessage(error), 'error');
        });
}

function loadHomePage(page) {
    navigateTo(`/?page=${page}`);
}
