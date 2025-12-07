package com.pies.projeto.integrado.piesfront.dto;

import java.time.LocalDate;

/**
 * DTO para informações de educando (aluno)
 * Representa a resposta do endpoint /api/educandos
 * 
 * Mudanças:
 * - Removido List<ResponsavelDTO> - responsavelDTO é agora um objeto único
 * - Responsável agora é acessado através de ResponsavelDTO singular
 * - Anamnese continua como um objeto único associado ao educando
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
    ResponsavelDTO responsavel,  // Responsável único (OneToOne)
    AnamneseDTO anamnese  // Dados da anamnese do educando (OneToOne)
) {}

