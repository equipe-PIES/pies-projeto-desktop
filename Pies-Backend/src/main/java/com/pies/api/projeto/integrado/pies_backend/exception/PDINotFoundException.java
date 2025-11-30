package com.pies.api.projeto.integrado.pies_backend.exception;

/**
 * Exceção lançada quando um PDI não é encontrado no sistema.
 * 
 * Esta exceção é usada para indicar que uma operação foi tentada em um PDI
 * que não existe no banco de dados, facilitando o tratamento de erros
 * e retornando respostas HTTP apropriadas (404 Not Found).
 */
public class PDINotFoundException extends RuntimeException {
    
    /**
     * Construtor que recebe o ID do PDI não encontrado.
     * 
     * @param id ID do PDI que não foi encontrado
     */
    public PDINotFoundException(String id) {
        super("PDI não encontrado com ID: " + id);
    }
}

