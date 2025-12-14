package com.pies.api.projeto.integrado.pies_backend.repository;

import com.pies.api.projeto.integrado.pies_backend.model.PAEE;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PAEERepository extends JpaRepository<PAEE, String> {
    
    // Método para buscar o histórico de PAEEs de um aluno
    List<PAEE> findByAlunoId(String alunoId);
    
    // Método para buscar PAEEs feitos por um professor
    List<PAEE> findByProfessorId(String professorId);
}