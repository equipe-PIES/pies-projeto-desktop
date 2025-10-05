package com.planoaee.model;

import java.time.LocalDateTime;

/**
 * Entidade que representa uma configuração do sistema
 */
public class Configuracao {
    
    private Integer id;
    private String chave;
    private String valor;
    private String descricao;
    private LocalDateTime dataModificacao;
    
    // Construtores
    public Configuracao() {
        this.dataModificacao = LocalDateTime.now();
    }
    
    public Configuracao(String chave, String valor, String descricao) {
        this();
        this.chave = chave;
        this.valor = valor;
        this.descricao = descricao;
    }
    
    // Getters e Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getChave() {
        return chave;
    }
    
    public void setChave(String chave) {
        this.chave = chave;
    }
    
    public String getValor() {
        return valor;
    }
    
    public void setValor(String valor) {
        this.valor = valor;
        this.dataModificacao = LocalDateTime.now();
    }
    
    public String getDescricao() {
        return descricao;
    }
    
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    
    public LocalDateTime getDataModificacao() {
        return dataModificacao;
    }
    
    public void setDataModificacao(LocalDateTime dataModificacao) {
        this.dataModificacao = dataModificacao;
    }
    
    /**
     * Retorna o valor como boolean
     * @return true se valor for "true", false caso contrário
     */
    public boolean getValorBoolean() {
        return "true".equalsIgnoreCase(valor);
    }
    
    /**
     * Define o valor como boolean
     * @param valor o valor boolean a ser definido
     */
    public void setValorBoolean(boolean valor) {
        this.valor = valor ? "true" : "false";
        this.dataModificacao = LocalDateTime.now();
    }
    
    /**
     * Retorna o valor como integer
     * @return valor convertido para integer, ou 0 se não for possível converter
     */
    public int getValorInt() {
        try {
            return Integer.parseInt(valor);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    /**
     * Define o valor como integer
     * @param valor o valor integer a ser definido
     */
    public void setValorInt(int valor) {
        this.valor = String.valueOf(valor);
        this.dataModificacao = LocalDateTime.now();
    }
    
    @Override
    public String toString() {
        return "Configuracao{" +
                "id=" + id +
                ", chave='" + chave + '\'' +
                ", valor='" + valor + '\'' +
                ", descricao='" + descricao + '\'' +
                ", dataModificacao=" + dataModificacao +
                '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Configuracao configuracao = (Configuracao) obj;
        return id != null && id.equals(configuracao.id);
    }
    
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}

