package com.pies.api.projeto.integrado.pies_backend.model;

import java.util.List;

import com.pies.api.projeto.integrado.pies_backend.model.Enums.ComposicaoAtendimento;
import com.pies.api.projeto.integrado.pies_backend.model.Enums.DiaSemana;
import com.pies.api.projeto.integrado.pies_backend.model.Enums.FrequenciaAtendimento;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * MODELO DE DOMÍNIO - ENTIDADE PDI (Plano de Desenvolvimento Individual)
 * 
 * Esta classe representa o Plano de Desenvolvimento Individual de um educando
 * no sistema AmparoEdu. O PDI contém informações sobre a organização do
 * atendimento e os objetivos do plano.
 * 
 * Características:
 * - Entidade JPA com mapeamento automático para tabela
 * - Relacionamento ManyToOne com Educando (um educando pode ter múltiplos PDIs)
 * - Validações robustas para garantir integridade dos dados
 * - Uso do Lombok para reduzir código repetitivo
 */
@Entity
@Table(name = "pdis")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PDI {
    
    /**
     * ID único do PDI.
     * Gerado automaticamente como UUID pelo banco de dados.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * Período do Plano de AEE (Atendimento Educacional Especializado).
     * Campo obrigatório - ex: "2024/1", "Janeiro a Junho de 2024"
     */
    @NotBlank(message = "Período do Plano de AEE é obrigatório")
    private String periodoPlanoAEE;

    /**
     * Horário e tempo de atendimento.
     * Campo obrigatório - ex: "08:00 às 09:30", "14:00 - 1h30min"
     */
    @NotBlank(message = "Horário e tempo de atendimento é obrigatório")
    private String horarioTempoAtendimento;

    /**
     * Frequência do atendimento na semana.
     * Campo obrigatório - UMA_VEZ ou DUAS_VEZES
     */
    @NotNull(message = "Frequência do atendimento é obrigatória")
    @Enumerated(EnumType.STRING)
    private FrequenciaAtendimento frequenciaAtendimento;

    /**
     * Dias da semana em que ocorre o atendimento.
     * Campo obrigatório - lista de dias (SEGUNDA_FEIRA, TERCA_FEIRA, etc.)
     * Permite múltiplos dias selecionados
     */
    @NotNull(message = "Dias da semana são obrigatórios")
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @Column(name = "dia_semana")
    private List<DiaSemana> diasSemana;

    /**
     * Composição do atendimento.
     * Campo obrigatório - INDIVIDUAL ou COLETIVO
     */
    @NotNull(message = "Composição do atendimento é obrigatória")
    @Enumerated(EnumType.STRING)
    private ComposicaoAtendimento composicaoAtendimento;

    /**
     * Objetivos do Plano.
     * Campo obrigatório - texto descritivo dos objetivos e metas a serem atingidos
     * Permite até 2000 caracteres para descrição detalhada
     */
    @NotBlank(message = "Objetivos do Plano são obrigatórios")
    @Column(length = 2000)
    private String objetivosPlano;

    /**
     * Potencialidades do educando.
     * Campo obrigatório - capacidade do aluno, que pode vir a ser
     * Permite até 2000 caracteres para descrição detalhada
     */
    @NotBlank(message = "Potencialidades são obrigatórias")
    @Column(length = 2000)
    private String potencialidades;

    /**
     * Necessidades Educacionais Especiais do educando.
     * Campo obrigatório - dificuldades do aluno
     * Permite até 2000 caracteres para descrição detalhada
     */
    @NotBlank(message = "Necessidades Educacionais Especiais são obrigatórias")
    @Column(length = 2000)
    private String necessidadesEducacionaisEspeciais;

    /**
     * Habilidades do educando.
     * Campo obrigatório - competências e capacidades do aluno
     * Permite até 2000 caracteres para descrição detalhada
     */
    @NotBlank(message = "Habilidades são obrigatórias")
    @Column(length = 2000)
    private String habilidades;

    /**
     * Atividades a serem desenvolvidas.
     * Campo obrigatório - o que será trabalhado no atendimento ao educando
     * Permite até 2000 caracteres para descrição detalhada
     */
    @NotBlank(message = "Atividades a serem desenvolvidas são obrigatórias")
    @Column(length = 2000)
    private String atividadesASeremDesenvolvidas;

    /**
     * Recursos Materiais.
     * Campo obrigatório - o que será utilizado com o educando
     * Permite até 2000 caracteres para descrição detalhada
     */
    @NotBlank(message = "Recursos Materiais são obrigatórios")
    @Column(length = 2000)
    private String recursosMateriais;

    /**
     * Recursos que necessitam adequação.
     * Campo obrigatório - exemplo: engrossadores de lápis, papel com pauta ampliada
     * Permite até 2000 caracteres para descrição detalhada
     */
    @NotBlank(message = "Recursos que necessitam adequação são obrigatórios")
    @Column(length = 2000)
    private String recursosQueNecessitamAdequacao;

    /**
     * Recursos Materiais a serem produzidos para o educando.
     * Campo obrigatório - o que será produzido de forma específica para o educando
     * Permite até 2000 caracteres para descrição detalhada
     */
    @NotBlank(message = "Recursos Materiais a serem produzidos são obrigatórios")
    @Column(length = 2000)
    private String recursosMateriaisASeremProduzidos;

    /**
     * Parcerias necessárias para aprimoramento do Atendimento da Produção de Materiais.
     * Campo obrigatório - exemplo: terapeuta ocupacional, costureira, marceneiro, psicopedagoga, psicólogo
     * Permite até 2000 caracteres para descrição detalhada
     */
    @NotBlank(message = "Parcerias necessárias são obrigatórias")
    @Column(length = 2000)
    private String parceriasNecessarias;

    /**
     * Relacionamento com Educando.
     * ManyToOne: múltiplos PDIs podem pertencer a um educando
     * O educando não pode ser nulo
     */
    @NotNull(message = "Educando é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "educando_id", nullable = false)
    private Educando educando;

    @NotNull(message = "Professor é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professor_id", nullable = false)
    private Professor professor;
}

