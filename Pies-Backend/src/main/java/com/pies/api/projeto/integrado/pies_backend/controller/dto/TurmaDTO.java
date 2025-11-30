package com.pies.api.projeto.integrado.pies_backend.controller.dto;

import com.pies.api.projeto.integrado.pies_backend.model.Enums.GrauEscolar;
import com.pies.api.projeto.integrado.pies_backend.model.Enums.Turno;

/**
 * DTO para representar uma turma completa com todos os seus dados.
 * Inclui informações do professor responsável e lista de educandos.
 */
public record TurmaDTO(
    String id,
    String nome,
    GrauEscolar grauEscolar,
    String faixaEtaria,
    Turno turno,
    String professorId,
    String professorNome,
    String professorCpf
) {}
