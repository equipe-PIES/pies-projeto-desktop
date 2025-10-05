package com.planoaee;

import com.planoaee.database.DatabaseInitializer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe principal da aplicação JavaFX
 * Responsável por inicializar o sistema e exibir a tela de splash/loading
 */
public class MainApp extends Application {
    
    private static final Logger logger = Logger.getLogger(MainApp.class.getName());
    private static final String SPLASH_FXML = "/com/planoaee/view/fxml/splash.fxml";
    private static final String LOGIN_FXML = "/com/planoaee/view/fxml/login.fxml";
    
    @Override
    public void start(Stage primaryStage) {
        try {
            // Inicializa o banco de dados
            initializeDatabase();
            
            // Mostra tela de splash
            showSplashScreen(primaryStage);
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao iniciar aplicação", e);
            showErrorDialog("Erro Fatal", "Não foi possível iniciar o sistema", 
                          "Entre em contato com o suporte técnico.");
            Platform.exit();
        }
    }
    
    /**
     * Inicializa o banco de dados SQLite
     */
    private void initializeDatabase() {
        try {
            DatabaseInitializer initializer = new DatabaseInitializer();
            initializer.initializeDatabase();
            logger.info("Banco de dados inicializado com sucesso");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao inicializar banco de dados", e);
            throw new RuntimeException("Falha na inicialização do banco de dados", e);
        }
    }
    
    /**
     * Exibe a tela de splash/loading
     */
    private void showSplashScreen(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(SPLASH_FXML));
            Scene splashScene = new Scene(loader.load());
            
            // Configura a janela de splash
            primaryStage.setTitle("Plano AEE - APAPEQ");
            primaryStage.setScene(splashScene);
            primaryStage.setResizable(false);
            primaryStage.initStyle(StageStyle.UNDECORATED);
            
            // Centraliza a janela
            primaryStage.centerOnScreen();
            primaryStage.show();
            
            // Simula tempo de carregamento e abre a tela de login
            Platform.runLater(() -> {
                try {
                    Thread.sleep(2000); // 2 segundos de splash
                    showLoginScreen(primaryStage);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logger.log(Level.WARNING, "Splash screen interrompido", e);
                }
            });
            
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erro ao carregar tela de splash", e);
            // Se não conseguir carregar o splash, vai direto para o login
            showLoginScreen(primaryStage);
        }
    }
    
    /**
     * Exibe a tela de login
     */
    private void showLoginScreen(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(LOGIN_FXML));
            Scene loginScene = new Scene(loader.load());
            
            // Aplica CSS se disponível
            String cssPath = getClass().getResource("/com/planoaee/view/css/style.css").toExternalForm();
            loginScene.getStylesheets().add(cssPath);
            
            // Configura a janela principal
            primaryStage.setTitle("Plano AEE - Sistema APAPEQ");
            primaryStage.setScene(loginScene);
            primaryStage.setResizable(true);
            primaryStage.initStyle(StageStyle.DECORATED);
            
            // Define tamanho mínimo
            primaryStage.setMinWidth(800);
            primaryStage.setMinHeight(600);
            
            // Centraliza a janela
            primaryStage.centerOnScreen();
            
            // Define ação ao fechar
            primaryStage.setOnCloseRequest(event -> {
                event.consume();
                showExitConfirmation(primaryStage);
            });
            
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erro ao carregar tela de login", e);
            showErrorDialog("Erro", "Não foi possível carregar a interface", 
                          "Verifique se os arquivos FXML estão corretos.");
        }
    }
    
    /**
     * Exibe confirmação antes de fechar o sistema
     */
    private void showExitConfirmation(Stage stage) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Saída");
        alert.setHeaderText("Deseja realmente sair do sistema?");
        alert.setContentText("Certifique-se de salvar todos os dados antes de sair.");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Fecha conexão com banco de dados
                com.planoaee.database.DatabaseConnection.getInstance().closeConnection();
                logger.info("Aplicação finalizada com sucesso");
            } catch (Exception e) {
                logger.log(Level.WARNING, "Erro ao fechar conexão com banco", e);
            } finally {
                Platform.exit();
                System.exit(0);
            }
        }
    }
    
    /**
     * Exibe dialog de erro
     */
    private void showErrorDialog(String title, String header, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(header);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }
    
    /**
     * Método main para iniciar a aplicação
     */
    public static void main(String[] args) {
        // Configura logging
        System.setProperty("java.util.logging.config.file", "logging.properties");
        
        logger.info("Iniciando aplicação Plano AEE - APAPEQ");
        launch(args);
    }
}

