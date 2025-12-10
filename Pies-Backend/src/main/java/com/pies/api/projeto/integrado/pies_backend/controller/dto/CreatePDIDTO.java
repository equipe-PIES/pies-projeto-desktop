package com.pies.api.projeto.integrado.pies_backend.controller.dto;

import java.util.List;

import com.pies.api.projeto.integrado.pies_backend.model.Enums.ComposicaoAtendimento;
import com.pies.api.projeto.integrado.pies_backend.model.Enums.DiaSemana;
import com.pies.api.projeto.integrado.pies_backend.model.Enums.FrequenciaAtendimento;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO (Data Transfer Object) para criação de um novo PDI.
 * 
 * Esta classe é usada para receber dados do PDI através da API REST
 * na criação de um novo Plano de Desenvolvimento Individual.
 * 
 * Contém validações Bean Validation para garantir a integridade dos dados
 * antes de serem processados pelo serviço.
 */
public record CreatePDIDTO(
    /**
     * Período do Plano de AEE (Atendimento Educacional Especializado).
     * Campo obrigatório - ex: "2024/1", "Janeiro a Junho de 2024"
     */
    @NotBlank(message = "Período do Plano de AEE é obrigatório")
    String periodoPlanoAEE,

    /**
     * Horário e tempo de atendimento.
     * Campo obrigatório - ex: "08:00 às 09:30", "14:00 - 1h30min"
     */
    @NotBlank(message = "Horário e tempo de atendimento é obrigatório")
    String horarioTempoAtendimento,

    /**
     * Frequência do atendimento na semana.
     * Campo obrigatório - UMA_VEZ ou DUAS_VEZES
     */
    @NotNull(message = "Frequência do atendimento é obrigatória")
    FrequenciaAtendimento frequenciaAtendimento,

    /**
     * Dias da semana em que ocorre o atendimento.
     * Campo obrigatório - lista de dias (SEGUNDA_FEIRA, TERCA_FEIRA, etc.)
     * Deve conter pelo menos um dia
     */
    @NotNull(message = "Dias da semana são obrigatórios")
    @NotEmpty(message = "Selecione pelo menos um dia da semana")
    List<DiaSemana> diasSemana,

    /**
     * Composição do atendimento.
     * Campo obrigatório - INDIVIDUAL ou COLETIVO
     */
    @NotNull(message = "Composição do atendimento é obrigatória")
    ComposicaoAtendimento composicaoAtendimento,

    /**
     * Objetivos do Plano.
     * Campo obrigatório - texto descritivo dos objetivos e metas a serem atingidos
     * Máximo de 2000 caracteres
     */
    @NotBlank(message = "Objetivos do Plano são obrigatórios")
    @Size(max = 2000, message = "Objetivos do Plano não podem exceder 2000 caracteres")
    String objetivosPlano,

    /**
     * Potencialidades do educando.
     * Campo obrigatório - capacidade do aluno, que pode vir a ser
     * Máximo de 2000 caracteres
     */
    @NotBlank(message = "Potencialidades são obrigatórias")
    @Size(max = 2000, message = "Potencialidades não podem exceder 2000 caracteres")
    String potencialidades,

    /**
     * Necessidades Educacionais Especiais do educando.
     * Campo obrigatório - dificuldades do aluno
     * Máximo de 2000 caracteres
     */
    @NotBlank(message = "Necessidades Educacionais Especiais são obrigatórias")
    @Size(max = 2000, message = "Necessidades Educacionais Especiais não podem exceder 2000 caracteres")
    String necessidadesEducacionaisEspeciais,

    /**
     * Habilidades do educando.
     * Campo obrigatório - competências e capacidades do aluno
     * Máximo de 2000 caracteres
     */
    @NotBlank(message = "Habilidades são obrigatórias")
    @Size(max = 2000, message = "Habilidades não podem exceder 2000 caracteres")
    String habilidades,

    /**
     * Atividades a serem desenvolvidas.
     * Campo obrigatório - o que será trabalhado no atendimento ao educando
     * Máximo de 2000 caracteres
     */
    @NotBlank(message = "Atividades a serem desenvolvidas são obrigatórias")
    @Size(max = 2000, message = "Atividades a serem desenvolvidas não podem exceder 2000 caracteres")
    String atividadesASeremDesenvolvidas,

    /**
     * Recursos Materiais.
     * Campo obrigatório - o que será utilizado com o educando
     * Máximo de 2000 caracteres
     */
    @NotBlank(message = "Recursos Materiais são obrigatórios")
    @Size(max = 2000, message = "Recursos Materiais não podem exceder 2000 caracteres")
    String recursosMateriais,

    /**
     * Recursos que necessitam adequação.
     * Campo obrigatório - exemplo: engrossadores de lápis, papel com pauta ampliada
     * Máximo de 2000 caracteres
     */
    @NotBlank(message = "Recursos que necessitam adequação são obrigatórios")
    @Size(max = 2000, message = "Recursos que necessitam adequação não podem exceder 2000 caracteres")
    String recursosQueNecessitamAdequacao,

    /**
     * Recursos Materiais a serem produzidos para o educando.
     * Campo obrigatório - o que será produzido de forma específica para o educando
     * Máximo de 2000 caracteres
     */
    @NotBlank(message = "Recursos Materiais a serem produzidos são obrigatórios")
    @Size(max = 2000, message = "Recursos Materiais a serem produzidos não podem exceder 2000 caracteres")
    String recursosMateriaisASeremProduzidos,

    /**
     * Parcerias necessárias para aprimoramento do Atendimento da Produção de Materiais.
     * Campo obrigatório - exemplo: terapeuta ocupacional, costureira, marceneiro, psicopedagoga, psicólogo
     * Máximo de 2000 caracteres
     */
    @NotBlank(message = "Parcerias necessárias são obrigatórias")
    @Size(max = 2000, message = "Parcerias necessárias não podem exceder 2000 caracteres")
    String parceriasNecessarias,

    /**
     * ID do educando ao qual este PDI pertence.
     * Campo obrigatório - deve ser um ID válido de um educando existente
     */
    @NotBlank(message = "ID do educando é obrigatório")
    String educandoId,

    /**
     * ID do professor responsável por este PDI.
     * Campo obrigatório - deve ser um ID válido de um professor existente
     */
    @NotBlank(message = "ID do professor é obrigatório")
    String professorId
) {}

