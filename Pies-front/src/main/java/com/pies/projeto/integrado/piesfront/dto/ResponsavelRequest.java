package com.pies.projeto.integrado.piesfront.dto;

public record ResponsavelRequest(
    String nome,
    String cpf,
    String contato,
    String parentesco,
    String outroParentesco,
    EnderecoRequest endereco
) {}