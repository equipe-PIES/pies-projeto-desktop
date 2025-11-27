package com.pies.projeto.integrado.piesfront.dto;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO para informações de educando (aluno)
 * Representa a resposta do endpoint /api/educandos
 */
public record EducandoDTO(
    String id,
    String nome,
    String cpf,
    LocalDate dataNascimento,
    String genero,  // Enum serializado como String
    String cid,
    String nis,
    String escola,
    String escolaridade,  // Enum serializado como String
    String observacao,  // Observações adicionais
    String turmaId,  // ID da turma vinculada
    List<ResponsavelDTO> responsaveis,  // Lista de responsáveis
    AnamneseDTO anamnese  // Dados da anamnese do educando
) {}

