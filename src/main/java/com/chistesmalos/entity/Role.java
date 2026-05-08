package com.chistesmalos.entity;

/**
 * Enum para los roles de usuario en la plataforma.
 */
public enum Role {
    USER("ROLE_USER", "Usuario regular de la plataforma"),
    ADMIN("ROLE_ADMIN", "Administrador del sistema"),
    MODERATOR("ROLE_MODERATOR", "Moderador de contenido");

    private final String authority;
    private final String description;

    Role(String authority, String description) {
        this.authority = authority;
        this.description = description;
    }

    public String getAuthority() {
        return authority;
    }

    public String getDescription() {
        return description;
    }
}
