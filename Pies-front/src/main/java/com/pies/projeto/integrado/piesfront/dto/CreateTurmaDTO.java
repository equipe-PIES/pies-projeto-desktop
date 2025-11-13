package com.pies.projeto.integrado.piesfront.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateTurmaDTO {
    @JsonProperty("nome")
    private String nome;
    
    @JsonProperty("grauEscolar")
    private String grauEscolar;
    
    @JsonProperty("faixaEtaria")
    private String faixaEtaria;
    
    @JsonProperty("turno")
    private String turno;
    
    @JsonProperty("professorId")
    private String professorId;

    // Construtor vazio
    public CreateTurmaDTO() {
    }

    // Construtor completo
    public CreateTurmaDTO(String nome, String grauEscolar, String faixaEtaria, String turno, String professorId) {
        this.nome = nome;
        this.grauEscolar = grauEscolar;
        this.faixaEtaria = faixaEtaria;
        this.turno = turno;
        this.professorId = professorId;
    }

    // Getters e Setters
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getGrauEscolar() {
        return grauEscolar;
    }

    public void setGrauEscolar(String grauEscolar) {
        this.grauEscolar = grauEscolar;
    }

    public String getFaixaEtaria() {
        return faixaEtaria;
    }

    public void setFaixaEtaria(String faixaEtaria) {
        this.faixaEtaria = faixaEtaria;
    }

    public String getTurno() {
        return turno;
    }

    public void setTurno(String turno) {
        this.turno = turno;
    }

    public String getProfessorId() {
        return professorId;
    }

    public void setProfessorId(String professorId) {
        this.professorId = professorId;
    }
}
