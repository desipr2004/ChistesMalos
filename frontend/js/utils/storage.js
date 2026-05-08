// LocalStorage Utilities

const STORAGE = {
    // Guardar datos
    set: (key, value) => {
        localStorage.setItem(key, JSON.stringify(value));
    },

    // Obtener datos
    get: (key) => {
        const value = localStorage.getItem(key);
        return value ? JSON.parse(value) : null;
    },

    // Eliminar datos
    remove: (key) => {
        localStorage.removeItem(key);
    },

    // Limpiar todo
    clear: () => {
        localStorage.clear();
    },

    // Guardar preferencias de usuario
    setUserPreferences: (preferences) => {
        STORAGE.set('userPreferences', preferences);
    },

    // Obtener preferencias
    getUserPreferences: () => {
        return STORAGE.get('userPreferences') || {
            theme: 'light',
            notifications: true,
            language: 'es'
        };
    }
};
