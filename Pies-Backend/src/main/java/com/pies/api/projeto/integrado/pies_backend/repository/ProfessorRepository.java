package com.pies.api.projeto.integrado.pies_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pies.api.projeto.integrado.pies_backend.model.Enums.Genero;
import com.pies.api.projeto.integrado.pies_backend.model.Professor;

public interface ProfessorRepository extends JpaRepository<Professor, String> {

    // 1. Listar Todos
    @Query("SELECT DISTINCT p FROM Professor p LEFT JOIN FETCH p.turmas ORDER BY p.nome ASC")
    List<Professor> findAllCompleto();

    // 2. Buscar por ID
    @Query("SELECT p FROM Professor p LEFT JOIN FETCH p.turmas WHERE p.id = :id")
    Optional<Professor> findByIdCompleto(@Param("id") String id);

    // 3. Buscar por User ID
    @Query("SELECT p FROM Professor p LEFT JOIN FETCH p.turmas WHERE p.userId = :userId")
    Optional<Professor> findByUserIdCompleto(@Param("userId") String userId);

    // Métodos simples 
    boolean existsByCpf(String cpf);
    Professor findByUserId(String userId);
    List<Professor> findAllByOrderByNomeAsc();
    List<Professor> findByNomeContainingIgnoreCase(String nome);
    List<Professor> findByGenero(Genero genero);
    List<Professor> findByFormacao(String formacao);
}