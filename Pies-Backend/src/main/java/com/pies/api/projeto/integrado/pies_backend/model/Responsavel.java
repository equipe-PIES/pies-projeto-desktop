package com.pies.api.projeto.integrado.pies_backend.model;

import org.hibernate.validator.constraints.br.CPF;

import com.pies.api.projeto.integrado.pies_backend.model.Enums.Parentesco;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "responsaveis")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Responsavel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @NotBlank(message = "Informe o CPF do educando") 
    @CPF(message = "CPF inválido")
    private String cpf;

    @NotBlank(message = "Informe o nome do responsável")
    private String nome;

    @NotBlank(message = "Informe um método de contato do responsável (ex: telefone, celular ou e-mail)")
    private String contato;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Informe qual o parentesco com o educando")
    private Parentesco parentesco;

    private String outroParentesco;

    @ManyToOne
    @JoinColumn(name = "educando_id", referencedColumnName = "id")
    private Educando educando;

    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "endereco_id", referencedColumnName = "id")
    private Endereco endereco;

}