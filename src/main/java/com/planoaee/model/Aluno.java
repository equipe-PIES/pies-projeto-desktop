package com.planoaee.model;

import java.time.LocalDateTime;


public class Aluno {
    
    private Integer id;
    private String nome;
    private Integer idade;
    private String responsavel;
    private String contato;
    private String observacoes;
    private LocalDateTime dataCadastro;
    private Boolean ativo;
    

    public Aluno() {
        this.ativo = true;
        this.dataCadastro = LocalDateTime.now();
    }
    
    public Aluno(String nome, Integer idade, String responsavel, String contato) {
        this();
        this.nome = nome;
        this.idade = idade;
        this.responsavel = responsavel;
        this.contato = contato;
    }
    

    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getNome() {
        return nome;
    }
    
    public void setNome(String nome) {
        this.nome = nome;
    }
    
    public Integer getIdade() {
        return idade;
    }
    
    public void setIdade(Integer idade) {
        this.idade = idade;
    }
    
    public String getResponsavel() {
        return responsavel;
    }
    
    public void setResponsavel(String responsavel) {
        this.responsavel = responsavel;
    }
    
    public String getContato() {
        return contato;
    }
    
    public void setContato(String contato) {
        this.contato = contato;
    }
    
    public String getObservacoes() {
        return observacoes;
    }
    
    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
    
    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }
    
    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }
    
    public Boolean getAtivo() {
        return ativo;
    }
    
    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
    

    public String getDescricaoResumida() {
        return nome + " (" + idade + " anos)";
    }

    public boolean isMenorIdade() {
        return idade != null && idade < 18;
    }
    
    @Override
    public String toString() {
        return "Aluno{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", idade=" + idade +
                ", responsavel='" + responsavel + '\'' +
                ", contato='" + contato + '\'' +
                ", dataCadastro=" + dataCadastro +
                ", ativo=" + ativo +
                '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Aluno aluno = (Aluno) obj;
        return id != null && id.equals(aluno.id);
    }
    
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}

