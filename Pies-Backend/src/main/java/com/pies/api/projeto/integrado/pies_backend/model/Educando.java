package com.pies.api.projeto.integrado.pies_backend.model;

import java.time.LocalDate;
import java.util.List;

import org.hibernate.validator.constraints.br.CPF;

import com.pies.api.projeto.integrado.pies_backend.model.Enums.Genero;
import com.pies.api.projeto.integrado.pies_backend.model.Enums.GrauEscolar;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidade que representa um Educando no sistema PIES.
 * Um educando é um aluno/estudante que possui responsáveis e informações
 * relacionadas à sua educação e situação pessoal.
 * 
 * A classe utiliza JPA para mapeamento ORM e validações Bean Validation
 * para garantir a integridade dos dados.
 */
@Entity
@Table(name = "educandos")
@Data
@NoArgsConstructor
public class Educando {
    
    /**
     * Identificador único do educando.
     * Gerado automaticamente como UUID pela estratégia de geração da JPA.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * CPF (Cadastro de Pessoa Física) do educando.
     * Validação brasileira específica para garantir formato correto do CPF.
     */
    @NotBlank(message = "Informe o CPF do educando") 
    @CPF(message = "CPF inválido")
    private String cpf;

    /**
     * Nome completo do educando.
     * Campo obrigatório para identificação do aluno.
     */
    @NotBlank(message = "Informe o nome do educando")
    private String nome;

    /**
     * Data de nascimento do educando.
     * Deve ser uma data no passado (validação lógica).
     */
    @NotNull(message = "Informe a data de nascimento do educando")
    @Past(message = "Data de nascimento deve ser no passado")
    private LocalDate dataNascimento;

    /**
     * Gênero do educando.
     * Armazenado como enum (MASCULINO, FEMININO, etc.) na tabela como STRING.
     */
    @NotNull(message = "Informe o gênero do educando")
    @Enumerated(EnumType.STRING)
    private Genero genero;

    /**
     * CID (Código Internacional de Doenças).
     * Código utilizado para identificar condições de saúde ou deficiências.
     */
    @NotBlank(message = "Informe o cid do educando")
    private String cid;

    /**
     * NIS (Número de Identificação Social).
     * Identificador usado em programas sociais do governo brasileiro.
     */
    @NotNull(message = "Informe o nis do educando")
    private String nis;

    /**
     * Nome da escola onde o educando está matriculado.
     */
    @NotNull(message = "Informe a escola do educando")
    private String escola;

    /**
     * Grau de escolaridade atual do educando.
     * Armazenado como enum (ENSINO_FUNDAMENTAL, ENSINO_MEDIO, etc.) como STRING.
     */
    @NotNull(message = "Informe o grau escolar do educando")
    @Enumerated(EnumType.STRING)
    private GrauEscolar escolaridade;

    /**
     * Campo de observações adicionais sobre o educando.
     * Permite até 500 caracteres para informações complementares.
     */
    @Column(length = 500)
    private String observacao;
    
    /**
     * ID da turma à qual o educando está vinculado.
     * Campo opcional - relaciona o educando com uma turma específica.
     */
    private String turmaId;

    /**
     * Lista de responsáveis vinculados a este educando.
     * 
     * Relacionamento OneToMany: um educando pode ter vários responsáveis.
     * 
     * Configurações do relacionamento:
     * - mappedBy: indica que o relacionamento é bidirecional e a propriedade
     *   "educando" na classe Responsavel é o dono do relacionamento (lado "many").
     * - cascade: CascadeType.ALL permite que operações no educando (persist, merge,
     *   remove, refresh, detach) sejam propagadas automaticamente aos responsáveis.
     * - orphanRemoval: true significa que quando um responsável é removido da lista
     *   ou quando o educando é removido, os responsáveis órfãos serão automaticamente
     *   deletados do banco de dados. Isso garante que não existam responsáveis sem
     *   um educando associado.
     */
    @OneToMany(mappedBy = "educando", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Responsavel> responsaveis;

    @OneToOne(mappedBy = "educando", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Anamnese anamnese;

    /**
     * Lista de turmas em que este educando está matriculado.
     * 
     * Relacionamento ManyToMany: um educando pode estar em múltiplas turmas
     * e uma turma pode ter múltiplos educandos.
     * 
     * Configurações do relacionamento:
     * - joinTable: define a tabela intermediária "educando_turma" que armazena
     *   os relacionamentos entre educandos e turmas.
     * - joinColumns: define a coluna "educando_id" na tabela intermediária
     *   que referencia o ID do educando.
     * - inverseJoinColumns: define a coluna "turma_id" na tabela intermediária
     *   que referencia o ID da turma.
     * - fetch: LAZY significa que as turmas só são carregadas quando explicitamente
     *   acessadas, melhorando a performance.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "educando_turma",
        joinColumns = @JoinColumn(name = "educando_id"),
        inverseJoinColumns = @JoinColumn(name = "turma_id")
    )
    private List<Turma> turmas;

    /**
     * Construtor parametrizado para criação de instâncias de Educando.
     * 
     * @param cpf CPF do educando
     * @param nome Nome completo do educando
     * @param dataNascimento Data de nascimento
     * @param genero Gênero do educando
     * @param cid Código CID
     * @param nis Número de Identificação Social
     * @param escola Nome da escola
     * @param escolaridade Grau de escolaridade
     * @param observacao Observações adicionais
     */
    public Educando(String cpf, String nome, LocalDate dataNascimento, Genero genero, 
                   String cid, String nis, String escola, GrauEscolar escolaridade, 
                   String observacao) {
        this.cpf = cpf;
        this.nome = nome;
        this.dataNascimento = dataNascimento;
        this.genero = genero;
        this.cid = cid;
        this.nis = nis;
        this.escola = escola;
        this.escolaridade = escolaridade;
        this.observacao = observacao;
    }
}
