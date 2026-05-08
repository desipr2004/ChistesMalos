// Página: Trending

function pageTrending(params) {
    const appContainer = document.getElementById('app');

    API.getWorstJokes(0, 20)
        .then(response => {
            const { content } = response.data;

            let html = `
                <div class="container">
                    <h1 class="mb-3"><i class="fas fa-fire"></i> Peores Chistes</h1>
                    <div id="jokesList">
            `;

            if (content.length === 0) {
                html += '<div class="empty-state"><p>No hay chistes</p></div>';
            } else {
                html += content.map(joke => createJokeCard(joke)).join('');
            }

            html += '</div></div>';
            appContainer.innerHTML = html;
        })
        .catch(error => {
            appContainer.innerHTML = `<div class="container"><div class="error-message">${getErrorMessage(error)}</div></div>`;
        });
}
