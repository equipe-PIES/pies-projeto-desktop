package com.pies.api.projeto.integrado.pies_backend.controller.dto;

import lombok.Data;

@Data

public class EnderecoDTO {
    private String id;
    private String cep;
    private String uf;
    private String cidade;
    private String bairro;
    private String rua;
    private String numero;
    private String complemento;
}
