package com.pies.api.projeto.integrado.pies_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pies.api.projeto.integrado.pies_backend.model.PAEE;

/**
 * Repositório para acesso aos dados dos PAEEs no banco de dados.
 * 
 * Estende JpaRepository fornecendo operações CRUD básicas e permite
 * adicionar métodos customizados de consulta.
 */
public interface PAEERepository extends JpaRepository<PAEE, String> {
    
    /**
     * Busca um PAEE por ID, carregando o educando relacionado.
     * 
     * @param id Identificador único do PAEE
     * @return Optional contendo o PAEE se encontrado
     */
    @EntityGraph(attributePaths = {"educando", "professor"})
    @Query("SELECT p FROM PAEE p WHERE p.id = :id")
    Optional<PAEE> findByIdWithEducando(@Param("id") String id);
    
    /**
     * Lista todos os PAEEs, carregando os educandos relacionados.
     * 
     * @return Lista de todos os PAEEs com seus educandos
     */
    @EntityGraph(attributePaths = {"educando", "professor"})
    @Query("SELECT p FROM PAEE p")
    List<PAEE> findAllWithEducando();
    
    /**
     * Busca todos os PAEEs de um educando específico.
     * 
     * @param educandoId ID do educando
     * @return Lista de PAEEs do educando
     */
    @EntityGraph(attributePaths = {"educando", "professor"})
    List<PAEE> findByEducandoId(String educandoId);
}

