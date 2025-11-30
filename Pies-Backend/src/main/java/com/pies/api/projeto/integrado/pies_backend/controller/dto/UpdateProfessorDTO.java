package com.pies.api.projeto.integrado.pies_backend.controller.dto;

// Import para manipulação de datas
import java.time.LocalDate;

import com.pies.api.projeto.integrado.pies_backend.model.Enums.Genero;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO PARA ATUALIZAÇÃO - VALIDAÇÃO DE ENTRADA
 * 
 * Este DTO é usado para atualização de professores existentes.
 * É enviado no corpo da requisição PUT /professores/{id}.
 * 
 * Características:
 * - NÃO inclui campo 'id' (passado via URL como parâmetro de path)
 * - Mesmas validações do CreateProfessorDTO para manter consistência
 * - Estratégia de atualização completa (todos os campos são obrigatórios)
 * - Usado com @Valid no controller para ativar validações automaticamente
 * 
 * Estratégia de Atualização:
 * - Atualização completa: todos os campos são enviados e atualizados
 * - Alternativa seria atualização parcial (PATCH), mas optamos por PUT completo
 * 
 * O Spring Boot valida automaticamente este DTO quando usado com @Valid.
 */
@Data // Lombok: gera automaticamente getters, setters, toString, equals e hashCode
@AllArgsConstructor // Lombok: gera construtor com todos os parâmetros
@NoArgsConstructor // Lombok: gera construtor vazio
public class UpdateProfessorDTO {
    
    // ========== CAMPOS OBRIGATÓRIOS COM VALIDAÇÕES ==========
    // Validações idênticas ao CreateProfessorDTO para manter consistência
    
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
     * 
     * IMPORTANTE: O controller verifica se o CPF está sendo alterado
     * e se já existe outro professor com o novo CPF
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
    @NotNull(message = "Gênero é obrigatório") // Gênero é obrigatório
    private Genero genero;

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
     * Sem validação = campo opcional
     */
    private String observacoes;
}
