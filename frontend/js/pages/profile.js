// Página: Profile

function pageProfile(params) {
    const appContainer = document.getElementById('app');

    if (!AUTH.isAuthenticated()) {
        navigateTo('/login');
        return;
    }

    const user = AUTH.getUser();

    appContainer.innerHTML = `
        <div class="container" style="max-width: 600px;">
            <div class="card">
                <div class="card-header">
                    <h2>Mi Perfil</h2>
                </div>
                <div class="card-body">
                    <div style="text-align: center; margin-bottom: var(--spacing-lg);">
                        <img src="https://via.placeholder.com/100" 
                             alt="${user.username}" 
                             style="width: 100px; height: 100px; border-radius: 50%; margin-bottom: var(--spacing-md);">
                        <h3>${user.username}</h3>
                        <p class="text-muted">${user.email}</p>
                        <span class="badge badge-primary">${user.role}</span>
                    </div>

                    <div style="display: grid; gap: var(--spacing-md);">
                        <div>
                            <label class="text-muted">Creado el</label>
                            <p>${FORMATTER.formatDateShort(user.createdAt)}</p>
                        </div>
                    </div>
                </div>
                <div class="card-footer">
                    <a href="/my-jokes" class="btn btn-primary">Mis Chistes</a>
                </div>
            </div>
        </div>
    `;
}
