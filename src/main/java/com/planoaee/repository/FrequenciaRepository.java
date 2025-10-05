package com.planoaee.repository;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.planoaee.database.DatabaseConnection;
import com.planoaee.model.Frequencia;

/**
 * Repositório para operações de banco de dados relacionadas à frequência
 */
public class FrequenciaRepository {
    
    private static final Logger logger = Logger.getLogger(FrequenciaRepository.class.getName());
    private final DatabaseConnection dbConnection;
    
    public FrequenciaRepository() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    /**
     * Salva uma nova frequência no banco de dados
     * @param frequencia frequência a ser salva
     * @return frequência salva com ID gerado
     * @throws SQLException se houver erro na operação
     */
    public Frequencia salvar(Frequencia frequencia) throws SQLException {
        String sql = """
            INSERT INTO frequencia (aluno_id, data, presente, observacoes, data_registro)
            VALUES (?, ?, ?, ?, ?)
            """;
        
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            statement.setInt(1, frequencia.getAluno().getId());
            statement.setDate(2, Date.valueOf(frequencia.getData()));
            statement.setBoolean(3, frequencia.getPresente());
            statement.setString(4, frequencia.getObservacoes());
            statement.setTimestamp(5, Timestamp.valueOf(frequencia.getDataRegistro()));
            
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        frequencia.setId(generatedKeys.getInt(1));
                        logger.info("Frequência salva com ID: " + frequencia.getId());
                    }
                }
            }
            
            return frequencia;
        }
    }
    
    /**
     * Atualiza uma frequência existente
     * @param frequencia frequência a ser atualizada
     * @return true se atualizada com sucesso, false caso contrário
     * @throws SQLException se houver erro na operação
     */
    public boolean atualizar(Frequencia frequencia) throws SQLException {
        String sql = """
            UPDATE frequencia 
            SET presente = ?, observacoes = ?
            WHERE id = ?
            """;
        
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setBoolean(1, frequencia.getPresente());
            statement.setString(2, frequencia.getObservacoes());
            statement.setInt(3, frequencia.getId());
            
            int rowsAffected = statement.executeUpdate();
            logger.info("Frequência atualizada: " + (rowsAffected > 0 ? "sucesso" : "nenhuma linha afetada"));
            return rowsAffected > 0;
        }
    }
    
    /**
     * Busca frequência por ID
     * @param id ID da frequência
     * @return frequência encontrada ou null se não existir
     * @throws SQLException se houver erro na operação
     */
    public Frequencia buscarPorId(Integer id) throws SQLException {
        String sql = """
            SELECT f.*, a.nome as aluno_nome 
            FROM frequencia f 
            JOIN alunos a ON f.aluno_id = a.id 
            WHERE f.id = ?
            """;
        
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, id);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapearResultSetParaFrequencia(resultSet);
                }
            }
        }
        
        return null;
    }
    
    /**
     * Lista frequências de um aluno
     * @param alunoId ID do aluno
     * @return lista de frequências do aluno
     * @throws SQLException se houver erro na operação
     */
    public List<Frequencia> buscarPorAluno(Integer alunoId) throws SQLException {
        String sql = """
            SELECT f.*, a.nome as aluno_nome 
            FROM frequencia f 
            JOIN alunos a ON f.aluno_id = a.id 
            WHERE f.aluno_id = ? 
            ORDER BY f.data DESC
            """;
        
        List<Frequencia> frequencias = new ArrayList<>();
        
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, alunoId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    frequencias.add(mapearResultSetParaFrequencia(resultSet));
                }
            }
        }
        
        return frequencias;
    }
    
    /**
     * Lista frequências de uma data específica
     * @param data data a ser consultada
     * @return lista de frequências da data
     * @throws SQLException se houver erro na operação
     */
    public List<Frequencia> buscarPorData(LocalDate data) throws SQLException {
        String sql = """
            SELECT f.*, a.nome as aluno_nome 
            FROM frequencia f 
            JOIN alunos a ON f.aluno_id = a.id 
            WHERE f.data = ? 
            ORDER BY a.nome
            """;
        
        List<Frequencia> frequencias = new ArrayList<>();
        
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setDate(1, Date.valueOf(data));
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    frequencias.add(mapearResultSetParaFrequencia(resultSet));
                }
            }
        }
        
        return frequencias;
    }
    
    /**
     * Lista frequências em um período
     * @param dataInicio data de início
     * @param dataFim data de fim
     * @return lista de frequências do período
     * @throws SQLException se houver erro na operação
     */
    public List<Frequencia> buscarPorPeriodo(LocalDate dataInicio, LocalDate dataFim) throws SQLException {
        String sql = """
            SELECT f.*, a.nome as aluno_nome 
            FROM frequencia f 
            JOIN alunos a ON f.aluno_id = a.id 
            WHERE f.data BETWEEN ? AND ? 
            ORDER BY f.data DESC, a.nome
            """;
        
        List<Frequencia> frequencias = new ArrayList<>();
        
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setDate(1, Date.valueOf(dataInicio));
            statement.setDate(2, Date.valueOf(dataFim));
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    frequencias.add(mapearResultSetParaFrequencia(resultSet));
                }
            }
        }
        
        return frequencias;
    }
    
    /**
     * Lista frequências de um aluno em um período
     * @param alunoId ID do aluno
     * @param dataInicio data de início
     * @param dataFim data de fim
     * @return lista de frequências do aluno no período
     * @throws SQLException se houver erro na operação
     */
    public List<Frequencia> buscarPorAlunoEPeriodo(Integer alunoId, LocalDate dataInicio, LocalDate dataFim) throws SQLException {
        String sql = """
            SELECT f.*, a.nome as aluno_nome 
            FROM frequencia f 
            JOIN alunos a ON f.aluno_id = a.id 
            WHERE f.aluno_id = ? AND f.data BETWEEN ? AND ? 
            ORDER BY f.data DESC
            """;
        
        List<Frequencia> frequencias = new ArrayList<>();
        
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, alunoId);
            statement.setDate(2, Date.valueOf(dataInicio));
            statement.setDate(3, Date.valueOf(dataFim));
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    frequencias.add(mapearResultSetParaFrequencia(resultSet));
                }
            }
        }
        
        return frequencias;
    }
    
    /**
     * Verifica se já existe frequência registrada para um aluno em uma data
     * @param alunoId ID do aluno
     * @param data data a ser verificada
     * @return true se já existe, false caso contrário
     * @throws SQLException se houver erro na operação
     */
    public boolean existeFrequencia(Integer alunoId, LocalDate data) throws SQLException {
        String sql = "SELECT COUNT(*) FROM frequencia WHERE aluno_id = ? AND data = ?";
        
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, alunoId);
            statement.setDate(2, Date.valueOf(data));
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        }
        
        return false;
    }
    
    /**
     * Calcula estatísticas de frequência de um aluno
     * @param alunoId ID do aluno
     * @param dataInicio data de início
     * @param dataFim data de fim
     * @return array com [total_dias, dias_presentes, dias_faltas, percentual_presenca]
     * @throws SQLException se houver erro na operação
     */
    public double[] calcularEstatisticas(Integer alunoId, LocalDate dataInicio, LocalDate dataFim) throws SQLException {
        String sql = """
            SELECT 
                COUNT(*) as total_dias,
                COUNT(CASE WHEN presente = 1 THEN 1 END) as dias_presentes,
                COUNT(CASE WHEN presente = 0 THEN 1 END) as dias_faltas
            FROM frequencia 
            WHERE aluno_id = ? AND data BETWEEN ? AND ?
            """;
        
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, alunoId);
            statement.setDate(2, Date.valueOf(dataInicio));
            statement.setDate(3, Date.valueOf(dataFim));
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int totalDias = resultSet.getInt("total_dias");
                    int diasPresentes = resultSet.getInt("dias_presentes");
                    int diasFaltas = resultSet.getInt("dias_faltas");
                    double percentualPresenca = totalDias > 0 ? (diasPresentes * 100.0 / totalDias) : 0.0;
                    
                    return new double[]{totalDias, diasPresentes, diasFaltas, percentualPresenca};
                }
            }
        }
        
        return new double[]{0, 0, 0, 0};
    }
    
    /**
     * Lista alunos com frequência baixa em um período
     * @param percentualMinimo percentual mínimo de presença
     * @param dataInicio data de início
     * @param dataFim data de fim
     * @return lista de alunos com frequência baixa
     * @throws SQLException se houver erro na operação
     */
    public List<Object[]> listarAlunosComFrequenciaBaixa(double percentualMinimo, LocalDate dataInicio, LocalDate dataFim) throws SQLException {
        String sql = """
            SELECT 
                a.id,
                a.nome,
                COUNT(f.id) as total_dias,
                COUNT(CASE WHEN f.presente = 1 THEN 1 END) as dias_presentes,
                (COUNT(CASE WHEN f.presente = 1 THEN 1 END) * 100.0 / COUNT(f.id)) as percentual
            FROM alunos a
            LEFT JOIN frequencia f ON a.id = f.aluno_id AND f.data BETWEEN ? AND ?
            WHERE a.ativo = 1
            GROUP BY a.id, a.nome
            HAVING COUNT(f.id) > 0 AND percentual < ?
            ORDER BY percentual ASC
            """;
        
        List<Object[]> resultados = new ArrayList<>();
        
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setDate(1, Date.valueOf(dataInicio));
            statement.setDate(2, Date.valueOf(dataFim));
            statement.setDouble(3, percentualMinimo);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Object[] resultado = {
                        resultSet.getInt("id"),
                        resultSet.getString("nome"),
                        resultSet.getInt("total_dias"),
                        resultSet.getInt("dias_presentes"),
                        resultSet.getDouble("percentual")
                    };
                    resultados.add(resultado);
                }
            }
        }
        
        return resultados;
    }
    
    /**
     * Mapeia um ResultSet para um objeto Frequencia
     * @param resultSet ResultSet com os dados
     * @return objeto Frequencia mapeado
     * @throws SQLException se houver erro na operação
     */
    private Frequencia mapearResultSetParaFrequencia(ResultSet resultSet) throws SQLException {
        Frequencia frequencia = new Frequencia();
        
        frequencia.setId(resultSet.getInt("id"));
        frequencia.setData(resultSet.getDate("data").toLocalDate());
        frequencia.setPresente(resultSet.getBoolean("presente"));
        frequencia.setObservacoes(resultSet.getString("observacoes"));
        
        Timestamp timestamp = resultSet.getTimestamp("data_registro");
        if (timestamp != null) {
            frequencia.setDataRegistro(timestamp.toLocalDateTime());
        }
        
        // Cria um objeto Aluno básico apenas com ID e nome
        com.planoaee.model.Aluno aluno = new com.planoaee.model.Aluno();
        aluno.setId(resultSet.getInt("aluno_id"));
        aluno.setNome(resultSet.getString("aluno_nome"));
        frequencia.setAluno(aluno);
        
        return frequencia;
    }
}
