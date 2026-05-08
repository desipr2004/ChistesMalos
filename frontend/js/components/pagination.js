// Componente: Pagination

function createPagination(currentPage, totalPages, onPageChange) {
    if (totalPages <= 1) return '';

    let html = '<div class="pagination flex-center gap-2 mt-3">';

    // Botón anterior
    if (currentPage > 0) {
        html += `<button class="btn btn-sm" onclick="handlePageChange(${currentPage - 1}, '${onPageChange}')">
            <i class="fas fa-chevron-left"></i> Anterior
        </button>`;
    }

    // Números de página
    for (let i = Math.max(0, currentPage - 2); i <= Math.min(totalPages - 1, currentPage + 2); i++) {
        if (i === currentPage) {
            html += `<button class="btn btn-sm btn-primary" disabled>${i + 1}</button>`;
        } else {
            html += `<button class="btn btn-sm" onclick="handlePageChange(${i}, '${onPageChange}')">${i + 1}</button>`;
        }
    }

    // Botón siguiente
    if (currentPage < totalPages - 1) {
        html += `<button class="btn btn-sm" onclick="handlePageChange(${currentPage + 1}, '${onPageChange}')">
            Siguiente <i class="fas fa-chevron-right"></i>
        </button>`;
    }

    html += '</div>';
    return html;
}

function handlePageChange(page, callback) {
    // Ejecutar callback con la nueva página
    if (window[callback]) {
        window[callback](page);
    }
}
