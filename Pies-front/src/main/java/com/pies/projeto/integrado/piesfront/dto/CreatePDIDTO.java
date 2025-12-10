package com.pies.projeto.integrado.piesfront.dto;

import java.util.List;

public record CreatePDIDTO(
        String periodoPlanoAEE,
        String horarioTempoAtendimento,
        String frequenciaAtendimento,
        List<String> diasSemana,
        String composicaoAtendimento,
        String objetivosPlano,
        String potencialidades,
        String necessidadesEducacionaisEspeciais,
        String habilidades,
        String atividadesASeremDesenvolvidas,
        String recursosMateriais,
        String recursosQueNecessitamAdequacao,
        String recursosMateriaisASeremProduzidos,
        String parceriasNecessarias,
        String professorId,
        String educandoId
) {}
