package com.pies.api.projeto.integrado.pies_backend.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.pies.api.projeto.integrado.pies_backend.model.Educando;
import com.pies.api.projeto.integrado.pies_backend.model.Enums.Genero;
import com.pies.api.projeto.integrado.pies_backend.model.Enums.GrauEscolar;


public interface EducandoRepository extends JpaRepository<Educando, String> {
    
    /**
     * Busca um educando por CPF, carregando os responsáveis.
     */
    @EntityGraph(attributePaths = {"responsaveis", "responsaveis.endereco"})
    Optional<Educando> findByCpf(String cpf);
    
    boolean existsByCpf(String cpf);
    
    /**
     * Busca um educando por ID, carregando os responsáveis.
     */
    @EntityGraph(attributePaths = {"responsaveis", "responsaveis.endereco"})
    @Query("SELECT e FROM Educando e WHERE e.id = :id")
    Optional<Educando> findByIdWithResponsaveis(@Param("id") String id);
    
    /**
     * Lista todos os educandos, carregando os responsáveis.
     */
    @EntityGraph(attributePaths = {"responsaveis", "responsaveis.endereco"})
    @Query("SELECT e FROM Educando e")
    List<Educando> findAllWithResponsaveis();
    
    List<Educando> findAllByGenero(Genero genero);
    
    List<Educando> findAllByEscola(String escola);

    List<Educando> findAllByEscolaridade(GrauEscolar escolaridade);

    List<Educando> findAllByCid(String cid);
    
    List<Educando> findAllByOrderByNomeAsc();
    
}