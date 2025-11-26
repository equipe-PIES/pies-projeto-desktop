package com.pies.api.projeto.integrado.pies_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.pies.api.projeto.integrado.pies_backend.model.Turma;
import com.pies.api.projeto.integrado.pies_backend.model.Professor;
import java.util.List;


public interface TurmaRepository extends JpaRepository<Turma, String> { //Acesso ao banco (CRUD automático)
    
    /**
     * Busca todas as turmas de um professor específico
     * @param professor Professor para buscar as turmas
     * @return Lista de turmas do professor
     */
    List<Turma> findByProfessor(Professor professor);

}
