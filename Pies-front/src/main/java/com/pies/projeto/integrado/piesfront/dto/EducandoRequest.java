package com.pies.projeto.integrado.piesfront.dto;

import java.util.List;

public record EducandoRequest(
    String nome,
    String cpf,
    String dataNascimento,
    String genero,
    String cid,
    String nis,
    String escola,
    String escolaridade,
    String observacao,
    ResponsavelRequest responsavel
) {}
