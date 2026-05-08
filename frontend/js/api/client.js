// API Client con Axios - Configuración centralizada

const API_BASE_URL = (typeof process !== 'undefined' && process.env && process.env.API_URL)
    ? process.env.API_URL
    : 'http://localhost:8080/api';

// Crear instancia de Axios
const apiClient = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        'Content-Type': 'application/json',
    }
});

// Interceptor de request
apiClient.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('accessToken');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

// Interceptor de response para refresh token
apiClient.interceptors.response.use(
    (response) => response,
    async (error) => {
        const originalRequest = error.config;
        const refreshToken = localStorage.getItem('refreshToken');

        if (error.response?.status === 401 && refreshToken && !originalRequest._retry) {
            originalRequest._retry = true;
            try {
                const refreshResponse = await API.refreshToken({ refreshToken });
                const { accessToken, refreshToken: newRefreshToken } = refreshResponse.data;
                AUTH.setToken(accessToken, newRefreshToken, AUTH.getUser());
                originalRequest.headers.Authorization = `Bearer ${accessToken}`;
                return apiClient(originalRequest);
            } catch (refreshError) {
                AUTH.logout();
                window.location.href = '/login';
                return Promise.reject(refreshError);
            }
        }

        if (error.response?.status === 401) {
            AUTH.logout();
            window.location.href = '/login';
        } else if (error.response?.status === 403) {
            showToast('No tienes permiso para realizar esta acción', 'error');
        } else if (error.response?.status === 404) {
            showToast('Recurso no encontrado', 'error');
        } else if (error.response?.status === 500) {
            showToast('Error interno del servidor', 'error');
        }
        return Promise.reject(error);
    }
);

// Interceptor de response
apiClient.interceptors.response.use(
    (response) => {
        return response;
    },
    (error) => {
        if (error.response?.status === 401) {
            // Token expirado, limpiar y redirigir a login
            localStorage.removeItem('accessToken');
            localStorage.removeItem('refreshToken');
            localStorage.removeItem('user');
            window.location.href = '/login';
        } else if (error.response?.status === 403) {
            showToast('No tienes permiso para realizar esta acción', 'error');
        } else if (error.response?.status === 404) {
            showToast('Recurso no encontrado', 'error');
        } else if (error.response?.status === 500) {
            showToast('Error interno del servidor', 'error');
        }
        return Promise.reject(error);
    }
);

// Exportar métodos de la API

const API = {
    // Auth
    register: (data) => apiClient.post('/auth/register', data),
    login: (data) => apiClient.post('/auth/login', data),
    refreshToken: (data) => apiClient.post('/auth/refresh', data),
    logout: (data) => apiClient.post('/auth/logout', data),
    
    // Jokes
    getAllJokes: (page = 0, size = 10, sort = 'createdAt', direction = 'DESC') => 
        apiClient.get(`/jokes?page=${page}&size=${size}&sort=${sort}&direction=${direction}`),
    getJokeById: (id) => apiClient.get(`/jokes/${id}`),
    getJokesByCategory: (categoryId, page = 0, size = 10) => 
        apiClient.get(`/jokes/category/${categoryId}?page=${page}&size=${size}`),
    createJoke: (data) => apiClient.post('/jokes', data),
    updateJoke: (id, data) => apiClient.put(`/jokes/${id}`, data),
    deleteJoke: (id) => apiClient.delete(`/jokes/${id}`),
    getJokeOfDay: () => apiClient.get('/jokes/daily/joke-of-day'),
    getWorstJokes: (page = 0, size = 10) => 
        apiClient.get(`/jokes/trending/worst?page=${page}&size=${size}`),
    
    // Categories
    getAllCategories: () => apiClient.get('/categories'),
    getCategoryById: (id) => apiClient.get(`/categories/${id}`),
    getCategoryByName: (name) => apiClient.get(`/categories/name/${name}`),
    
    // Comments
    getCommentsByJoke: (jokeId, page = 0, size = 10) => 
        apiClient.get(`/comments/joke/${jokeId}?page=${page}&size=${size}`),
    createComment: (data) => apiClient.post('/comments', data),
    updateComment: (id, data) => apiClient.put(`/comments/${id}`, data),
    deleteComment: (id) => apiClient.delete(`/comments/${id}`),
    
    // Votes
    voteOnJoke: (jokeId, data) => apiClient.post(`/votes/jokes/${jokeId}`, data),
    voteOnComment: (commentId, data) => apiClient.post(`/votes/comments/${commentId}`, data),
    
    // Users
    getUserById: (id) => apiClient.get(`/users/${id}`),
    getUserByUsername: (username) => apiClient.get(`/users/username/${username}`),
};

// Función auxiliar para manejar errores de API
function getErrorMessage(error) {
    if (error.response?.data?.message) {
        return error.response.data.message;
    } else if (error.response?.data?.validationErrors) {
        return Object.values(error.response.data.validationErrors).join(', ');
    } else if (error.message) {
        return error.message;
    }
    return 'Error desconocido';
}
