package com.pies.api.projeto.integrado.pies_backend.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.pies.api.projeto.integrado.pies_backend.model.Endereco;

public interface EnderecoRepository extends JpaRepository<Endereco, String> {
    
    List<Endereco> findAllByCidade(String cidade);
    
    List<Endereco> findAllByBairro(String bairro);

    List<Endereco> findAllByCep(String cep);
    
}