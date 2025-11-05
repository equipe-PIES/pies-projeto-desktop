package com.pies.api.projeto.integrado.pies_backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "enderecos")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Endereco {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @NotBlank(message = "Informe o CEP")
    private String cep;

    @NotBlank(message = "Informe a sigla do Estado(Ex: CE, SP...)")
    private String uf;

    @NotBlank(message = "Informe a cidade")
    private String cidade;

    @NotBlank(message = "Informe o bairro")
    private String bairro;

    @NotBlank(message = "Informe a rua")
    private String rua;

    @NotBlank(message = "Informe o numero")
    private String numero;

    private String complemento;

}
