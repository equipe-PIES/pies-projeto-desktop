package com.pies.projeto.integrado.piesfront.dto;

import java.time.LocalDate;

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
    String escolaridade  // Enum serializado como String
) {}

