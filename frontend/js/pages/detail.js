// Página: Chiste Detail

function pageDetail(params) {
    const jokeId = params.id;
    const appContainer = document.getElementById('app');

    API.getJokeById(jokeId)
        .then(response => {
            const joke = response.data;

            // Cargar comentarios
            return API.getCommentsByJoke(jokeId, 0, 10)
                .then(commentsResponse => {
                    renderJokeDetail(joke, commentsResponse.data.content);
                });
        })
        .catch(error => {
            appContainer.innerHTML = `<div class="container"><div class="error-message">${getErrorMessage(error)}</div></div>`;
        });

    function renderJokeDetail(joke, comments) {
        let html = `
            <div class="container" style="max-width: 800px; margin: 0 auto;">
                <div class="mt-3 mb-3">
                    <a href="/" class="btn btn-outline btn-sm">
                        <i class="fas fa-arrow-left"></i> Volver
                    </a>
                </div>

                ${createJokeCard(joke)}

                <div class="card mt-3">
                    <div class="card-header">
                        <h3><i class="fas fa-comments"></i> Comentarios (${comments.length})</h3>
                    </div>

                    ${AUTH.isAuthenticated() ? `
                        <form id="commentForm" class="mb-3" style="padding-bottom: var(--spacing-lg); border-bottom: 1px solid var(--gray);">
                            <div class="form-group">
                                <textarea id="commentContent" placeholder="Escribe tu comentario..." 
                                          required minlength="1" maxlength="1000"></textarea>
                            </div>
                            <button type="submit" class="btn btn-primary btn-sm">
                                <i class="fas fa-comment"></i> Comentar
                            </button>
                        </form>
                    ` : `
                        <div class="mb-3" style="padding-bottom: var(--spacing-lg); border-bottom: 1px solid var(--gray);">
                            <p class="text-muted"><a href="/login">Inicia sesión</a> para comentar</p>
                        </div>
                    `}

                    <div id="commentsList">
                        ${comments.length === 0 ? `
                            <div class="empty-state">
                                <p class="text-muted">No hay comentarios aún. ¡Sé el primero!</p>
                            </div>
                        ` : comments.map(comment => `
                            <div class="comment" style="padding: var(--spacing-md); border-bottom: 1px solid var(--gray);">
                                <div class="d-flex flex-between mb-2">
                                    <div>
                                        <strong>${comment.author?.username}</strong>
                                        <span class="text-muted" style="font-size: var(--font-size-sm);">
                                            ${FORMATTER.timeAgo(comment.createdAt)}
                                        </span>
                                    </div>
                                </div>
                                <p>${VALIDATOR.sanitizeHTML(comment.content)}</p>
                            </div>
                        `).join('')}
                    </div>
                </div>
            </div>
        `;

        appContainer.innerHTML = html;

        // Event listener para comentarios
        if (AUTH.isAuthenticated()) {
            document.getElementById('commentForm').addEventListener('submit', (e) => {
                e.preventDefault();

                const content = document.getElementById('commentContent').value;

                API.createComment({ jokeId, content })
                    .then(() => {
                        showToast('Comentario publicado', 'success');
                        // Recargar página
                        pageDetail(params);
                    })
                    .catch(error => {
                        showToast(getErrorMessage(error), 'error');
                    });
            });
        }
    }
}
