package com.pies.api.projeto.integrado.pies_backend.exception;

/**
 * Exceção lançada quando um PAEE não é encontrado no sistema.
 * 
 * Esta exceção é usada para indicar que uma operação foi tentada em um PAEE
 * que não existe no banco de dados, facilitando o tratamento de erros
 * e retornando respostas HTTP apropriadas (404 Not Found).
 */
public class PAEENotFoundException extends RuntimeException {
    
    /**
     * Construtor que recebe o ID do PAEE não encontrado.
     * 
     * @param id ID do PAEE que não foi encontrado
     */
    public PAEENotFoundException(String id) {
        super("PAEE não encontrado com ID: " + id);
    }
}

