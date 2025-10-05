package com.planoaee.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidade que representa o controle de frequência de um aluno
 */
public class Frequencia {
    
    private Integer id;
    private Aluno aluno;
    private LocalDate data;
    private Boolean presente;
    private String observacoes;
    private LocalDateTime dataRegistro;
    
    // Construtores
    public Frequencia() {
        this.dataRegistro = LocalDateTime.now();
    }
    
    public Frequencia(Aluno aluno, LocalDate data, Boolean presente) {
        this();
        this.aluno = aluno;
        this.data = data;
        this.presente = presente;
    }
    
    // Getters e Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public Aluno getAluno() {
        return aluno;
    }
    
    public void setAluno(Aluno aluno) {
        this.aluno = aluno;
    }
    
    public LocalDate getData() {
        return data;
    }
    
    public void setData(LocalDate data) {
        this.data = data;
    }
    
    public Boolean getPresente() {
        return presente;
    }
    
    public void setPresente(Boolean presente) {
        this.presente = presente;
    }
    
    public String getObservacoes() {
        return observacoes;
    }
    
    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
    
    public LocalDateTime getDataRegistro() {
        return dataRegistro;
    }
    
    public void setDataRegistro(LocalDateTime dataRegistro) {
        this.dataRegistro = dataRegistro;
    }
    
    /**
     * Retorna o status da frequência como string
     * @return "Presente" ou "Faltou"
     */
    public String getStatusFrequencia() {
        return presente ? "Presente" : "Faltou";
    }
    
    /**
     * Verifica se o aluno estava presente
     * @return true se presente, false caso contrário
     */
    public boolean isPresente() {
        return Boolean.TRUE.equals(presente);
    }
    
    /**
     * Verifica se o aluno faltou
     * @return true se faltou, false caso contrário
     */
    public boolean isFaltou() {
        return !isPresente();
    }
    
    @Override
    public String toString() {
        return "Frequencia{" +
                "id=" + id +
                ", aluno=" + (aluno != null ? aluno.getNome() : "null") +
                ", data=" + data +
                ", presente=" + presente +
                ", observacoes='" + observacoes + '\'' +
                ", dataRegistro=" + dataRegistro +
                '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Frequencia frequencia = (Frequencia) obj;
        return id != null && id.equals(frequencia.id);
    }
    
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}

