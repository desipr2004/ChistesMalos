package com.chistesmalos.security;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * Sanitizador de inputs para prevenir XSS.
 */
@Slf4j
@Component
public class InputSanitizer {

    // Patrones para detectar y limpiar posibles ataques XSS
    private static final Pattern SCRIPT_PATTERN = Pattern.compile(
            "<script[^>]*>.*?</script>",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL
    );

    private static final Pattern ON_EVENT_PATTERN = Pattern.compile(
            "\\s*on\\w+\\s*=",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern JAVASCRIPT_PROTOCOL = Pattern.compile(
            "javascript:",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern DATA_PROTOCOL = Pattern.compile(
            "data:text/html",
            Pattern.CASE_INSENSITIVE
    );

    /**
     * Sanitizar input de usuario.
     */
    public String sanitize(String input) {
        if (StringUtils.isBlank(input)) {
            return input;
        }

        String sanitized = input;

        // Remover scripts
        sanitized = SCRIPT_PATTERN.matcher(sanitized).replaceAll("");

        // Remover event handlers (onclick, onload, etc.)
        sanitized = ON_EVENT_PATTERN.matcher(sanitized).replaceAll("");

        // Remover javascript: protocol
        sanitized = JAVASCRIPT_PROTOCOL.matcher(sanitized).replaceAll("");

        // Remover data:text/html protocol
        sanitized = DATA_PROTOCOL.matcher(sanitized).replaceAll("");

        // HTML encode caracteres especiales peligrosos
        sanitized = sanitized
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");

        // Restaurar caracteres HTML válidos si es necesario
        sanitized = sanitized
                .replace("&amp;lt;", "&lt;")
                .replace("&amp;gt;", "&gt;");

        return sanitized;
    }

    /**
     * Verificar si un input contiene patrones sospechosos.
     */
    public boolean isSuspicious(String input) {
        if (StringUtils.isBlank(input)) {
            return false;
        }

        return SCRIPT_PATTERN.matcher(input).find()
                || ON_EVENT_PATTERN.matcher(input).find()
                || JAVASCRIPT_PROTOCOL.matcher(input).find()
                || DATA_PROTOCOL.matcher(input).find()
                || input.toLowerCase().contains("exec(")
                || input.toLowerCase().contains("eval(");
    }
}
