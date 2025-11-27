package com.pies.api.projeto.integrado.pies_backend.exception;

public class AnamneseAlreadyExistsException extends RuntimeException {

    public AnamneseAlreadyExistsException(String educandoId) {
        super("Educando " + educandoId + " já possui anamnese cadastrada.");
    }
}

