package com.pies.api.projeto.integrado.pies_backend.controller.dto;

// Import para manipulação de datas
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

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
    
    private String id;
    
    private String nome;
    
    private String cpf;
    
    private LocalDate dataNascimento;
    
    private String genero;
    
    private String formacao;
    
    private String observacoes;
    
    /**
     * Lista de IDs das turmas vinculadas ao professor
     * Útil para exibir as turmas que o professor leciona
     */
    private List<String> turmasIds;

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
        // Lista de turmas vazia por padrão - deve ser preenchida via setter
        this.turmasIds = new ArrayList<>();
    }
}
