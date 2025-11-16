package com.pies.projeto.integrado.piesfront.dto;

/**
 * DTO para informações de turma
 * Representa a resposta do endpoint /turmas
 */
public record TurmaDTO(
    String id,
    String nome,
    String grauEscolar,  // Enum serializado como String
    String FaixaEtaria,  // Nome exato do backend (com F maiúsculo)
    String turno,        // Enum serializado como String
    String professorId,
    String professorNome
) {}

