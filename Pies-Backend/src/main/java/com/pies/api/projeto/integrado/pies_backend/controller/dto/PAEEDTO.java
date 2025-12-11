package com.pies.api.projeto.integrado.pies_backend.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO (Data Transfer Object) para transferência de dados do PAEE.
 * 
 * Esta classe é usada para enviar e receber dados do PAEE através da API REST,
 * separando a camada de apresentação da camada de persistência.
 * 
 * Inclui informações do educando relacionado para facilitar a visualização
 * na interface do usuário.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PAEEDTO {
    
    /**
     * Identificador único do PAEE.
     * Gerado automaticamente pelo sistema.
     */
    private String id;

    /**
     * Resumo de caso do educando.
     */
    private String resumoCaso;

    /**
     * Dificuldades apresentadas - Motores/Psicomotores.
     */
    private String dificuldadesMotoresPsicomotores;

    /**
     * Dificuldades apresentadas - Cognitivo.
     */
    private String dificuldadesCognitivo;

    /**
     * Dificuldades apresentadas - Sensorial.
     */
    private String dificuldadesSensorial;

    /**
     * Dificuldades apresentadas - Linguagem oral/Comunicação.
     */
    private String dificuldadesLinguagemComunicacao;

    /**
     * Dificuldades apresentadas - Familiar.
     */
    private String dificuldadesFamiliar;

    /**
     * Dificuldades apresentadas - Afetivo/Interpessoais.
     */
    private String dificuldadesAfetivoInterpessoais;

    /**
     * Dificuldades apresentadas - Raciocínio Lógico/Matemático.
     */
    private String dificuldadesRaciocinioLogicoMatematico;

    /**
     * Dificuldades apresentadas - AVAs.
     */
    private String dificuldadesAVAs;

    /**
     * Desenvolvimento Motores/Psicomotores - Dificuldades.
     */
    private String desenvolvimentoMotoresPsicomotoresDificuldades;

    /**
     * Desenvolvimento Motores/Psicomotores - Intervenções.
     */
    private String desenvolvimentoMotoresPsicomotoresIntervencoes;

    /**
     * Comunicação e Linguagem - Dificuldades.
     */
    private String comunicacaoLinguagemDificuldades;

    /**
     * Comunicação e Linguagem - Intervenções.
     */
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
}