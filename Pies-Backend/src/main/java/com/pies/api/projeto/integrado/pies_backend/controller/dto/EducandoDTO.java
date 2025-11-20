package com.pies.api.projeto.integrado.pies_backend.controller.dto;

import java.time.LocalDate;
import java.util.List;
import com.pies.api.projeto.integrado.pies_backend.model.Enums.Genero;
import com.pies.api.projeto.integrado.pies_backend.model.Enums.GrauEscolar;
import org.hibernate.validator.constraints.br.CPF;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * DTO (Data Transfer Object) para transferência de dados do Educando.
 * 
 * Esta classe é usada para enviar e receber dados do educando através da API REST,
 * separando a camada de apresentação da camada de persistência.
 * 
 * Contém validações Bean Validation para garantir a integridade dos dados
 * antes de serem processados pelo serviço.
 */
@Data
public class EducandoDTO {
    
    /**
     * Identificador único do educando.
     * Gerado automaticamente pelo sistema, não deve ser enviado na criação.
     */
    private String id;

    /**
     * Nome completo do educando.
     * Campo obrigatório para identificação do aluno.
     */
    @NotBlank(message = "Informe o nome do educando")
    private String nome;

    /**
     * CPF (Cadastro de Pessoa Física) do educando.
     * Deve estar no formato válido brasileiro e ser único no sistema.
     */
    @NotBlank(message = "Informe o CPF do educando")
    @CPF(message = "CPF inválido")
    private String cpf;

    /**
     * Data de nascimento do educando.
     * Deve ser uma data no passado.
     */
    @NotNull(message = "Informe a data de nascimento do educando")
    @Past(message = "Data de nascimento deve ser no passado")
    private LocalDate dataNascimento;

    /**
     * Gênero do educando.
     * Valores possíveis: MASCULINO, FEMININO, etc.
     */
    @NotNull(message = "Informe o gênero do educando")
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
     * Valores possíveis: EDUCACAO_INFANTIL, ENSINO_FUNDAMENTAL, ENSINO_MEDIO, etc.
     */
    @NotNull(message = "Informe o grau escolar do educando")
    private GrauEscolar escolaridade;

    /**
     * Campo de observações adicionais sobre o educando.
     * Permite informações complementares opcionais.
     */
    private String observacao;

    /**
     * Lista de responsáveis vinculados a este educando.
     * Populada automaticamente quando o educando é buscado do banco de dados.
     */

    @Valid
    private List<ResponsavelDTO> responsaveis;

    @Valid
    private AnamneseDTO anamnese;
}
