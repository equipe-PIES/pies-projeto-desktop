package com.pies.projeto.integrado.piesfront.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO simples para exibir informações básicas do educando/aluno
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AlunoSimplificadoDTO {
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
    
    @JsonProperty("cid")
    private String cid;
    
    @JsonProperty("nis")
    private String nis;
    
    @JsonProperty("escola")
    private String escola;
    
    @JsonProperty("escolaridade")
    private String escolaridade;

    // Construtor vazio
    public AlunoSimplificadoDTO() {
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

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getNis() {
        return nis;
    }

    public void setNis(String nis) {
        this.nis = nis;
    }

    public String getEscola() {
        return escola;
    }

    public void setEscola(String escola) {
        this.escola = escola;
    }

    public String getEscolaridade() {
        return escolaridade;
    }

    public void setEscolaridade(String escolaridade) {
        this.escolaridade = escolaridade;
    }
}
