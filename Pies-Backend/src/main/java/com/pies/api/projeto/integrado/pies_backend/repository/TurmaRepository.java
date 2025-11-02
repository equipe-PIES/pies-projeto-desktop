package com.pies.api.projeto.integrado.pies_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.pies.api.projeto.integrado.pies_backend.model.Turma;


public interface TurmaRepository extends JpaRepository<Turma, String> { //Acesso ao banco (CRUD automático)

}
