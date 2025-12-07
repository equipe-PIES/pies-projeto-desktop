package com.pies.projeto.integrado.piesfront.dto;

import java.util.List;

/**
 * DTO para informações de turma
 * Representa a resposta do endpoint /turmas
 * 
 * Inclui informações do professor responsável pela turma:
 * - professorId: ID único do professor
 * - professorNome: Nome completo do professor
 * - professorCpf: CPF do professor
 */
public record TurmaDTO(
    String id,
    String nome,
    String grauEscolar,
    String faixaEtaria,
    String turno,
    String professorId,
    String professorNome,
    String professorCpf,
    List<EducandoDTO> educandos
) {}

