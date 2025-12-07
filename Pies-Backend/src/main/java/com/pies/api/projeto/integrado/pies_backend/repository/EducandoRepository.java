package com.pies.api.projeto.integrado.pies_backend.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.pies.api.projeto.integrado.pies_backend.model.Educando;
import com.pies.api.projeto.integrado.pies_backend.model.Enums.Genero;
import com.pies.api.projeto.integrado.pies_backend.model.Enums.GrauEscolar;

public interface EducandoRepository extends JpaRepository<Educando, String> {

    @Query("""
        SELECT DISTINCT e 
        FROM Educando e 
        LEFT JOIN FETCH e.turmas 
        LEFT JOIN FETCH e.responsavel
        ORDER BY e.nome ASC
        """)
    List<Educando> findAllCompleto();

    @Query("""
        SELECT e 
        FROM Educando e 
        LEFT JOIN FETCH e.turmas 
        LEFT JOIN FETCH e.responsavel 
        WHERE e.id = :id
        """)
    Optional<Educando> findByIdCompleto(@Param("id") String id);

    @Query("""
        SELECT DISTINCT e 
        FROM Educando e 
        LEFT JOIN FETCH e.responsavel 
        JOIN e.turmas t 
        WHERE t.id = :turmaId
        ORDER BY e.nome ASC
        """)
    List<Educando> findAllByTurmaIdCompleto(@Param("turmaId") String turmaId);
    
    boolean existsByCpf(String cpf);
    
    Optional<Educando> findByCpf(String cpf);

    List<Educando> findAllByGenero(Genero genero);

    List<Educando> findAllByEscola(String escola);

    List<Educando> findAllByEscolaridade(GrauEscolar escolaridade);

    List<Educando> findAllByCid(String cid);
}