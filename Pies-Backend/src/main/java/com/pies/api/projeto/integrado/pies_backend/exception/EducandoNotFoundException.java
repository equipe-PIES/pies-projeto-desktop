package com.pies.api.projeto.integrado.pies_backend.exception;

/**
 * Exceção lançada quando um educando não é encontrado no sistema.
 * 
 * Esta exceção é usada para casos específicos onde um educando é buscado
 * por ID mas não existe no banco de dados.
 */
public class EducandoNotFoundException extends RuntimeException {
    
    public EducandoNotFoundException(String id) {
        super("Educando não encontrado com ID: " + id);
    }
    
    public EducandoNotFoundException(String campo, String valor) {
        super("Educando não encontrado com " + campo + ": " + valor);
    }
}

