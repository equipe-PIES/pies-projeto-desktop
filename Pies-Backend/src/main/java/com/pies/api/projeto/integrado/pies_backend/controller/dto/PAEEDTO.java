package com.pies.api.projeto.integrado.pies_backend.controller.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PAEEDTO {
    // Campos de metadados
    private String id;
    private String alunoId;
    private LocalDateTime dataCriacao;
    
    // --- Campos de Dados (Cópia do CreatePAEEDTO) ---
    
    private String resumoCaso;
    
    // Checkboxes
    private Boolean apresentaDificuldadeMotora;
    private Boolean apresentaDificuldadeCognitiva;
    private Boolean apresentaDificuldadeSensorial;
    private Boolean apresentaDificuldadeLinguagem;
    private Boolean apresentaDificuldadeFamiliar;
    private Boolean apresentaDificuldadeAfetiva;
    private Boolean apresentaDificuldadeLogica;
    private Boolean apresentaDificuldadeAVA;

    // Detalhes
    private String memoriaDificuldades;
    private String memoriaIntervencoes;
    private String percepcaoDificuldades;
    private String percepcaoIntervencoes;
    private String raciocinioLogicoDificuldades;
    private String raciocinioLogicoIntervencoes;
    private String atencaoConcentracaoDificuldades;
    private String atencaoConcentracaoIntervencoes;
    private String sociabilidadeDificuldades;
    private String sociabilidadeIntervencoes;
    private String avasDificuldades;
    private String avasIntervencoes;
    private String desenvolvimentoMotorDificuldades;
    private String desenvolvimentoMotorIntervencoes;
    private String comunicacaoLinguagemDificuldades;
    private String comunicacaoLinguagemIntervencoes;

    /**
     * Raciocínio - Dificuldades.
     */
    private String dificuldadesRaciocinio;

    /**
     * Raciocínio - Intervenções.
     */
    private String intervencoesRaciocinio;

    /**
     * Atenção - Dificuldades.
     */
    private String dificuldadesAtencao;

    /**
     * Atenção - Intervenções.
     */
    private String intervencoesAtencao;

    /**
     * Memória - Dificuldades.
     */
    private String dificuldadesMemoria;

    /**
     * Memória - Intervenções.
     */
    private String intervencoesMemoria;

    /**
     * Percepção - Dificuldades.
     */
    private String dificuldadesPercepcao;

    /**
     * Percepção - Intervenções.
     */
    private String intervencoesPercepcao;

    /**
     * Sociabilidade - Dificuldades.
     */
    private String dificuldadesSociabilidade;

    /**
     * Sociabilidade - Intervenções.
     */
    private String intervencoesSociabilidade;

    /**
     * AVA - Dificuldades.
     */
    private String dificuldadesAVA;

    /**
     * AVA - Intervenções.
     */
    private String intervencoesAVA;

    /**
     * Objetivos AEE.
     */
    private String objetivosAEE;

    /**
     * Encaminhamento AEE.
     */
    private String envAEE;

    /**
     * Encaminhamento Psicólogo.
     */
    private String envPsicologo;

    /**
     * Encaminhamento Fisioterapeuta.
     */
    private String envFisioterapeuta;

    /**
     * Encaminhamento Psicopedagogo.
     */
    private String envPsicopedagogo;

    /**
     * Encaminhamento TO (Terapeuta Ocupacional).
     */
    private String envTO;

    /**
     * Encaminhamento Educação Física.
     */
    private String envEducacaoFisica;

    /**
     * Encaminhamento Estimulação Precoce.
     */
    private String envEstimulacaoPrecoce;

    /**
     * ID do professor responsável.
     */
    private String professorId;

    /**
     * Nome do professor responsável.
     */
    private String professorNome;

    /**
     * ID do educando ao qual este PAEE pertence.
     */
    private String educandoId;

    /**
     * Nome do educando ao qual este PAEE pertence.
     * Incluído para facilitar a visualização na interface.
     */
    private String educandoNome;

    // Final
    private String objetivosGerais;
    private Boolean apoioAEE;
    private Boolean apoioPsicologo;
    private Boolean apoioFisioterapeuta;
    private Boolean apoioPsicopedagogo;
    private Boolean apoioTO;
    private Boolean apoioEdFisica;
    private Boolean apoioEstimulacaoPrecoce;

}