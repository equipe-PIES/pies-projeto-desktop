package com.pies.api.projeto.integrado.pies_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pies.api.projeto.integrado.pies_backend.model.Professor;
import com.pies.api.projeto.integrado.pies_backend.model.Turma;

public interface TurmaRepository extends JpaRepository<Turma, String> {

    // -------------------------------------------------------------------
    // 1. LISTAGEM GERAL (Para a tela de "Todas as Turmas")
    // Traz a Turma + O Professor responsável + Educandos em uma única consulta.
    // -------------------------------------------------------------------
    @Query("""
        SELECT DISTINCT t 
        FROM Turma t 
        LEFT JOIN FETCH t.professor 
        LEFT JOIN FETCH t.educandos 
        ORDER BY t.nome ASC
        """)
    List<Turma> findAllCompleto();

    // -------------------------------------------------------------------
    // 2. BUSCA POR ID (Para "Entrar" na turma / Detalhes)
    // Traz Turma + Professor + Lista de Alunos (Educandos)
    // -------------------------------------------------------------------
    @Query("""
        SELECT t 
        FROM Turma t 
        LEFT JOIN FETCH t.professor 
        LEFT JOIN FETCH t.educandos e
        LEFT JOIN FETCH e.responsavel r
        LEFT JOIN FETCH r.endereco
        WHERE t.id = :id
        """)
    Optional<Turma> findByIdCompleto(@Param("id") String id);

    // -------------------------------------------------------------------
    // 3. BUSCA POR PROFESSOR (Para a tela "Turmas" do Professor)
    // -------------------------------------------------------------------
    
    // Opção A: Apenas as turmas (Rápido) - Use se só for mostrar o nome da turma
    List<Turma> findByProfessorOrderByNomeAsc(Professor professor);

    // Opção B: Turmas + Alunos (Mais pesado, mas completo) - Use se for mostrar "Turma A (20 alunos)"
    @Query("""
        SELECT DISTINCT t 
        FROM Turma t 
        LEFT JOIN FETCH t.educandos 
        WHERE t.professor = :professor 
        ORDER BY t.nome ASC
        """)
    List<Turma> findByProfessorComAlunos(@Param("professor") Professor professor);
}