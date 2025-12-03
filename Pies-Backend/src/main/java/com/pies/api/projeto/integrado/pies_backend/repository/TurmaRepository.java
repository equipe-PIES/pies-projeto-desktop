package com.pies.api.projeto.integrado.pies_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pies.api.projeto.integrado.pies_backend.model.Enums.GrauEscolar;
import com.pies.api.projeto.integrado.pies_backend.model.Professor;
import com.pies.api.projeto.integrado.pies_backend.model.Turma;

public interface TurmaRepository extends JpaRepository<Turma, String> { //Acesso ao banco (CRUD automático)
    
    /**
     * Busca todas as turmas de um professor específico
     * @param professor Professor para buscar as turmas
     * @return Lista de turmas do professor
     */
    List<Turma> findByProfessor(Professor professor);
    
    /**
     * Busca turmas pelo nome (busca parcial, case insensitive).
     * 
     * @param nome Nome ou parte do nome da turma
     * @return Lista de turmas que contêm o nome informado
     */
    @Query("SELECT t FROM Turma t WHERE LOWER(t.nome) LIKE LOWER(CONCAT('%', :nome, '%'))")
    List<Turma> findByNomeContainingIgnoreCase(@Param("nome") String nome);
    
    /**
     * Busca turmas por nome, professor e grau de escolaridade (filtro combinado).
     * 
     * @param nome Nome ou parte do nome da turma (pode ser null ou vazio para ignorar)
     * @param professorId ID do professor (pode ser null para ignorar)
     * @param grauEscolar Grau de escolaridade (pode ser null para ignorar)
     * @return Lista de turmas que atendem aos critérios
     */
    @Query("SELECT t FROM Turma t WHERE " +
           "(:nome IS NULL OR :nome = '' OR LOWER(t.nome) LIKE LOWER(CONCAT('%', :nome, '%'))) AND " +
           "(:professorId IS NULL OR t.professor.id = :professorId) AND " +
           "(:grauEscolar IS NULL OR t.grauEscolar = :grauEscolar)")
    List<Turma> findByNomeAndProfessorAndGrauEscolar(@Param("nome") String nome,
                                                      @Param("professorId") String professorId,
                                                      @Param("grauEscolar") GrauEscolar grauEscolar);

}
