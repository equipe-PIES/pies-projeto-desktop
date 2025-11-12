package com.pies.projeto.integrado.piesfront.dto;

public record EnderecoRequest(
    String uf,
    String cidade,
    String cep,
    String rua,
    String numero,
    String bairro,
    String complemento
) {}