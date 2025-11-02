package com.pies.api.projeto.integrado.pies_backend.model.Enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum UserRole {
    ADMIN("admin"),
    USER("user"),
    PROFESSOR("professor"),
    COORDENADOR("coordenador");

    private final String role;

    UserRole(String role){
        this.role = role;
    }

    @JsonValue
    public String getRole(){
        return role;
    }

    @JsonCreator
    public static UserRole fromString(String value) {
        if (value == null) {
            return null;
        }
        
        // Tenta encontrar pelo valor customizado (admin, user)
        for (UserRole role : UserRole.values()) {
            if (role.role.equalsIgnoreCase(value)) {
                return role;
            }
        }
        
        // Tenta encontrar pelo nome do enum (ADMIN, USER)
        try {
            return UserRole.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role: " + value + ". Valid roles are: admin, user, professor, coordenador, ADMIN, USER, PROFESSOR, COORDENADOR");
        }
    }
}