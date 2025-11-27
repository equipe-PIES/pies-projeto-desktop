package com.pies.api.projeto.integrado.pies_backend.model;

// Import para manipulação de datas
import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * MODELO DE DOMÍNIO - ENTIDADE PROFESSOR
 * 
 * Esta classe representa a entidade Professor no banco de dados.
 * É mapeada para a tabela "professores" e contém todas as informações
 * necessárias para um professor no sistema PIES.
 * 
 * Características:
 * - Entidade JPA com mapeamento automático para tabela
 * - Validações robustas para garantir integridade dos dados
 * - Construtores personalizados para diferentes cenários de uso
 * - Uso do Lombok para reduzir código repetitivo
 */
@Entity // Marca como entidade JPA - será persistida no banco de dados
@Table(name = "professores") // Define o nome da tabela no banco de dados
@Data // Lombok: gera automaticamente getters, setters, toString, equals e hashCode
@AllArgsConstructor // Lombok: gera construtor com todos os parâmetros
@NoArgsConstructor // Lombok: gera construtor vazio (obrigatório para JPA)
public class Professor {
    
    // ========== CHAVE PRIMÁRIA ==========
    
    /**
     * ID único do professor
     * Gerado automaticamente como UUID pelo banco de dados
     */
    @Id // Marca como chave primária
    @GeneratedValue(strategy = GenerationType.UUID) // Gera UUID automaticamente
    private String id;

    // ========== CAMPOS OBRIGATÓRIOS ==========
    
    /**
     * Nome completo do professor
     * Campo obrigatório - não pode ser nulo, vazio ou apenas espaços
     */
    @NotBlank(message = "Nome é obrigatório") // Validação: não pode ser null, vazio ou só espaços
    private String nome;

    /**
     * CPF do professor no formato brasileiro
     * Campo obrigatório com validação de formato específico
     * Formato: 000.000.000-00
     */
    @NotBlank(message = "CPF é obrigatório") // CPF é obrigatório
    @Pattern(regexp = "\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}", 
             message = "CPF deve estar no formato 000.000.000-00") // Valida formato específico
    private String cpf;

    /**
     * Data de nascimento do professor
     * Campo obrigatório - deve ser uma data no passado
     * Usa LocalDate para manipulação adequada de datas
     */
    @NotNull(message = "Data de nascimento é obrigatória") // Data é obrigatória
    @Past(message = "Data de nascimento deve ser no passado") // Deve ser data passada
    private LocalDate dataNascimento;

    /**
     * Gênero do professor
     * Campo obrigatório - usado para relatórios e estatísticas
     */
    @NotBlank(message = "Gênero é obrigatório") // Gênero é obrigatório
    private String genero;

    /**
     * Formação acadêmica do professor
     * Campo obrigatório - ex: "Graduação", "Mestrado", "Doutorado"
     */
    @NotBlank(message = "Formação é obrigatória") // Formação é obrigatória
    private String formacao;

    // ========== CAMPOS OPCIONAIS ==========
    
    /**
     * Observações adicionais sobre o professor
     * Campo opcional - pode ser nulo ou vazio
     * Usado para informações complementares
     */
    private String observacoes; // Sem validação = campo opcional
    
    /**
     * ID do usuário vinculado (chave estrangeira para tabela users)
     * Campo opcional - relaciona o professor com sua conta de usuário
     */
    private String userId; // Campo que vincula com a tabela users

    // ========== CONSTRUTORES PERSONALIZADOS ==========
    
    /**
     * Construtor para criação básica de professor
     * Usado quando observações não são necessárias
     * 
     * @param nome Nome completo do professor
     * @param cpf CPF no formato 000.000.000-00
     * @param dataNascimento Data de nascimento
     * @param genero Gênero do professor
     * @param formacao Formação acadêmica
     */
    public Professor(String nome, String cpf, LocalDate dataNascimento, String genero, String formacao) {
        this.nome = nome;
        this.cpf = cpf;
        this.dataNascimento = dataNascimento;
        this.genero = genero;
        this.formacao = formacao;
        // observacoes fica null (padrão)
    }

    /**
     * Construtor completo incluindo observações
     * Usado quando todas as informações estão disponíveis
     * 
     * @param nome Nome completo do professor
     * @param cpf CPF no formato 000.000.000-00
     * @param dataNascimento Data de nascimento
     * @param genero Gênero do professor
     * @param formacao Formação acadêmica
     * @param observacoes Observações adicionais (pode ser null)
     */
    public Professor(String nome, String cpf, LocalDate dataNascimento, String genero, String formacao, String observacoes) {
        this.nome = nome;
        this.cpf = cpf;
        this.dataNascimento = dataNascimento;
        this.genero = genero;
        this.formacao = formacao;
        this.observacoes = observacoes;
    }
}
