package com.pies.api.projeto.integrado.pies_backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
 * MODELO DE DOMÍNIO - ENTIDADE PAEE (Plano de Atendimento Educacional Especializado)
 * 
 * Esta classe representa o Plano de Atendimento Educacional Especializado de um educando
 * no sistema AmparoEdu. O PAEE contém informações sobre o resumo do caso, dificuldades
 * apresentadas e intervenções planejadas.
 * 
 * Características:
 * - Entidade JPA com mapeamento automático para tabela
 * - Relacionamento ManyToOne com Educando (um educando pode ter múltiplos PAEEs)
 * - Validações robustas para garantir integridade dos dados
 * - Uso do Lombok para reduzir código repetitivo
 */
@Entity
@Table(name = "paees")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PAEE {
    
    /**
     * ID único do PAEE.
     * Gerado automaticamente como UUID pelo banco de dados.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * Resumo de caso do educando.
     * Campo obrigatório - apresentação do resumo de caso do educando
     * Permite até 2000 caracteres para descrição detalhada
     */
    @NotBlank(message = "Resumo de caso é obrigatório")
    @Column(name = "resumo_caso", length = 2000)
    private String resumoCaso;

    /**
     * Dificuldades apresentadas - Motores/Psicomotores.
     * Campo opcional - valor selecionado no dropdown
     */
    @Column(name = "dificuldades_motores_psicomotores", length = 500)
    private String dificuldadesMotoresPsicomotores;

    /**
     * Dificuldades apresentadas - Cognitivo.
     * Campo opcional - valor selecionado no dropdown
     */
    @Column(name = "dificuldades_cognitivo", length = 500)
    private String dificuldadesCognitivo;

    /**
     * Dificuldades apresentadas - Sensorial.
     * Campo opcional - valor selecionado no dropdown
     */
    @Column(name = "dificuldades_sensorial", length = 500)
    private String dificuldadesSensorial;

    /**
     * Dificuldades apresentadas - Linguagem oral/Comunicação.
     * Campo opcional - valor selecionado no dropdown
     */
    @Column(name = "dificuldades_linguagem_comunicacao", length = 500)
    private String dificuldadesLinguagemComunicacao;

    /**
     * Dificuldades apresentadas - Familiar.
     * Campo opcional - valor selecionado no dropdown
     */
    @Column(name = "dificuldades_familiar", length = 500)
    private String dificuldadesFamiliar;

    /**
     * Dificuldades apresentadas - Afetivo/Interpessoais.
     * Campo opcional - valor selecionado no dropdown
     */
    @Column(name = "dificuldades_afetivo_interpessoais", length = 500)
    private String dificuldadesAfetivoInterpessoais;

    /**
     * Dificuldades apresentadas - Raciocínio Lógico/Matemático.
     * Campo opcional - valor selecionado no dropdown
     */
    @Column(name = "dificuldades_raciocinio_logico_matematico", length = 500)
    private String dificuldadesRaciocinioLogicoMatematico;

    /**
     * Dificuldades apresentadas - AVAs.
     * Campo opcional - valor selecionado no dropdown
     */
    @Column(name = "dificuldades_avas", length = 500)
    private String dificuldadesAVAs;

    /**
     * Desenvolvimento Motores/Psicomotores - Dificuldades.
     * Campo opcional - descrição das dificuldades
     * Permite até 2000 caracteres
     */
    @Column(name = "desenvolvimento_motores_psicomotores_dificuldades", length = 2000)
    private String desenvolvimentoMotoresPsicomotoresDificuldades;

    /**
     * Desenvolvimento Motores/Psicomotores - Intervenções.
     * Campo opcional - descrição das intervenções
     * Permite até 2000 caracteres
     */
    @Column(name = "desenvolvimento_motores_psicomotores_intervencoes", length = 2000)
    private String desenvolvimentoMotoresPsicomotoresIntervencoes;

    /**
     * Comunicação e Linguagem - Dificuldades.
     * Campo opcional - descrição das dificuldades
     * Permite até 2000 caracteres
     */
    @Column(name = "comunicacao_linguagem_dificuldades", length = 2000)
    private String comunicacaoLinguagemDificuldades;

    /**
     * Comunicação e Linguagem - Intervenções.
     * Campo opcional - descrição das intervenções
     * Permite até 2000 caracteres
     */
    @Column(name = "comunicacao_linguagem_intervencoes", length = 2000)
    private String comunicacaoLinguagemIntervencoes;

    /**
     * Raciocínio - Dificuldades.
     * Campo opcional - descrição das dificuldades
     * Permite até 2000 caracteres
     */
    @Column(name = "dificuldades_raciocinio", length = 2000)
    private String dificuldadesRaciocinio;

    /**
     * Raciocínio - Intervenções.
     * Campo opcional - descrição das intervenções
     * Permite até 2000 caracteres
     */
    @Column(name = "intervencoes_raciocinio", length = 2000)
    private String intervencoesRaciocinio;

    /**
     * Atenção - Dificuldades.
     * Campo opcional - descrição das dificuldades
     * Permite até 2000 caracteres
     */
    @Column(name = "dificuldades_atencao", length = 2000)
    private String dificuldadesAtencao;

    /**
     * Atenção - Intervenções.
     * Campo opcional - descrição das intervenções
     * Permite até 2000 caracteres
     */
    @Column(name = "intervencoes_atencao", length = 2000)
    private String intervencoesAtencao;

    /**
     * Memória - Dificuldades.
     * Campo opcional - descrição das dificuldades
     * Permite até 2000 caracteres
     */
    @Column(name = "dificuldades_memoria", length = 2000)
    private String dificuldadesMemoria;

    /**
     * Memória - Intervenções.
     * Campo opcional - descrição das intervenções
     * Permite até 2000 caracteres
     */
    @Column(name = "intervencoes_memoria", length = 2000)
    private String intervencoesMemoria;

    /**
     * Percepção - Dificuldades.
     * Campo opcional - descrição das dificuldades
     * Permite até 2000 caracteres
     */
    @Column(name = "dificuldades_percepcao", length = 2000)
    private String dificuldadesPercepcao;

    /**
     * Percepção - Intervenções.
     * Campo opcional - descrição das intervenções
     * Permite até 2000 caracteres
     */
    @Column(name = "intervencoes_percepcao", length = 2000)
    private String intervencoesPercepcao;

    /**
     * Sociabilidade - Dificuldades.
     * Campo opcional - descrição das dificuldades
     * Permite até 2000 caracteres
     */
    @Column(name = "dificuldades_sociabilidade", length = 2000)
    private String dificuldadesSociabilidade;

    /**
     * Sociabilidade - Intervenções.
     * Campo opcional - descrição das intervenções
     * Permite até 2000 caracteres
     */
    @Column(name = "intervencoes_sociabilidade", length = 2000)
    private String intervencoesSociabilidade;

    /**
     * AVA - Dificuldades.
     * Campo opcional - descrição das dificuldades
     * Permite até 2000 caracteres
     */
    @Column(name = "dificuldades_ava", length = 2000)
    private String dificuldadesAVA;

    /**
     * AVA - Intervenções.
     * Campo opcional - descrição das intervenções
     * Permite até 2000 caracteres
     */
    @Column(name = "intervencoes_ava", length = 2000)
    private String intervencoesAVA;

    /**
     * Objetivos AEE.
     * Campo opcional - descrição dos objetivos
     * Permite até 2000 caracteres
     */
    @Column(name = "objetivos_aee", length = 2000)
    private String objetivosAEE;

    /**
     * Encaminhamento AEE.
     * Campo opcional - Sim/Não
     */
    @Column(name = "env_aee", length = 50)
    private String envAEE;

    /**
     * Encaminhamento Psicólogo.
     * Campo opcional - Sim/Não
     */
    @Column(name = "env_psicologo", length = 50)
    private String envPsicologo;

    /**
     * Encaminhamento Fisioterapeuta.
     * Campo opcional - Sim/Não
     */
    @Column(name = "env_fisioterapeuta", length = 50)
    private String envFisioterapeuta;

    /**
     * Encaminhamento Psicopedagogo.
     * Campo opcional - Sim/Não
     */
    @Column(name = "env_psicopedagogo", length = 50)
    private String envPsicopedagogo;

    /**
     * Encaminhamento TO (Terapeuta Ocupacional).
     * Campo opcional - Sim/Não
     */
    @Column(name = "env_to", length = 50)
    private String envTO;

    /**
     * Encaminhamento Educação Física.
     * Campo opcional - Sim/Não
     */
    @Column(name = "env_educacao_fisica", length = 50)
    private String envEducacaoFisica;

    /**
     * Encaminhamento Estimulação Precoce.
     * Campo opcional - Sim/Não
     */
    @Column(name = "env_estimulacao_precoce", length = 50)
    private String envEstimulacaoPrecoce;

    /**
     * Relacionamento com Educando.
     * ManyToOne: múltiplos PAEEs podem pertencer a um educando
     * O educando não pode ser nulo
     */
    @NotNull(message = "Educando é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "educando_id", nullable = false)
    private Educando educando;
}

