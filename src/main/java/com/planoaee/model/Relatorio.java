package com.planoaee.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidade que representa um relatório profissional sobre um aluno
 */
public class Relatorio {
    
    private Integer id;
    private Usuario profissional;
    private Aluno aluno;
    private LocalDate data;
    private TipoRelatorio tipo;
    private String conteudo;
    private String periodo;
    private LocalDateTime dataCriacao;
    
    /**
     * Enum que define os tipos de relatórios
     */
    public enum TipoRelatorio {
        EDUCACIONAL("Relatório Educacional"),
        PSICOLOGICO("Relatório Psicológico"),
        FISIOTERAPICO("Relatório Fisioterápico");
        
        private final String descricao;
        
        TipoRelatorio(String descricao) {
            this.descricao = descricao;
        }
        
        public String getDescricao() {
            return descricao;
        }
        
        @Override
        public String toString() {
            return descricao;
        }
    }
    
    // Construtores
    public Relatorio() {
        this.dataCriacao = LocalDateTime.now();
    }
    
    public Relatorio(Usuario profissional, Aluno aluno, LocalDate data, TipoRelatorio tipo, String conteudo) {
        this();
        this.profissional = profissional;
        this.aluno = aluno;
        this.data = data;
        this.tipo = tipo;
        this.conteudo = conteudo;
    }
    
    // Getters e Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public Usuario getProfissional() {
        return profissional;
    }
    
    public void setProfissional(Usuario profissional) {
        this.profissional = profissional;
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
    
    public TipoRelatorio getTipo() {
        return tipo;
    }
    
    public void setTipo(TipoRelatorio tipo) {
        this.tipo = tipo;
    }
    
    public String getConteudo() {
        return conteudo;
    }
    
    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }
    
    public String getPeriodo() {
        return periodo;
    }
    
    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }
    
    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }
    
    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }
    
    /**
     * Retorna o título formatado do relatório
     * @return string com tipo e período do relatório
     */
    public String getTitulo() {
        StringBuilder titulo = new StringBuilder();
        if (tipo != null) {
            titulo.append(tipo.getDescricao());
        }
        if (periodo != null && !periodo.trim().isEmpty()) {
            titulo.append(" - ").append(periodo);
        }
        return titulo.toString();
    }
    
    /**
     * Verifica se o relatório é educacional
     * @return true se for educacional, false caso contrário
     */
    public boolean isEducacional() {
        return TipoRelatorio.EDUCACIONAL.equals(this.tipo);
    }
    
    /**
     * Verifica se o relatório é psicológico
     * @return true se for psicológico, false caso contrário
     */
    public boolean isPsicologico() {
        return TipoRelatorio.PSICOLOGICO.equals(this.tipo);
    }
    
    /**
     * Verifica se o relatório é fisioterápico
     * @return true se for fisioterápico, false caso contrário
     */
    public boolean isFisioterapico() {
        return TipoRelatorio.FISIOTERAPICO.equals(this.tipo);
    }
    
    @Override
    public String toString() {
        return "Relatorio{" +
                "id=" + id +
                ", profissional=" + (profissional != null ? profissional.getNome() : "null") +
                ", aluno=" + (aluno != null ? aluno.getNome() : "null") +
                ", data=" + data +
                ", tipo=" + tipo +
                ", periodo='" + periodo + '\'' +
                ", dataCriacao=" + dataCriacao +
                '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Relatorio relatorio = (Relatorio) obj;
        return id != null && id.equals(relatorio.id);
    }
    
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}

