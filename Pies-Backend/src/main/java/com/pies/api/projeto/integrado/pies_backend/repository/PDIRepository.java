package com.pies.api.projeto.integrado.pies_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pies.api.projeto.integrado.pies_backend.model.PDI;

/**
 * Repositório para acesso aos dados dos PDIs no banco de dados.
 * 
 * Estende JpaRepository fornecendo operações CRUD básicas e permite
 * adicionar métodos customizados de consulta.
 */
public interface PDIRepository extends JpaRepository<PDI, String> {
    
    /**
     * Busca um PDI por ID, carregando o educando relacionado.
     * 
     * @param id Identificador único do PDI
     * @return Optional contendo o PDI se encontrado
     */
    @EntityGraph(attributePaths = {"educando"})
    @Query("SELECT p FROM PDI p WHERE p.id = :id")
    Optional<PDI> findByIdWithEducando(@Param("id") String id);
    
    /**
     * Lista todos os PDIs, carregando os educandos relacionados.
     * 
     * @return Lista de todos os PDIs com seus educandos
     */
    @EntityGraph(attributePaths = {"educando"})
    @Query("SELECT p FROM PDI p")
    List<PDI> findAllWithEducando();
    
    /**
     * Busca todos os PDIs de um educando específico.
     * 
     * @param educandoId ID do educando
     * @return Lista de PDIs do educando
     */
    @EntityGraph(attributePaths = {"educando"})
    List<PDI> findByEducandoId(String educandoId);
}

