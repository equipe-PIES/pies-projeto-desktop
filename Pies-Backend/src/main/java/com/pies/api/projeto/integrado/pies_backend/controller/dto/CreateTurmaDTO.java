package com.pies.api.projeto.integrado.pies_backend.controller.dto;

import com.pies.api.projeto.integrado.pies_backend.model.Enums.GrauEscolar;
import com.pies.api.projeto.integrado.pies_backend.model.Enums.Turno;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateTurmaDTO(
    @NotBlank String nome,//n é nulo, n é espaço,n é vazio
    @NotNull GrauEscolar grauEscolar,//so nulo
    @NotBlank String faixaEtaria,
    @NotNull Turno turno,
    @NotBlank String professorId
) {}
