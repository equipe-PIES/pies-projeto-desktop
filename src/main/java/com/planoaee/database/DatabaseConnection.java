package com.planoaee.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe responsável por gerenciar a conexão com o banco de dados SQLite
 * Implementa o padrão Singleton para garantir uma única instância de conexão
 */
public class DatabaseConnection {
    
    private static final Logger logger = Logger.getLogger(DatabaseConnection.class.getName());
    private static DatabaseConnection instance;
    private Connection connection;
    
    // Configurações do banco de dados
    private static final String DB_URL = "jdbc:sqlite:plano_aee.db";
    private static final String DRIVER_CLASS = "org.sqlite.JDBC";
    
    /**
     * Construtor privado para implementar o padrão Singleton
     */
    private DatabaseConnection() {
        try {
            Class.forName(DRIVER_CLASS);
            this.connection = DriverManager.getConnection(DB_URL);
            logger.info("Conexão com o banco de dados estabelecida com sucesso");
        } catch (ClassNotFoundException | SQLException e) {
            logger.log(Level.SEVERE, "Erro ao estabelecer conexão com o banco de dados", e);
            throw new RuntimeException("Falha na conexão com o banco de dados", e);
        }
    }
    
    /**
     * Retorna a instância única da classe DatabaseConnection
     * @return instância singleton da DatabaseConnection
     */
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }
    
    /**
     * Retorna a conexão ativa com o banco de dados
     * @return objeto Connection para interação com o SQLite
     * @throws SQLException se houver erro ao obter a conexão
     */
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                connection = DriverManager.getConnection(DB_URL);
                logger.info("Nova conexão estabelecida com o banco de dados");
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Erro ao recriar conexão com o banco de dados", e);
                throw e;
            }
        }
        return connection;
    }
    
    /**
     * Fecha a conexão com o banco de dados
     * @throws SQLException se houver erro ao fechar a conexão
     */
    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            logger.info("Conexão com o banco de dados fechada");
        }
    }
    
    /**
     * Testa se a conexão está ativa e funcionando
     * @return true se a conexão está válida, false caso contrário
     */
    public boolean isConnectionValid() {
        try {
            return connection != null && !connection.isClosed() && connection.isValid(5);
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Erro ao validar conexão", e);
            return false;
        }
    }
    
    /**
     * Reinicia a conexão com o banco de dados
     * Útil em caso de problemas de conectividade
     */
    public void resetConnection() {
        try {
            closeConnection();
            this.connection = DriverManager.getConnection(DB_URL);
            logger.info("Conexão com o banco de dados reiniciada");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao reiniciar conexão com o banco de dados", e);
            throw new RuntimeException("Falha ao reiniciar conexão com o banco de dados", e);
        }
    }
}



