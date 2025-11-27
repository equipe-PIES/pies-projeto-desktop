package com.pies.api.projeto.integrado.pies_backend.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.pies.api.projeto.integrado.pies_backend.model.Anamnese;

public interface AnamneseRepository extends JpaRepository<Anamnese, String> {
    Optional<Anamnese> findByEducandoId(String educandoId);
    boolean existsByEducandoId(String educandoId);
}

