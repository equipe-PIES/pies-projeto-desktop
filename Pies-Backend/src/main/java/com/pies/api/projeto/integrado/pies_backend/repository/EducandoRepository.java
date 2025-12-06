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
    List<Educando> findAllByTurmaId(String turmaId);
    
    /**
     * Busca educandos pelo nome (busca parcial, case insensitive).
     * 
     * @param nome Nome ou parte do nome do educando
     * @return Lista de educandos que contêm o nome informado
     */
    @Query("SELECT e FROM Educando e WHERE LOWER(e.nome) LIKE LOWER(CONCAT('%', :nome, '%'))")
    List<Educando> findByNomeContainingIgnoreCase(@Param("nome") String nome);
    
    /**
     * Busca educandos por nome e grau de escolaridade (filtro combinado).
     * 
     * @param nome Nome ou parte do nome do educando (pode ser null para ignorar)
     * @param escolaridade Grau de escolaridade (pode ser null para ignorar)
     * @return Lista de educandos que atendem aos critérios
     */
    @Query("SELECT e FROM Educando e WHERE " +
           "(:nome IS NULL OR :nome = '' OR LOWER(e.nome) LIKE LOWER(CONCAT('%', :nome, '%'))) AND " +
           "(:escolaridade IS NULL OR e.escolaridade = :escolaridade)")
    List<Educando> findByNomeAndEscolaridade(@Param("nome") String nome, 
                                             @Param("escolaridade") GrauEscolar escolaridade);
    
}
