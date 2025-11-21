package com.pies.api.projeto.integrado.pies_backend.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO (Data Transfer Object) para criação de um novo PAEE.
 * 
 * Esta classe é usada para receber dados do PAEE através da API REST
 * na criação de um novo Plano de Atendimento Educacional Especializado.
 * 
 * Contém validações Bean Validation para garantir a integridade dos dados
 * antes de serem processados pelo serviço.
 */
public record CreatePAEEDTO(
    /**
     * Resumo de caso do educando.
     * Campo obrigatório - apresentação do resumo de caso do educando
     * Máximo de 2000 caracteres
     */
    @NotBlank(message = "Resumo de caso é obrigatório")
    @Size(max = 2000, message = "Resumo de caso não pode exceder 2000 caracteres")
    String resumoCaso,

    /**
     * Dificuldades apresentadas - Motores/Psicomotores.
     * Campo opcional - valor selecionado no dropdown
     * Máximo de 500 caracteres
     */
    @Size(max = 500, message = "Dificuldades motores/psicomotores não podem exceder 500 caracteres")
    String dificuldadesMotoresPsicomotores,

    /**
     * Dificuldades apresentadas - Cognitivo.
     * Campo opcional - valor selecionado no dropdown
     * Máximo de 500 caracteres
     */
    @Size(max = 500, message = "Dificuldades cognitivo não podem exceder 500 caracteres")
    String dificuldadesCognitivo,

    /**
     * Dificuldades apresentadas - Sensorial.
     * Campo opcional - valor selecionado no dropdown
     * Máximo de 500 caracteres
     */
    @Size(max = 500, message = "Dificuldades sensorial não podem exceder 500 caracteres")
    String dificuldadesSensorial,

    /**
     * Dificuldades apresentadas - Linguagem oral/Comunicação.
     * Campo opcional - valor selecionado no dropdown
     * Máximo de 500 caracteres
     */
    @Size(max = 500, message = "Dificuldades linguagem/comunicação não podem exceder 500 caracteres")
    String dificuldadesLinguagemComunicacao,

    /**
     * Dificuldades apresentadas - Familiar.
     * Campo opcional - valor selecionado no dropdown
     * Máximo de 500 caracteres
     */
    @Size(max = 500, message = "Dificuldades familiar não podem exceder 500 caracteres")
    String dificuldadesFamiliar,

    /**
     * Dificuldades apresentadas - Afetivo/Interpessoais.
     * Campo opcional - valor selecionado no dropdown
     * Máximo de 500 caracteres
     */
    @Size(max = 500, message = "Dificuldades afetivo/interpessoais não podem exceder 500 caracteres")
    String dificuldadesAfetivoInterpessoais,

    /**
     * Dificuldades apresentadas - Raciocínio Lógico/Matemático.
     * Campo opcional - valor selecionado no dropdown
     * Máximo de 500 caracteres
     */
    @Size(max = 500, message = "Dificuldades raciocínio lógico/matemático não podem exceder 500 caracteres")
    String dificuldadesRaciocinioLogicoMatematico,

    /**
     * Dificuldades apresentadas - AVAs.
     * Campo opcional - valor selecionado no dropdown
     * Máximo de 500 caracteres
     */
    @Size(max = 500, message = "Dificuldades AVAs não podem exceder 500 caracteres")
    String dificuldadesAVAs,

    /**
     * Desenvolvimento Motores/Psicomotores - Dificuldades.
     * Campo opcional - descrição das dificuldades
     * Máximo de 2000 caracteres
     */
    @Size(max = 2000, message = "Dificuldades de desenvolvimento motores/psicomotores não podem exceder 2000 caracteres")
    String desenvolvimentoMotoresPsicomotoresDificuldades,

    /**
     * Desenvolvimento Motores/Psicomotores - Intervenções.
     * Campo opcional - descrição das intervenções
     * Máximo de 2000 caracteres
     */
    @Size(max = 2000, message = "Intervenções de desenvolvimento motores/psicomotores não podem exceder 2000 caracteres")
    String desenvolvimentoMotoresPsicomotoresIntervencoes,

    /**
     * Comunicação e Linguagem - Dificuldades.
     * Campo opcional - descrição das dificuldades
     * Máximo de 2000 caracteres
     */
    @Size(max = 2000, message = "Dificuldades de comunicação e linguagem não podem exceder 2000 caracteres")
    String comunicacaoLinguagemDificuldades,

    /**
     * Comunicação e Linguagem - Intervenções.
     * Campo opcional - descrição das intervenções
     * Máximo de 2000 caracteres
     */
    @Size(max = 2000, message = "Intervenções de comunicação e linguagem não podem exceder 2000 caracteres")
    String comunicacaoLinguagemIntervencoes,

    /**
     * ID do educando ao qual este PAEE pertence.
     * Campo obrigatório - deve ser um ID válido de um educando existente
     */
    @NotBlank(message = "ID do educando é obrigatório")
    String educandoId
) {}

