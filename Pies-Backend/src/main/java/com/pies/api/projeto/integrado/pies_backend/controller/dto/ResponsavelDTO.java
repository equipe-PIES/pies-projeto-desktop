package com.pies.api.projeto.integrado.pies_backend.controller.dto;

import org.hibernate.validator.constraints.br.CPF;

import com.pies.api.projeto.integrado.pies_backend.model.Enums.Parentesco;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * DTO (Data Transfer Object) para transferência de dados do Responsável.
 * 
 * Esta classe é usada para enviar e receber dados do responsável através da API REST,
 * separando a camada de apresentação da camada de persistência.
 * 
 * Contém validações Bean Validation para garantir a integridade dos dados
 * antes de serem processados pelo serviço.
 */
@Data
public class ResponsavelDTO {
    
    /**
     * Identificador único do responsável.
     * Gerado automaticamente pelo sistema, não deve ser enviado na criação.
     */
    private String id;

    /**
     * Nome completo do responsável.
     * Campo obrigatório para identificação do responsável.
     */
    @NotBlank(message = "Informe o nome do responsável")
    private String nome;

    /**
     * CPF (Cadastro de Pessoa Física) do responsável.
     * Deve estar no formato 000.000.000-00 (com pontos e traço).
     */
    @NotBlank(message = "Informe o CPF do educando") 
    @CPF(message = "CPF inválido")
    private String cpf;

    /**
     * Método de contato do responsável.
     * Pode ser telefone, celular ou e-mail.
     */
    @NotBlank(message = "Informe um método de contato do responsável (ex: telefone, celular ou e-mail)")
    private String contato;

    /**
     * Parentesco do responsável com o educando.
     * Valores possíveis definidos no enum Parentesco.
     */
    @NotNull(message = "Informe qual o parentesco com o educando")
    private Parentesco parentesco;

    /**
     * Campo opcional para especificar outro tipo de parentesco.
     * Deve ser preenchido apenas quando o parentesco for "OUTRO".
     */
    private String outroParentesco;

    /**
     * Endereço do responsável.
     * Validação aninhada garante que o endereço também seja validado.
     */
    @Valid
    private EnderecoDTO endereco;
}
