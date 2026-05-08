// Toast Notifications

function showToast(message, type = 'info', duration = 3000) {
    const container = document.getElementById('toastContainer');
    
    const toast = document.createElement('div');
    toast.className = `toast ${type}`;
    toast.innerHTML = `
        <i class="fas fa-${getIconByType(type)}"></i>
        <span>${message}</span>
    `;
    
    container.appendChild(toast);
    
    // Animar entrada
    setTimeout(() => toast.style.animation = 'slideIn 0.3s ease-in-out', 10);
    
    // Auto-remover
    setTimeout(() => {
        toast.style.animation = 'slideOut 0.3s ease-in-out';
        setTimeout(() => toast.remove(), 300);
    }, duration);
    
    return toast;
}

function getIconByType(type) {
    const icons = {
        success: 'check-circle',
        error: 'exclamation-circle',
        warning: 'exclamation-triangle',
        info: 'info-circle'
    };
    return icons[type] || 'info-circle';
}
