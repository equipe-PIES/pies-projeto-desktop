package com.pies.projeto.integrado.piesfront.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO para representar um professor retornado pela API
 */
public class ProfessorDTO {
    @JsonProperty("id")
    private String id;
    
    @JsonProperty("nome")
    private String nome;
    
    @JsonProperty("cpf")
    private String cpf;
    
    @JsonProperty("dataNascimento")
    private String dataNascimento;
    
    @JsonProperty("genero")
    private String genero;
    
    @JsonProperty("formacao")
    private String formacao;
    
    @JsonProperty("observacoes")
    private String observacoes;

    // Construtor vazio
    public ProfessorDTO() {
    }

    // Getters e Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(String dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getFormacao() {
        return formacao;
    }

    public void setFormacao(String formacao) {
        this.formacao = formacao;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}
