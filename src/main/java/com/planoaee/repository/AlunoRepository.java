package com.planoaee.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.planoaee.database.DatabaseConnection;
import com.planoaee.model.Aluno;

/**
 * Repositório para operações de banco de dados relacionadas aos alunos
 */
public class AlunoRepository {
    
    private static final Logger logger = Logger.getLogger(AlunoRepository.class.getName());
    private final DatabaseConnection dbConnection;
    
    public AlunoRepository() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    /**
     * Salva um novo aluno no banco de dados
     * @param aluno aluno a ser salvo
     * @return aluno salvo com ID gerado
     * @throws SQLException se houver erro na operação
     */
    public Aluno salvar(Aluno aluno) throws SQLException {
        String sql = """
            INSERT INTO alunos (nome, idade, responsavel, contato, observacoes, data_cadastro, ativo)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;
        
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            statement.setString(1, aluno.getNome());
            statement.setInt(2, aluno.getIdade());
            statement.setString(3, aluno.getResponsavel());
            statement.setString(4, aluno.getContato());
            statement.setString(5, aluno.getObservacoes());
            statement.setTimestamp(6, Timestamp.valueOf(aluno.getDataCadastro()));
            statement.setBoolean(7, aluno.getAtivo());
            
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        aluno.setId(generatedKeys.getInt(1));
                        logger.info("Aluno salvo com ID: " + aluno.getId());
                    }
                }
            }
            
            return aluno;
        }
    }
    
    /**
     * Atualiza um aluno existente
     * @param aluno aluno a ser atualizado
     * @return true se atualizado com sucesso, false caso contrário
     * @throws SQLException se houver erro na operação
     */
    public boolean atualizar(Aluno aluno) throws SQLException {
        String sql = """
            UPDATE alunos 
            SET nome = ?, idade = ?, responsavel = ?, contato = ?, observacoes = ?, ativo = ?
            WHERE id = ?
            """;
        
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, aluno.getNome());
            statement.setInt(2, aluno.getIdade());
            statement.setString(3, aluno.getResponsavel());
            statement.setString(4, aluno.getContato());
            statement.setString(5, aluno.getObservacoes());
            statement.setBoolean(6, aluno.getAtivo());
            statement.setInt(7, aluno.getId());
            
            int rowsAffected = statement.executeUpdate();
            logger.info("Aluno atualizado: " + (rowsAffected > 0 ? "sucesso" : "nenhuma linha afetada"));
            return rowsAffected > 0;
        }
    }
    
    /**
     * Busca aluno por ID
     * @param id ID do aluno
     * @return aluno encontrado ou null se não existir
     * @throws SQLException se houver erro na operação
     */
    public Aluno buscarPorId(Integer id) throws SQLException {
        String sql = "SELECT * FROM alunos WHERE id = ?";
        
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, id);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapearResultSetParaAluno(resultSet);
                }
            }
        }
        
        return null;
    }
    
    /**
     * Lista todos os alunos ativos
     * @return lista de alunos
     * @throws SQLException se houver erro na operação
     */
    public List<Aluno> listarTodos() throws SQLException {
        String sql = "SELECT * FROM alunos WHERE ativo = 1 ORDER BY nome";
        List<Aluno> alunos = new ArrayList<>();
        
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            
            while (resultSet.next()) {
                alunos.add(mapearResultSetParaAluno(resultSet));
            }
        }
        
        return alunos;
    }
    
    /**
     * Busca alunos por nome
     * @param nome nome ou parte do nome do aluno
     * @return lista de alunos encontrados
     * @throws SQLException se houver erro na operação
     */
    public List<Aluno> buscarPorNome(String nome) throws SQLException {
        String sql = "SELECT * FROM alunos WHERE nome LIKE ? AND ativo = 1 ORDER BY nome";
        List<Aluno> alunos = new ArrayList<>();
        
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, "%" + nome + "%");
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    alunos.add(mapearResultSetParaAluno(resultSet));
                }
            }
        }
        
        return alunos;
    }
    
    /**
     * Busca alunos por faixa etária
     * @param idadeMinima idade mínima
     * @param idadeMaxima idade máxima
     * @return lista de alunos na faixa etária
     * @throws SQLException se houver erro na operação
     */
    public List<Aluno> buscarPorIdade(int idadeMinima, int idadeMaxima) throws SQLException {
        String sql = "SELECT * FROM alunos WHERE idade BETWEEN ? AND ? AND ativo = 1 ORDER BY idade, nome";
        List<Aluno> alunos = new ArrayList<>();
        
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, idadeMinima);
            statement.setInt(2, idadeMaxima);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    alunos.add(mapearResultSetParaAluno(resultSet));
                }
            }
        }
        
        return alunos;
    }
    
    /**
     * Desativa um aluno (soft delete)
     * @param id ID do aluno
     * @return true se desativado com sucesso, false caso contrário
     * @throws SQLException se houver erro na operação
     */
    public boolean desativar(Integer id) throws SQLException {
        String sql = "UPDATE alunos SET ativo = 0 WHERE id = ?";
        
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, id);
            int rowsAffected = statement.executeUpdate();
            logger.info("Aluno desativado: " + (rowsAffected > 0 ? "sucesso" : "nenhuma linha afetada"));
            return rowsAffected > 0;
        }
    }
    
    /**
     * Conta o total de alunos ativos
     * @return número total de alunos
     * @throws SQLException se houver erro na operação
     */
    public int contarAlunos() throws SQLException {
        String sql = "SELECT COUNT(*) FROM alunos WHERE ativo = 1";
        
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
     * Lista alunos com frequência baixa
     * @param percentualMinimo percentual mínimo de presença
     * @param diasPeriodo período em dias para análise
     * @return lista de alunos com frequência baixa
     * @throws SQLException se houver erro na operação
     */
    public List<Aluno> listarAlunosComFrequenciaBaixa(double percentualMinimo, int diasPeriodo) throws SQLException {
        String sql = """
            SELECT DISTINCT a.* FROM alunos a
            LEFT JOIN frequencia f ON a.id = f.aluno_id 
            WHERE a.ativo = 1 
            AND f.data >= date('now', '-%d days')
            GROUP BY a.id
            HAVING (COUNT(CASE WHEN f.presente = 1 THEN 1 END) * 100.0 / COUNT(f.id)) < ?
            ORDER BY a.nome
            """.formatted(diasPeriodo);
        
        List<Aluno> alunos = new ArrayList<>();
        
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setDouble(1, percentualMinimo);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    alunos.add(mapearResultSetParaAluno(resultSet));
                }
            }
        }
        
        return alunos;
    }
    
    /**
     * Mapeia um ResultSet para um objeto Aluno
     * @param resultSet ResultSet com os dados
     * @return objeto Aluno mapeado
     * @throws SQLException se houver erro na operação
     */
    private Aluno mapearResultSetParaAluno(ResultSet resultSet) throws SQLException {
        Aluno aluno = new Aluno();
        
        aluno.setId(resultSet.getInt("id"));
        aluno.setNome(resultSet.getString("nome"));
        aluno.setIdade(resultSet.getInt("idade"));
        aluno.setResponsavel(resultSet.getString("responsavel"));
        aluno.setContato(resultSet.getString("contato"));
        aluno.setObservacoes(resultSet.getString("observacoes"));
        
        Timestamp timestamp = resultSet.getTimestamp("data_cadastro");
        if (timestamp != null) {
            aluno.setDataCadastro(timestamp.toLocalDateTime());
        }
        
        aluno.setAtivo(resultSet.getBoolean("ativo"));
        
        return aluno;
    }
}
