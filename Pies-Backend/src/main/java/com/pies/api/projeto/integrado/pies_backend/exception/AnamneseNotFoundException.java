package com.pies.api.projeto.integrado.pies_backend.exception;

public class AnamneseNotFoundException extends RuntimeException {

    public AnamneseNotFoundException(String identifier) {
        super("Anamnese não encontrada para referência: " + identifier);
    }
}

