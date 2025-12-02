package com.pies.api.projeto.integrado.pies_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pies.api.projeto.integrado.pies_backend.model.DiagnosticoInicial;

public interface DiagnosticoInicialRepository extends JpaRepository<DiagnosticoInicial, String> {

    @EntityGraph(attributePaths = {"educando", "professor"})
    @Query("SELECT d FROM DiagnosticoInicial d WHERE d.id = :id")
    Optional<DiagnosticoInicial> findByIdWithRelations(@Param("id") String id);

    @EntityGraph(attributePaths = {"educando", "professor"})
    @Query("SELECT d FROM DiagnosticoInicial d WHERE d.educando.id = :educandoId")
    Optional<DiagnosticoInicial> findByEducandoId(@Param("educandoId") String educandoId);

    @EntityGraph(attributePaths = {"educando", "professor"})
    @Query("SELECT d FROM DiagnosticoInicial d WHERE d.professor.id = :professorId ORDER BY d.dataCriacao DESC")
    List<DiagnosticoInicial> findByProfessorId(@Param("professorId") String professorId);

    boolean existsByEducandoId(String educandoId);
}
