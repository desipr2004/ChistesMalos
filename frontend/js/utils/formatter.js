// Utilidades de Formato

const FORMATTER = {
    // Formatear fecha
    formatDate: (date) => {
        const options = { year: 'numeric', month: 'long', day: 'numeric', hour: '2-digit', minute: '2-digit' };
        return new Date(date).toLocaleDateString('es-ES', options);
    },

    // Formatear fecha corta
    formatDateShort: (date) => {
        const options = { year: 'numeric', month: 'short', day: 'numeric' };
        return new Date(date).toLocaleDateString('es-ES', options);
    },

    // Tiempo relativo
    timeAgo: (date) => {
        const seconds = Math.floor((new Date() - new Date(date)) / 1000);
        let interval = seconds / 31536000;

        if (interval > 1) return Math.floor(interval) + " años atrás";
        interval = seconds / 2592000;
        if (interval > 1) return Math.floor(interval) + " meses atrás";
        interval = seconds / 86400;
        if (interval > 1) return Math.floor(interval) + " días atrás";
        interval = seconds / 3600;
        if (interval > 1) return Math.floor(interval) + " horas atrás";
        interval = seconds / 60;
        if (interval > 1) return Math.floor(interval) + " minutos atrás";
        return Math.floor(seconds) + " segundos atrás";
    },

    // Truncar texto
    truncate: (text, length = 100) => {
        return text.length > length ? text.substring(0, length) + '...' : text;
    },

    // Capitalizar primera letra
    capitalize: (str) => {
        return str.charAt(0).toUpperCase() + str.slice(1);
    },

    // Formatear números
    formatNumber: (num) => {
        return new Intl.NumberFormat('es-ES').format(num);
    }
};
