package com.planoaee.repository;

import com.planoaee.database.DatabaseConnection;
import com.planoaee.model.Usuario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Repositório para operações de banco de dados relacionadas aos usuários
 */
public class UsuarioRepository {
    
    private static final Logger logger = Logger.getLogger(UsuarioRepository.class.getName());
    private final DatabaseConnection dbConnection;
    
    public UsuarioRepository() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    /**
     * Salva um novo usuário no banco de dados
     * @param usuario usuário a ser salvo
     * @return usuário salvo com ID gerado
     * @throws SQLException se houver erro na operação
     */
    public Usuario salvar(Usuario usuario) throws SQLException {
        String sql = """
            INSERT INTO usuarios (nome, email, senha, tipo, data_cadastro, ativo)
            VALUES (?, ?, ?, ?, ?, ?)
            """;
        
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            statement.setString(1, usuario.getNome());
            statement.setString(2, usuario.getEmail());
            statement.setString(3, usuario.getSenha());
            statement.setString(4, usuario.getTipo().name());
            statement.setTimestamp(5, Timestamp.valueOf(usuario.getDataCadastro()));
            statement.setBoolean(6, usuario.getAtivo());
            
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        usuario.setId(generatedKeys.getInt(1));
                        logger.info("Usuário salvo com ID: " + usuario.getId());
                    }
                }
            }
            
            return usuario;
        }
    }
    
    /**
     * Atualiza um usuário existente
     * @param usuario usuário a ser atualizado
     * @return true se atualizado com sucesso, false caso contrário
     * @throws SQLException se houver erro na operação
     */
    public boolean atualizar(Usuario usuario) throws SQLException {
        String sql = """
            UPDATE usuarios 
            SET nome = ?, email = ?, senha = ?, tipo = ?, ativo = ?
            WHERE id = ?
            """;
        
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, usuario.getNome());
            statement.setString(2, usuario.getEmail());
            statement.setString(3, usuario.getSenha());
            statement.setString(4, usuario.getTipo().name());
            statement.setBoolean(5, usuario.getAtivo());
            statement.setInt(6, usuario.getId());
            
            int rowsAffected = statement.executeUpdate();
            logger.info("Usuário atualizado: " + (rowsAffected > 0 ? "sucesso" : "nenhuma linha afetada"));
            return rowsAffected > 0;
        }
    }
    
    /**
     * Busca usuário por email
     * @param email email do usuário
     * @return usuário encontrado ou null se não existir
     * @throws SQLException se houver erro na operação
     */
    public Usuario buscarPorEmail(String email) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE email = ? AND ativo = 1";
        
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, email);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapearResultSetParaUsuario(resultSet);
                }
            }
        }
        
        return null;
    }
    
    /**
     * Busca usuário por ID
     * @param id ID do usuário
     * @return usuário encontrado ou null se não existir
     * @throws SQLException se houver erro na operação
     */
    public Usuario buscarPorId(Integer id) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE id = ?";
        
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, id);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapearResultSetParaUsuario(resultSet);
                }
            }
        }
        
        return null;
    }
    
    /**
     * Lista todos os usuários ativos
     * @return lista de usuários
     * @throws SQLException se houver erro na operação
     */
    public List<Usuario> listarTodos() throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE ativo = 1 ORDER BY nome";
        List<Usuario> usuarios = new ArrayList<>();
        
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            
            while (resultSet.next()) {
                usuarios.add(mapearResultSetParaUsuario(resultSet));
            }
        }
        
        return usuarios;
    }
    
    /**
     * Lista usuários por tipo
     * @param tipo tipo do usuário
     * @return lista de usuários do tipo especificado
     * @throws SQLException se houver erro na operação
     */
    public List<Usuario> listarPorTipo(Usuario.TipoUsuario tipo) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE tipo = ? AND ativo = 1 ORDER BY nome";
        List<Usuario> usuarios = new ArrayList<>();
        
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, tipo.name());
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    usuarios.add(mapearResultSetParaUsuario(resultSet));
                }
            }
        }
        
        return usuarios;
    }
    
    /**
     * Lista professores
     * @return lista de professores
     * @throws SQLException se houver erro na operação
     */
    public List<Usuario> listarProfessores() throws SQLException {
        return listarPorTipo(Usuario.TipoUsuario.PROFESSOR);
    }
    
    /**
     * Verifica se email já existe
     * @param email email a ser verificado
     * @param idUsuario ID do usuário (para excluir da verificação em atualizações)
     * @return true se email existe, false caso contrário
     * @throws SQLException se houver erro na operação
     */
    public boolean emailExiste(String email, Integer idUsuario) throws SQLException {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE email = ? AND id != ?";
        
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, email);
            statement.setInt(2, idUsuario != null ? idUsuario : -1);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        }
        
        return false;
    }
    
    /**
     * Desativa um usuário (soft delete)
     * @param id ID do usuário
     * @return true se desativado com sucesso, false caso contrário
     * @throws SQLException se houver erro na operação
     */
    public boolean desativar(Integer id) throws SQLException {
        String sql = "UPDATE usuarios SET ativo = 0 WHERE id = ?";
        
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, id);
            int rowsAffected = statement.executeUpdate();
            logger.info("Usuário desativado: " + (rowsAffected > 0 ? "sucesso" : "nenhuma linha afetada"));
            return rowsAffected > 0;
        }
    }
    
    /**
     * Conta o total de usuários ativos
     * @return número total de usuários
     * @throws SQLException se houver erro na operação
     */
    public int contarUsuarios() throws SQLException {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE ativo = 1";
        
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        }
        
        return 0;
    }
    
    /**
     * Mapeia um ResultSet para um objeto Usuario
     * @param resultSet ResultSet com os dados
     * @return objeto Usuario mapeado
     * @throws SQLException se houver erro na operação
     */
    private Usuario mapearResultSetParaUsuario(ResultSet resultSet) throws SQLException {
        Usuario usuario = new Usuario();
        
        usuario.setId(resultSet.getInt("id"));
        usuario.setNome(resultSet.getString("nome"));
        usuario.setEmail(resultSet.getString("email"));
        usuario.setSenha(resultSet.getString("senha"));
        usuario.setTipo(Usuario.TipoUsuario.valueOf(resultSet.getString("tipo")));
        
        Timestamp timestamp = resultSet.getTimestamp("data_cadastro");
        if (timestamp != null) {
            usuario.setDataCadastro(timestamp.toLocalDateTime());
        }
        
        usuario.setAtivo(resultSet.getBoolean("ativo"));
        
        return usuario;
    }
}

