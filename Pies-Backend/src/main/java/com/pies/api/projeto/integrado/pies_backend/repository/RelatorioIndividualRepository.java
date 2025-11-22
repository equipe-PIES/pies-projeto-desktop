package com.pies.api.projeto.integrado.pies_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pies.api.projeto.integrado.pies_backend.model.RelatorioIndividual;

/**
 * Repository para operações de persistência relacionadas a Relatórios Individuais.
 * 
 * Estende JpaRepository fornecendo operações CRUD básicas e permite
 * consultas customizadas com EntityGraph para carregamento eficiente
 * de relacionamentos.
 */
public interface RelatorioIndividualRepository extends JpaRepository<RelatorioIndividual, String> {
    
    /**
     * Busca um relatório por ID, carregando o educando e o professor.
     * 
     * @param id ID do relatório
     * @return Optional contendo o relatório se encontrado
     */
    @EntityGraph(attributePaths = {"educando", "professor"})
    @Query("SELECT r FROM RelatorioIndividual r WHERE r.id = :id")
    Optional<RelatorioIndividual> findByIdWithRelations(@Param("id") String id);
    
    /**
     * Lista todos os relatórios de um educando específico.
     * 
     * @param educandoId ID do educando
     * @return Lista de relatórios do educando, ordenados por data de criação (mais recente primeiro)
     */
    @EntityGraph(attributePaths = {"educando", "professor"})
    @Query("SELECT r FROM RelatorioIndividual r WHERE r.educando.id = :educandoId ORDER BY r.dataCriacao DESC")
    List<RelatorioIndividual> findByEducandoId(@Param("educandoId") String educandoId);
    
    /**
     * Lista todos os relatórios criados por um professor específico.
     * 
     * @param professorId ID do professor
     * @return Lista de relatórios criados pelo professor, ordenados por data de criação (mais recente primeiro)
     */
    @EntityGraph(attributePaths = {"educando", "professor"})
    @Query("SELECT r FROM RelatorioIndividual r WHERE r.professor.id = :professorId ORDER BY r.dataCriacao DESC")
    List<RelatorioIndividual> findByProfessorId(@Param("professorId") String professorId);
    
    /**
     * Busca o relatório mais recente de um educando.
     * 
     * @param educandoId ID do educando
     * @return Optional contendo o relatório mais recente se existir
     */
    @EntityGraph(attributePaths = {"educando", "professor"})
    @Query("SELECT r FROM RelatorioIndividual r WHERE r.educando.id = :educandoId ORDER BY r.dataCriacao DESC LIMIT 1")
    Optional<RelatorioIndividual> findMostRecentByEducandoId(@Param("educandoId") String educandoId);
    
    /**
     * Verifica se existe algum relatório para um educando específico.
     * 
     * @param educandoId ID do educando
     * @return true se existir pelo menos um relatório, false caso contrário
     */
    boolean existsByEducandoId(String educandoId);
}

