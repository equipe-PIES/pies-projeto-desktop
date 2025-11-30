package com.pies.projeto.integrado.piesfront.dto;

public record CreateRelatorioIndividualDTO(
        String educandoId,
        String dadosFuncionais,
        String funcionalidadeCognitiva,
        String alfabetizacaoLetramento,
        String adaptacoesCurriculares,
        String participacaoAtividades,
        String autonomia,
        String interacaoProfessora,
        String atividadesVidaDiaria
) {}

