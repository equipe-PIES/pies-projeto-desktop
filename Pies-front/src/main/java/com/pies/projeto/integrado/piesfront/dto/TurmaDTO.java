package com.pies.projeto.integrado.piesfront.dto;

/**
 * DTO para informações de turma
 * Representa a resposta do endpoint /turmas
 */
public record TurmaDTO(
    String id,
    String nome,
    String grauEscolar,
    String faixaEtaria,
    String turno,
    String professorId,
    String professorNome,
    String professorCpf
) {}

