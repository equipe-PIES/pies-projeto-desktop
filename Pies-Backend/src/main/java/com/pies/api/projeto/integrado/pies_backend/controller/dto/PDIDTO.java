package com.pies.api.projeto.integrado.pies_backend.controller.dto;

import java.util.List;

import com.pies.api.projeto.integrado.pies_backend.model.Enums.ComposicaoAtendimento;
import com.pies.api.projeto.integrado.pies_backend.model.Enums.DiaSemana;
import com.pies.api.projeto.integrado.pies_backend.model.Enums.FrequenciaAtendimento;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO (Data Transfer Object) para transferência de dados do PDI.
 * 
 * Esta classe é usada para enviar e receber dados do PDI através da API REST,
 * separando a camada de apresentação da camada de persistência.
 * 
 * Inclui informações do educando relacionado para facilitar a visualização
 * na interface do usuário.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PDIDTO {
    
    /**
     * Identificador único do PDI.
     * Gerado automaticamente pelo sistema.
     */
    private String id;

    /**
     * Período do Plano de AEE (Atendimento Educacional Especializado).
     */
    private String periodoPlanoAEE;

    /**
     * Horário e tempo de atendimento.
     */
    private String horarioTempoAtendimento;

    /**
     * Frequência do atendimento na semana.
     */
    private FrequenciaAtendimento frequenciaAtendimento;

    /**
     * Dias da semana em que ocorre o atendimento.
     */
    private List<DiaSemana> diasSemana;

    /**
     * Composição do atendimento.
     */
    private ComposicaoAtendimento composicaoAtendimento;

    /**
     * Objetivos do Plano.
     */
    private String objetivosPlano;

    /**
     * Potencialidades do educando.
     * Capacidade do aluno, que pode vir a ser.
     */
    private String potencialidades;

    /**
     * Necessidades Educacionais Especiais do educando.
     * Dificuldades do aluno.
     */
    private String necessidadesEducacionaisEspeciais;

    /**
     * Habilidades do educando.
     * Competências e capacidades do aluno.
     */
    private String habilidades;

    /**
     * Atividades a serem desenvolvidas.
     * O que será trabalhado no atendimento ao educando.
     */
    private String atividadesASeremDesenvolvidas;

    /**
     * Recursos Materiais.
     * O que será utilizado com o educando.
     */
    private String recursosMateriais;

    /**
     * Recursos que necessitam adequação.
     * Exemplo: engrossadores de lápis, papel com pauta ampliada.
     */
    private String recursosQueNecessitamAdequacao;

    /**
     * Recursos Materiais a serem produzidos para o educando.
     * O que será produzido de forma específica para o educando.
     */
    private String recursosMateriaisASeremProduzidos;

    /**
     * Parcerias necessárias para aprimoramento do Atendimento da Produção de Materiais.
     * Exemplo: terapeuta ocupacional, costureira, marceneiro, psicopedagoga, psicólogo.
     */
    private String parceriasNecessarias;

    /**
     * ID do professor responsável.
     */
    private String professorId;

    /**
     * Nome do professor responsável.
     */
    private String professorNome;

    /**
     * ID do educando ao qual este PDI pertence.
     */
    private String educandoId;

    /**
     * Nome do educando ao qual este PDI pertence.
     * Incluído para facilitar a visualização na interface.
     */
    private String educandoNome;
}

