// Componente: Modal

function createModal(title, content, actions = []) {
    const modal = document.getElementById('modalContainer');
    const modalBody = document.getElementById('modalBody');
    const closeBtn = modal.querySelector('.modal-close');

    // Limpiar modal anterior
    modalBody.innerHTML = '';

    // Crear contenido
    const html = `
        <div class="modal-header mb-3">
            <h2>${title}</h2>
        </div>
        <div class="modal-content-body">
            ${content}
        </div>
        ${actions.length > 0 ? `
            <div class="modal-footer mt-3" style="display: flex; gap: 10px;">
                ${actions.map(action => `
                    <button class="btn ${action.class || 'btn-primary'}" 
                            onclick="${action.onClick}">
                        ${action.text}
                    </button>
                `).join('')}
            </div>
        ` : ''}
    `;

    modalBody.innerHTML = html;
    modal.classList.add('active');
    modal.style.display = 'flex';

    // Event listener para cerrar
    closeBtn.onclick = () => closeModal();
    modal.onclick = (e) => {
        if (e.target === modal) closeModal();
    };
}

function closeModal() {
    const modal = document.getElementById('modalContainer');
    modal.classList.remove('active');
    modal.style.display = 'none';
}

// Cerrar modal con ESC
document.addEventListener('keydown', (e) => {
    if (e.key === 'Escape') {
        closeModal();
    }
});
