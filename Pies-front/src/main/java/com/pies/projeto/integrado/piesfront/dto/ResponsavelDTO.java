package com.pies.projeto.integrado.piesfront.dto;

/**
 * DTO para informações de responsável
 */
public record ResponsavelDTO(
    String id,
    String nome,
    String cpf,
    String contato,
    String parentesco,
    String outroParentesco,
    EnderecoDTO endereco
) {}
