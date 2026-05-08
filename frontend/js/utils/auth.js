// Utilidades de Autenticación

const AUTH = {
    // Guardar token y usuario
    setToken: (token, refreshToken, user) => {
        localStorage.setItem('accessToken', token);
        localStorage.setItem('refreshToken', refreshToken);
        localStorage.setItem('user', JSON.stringify(user));
    },

    // Obtener token
    getToken: () => {
        return localStorage.getItem('accessToken');
    },

    // Obtener usuario
    getUser: () => {
        const user = localStorage.getItem('user');
        return user ? JSON.parse(user) : null;
    },

    // Verificar si está autenticado
    isAuthenticated: () => {
        return !!localStorage.getItem('accessToken');
    },

    // Logout
    logout: () => {
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        localStorage.removeItem('user');
    },

    // Verificar si es admin
    isAdmin: () => {
        const user = AUTH.getUser();
        return user && user.role === 'ADMIN';
    }
};
