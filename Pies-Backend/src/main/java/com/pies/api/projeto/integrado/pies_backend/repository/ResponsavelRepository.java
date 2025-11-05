package com.pies.api.projeto.integrado.pies_backend.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.pies.api.projeto.integrado.pies_backend.model.Responsavel;

public interface ResponsavelRepository extends JpaRepository<Responsavel, String> {
    
   
    Optional<Responsavel> findByCpf(String cpf);
    
    boolean existsByCpf(String cpf);
    
    List<Responsavel> findAllByEducando_id(String id);
    
    List<Responsavel> findAllByOrderByNomeAsc();
    
}