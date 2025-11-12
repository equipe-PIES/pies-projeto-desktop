package com.pies.api.projeto.integrado.pies_backend.exception;

/**
 * Exceção lançada quando um CPF já está cadastrado no sistema.
 * 
 * Esta exceção é usada para prevenir duplicação de CPF ao cadastrar
 * um novo educando.
 */
public class CpfJaCadastradoException extends RuntimeException {
    
    public CpfJaCadastradoException(String cpf) {
        super("CPF já cadastrado: " + cpf);
    }
}

