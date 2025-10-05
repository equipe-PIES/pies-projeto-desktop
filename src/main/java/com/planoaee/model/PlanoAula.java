package com.planoaee.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidade que representa um plano de aula para um aluno
 */
public class PlanoAula {
    
    private Integer id;
    private Usuario professor;
    private Aluno aluno;
    private LocalDate data;
    private String objetivos;
    private String atividades;
    private String materiais;
    private String avaliacao;
    private LocalDateTime dataCriacao;
    
    // Construtores
    public PlanoAula() {
        this.dataCriacao = LocalDateTime.now();
    }
    
    public PlanoAula(Usuario professor, Aluno aluno, LocalDate data, String objetivos, String atividades) {
        this();
        this.professor = professor;
        this.aluno = aluno;
        this.data = data;
        this.objetivos = objetivos;
        this.atividades = atividades;
    }
    
    // Getters e Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public Usuario getProfessor() {
        return professor;
    }
    
    public void setProfessor(Usuario professor) {
        this.professor = professor;
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
    
    public String getObjetivos() {
        return objetivos;
    }
    
    public void setObjetivos(String objetivos) {
        this.objetivos = objetivos;
    }
    
    public String getAtividades() {
        return atividades;
    }
    
    public void setAtividades(String atividades) {
        this.atividades = atividades;
    }
    
    public String getMateriais() {
        return materiais;
    }
    
    public void setMateriais(String materiais) {
        this.materiais = materiais;
    }
    
    public String getAvaliacao() {
        return avaliacao;
    }
    
    public void setAvaliacao(String avaliacao) {
        this.avaliacao = avaliacao;
    }
    
    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }
    
    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }
    
    /**
     * Retorna uma descrição resumida do plano
     * @return string formatada com aluno e data
     */
    public String getDescricaoResumida() {
        StringBuilder descricao = new StringBuilder();
        if (aluno != null) {
            descricao.append(aluno.getNome());
        }
        if (data != null) {
            descricao.append(" - ").append(data.toString());
        }
        return descricao.toString();
    }
    
    /**
     * Verifica se o plano tem objetivos definidos
     * @return true se tem objetivos, false caso contrário
     */
    public boolean hasObjetivos() {
        return objetivos != null && !objetivos.trim().isEmpty();
    }
    
    /**
     * Verifica se o plano tem atividades definidas
     * @return true se tem atividades, false caso contrário
     */
    public boolean hasAtividades() {
        return atividades != null && !atividades.trim().isEmpty();
    }
    
    /**
     * Verifica se o plano tem materiais definidos
     * @return true se tem materiais, false caso contrário
     */
    public boolean hasMateriais() {
        return materiais != null && !materiais.trim().isEmpty();
    }
    
    /**
     * Verifica se o plano tem avaliação definida
     * @return true se tem avaliação, false caso contrário
     */
    public boolean hasAvaliacao() {
        return avaliacao != null && !avaliacao.trim().isEmpty();
    }
    
    /**
     * Retorna o percentual de completude do plano (0-100)
     * @return percentual baseado nos campos preenchidos
     */
    public int getPercentualCompletude() {
        int camposPreenchidos = 0;
        int totalCampos = 4; // objetivos, atividades, materiais, avaliacao
        
        if (hasObjetivos()) camposPreenchidos++;
        if (hasAtividades()) camposPreenchidos++;
        if (hasMateriais()) camposPreenchidos++;
        if (hasAvaliacao()) camposPreenchidos++;
        
        return (camposPreenchidos * 100) / totalCampos;
    }
    
    @Override
    public String toString() {
        return "PlanoAula{" +
                "id=" + id +
                ", professor=" + (professor != null ? professor.getNome() : "null") +
                ", aluno=" + (aluno != null ? aluno.getNome() : "null") +
                ", data=" + data +
                ", dataCriacao=" + dataCriacao +
                '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        PlanoAula planoAula = (PlanoAula) obj;
        return id != null && id.equals(planoAula.id);
    }
    
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}

