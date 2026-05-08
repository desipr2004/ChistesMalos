// Componente: Joke Card

function createJokeCard(joke) {
    const isUpvoted = false; // TODO: Verificar si el usuario ya votó
    const isDownvoted = false;

    return `
        <div class="joke-card">
            <div class="joke-header">
                <div class="joke-author">
                    <img src="${joke.author?.avatarUrl || 'https://via.placeholder.com/40'}" 
                         alt="${joke.author?.username}"
                         loading="lazy">
                    <div>
                        <div class="text-dark"><strong>${joke.author?.username}</strong></div>
                        <div class="text-muted text-sm">${FORMATTER.timeAgo(joke.createdAt)}</div>
                    </div>
                </div>
                <span class="badge badge-primary">${joke.categoryName}</span>
            </div>

            <h3 class="joke-title">${VALIDATOR.sanitizeHTML(joke.title)}</h3>
            <p class="joke-content">${VALIDATOR.sanitizeHTML(joke.content)}</p>

            <div class="joke-meta">
                <div class="joke-meta-item">
                    <i class="fas fa-eye"></i>
                    <span>${FORMATTER.formatNumber(joke.views)}</span>
                </div>
                <div class="joke-meta-item">
                    <i class="fas fa-comments"></i>
                    <span>${joke.commentCount || 0}</span>
                </div>
                <div class="joke-meta-item">
                    <i class="fas fa-thumbs-up"></i>
                    <span>${FORMATTER.formatNumber(joke.upvotes)}</span>
                </div>
                <div class="joke-meta-item">
                    <i class="fas fa-thumbs-down"></i>
                    <span>${FORMATTER.formatNumber(joke.downvotes)}</span>
                </div>
            </div>

            <div class="joke-actions">
                <div class="vote-buttons">
                    <button class="vote-btn upvote-btn ${isUpvoted ? 'active' : ''}" 
                            data-joke-id="${joke.id}" 
                            ${!AUTH.isAuthenticated() ? 'disabled' : ''}>
                        <i class="fas fa-thumbs-up"></i>
                        <span>${joke.upvotes}</span>
                    </button>
                    <button class="vote-btn downvote-btn ${isDownvoted ? 'active' : ''}" 
                            data-joke-id="${joke.id}"
                            ${!AUTH.isAuthenticated() ? 'disabled' : ''}>
                        <i class="fas fa-thumbs-down"></i>
                        <span>${joke.downvotes}</span>
                    </button>
                </div>
                <div class="joke-actions-right">
                    <button class="btn btn-sm btn-outline share-btn" data-joke-id="${joke.id}" aria-label="Compartir chiste">
                        <i class="fas fa-share"></i>
                        Compartir
                    </button>
                    <button class="btn btn-sm btn-outline" 
                            onclick="navigateTo('/jokes/${joke.id}')">
                        <i class="fas fa-comment"></i>
                        Ver Chiste
                    </button>
                </div>
            </div>
        </div>
    `;
}

// Event listeners para votos y compartir
document.addEventListener('click', function(e) {
    if (e.target.closest('.share-btn')) {
        const btn = e.target.closest('.share-btn');
        shareJoke(btn.dataset.jokeId);
        return;
    }

    if (e.target.closest('.upvote-btn')) {
        const btn = e.target.closest('.upvote-btn');
        const jokeId = btn.dataset.jokeId;
        voteOnJoke(jokeId, 'UPVOTE');
    }
    if (e.target.closest('.downvote-btn')) {
        const btn = e.target.closest('.downvote-btn');
        const jokeId = btn.dataset.jokeId;
        voteOnJoke(jokeId, 'DOWNVOTE');
    }
});

function shareJoke(jokeId) {
    const url = `${window.location.origin}/jokes/${jokeId}`;
    if (!navigator.clipboard) {
        showToast('Tu navegador no soporta copiar al portapapeles', 'error');
        return;
    }

    navigator.clipboard.writeText(url)
        .then(() => showToast('Enlace copiado al portapapeles', 'success'))
        .catch(() => showToast('No se pudo copiar el enlace', 'error'));
}

function voteOnJoke(jokeId, voteType) {
    if (!AUTH.isAuthenticated()) {
        showToast('Debes iniciar sesión para votar', 'warning');
        navigateTo('/login');
        return;
    }

    API.voteOnJoke(jokeId, { voteType })
        .then(() => {
            showToast('Voto registrado', 'success');
            router();
        })
        .catch(error => {
            showToast(getErrorMessage(error), 'error');
        });
}
