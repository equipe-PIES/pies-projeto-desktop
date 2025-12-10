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

