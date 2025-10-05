package com.planoaee.service;

import com.planoaee.model.Usuario;
import com.planoaee.repository.UsuarioRepository;
import com.planoaee.util.PasswordUtil;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Serviço para regras de negócio relacionadas aos usuários
 */
public class UsuarioService {
    
    private static final Logger logger = Logger.getLogger(UsuarioService.class.getName());
    private final UsuarioRepository usuarioRepository;
    
    public UsuarioService() {
        this.usuarioRepository = new UsuarioRepository();
    }
    
    /**
     * Cadastra um novo usuário no sistema
     * @param usuario usuário a ser cadastrado
     * @return usuário cadastrado com ID gerado
     * @throws IllegalArgumentException se dados inválidos
     * @throws RuntimeException se houver erro na operação
     */
    public Usuario cadastrarUsuario(Usuario usuario) {
        try {
            // Validações
            validarDadosUsuario(usuario);
            
            // Verifica se email já existe
            if (usuarioRepository.emailExiste(usuario.getEmail(), null)) {
                throw new IllegalArgumentException("E-mail já cadastrado no sistema");
            }
            
            // Criptografa a senha
            String senhaCriptografada = PasswordUtil.hashPassword(usuario.getSenha());
            usuario.setSenha(senhaCriptografada);
            
            // Salva no banco
            Usuario usuarioSalvo = usuarioRepository.salvar(usuario);
            
            logger.info("Usuário cadastrado com sucesso: " + usuarioSalvo.getEmail());
            return usuarioSalvo;
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao cadastrar usuário", e);
            throw new RuntimeException("Erro interno do sistema", e);
        }
    }
    
    /**
     * Autentica um usuário no sistema
     * @param email email do usuário
     * @param senha senha em texto plano
     * @return usuário autenticado ou null se credenciais inválidas
     */
    public Usuario autenticar(String email, String senha) {
        try {
            if (email == null || email.trim().isEmpty() || 
                senha == null || senha.isEmpty()) {
                return null;
            }
            
            Usuario usuario = usuarioRepository.buscarPorEmail(email.trim());
            if (usuario != null && PasswordUtil.verifyPassword(senha, usuario.getSenha())) {
                logger.info("Usuário autenticado com sucesso: " + email);
                return usuario;
            }
            
            logger.warning("Tentativa de login falhada para: " + email);
            return null;
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao autenticar usuário", e);
            throw new RuntimeException("Erro interno do sistema", e);
        }
    }
    
    /**
     * Atualiza dados de um usuário
     * @param usuario usuário com dados atualizados
     * @return true se atualizado com sucesso, false caso contrário
     * @throws IllegalArgumentException se dados inválidos
     * @throws RuntimeException se houver erro na operação
     */
    public boolean atualizarUsuario(Usuario usuario) {
        try {
            // Validações
            validarDadosUsuario(usuario);
            
            // Verifica se email já existe para outro usuário
            if (usuarioRepository.emailExiste(usuario.getEmail(), usuario.getId())) {
                throw new IllegalArgumentException("E-mail já cadastrado para outro usuário");
            }
            
            // Se a senha foi alterada, criptografa
            if (usuario.getSenha() != null && !usuario.getSenha().isEmpty()) {
                // Verifica se a senha já está criptografada (hash tem 64 caracteres)
                if (usuario.getSenha().length() != 64) {
                    String senhaCriptografada = PasswordUtil.hashPassword(usuario.getSenha());
                    usuario.setSenha(senhaCriptografada);
                }
            }
            
            boolean sucesso = usuarioRepository.atualizar(usuario);
            
            if (sucesso) {
                logger.info("Usuário atualizado com sucesso: " + usuario.getEmail());
            }
            
            return sucesso;
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao atualizar usuário", e);
            throw new RuntimeException("Erro interno do sistema", e);
        }
    }
    
    /**
     * Lista usuários por tipo
     * @param tipo tipo do usuário
     * @return lista de usuários do tipo especificado
     */
    public List<Usuario> listarPorTipo(Usuario.TipoUsuario tipo) {
        try {
            return usuarioRepository.listarPorTipo(tipo);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao listar usuários por tipo", e);
            throw new RuntimeException("Erro interno do sistema", e);
        }
    }
    
    /**
     * Lista todos os usuários ativos
     * @return lista de usuários
     */
    public List<Usuario> listarTodos() {
        try {
            return usuarioRepository.listarTodos();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao listar usuários", e);
            throw new RuntimeException("Erro interno do sistema", e);
        }
    }
    
    /**
     * Lista professores
     * @return lista de professores
     */
    public List<Usuario> listarProfessores() {
        return listarPorTipo(Usuario.TipoUsuario.PROFESSOR);
    }
    
    /**
     * Lista psicólogos
     * @return lista de psicólogos
     */
    public List<Usuario> listarPsicologos() {
        return listarPorTipo(Usuario.TipoUsuario.PSICOLOGO);
    }
    
    /**
     * Lista fisioterapeutas
     * @return lista de fisioterapeutas
     */
    public List<Usuario> listarFisioterapeutas() {
        return listarPorTipo(Usuario.TipoUsuario.FISIOTERAPEUTA);
    }
    
    /**
     * Busca usuário por ID
     * @param id ID do usuário
     * @return usuário encontrado ou null
     */
    public Usuario buscarPorId(Integer id) {
        try {
            return usuarioRepository.buscarPorId(id);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao buscar usuário por ID", e);
            throw new RuntimeException("Erro interno do sistema", e);
        }
    }
    
    /**
     * Busca usuário por email
     * @param email email do usuário
     * @return usuário encontrado ou null
     */
    public Usuario buscarPorEmail(String email) {
        try {
            return usuarioRepository.buscarPorEmail(email);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao buscar usuário por email", e);
            throw new RuntimeException("Erro interno do sistema", e);
        }
    }
    
    /**
     * Desativa um usuário
     * @param id ID do usuário
     * @return true se desativado com sucesso, false caso contrário
     */
    public boolean desativarUsuario(Integer id) {
        try {
            boolean sucesso = usuarioRepository.desativar(id);
            
            if (sucesso) {
                logger.info("Usuário desativado com sucesso: ID " + id);
            }
            
            return sucesso;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao desativar usuário", e);
            throw new RuntimeException("Erro interno do sistema", e);
        }
    }
    
    /**
     * Altera senha de um usuário
     * @param usuarioId ID do usuário
     * @param senhaAtual senha atual
     * @param novaSenha nova senha
     * @return true se alterada com sucesso, false caso contrário
     */
    public boolean alterarSenha(Integer usuarioId, String senhaAtual, String novaSenha) {
        try {
            Usuario usuario = usuarioRepository.buscarPorId(usuarioId);
            if (usuario == null) {
                throw new IllegalArgumentException("Usuário não encontrado");
            }
            
            // Verifica senha atual
            if (!PasswordUtil.verifyPassword(senhaAtual, usuario.getSenha())) {
                throw new IllegalArgumentException("Senha atual incorreta");
            }
            
            // Valida nova senha
            if (novaSenha == null || novaSenha.length() < 6) {
                throw new IllegalArgumentException("Nova senha deve ter pelo menos 6 caracteres");
            }
            
            if (!PasswordUtil.isStrongPassword(novaSenha)) {
                throw new IllegalArgumentException("Nova senha deve conter pelo menos uma letra maiúscula, uma minúscula e um número");
            }
            
            // Criptografa nova senha
            String novaSenhaCriptografada = PasswordUtil.hashPassword(novaSenha);
            usuario.setSenha(novaSenhaCriptografada);
            
            boolean sucesso = usuarioRepository.atualizar(usuario);
            
            if (sucesso) {
                logger.info("Senha alterada com sucesso para usuário: " + usuario.getEmail());
            }
            
            return sucesso;
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao alterar senha", e);
            throw new RuntimeException("Erro interno do sistema", e);
        }
    }
    
    /**
     * Gera nova senha temporária para um usuário
     * @param email email do usuário
     * @return nova senha temporária
     */
    public String gerarNovaSenhaTemporaria(String email) {
        try {
            Usuario usuario = usuarioRepository.buscarPorEmail(email);
            if (usuario == null) {
                throw new IllegalArgumentException("Usuário não encontrado");
            }
            
            String novaSenha = PasswordUtil.generateTemporaryPassword(8);
            String senhaCriptografada = PasswordUtil.hashPassword(novaSenha);
            
            usuario.setSenha(senhaCriptografada);
            usuarioRepository.atualizar(usuario);
            
            logger.info("Nova senha temporária gerada para: " + email);
            return novaSenha;
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao gerar nova senha", e);
            throw new RuntimeException("Erro interno do sistema", e);
        }
    }
    
    /**
     * Valida dados básicos de um usuário
     * @param usuario usuário a ser validado
     * @throws IllegalArgumentException se dados inválidos
     */
    private void validarDadosUsuario(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("Usuário não pode ser nulo");
        }
        
        if (usuario.getNome() == null || usuario.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }
        
        if (usuario.getEmail() == null || usuario.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("E-mail é obrigatório");
        }
        
        if (!isEmailValido(usuario.getEmail())) {
            throw new IllegalArgumentException("E-mail inválido");
        }
        
        if (usuario.getSenha() == null || usuario.getSenha().isEmpty()) {
            throw new IllegalArgumentException("Senha é obrigatória");
        }
        
        if (usuario.getTipo() == null) {
            throw new IllegalArgumentException("Tipo de usuário é obrigatório");
        }
    }
    
    /**
     * Valida formato do e-mail
     * @param email e-mail a ser validado
     * @return true se válido, false caso contrário
     */
    private boolean isEmailValido(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
    
    /**
     * Conta o total de usuários ativos
     * @return número total de usuários
     */
    public int contarUsuarios() {
        try {
            return usuarioRepository.contarUsuarios();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao contar usuários", e);
            throw new RuntimeException("Erro interno do sistema", e);
        }
    }
}

