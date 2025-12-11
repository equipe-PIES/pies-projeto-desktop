package com.pies.api.projeto.integrado.pies_backend.controller.dto;

import lombok.Data;

@Data
public class CreatePAEEDTO {

    private String alunoId;
    private String professorId;
    
    // Tela 1
    private String resumoCaso;
    private Boolean apresentaDificuldadeMotora;
    private Boolean apresentaDificuldadeCognitiva;
    private Boolean apresentaDificuldadeSensorial;
    private Boolean apresentaDificuldadeLinguagem;
    private Boolean apresentaDificuldadeFamiliar;
    private Boolean apresentaDificuldadeAfetiva;
    private Boolean apresentaDificuldadeLogica;
    private Boolean apresentaDificuldadeAVA;

    // Telas de Áreas (Os nomes devem ser IDÊNTICOS aos da Entity PAEE)
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

    // Tela 6
    private String objetivosGerais;
    private Boolean apoioAEE;
    private Boolean apoioPsicologo;
    private Boolean apoioFisioterapeuta;
    private Boolean apoioPsicopedagogo;
    private Boolean apoioTO;
    private Boolean apoioEdFisica;
    private Boolean apoioEstimulacaoPrecoce;
}