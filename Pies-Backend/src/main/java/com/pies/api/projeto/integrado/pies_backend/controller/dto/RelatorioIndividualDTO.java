package com.pies.api.projeto.integrado.pies_backend.controller.dto;

import java.time.LocalDateTime;

/**
 * DTO para representar um Relatório Individual completo.
 * 
 * Inclui todas as informações do relatório, incluindo dados do educando
 * e do professor que o criou, além da data de criação.
 */
public record RelatorioIndividualDTO(
    /**
     * ID único do relatório.
     */
    String id,
    
    /**
     * ID do educando ao qual o relatório se refere.
     */
    String educandoId,
    
    /**
     * Nome do educando.
     */
    String educandoNome,
    
    /**
     * ID do professor que criou o relatório.
     */
    String professorId,
    
    /**
     * Nome do professor que criou o relatório.
     */
    String professorNome,
    
    /**
     * Data e hora de criação do relatório.
     */
    LocalDateTime dataCriacao,
    
    /**
     * Dados Funcionais.
     */
    String dadosFuncionais,
    
    /**
     * Funcionalidade Cognitiva.
     */
    String funcionalidadeCognitiva,
    
    /**
     * Alfabetização e Letramento.
     */
    String alfabetizacaoLetramento,
    
    /**
     * Adaptações Curriculares.
     */
    String adaptacoesCurriculares,
    
    /**
     * Participação nas Atividades Propostas.
     */
    String participacaoAtividades,
    
    /**
     * Autonomia.
     */
    String autonomia,
    
    /**
     * Interação com a Professora.
     */
    String interacaoProfessora,
    
    /**
     * Atividades de Vida Diária (AVDs).
     */
    String atividadesVidaDiaria
) {}

