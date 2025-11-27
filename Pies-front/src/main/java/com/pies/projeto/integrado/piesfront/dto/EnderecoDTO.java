package com.pies.projeto.integrado.piesfront.dto;

/**
 * DTO para informações de endereço
 */
public record EnderecoDTO(
    String id,
    String cep,
    String uf,
    String cidade,
    String bairro,
    String rua,
    String numero,
    String complemento
) {}
