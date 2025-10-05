package com.planoaee.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe responsável por inicializar o banco de dados SQLite
 * Cria todas as tabelas necessárias para o sistema Plano AEE
 */
public class DatabaseInitializer {
    
    private static final Logger logger = Logger.getLogger(DatabaseInitializer.class.getName());
    private final DatabaseConnection dbConnection;
    
    public DatabaseInitializer() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    /**
     * Inicializa o banco de dados criando todas as tabelas necessárias
     */
    public void initializeDatabase() {
        try (Connection connection = dbConnection.getConnection()) {
            createTables(connection);
            insertInitialData(connection);
            logger.info("Banco de dados inicializado com sucesso");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao inicializar banco de dados", e);
            throw new RuntimeException("Falha na inicialização do banco de dados", e);
        }
    }
    
    /**
     * Cria todas as tabelas do sistema
     */
    private void createTables(Connection connection) throws SQLException {
        String[] createTableQueries = {
            // Tabela de usuários (profissionais)
            """
            CREATE TABLE IF NOT EXISTS usuarios (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nome VARCHAR(100) NOT NULL,
                email VARCHAR(100) UNIQUE NOT NULL,
                senha VARCHAR(255) NOT NULL,
                tipo VARCHAR(20) NOT NULL CHECK (tipo IN ('PROFESSOR', 'PSICOLOGO', 'PSIQUIATRA', 'FISIOTERAPEUTA')),
                data_cadastro DATETIME DEFAULT CURRENT_TIMESTAMP,
                ativo BOOLEAN DEFAULT 1
            )
            """,
            
            // Tabela de alunos
            """
            CREATE TABLE IF NOT EXISTS alunos (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nome VARCHAR(100) NOT NULL,
                idade INTEGER NOT NULL,
                responsavel VARCHAR(100),
                contato VARCHAR(50),
                observacoes TEXT,
                data_cadastro DATETIME DEFAULT CURRENT_TIMESTAMP,
                ativo BOOLEAN DEFAULT 1
            )
            """,
            
            // Tabela de frequência
            """
            CREATE TABLE IF NOT EXISTS frequencia (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                aluno_id INTEGER NOT NULL,
                data DATE NOT NULL,
                presente BOOLEAN NOT NULL,
                observacoes TEXT,
                data_registro DATETIME DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (aluno_id) REFERENCES alunos (id)
            )
            """,
            
            // Tabela de relatórios
            """
            CREATE TABLE IF NOT EXISTS relatorios (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                profissional_id INTEGER NOT NULL,
                aluno_id INTEGER NOT NULL,
                data DATE NOT NULL,
                tipo VARCHAR(20) NOT NULL CHECK (tipo IN ('EDUCACIONAL', 'PSICOLOGICO', 'FISIOTERAPICO')),
                conteudo TEXT NOT NULL,
                periodo VARCHAR(50),
                data_criacao DATETIME DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (profissional_id) REFERENCES usuarios (id),
                FOREIGN KEY (aluno_id) REFERENCES alunos (id)
            )
            """,
            
            // Tabela de planos de aula
            """
            CREATE TABLE IF NOT EXISTS planos_aula (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                professor_id INTEGER NOT NULL,
                aluno_id INTEGER NOT NULL,
                data DATE NOT NULL,
                objetivos TEXT NOT NULL,
                atividades TEXT NOT NULL,
                materiais TEXT,
                avaliacao TEXT,
                data_criacao DATETIME DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (professor_id) REFERENCES usuarios (id),
                FOREIGN KEY (aluno_id) REFERENCES alunos (id)
            )
            """,
            
            // Tabela de configurações do sistema
            """
            CREATE TABLE IF NOT EXISTS configuracoes (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                chave VARCHAR(50) UNIQUE NOT NULL,
                valor TEXT NOT NULL,
                descricao TEXT,
                data_modificacao DATETIME DEFAULT CURRENT_TIMESTAMP
            )
            """
        };
        
        try (Statement statement = connection.createStatement()) {
            for (String query : createTableQueries) {
                statement.execute(query);
                logger.info("Tabela criada/verificada com sucesso");
            }
        }
    }
    
    /**
     * Insere dados iniciais no banco de dados
     */
    private void insertInitialData(Connection connection) throws SQLException {
        // Verifica se já existem usuários
        String checkUsersQuery = "SELECT COUNT(*) FROM usuarios";
        try (Statement statement = connection.createStatement();
             var resultSet = statement.executeQuery(checkUsersQuery)) {
            
            if (resultSet.getInt(1) == 0) {
                insertInitialUsers(connection);
                insertInitialConfigurations(connection);
                logger.info("Dados iniciais inseridos com sucesso");
            } else {
                logger.info("Dados iniciais já existem no banco de dados");
            }
        }
    }
    
    /**
     * Insere usuários iniciais do sistema
     */
    private void insertInitialUsers(Connection connection) throws SQLException {
        // Senha padrão: "admin123" em SHA-256
        String defaultPassword = "240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9";
        
        String insertUsersQuery = """
            INSERT INTO usuarios (nome, email, senha, tipo) VALUES
            ('Administrador', 'admin@apapeq.com', ?, 'PROFESSOR'),
            ('Maria Silva', 'maria.silva@apapeq.com', ?, 'PSICOLOGO'),
            ('João Santos', 'joao.santos@apapeq.com', ?, 'FISIOTERAPEUTA')
            """;
        
        try (var preparedStatement = connection.prepareStatement(insertUsersQuery)) {
            preparedStatement.setString(1, defaultPassword);
            preparedStatement.setString(2, defaultPassword);
            preparedStatement.setString(3, defaultPassword);
            preparedStatement.executeUpdate();
        }
    }
    
    /**
     * Insere configurações iniciais do sistema
     */
    private void insertInitialConfigurations(Connection connection) throws SQLException {
        String insertConfigQuery = """
            INSERT INTO configuracoes (chave, valor, descricao) VALUES
            ('sistema.tema', 'light', 'Tema da interface (light/dark)'),
            ('sistema.idioma', 'pt-BR', 'Idioma do sistema'),
            ('backup.caminho', './backups/', 'Caminho para arquivos de backup'),
            ('backup.automatico', 'true', 'Backup automático habilitado'),
            ('backup.intervalo', '7', 'Intervalo de backup em dias'),
            ('relatorio.cabecalho', 'APAPEQ - Plano AEE', 'Cabeçalho dos relatórios'),
            ('relatorio.rodape', 'Sistema Plano AEE v1.0', 'Rodapé dos relatórios')
            """;
        
        try (Statement statement = connection.createStatement()) {
            statement.execute(insertConfigQuery);
        }
    }
    
    /**
     * Verifica se o banco de dados está inicializado corretamente
     * @return true se todas as tabelas existem, false caso contrário
     */
    public boolean isDatabaseInitialized() {
        try (Connection connection = dbConnection.getConnection()) {
            String[] tables = {"usuarios", "alunos", "frequencia", "relatorios", "planos_aula", "configuracoes"};
            
            for (String table : tables) {
                String checkTableQuery = "SELECT name FROM sqlite_master WHERE type='table' AND name=?";
                try (var preparedStatement = connection.prepareStatement(checkTableQuery)) {
                    preparedStatement.setString(1, table);
                    try (var resultSet = preparedStatement.executeQuery()) {
                        if (!resultSet.next()) {
                            logger.warning("Tabela " + table + " não encontrada");
                            return false;
                        }
                    }
                }
            }
            return true;
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Erro ao verificar inicialização do banco", e);
            return false;
        }
    }
}

