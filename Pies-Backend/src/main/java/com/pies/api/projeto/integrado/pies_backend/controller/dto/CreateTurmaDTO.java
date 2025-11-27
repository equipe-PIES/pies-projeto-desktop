package com.pies.api.projeto.integrado.pies_backend.controller.dto;

import com.pies.api.projeto.integrado.pies_backend.model.Enums.GrauEscolar;
import com.pies.api.projeto.integrado.pies_backend.model.Enums.Turno;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record CreateTurmaDTO(
    @NotBlank String nome,
    @NotNull GrauEscolar grauEscolar,
    @NotBlank String faixaEtaria,
    @NotNull Turno turno,
    @NotBlank String professorId,
    List<String> cpfsAlunos  // Lista de CPFs dos alunos a serem vinculados
) {}
