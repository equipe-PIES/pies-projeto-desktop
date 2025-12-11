package com.pies.api.projeto.integrado.pies_backend.controller.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PAEEDTO {
    // Campos de metadados
    private String id;
    private String alunoId;
    private String professorId;
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