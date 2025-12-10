package com.pies.api.projeto.integrado.pies_backend.exception;

/**
 * Exceção lançada quando um professor não é encontrado no sistema.
 */
public class ProfessorNotFoundException extends RuntimeException {
    public ProfessorNotFoundException(String id) {
        super("Professor não encontrado com ID: " + id);
    }
}
