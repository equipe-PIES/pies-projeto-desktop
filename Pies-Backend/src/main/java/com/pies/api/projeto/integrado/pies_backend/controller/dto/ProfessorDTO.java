package com.pies.api.projeto.integrado.pies_backend.controller.dto;

// Import para manipulação de datas
import java.time.LocalDate;

import com.pies.api.projeto.integrado.pies_backend.model.Professor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO (DATA TRANSFER OBJECT) - PADRÃO DE TRANSFERÊNCIA DE DADOS
 * 
 * Este DTO é usado para representar dados de um Professor nas respostas da API.
 * 
 * Benefícios dos DTOs:
 * - Controle sobre quais dados são expostos na API
 * - Desacoplamento entre modelo de domínio e API
 * - Facilita versionamento da API
 * - Melhora performance (não expõe dados desnecessários)
 * - Permite transformações de dados antes da serialização
 * 
 * Este DTO contém todos os campos do Professor para exibição completa.
 */
@Data // Lombok: gera automaticamente getters, setters, toString, equals e hashCode
@AllArgsConstructor // Lombok: gera construtor com todos os parâmetros
@NoArgsConstructor // Lombok: gera construtor vazio
public class ProfessorDTO {
    
    // ========== CAMPOS IDÊNTICOS AO MODELO ==========
    // Todos os campos são expostos para resposta completa da API
    
    /**
     * ID único do professor
     * Gerado automaticamente pelo banco de dados
     */
    private String id;
    
    /**
     * Nome completo do professor
     */
    private String nome;
    
    /**
     * CPF do professor no formato brasileiro (000.000.000-00)
     */
    private String cpf;
    
    /**
     * Data de nascimento do professor
     * Formato: YYYY-MM-DD
     */
    private LocalDate dataNascimento;
    
    /**
     * Gênero do professor
     */
    private String genero;
    
    /**
     * Formação acadêmica do professor
     */
    private String formacao;
    
    /**
     * Observações adicionais sobre o professor
     * Pode ser null se não houver observações
     */
    private String observacoes;

    // ========== CONSTRUTOR DE CONVERSÃO ==========
    
    /**
     * Construtor que converte um Professor (entidade) em ProfessorDTO
     * 
     * Este é um padrão muito usado para transformar objetos de domínio em DTOs.
     * Facilita a conversão entre camadas da aplicação.
     * 
     * Uso: new ProfessorDTO(professor)
     * 
     * @param professor Entidade Professor a ser convertida
     */
    public ProfessorDTO(Professor professor) {
        // Copia todos os campos da entidade para o DTO
        this.id = professor.getId();
        this.nome = professor.getNome();
        this.cpf = professor.getCpf();
        this.dataNascimento = professor.getDataNascimento();
        this.genero = professor.getGenero();
        this.formacao = professor.getFormacao();
        this.observacoes = professor.getObservacoes();
    }
}
