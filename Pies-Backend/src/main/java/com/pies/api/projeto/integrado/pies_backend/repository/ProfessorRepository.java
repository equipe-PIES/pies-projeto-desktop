package com.pies.api.projeto.integrado.pies_backend.repository;

// Imports para tipos de retorno
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pies.api.projeto.integrado.pies_backend.model.Professor;

/**
 * REPOSITORY LAYER - CAMADA DE ACESSO A DADOS
 * 
 * Interface que define métodos para operações de banco de dados relacionadas ao Professor.
 * Estende JpaRepository que fornece operações CRUD básicas automaticamente.
 * 
 * JpaRepository<Professor, String> significa:
 * - Professor: entidade gerenciada
 * - String: tipo da chave primária (id)
 * 
 * O Spring Data JPA gera automaticamente a implementação desta interface.
 */
public interface ProfessorRepository extends JpaRepository<Professor, String> {
    
    // ========== MÉTODOS DE BUSCA AUTOMÁTICOS ==========
    // O Spring Data JPA interpreta o nome do método e gera a query automaticamente
    
    /**
     * Busca professor pelo CPF
     * 
     * Spring Data JPA gera automaticamente:
     * SELECT * FROM professores WHERE cpf = ?
     * 
     * @param cpf CPF do professor a ser buscado
     * @return Optional contendo o professor se encontrado, vazio caso contrário
     */
    Optional<Professor> findByCpf(String cpf);
    
    /**
     * Verifica se existe um professor com o CPF informado
     * 
     * Spring Data JPA gera automaticamente:
     * SELECT COUNT(*) > 0 FROM professores WHERE cpf = ?
     * 
     * Usado para validação de CPF único antes de criar/atualizar professores
     * 
     * @param cpf CPF a ser verificado
     * @return true se existe um professor com este CPF, false caso contrário
     */
    boolean existsByCpf(String cpf);
    
    /**
     * Busca professores por gênero
     * 
     * Spring Data JPA gera automaticamente:
     * SELECT * FROM professores WHERE genero = ?
     * 
     * Útil para relatórios e filtros por gênero
     * 
     * @param genero Gênero dos professores a serem buscados
     * @return Lista de professores do gênero informado
     */
    List<Professor> findByGenero(String genero);
    
    /**
     * Busca professores por formação
     * 
     * Spring Data JPA gera automaticamente:
     * SELECT * FROM professores WHERE formacao = ?
     * 
     * Permite filtrar professores por nível de formação (ex: "Mestrado", "Doutorado")
     * 
     * @param formacao Formação dos professores a serem buscados
     * @return Lista de professores com a formação informada
     */
    List<Professor> findByFormacao(String formacao);
    
    /**
     * Lista todos os professores ordenados por nome
     * 
     * Spring Data JPA gera automaticamente:
     * SELECT * FROM professores ORDER BY nome ASC
     * 
     * @return Lista de todos os professores ordenados alfabeticamente por nome
     */
    List<Professor> findAllByOrderByNomeAsc();
    
    // ========== MÉTODOS COM QUERY CUSTOMIZADA ==========
    
    /**
     * Busca professores pelo nome (busca parcial, case insensitive)
     * 
     * Query JPQL personalizada para busca flexível:
     * - LOWER() converte para minúsculas (case insensitive)
     * - LIKE com % permite busca parcial
     * - CONCAT('%', :nome, '%') adiciona % antes e depois do termo
     * 
     * Exemplos de uso:
     * - busca "joão" encontra "João Silva", "Maria João", "João Pedro"
     * - busca "silva" encontra "João Silva", "Maria Silva Santos"
     * 
     * @param nome Nome ou parte do nome do professor
     * @return Lista de professores que contêm o nome informado
     */
    @Query("SELECT p FROM Professor p WHERE LOWER(p.nome) LIKE LOWER(CONCAT('%', :nome, '%'))")
    List<Professor> findByNomeContainingIgnoreCase(@Param("nome") String nome);
    
    Professor findByUserId(String userId);
}