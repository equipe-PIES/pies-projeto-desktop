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
import com.planoaee.model.Relatorio;

/**
 * Repositório para operações de banco de dados relacionadas aos relatórios
 */
public class RelatorioRepository {
    
    private static final Logger logger = Logger.getLogger(RelatorioRepository.class.getName());
    private final DatabaseConnection dbConnection;
    
    public RelatorioRepository() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    /**
     * Salva um novo relatório no banco de dados
     * @param relatorio relatório a ser salvo
     * @return relatório salvo com ID gerado
     * @throws SQLException se houver erro na operação
     */
    public Relatorio salvar(Relatorio relatorio) throws SQLException {
        String sql = """
            INSERT INTO relatorios (profissional_id, aluno_id, data, tipo, conteudo, periodo, data_criacao)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;
        
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            statement.setInt(1, relatorio.getProfissional().getId());
            statement.setInt(2, relatorio.getAluno().getId());
            statement.setDate(3, Date.valueOf(relatorio.getData()));
            statement.setString(4, relatorio.getTipo().name());
            statement.setString(5, relatorio.getConteudo());
            statement.setString(6, relatorio.getPeriodo());
            statement.setTimestamp(7, Timestamp.valueOf(relatorio.getDataCriacao()));
            
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        relatorio.setId(generatedKeys.getInt(1));
                        logger.info("Relatório salvo com ID: " + relatorio.getId());
                    }
                }
            }
            
            return relatorio;
        }
    }
    
    /**
     * Atualiza um relatório existente
     * @param relatorio relatório a ser atualizado
     * @return true se atualizado com sucesso, false caso contrário
     * @throws SQLException se houver erro na operação
     */
    public boolean atualizar(Relatorio relatorio) throws SQLException {
        String sql = """
            UPDATE relatorios 
            SET data = ?, tipo = ?, conteudo = ?, periodo = ?
            WHERE id = ?
            """;
        
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setDate(1, Date.valueOf(relatorio.getData()));
            statement.setString(2, relatorio.getTipo().name());
            statement.setString(3, relatorio.getConteudo());
            statement.setString(4, relatorio.getPeriodo());
            statement.setInt(5, relatorio.getId());
            
            int rowsAffected = statement.executeUpdate();
            logger.info("Relatório atualizado: " + (rowsAffected > 0 ? "sucesso" : "nenhuma linha afetada"));
            return rowsAffected > 0;
        }
    }
    
    /**
     * Busca relatório por ID
     * @param id ID do relatório
     * @return relatório encontrado ou null se não existir
     * @throws SQLException se houver erro na operação
     */
    public Relatorio buscarPorId(Integer id) throws SQLException {
        String sql = """
            SELECT r.*, 
                   u.nome as profissional_nome, u.tipo as profissional_tipo,
                   a.nome as aluno_nome
            FROM relatorios r
            JOIN usuarios u ON r.profissional_id = u.id
            JOIN alunos a ON r.aluno_id = a.id
            WHERE r.id = ?
            """;
        
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, id);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapearResultSetParaRelatorio(resultSet);
                }
            }
        }
        
        return null;
    }
    
    /**
     * Lista relatórios de um aluno
     * @param alunoId ID do aluno
     * @return lista de relatórios do aluno
     * @throws SQLException se houver erro na operação
     */
    public List<Relatorio> buscarPorAluno(Integer alunoId) throws SQLException {
        String sql = """
            SELECT r.*, 
                   u.nome as profissional_nome, u.tipo as profissional_tipo,
                   a.nome as aluno_nome
            FROM relatorios r
            JOIN usuarios u ON r.profissional_id = u.id
            JOIN alunos a ON r.aluno_id = a.id
            WHERE r.aluno_id = ? 
            ORDER BY r.data DESC, r.data_criacao DESC
            """;
        
        List<Relatorio> relatorios = new ArrayList<>();
        
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, alunoId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    relatorios.add(mapearResultSetParaRelatorio(resultSet));
                }
            }
        }
        
        return relatorios;
    }
    
    /**
     * Lista relatórios de um profissional
     * @param profissionalId ID do profissional
     * @return lista de relatórios do profissional
     * @throws SQLException se houver erro na operação
     */
    public List<Relatorio> buscarPorProfissional(Integer profissionalId) throws SQLException {
        String sql = """
            SELECT r.*, 
                   u.nome as profissional_nome, u.tipo as profissional_tipo,
                   a.nome as aluno_nome
            FROM relatorios r
            JOIN usuarios u ON r.profissional_id = u.id
            JOIN alunos a ON r.aluno_id = a.id
            WHERE r.profissional_id = ? 
            ORDER BY r.data DESC, r.data_criacao DESC
            """;
        
        List<Relatorio> relatorios = new ArrayList<>();
        
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, profissionalId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    relatorios.add(mapearResultSetParaRelatorio(resultSet));
                }
            }
        }
        
        return relatorios;
    }
    
    /**
     * Lista relatórios em um período
     * @param dataInicio data de início
     * @param dataFim data de fim
     * @return lista de relatórios do período
     * @throws SQLException se houver erro na operação
     */
    public List<Relatorio> buscarPorPeriodo(LocalDate dataInicio, LocalDate dataFim) throws SQLException {
        String sql = """
            SELECT r.*, 
                   u.nome as profissional_nome, u.tipo as profissional_tipo,
                   a.nome as aluno_nome
            FROM relatorios r
            JOIN usuarios u ON r.profissional_id = u.id
            JOIN alunos a ON r.aluno_id = a.id
            WHERE r.data BETWEEN ? AND ? 
            ORDER BY r.data DESC, r.data_criacao DESC
            """;
        
        List<Relatorio> relatorios = new ArrayList<>();
        
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setDate(1, Date.valueOf(dataInicio));
            statement.setDate(2, Date.valueOf(dataFim));
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    relatorios.add(mapearResultSetParaRelatorio(resultSet));
                }
            }
        }
        
        return relatorios;
    }
    
    /**
     * Lista relatórios por tipo
     * @param tipo tipo do relatório
     * @return lista de relatórios do tipo
     * @throws SQLException se houver erro na operação
     */
    public List<Relatorio> buscarPorTipo(Relatorio.TipoRelatorio tipo) throws SQLException {
        String sql = """
            SELECT r.*, 
                   u.nome as profissional_nome, u.tipo as profissional_tipo,
                   a.nome as aluno_nome
            FROM relatorios r
            JOIN usuarios u ON r.profissional_id = u.id
            JOIN alunos a ON r.aluno_id = a.id
            WHERE r.tipo = ? 
            ORDER BY r.data DESC, r.data_criacao DESC
            """;
        
        List<Relatorio> relatorios = new ArrayList<>();
        
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, tipo.name());
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    relatorios.add(mapearResultSetParaRelatorio(resultSet));
                }
            }
        }
        
        return relatorios;
    }
    
    /**
     * Lista todos os relatórios
     * @return lista de todos os relatórios
     * @throws SQLException se houver erro na operação
     */
    public List<Relatorio> listarTodos() throws SQLException {
        String sql = """
            SELECT r.*, 
                   u.nome as profissional_nome, u.tipo as profissional_tipo,
                   a.nome as aluno_nome
            FROM relatorios r
            JOIN usuarios u ON r.profissional_id = u.id
            JOIN alunos a ON r.aluno_id = a.id
            ORDER BY r.data DESC, r.data_criacao DESC
            """;
        
        List<Relatorio> relatorios = new ArrayList<>();
        
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            
            while (resultSet.next()) {
                relatorios.add(mapearResultSetParaRelatorio(resultSet));
            }
        }
        
        return relatorios;
    }
    
    /**
     * Busca relatórios por período específico (ex: "1º Semestre 2024")
     * @param periodo período a ser buscado
     * @return lista de relatórios do período
     * @throws SQLException se houver erro na operação
     */
    public List<Relatorio> buscarPorPeriodoEspecifico(String periodo) throws SQLException {
        String sql = """
            SELECT r.*, 
                   u.nome as profissional_nome, u.tipo as profissional_tipo,
                   a.nome as aluno_nome
            FROM relatorios r
            JOIN usuarios u ON r.profissional_id = u.id
            JOIN alunos a ON r.aluno_id = a.id
            WHERE r.periodo = ? 
            ORDER BY r.data DESC, r.data_criacao DESC
            """;
        
        List<Relatorio> relatorios = new ArrayList<>();
        
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, periodo);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    relatorios.add(mapearResultSetParaRelatorio(resultSet));
                }
            }
        }
        
        return relatorios;
    }
    
    /**
     * Conta o total de relatórios
     * @return número total de relatórios
     * @throws SQLException se houver erro na operação
     */
    public int contarRelatorios() throws SQLException {
        String sql = "SELECT COUNT(*) FROM relatorios";
        
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
     * Conta relatórios por tipo
     * @return mapa com contagem por tipo
     * @throws SQLException se houver erro na operação
     */
    public java.util.Map<Relatorio.TipoRelatorio, Integer> contarPorTipo() throws SQLException {
        String sql = "SELECT tipo, COUNT(*) FROM relatorios GROUP BY tipo";
        java.util.Map<Relatorio.TipoRelatorio, Integer> contagem = new java.util.HashMap<>();
        
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            
            while (resultSet.next()) {
                String tipoStr = resultSet.getString("tipo");
                int count = resultSet.getInt(2);
                try {
                    Relatorio.TipoRelatorio tipo = Relatorio.TipoRelatorio.valueOf(tipoStr);
                    contagem.put(tipo, count);
                } catch (IllegalArgumentException e) {
                    logger.warning("Tipo de relatório inválido: " + tipoStr);
                }
            }
        }
        
        return contagem;
    }
    
    /**
     * Mapeia um ResultSet para um objeto Relatorio
     * @param resultSet ResultSet com os dados
     * @return objeto Relatorio mapeado
     * @throws SQLException se houver erro na operação
     */
    private Relatorio mapearResultSetParaRelatorio(ResultSet resultSet) throws SQLException {
        Relatorio relatorio = new Relatorio();
        
        relatorio.setId(resultSet.getInt("id"));
        relatorio.setData(resultSet.getDate("data").toLocalDate());
        relatorio.setTipo(Relatorio.TipoRelatorio.valueOf(resultSet.getString("tipo")));
        relatorio.setConteudo(resultSet.getString("conteudo"));
        relatorio.setPeriodo(resultSet.getString("periodo"));
        
        Timestamp timestamp = resultSet.getTimestamp("data_criacao");
        if (timestamp != null) {
            relatorio.setDataCriacao(timestamp.toLocalDateTime());
        }
        
        // Cria objetos Usuario e Aluno básicos
        com.planoaee.model.Usuario profissional = new com.planoaee.model.Usuario();
        profissional.setId(resultSet.getInt("profissional_id"));
        profissional.setNome(resultSet.getString("profissional_nome"));
        try {
            profissional.setTipo(com.planoaee.model.Usuario.TipoUsuario.valueOf(resultSet.getString("profissional_tipo")));
        } catch (IllegalArgumentException e) {
            profissional.setTipo(com.planoaee.model.Usuario.TipoUsuario.PROFESSOR);
        }
        relatorio.setProfissional(profissional);
        
        com.planoaee.model.Aluno aluno = new com.planoaee.model.Aluno();
        aluno.setId(resultSet.getInt("aluno_id"));
        aluno.setNome(resultSet.getString("aluno_nome"));
        relatorio.setAluno(aluno);
        
        return relatorio;
    }
}
