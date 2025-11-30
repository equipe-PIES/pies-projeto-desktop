package com.pies.projeto.integrado.piesfront.dto;

import java.time.LocalDateTime;

public record RelatorioIndividualDTO(
        String id,
        String educandoId,
        String educandoNome,
        String professorId,
        String professorNome,
        LocalDateTime dataCriacao,
        String dadosFuncionais,
        String funcionalidadeCognitiva,
        String alfabetizacaoLetramento,
        String adaptacoesCurriculares,
        String participacaoAtividades,
        String autonomia,
        String interacaoProfessora,
        String atividadesVidaDiaria
) {}

