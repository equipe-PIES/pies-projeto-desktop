package com.planoaee.model;

import java.time.LocalDateTime;

/**
 * Entidade que representa um usuário do sistema (profissional da APAPEQ)
 * Pode ser Professor, Psicólogo, Psiquiatra ou Fisioterapeuta
 */
public class Usuario {
    
    private Integer id;
    private String nome;
    private String email;
    private String senha;
    private TipoUsuario tipo;
    private LocalDateTime dataCadastro;
    private Boolean ativo;
    
    /**
     * Enum que define os tipos de usuários do sistema
     */
    public enum TipoUsuario {
        PROFESSOR("Professor"),
        PSICOLOGO("Psicólogo"),
        PSIQUIATRA("Psiquiatra"),
        FISIOTERAPEUTA("Fisioterapeuta");
        
        private final String descricao;
        
        TipoUsuario(String descricao) {
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
    public Usuario() {
        this.ativo = true;
        this.dataCadastro = LocalDateTime.now();
    }
    
    public Usuario(String nome, String email, String senha, TipoUsuario tipo) {
        this();
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.tipo = tipo;
    }
    
    // Getters e Setters
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
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getSenha() {
        return senha;
    }
    
    public void setSenha(String senha) {
        this.senha = senha;
    }
    
    public TipoUsuario getTipo() {
        return tipo;
    }
    
    public void setTipo(TipoUsuario tipo) {
        this.tipo = tipo;
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
    
    /**
     * Verifica se o usuário é um professor
     * @return true se for professor, false caso contrário
     */
    public boolean isProfessor() {
        return TipoUsuario.PROFESSOR.equals(this.tipo);
    }
    
    /**
     * Verifica se o usuário é um psicólogo
     * @return true se for psicólogo, false caso contrário
     */
    public boolean isPsicologo() {
        return TipoUsuario.PSICOLOGO.equals(this.tipo);
    }
    
    /**
     * Verifica se o usuário é um psiquiatra
     * @return true se for psiquiatra, false caso contrário
     */
    public boolean isPsiquiatra() {
        return TipoUsuario.PSIQUIATRA.equals(this.tipo);
    }
    
    /**
     * Verifica se o usuário é um fisioterapeuta
     * @return true se for fisioterapeuta, false caso contrário
     */
    public boolean isFisioterapeuta() {
        return TipoUsuario.FISIOTERAPEUTA.equals(this.tipo);
    }
    
    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", tipo=" + tipo +
                ", dataCadastro=" + dataCadastro +
                ", ativo=" + ativo +
                '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Usuario usuario = (Usuario) obj;
        return id != null && id.equals(usuario.id);
    }
    
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}

