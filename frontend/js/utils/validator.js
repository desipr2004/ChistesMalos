// Validaciones

const VALIDATOR = {
    // Validar email
    isValidEmail: (email) => {
        const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return regex.test(email);
    },

    // Validar contraseña
    isValidPassword: (password) => {
        return password.length >= 8;
    },

    // Validar username
    isValidUsername: (username) => {
        return username.length >= 3 && username.length <= 50 && /^[a-zA-Z0-9_-]+$/.test(username);
    },

    // Validar title
    isValidTitle: (title) => {
        return title.length >= 5 && title.length <= 200;
    },

    // Validar content
    isValidContent: (content) => {
        return content.length >= 10 && content.length <= 5000;
    },

    // Validar comentario
    isValidComment: (comment) => {
        return comment.length >= 1 && comment.length <= 1000;
    },

    // Sanitizar HTML
    sanitizeHTML: (html) => {
        const div = document.createElement('div');
        div.textContent = html;
        return div.innerHTML;
    }
};
