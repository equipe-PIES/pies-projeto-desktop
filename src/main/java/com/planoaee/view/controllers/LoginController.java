package com.planoaee.view.controllers;

import com.planoaee.model.Usuario;
import com.planoaee.service.UsuarioService;
import com.planoaee.util.PasswordUtil;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller para a tela de login do sistema
 */
public class LoginController {
    
    private static final Logger logger = Logger.getLogger(LoginController.class.getName());
    
    @FXML
    private TextField txtEmail;
    
    @FXML
    private PasswordField txtSenha;
    
    @FXML
    private CheckBox chkLembrar;
    
    @FXML
    private Button btnEntrar;
    
    @FXML
    private Button btnCancelar;
    
    @FXML
    private Hyperlink linkEsqueciSenha;
    
    @FXML
    private StackPane stackPane;
    
    private UsuarioService usuarioService;
    private Stage stage;
    
    /**
     * Inicializa o controller
     */
    @FXML
    private void initialize() {
        this.usuarioService = new UsuarioService();
        
        // Configura ação do Enter nos campos
        txtSenha.setOnAction(event -> handleLogin());
        
        // Carrega dados salvos se "Lembrar usuário" estiver marcado
        carregarDadosSalvos();
        
        logger.info("LoginController inicializado");
    }
    
    /**
     * Define o stage da aplicação
     * @param stage stage principal
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    /**
     * Manipula o evento de login
     */
    @FXML
    private void handleLogin() {
        if (!validarCampos()) {
            return;
        }
        
        String email = txtEmail.getText().trim();
        String senha = txtSenha.getText();
        
        // Mostra loading
        mostrarLoading("Autenticando usuário...");
        
        // Executa autenticação em thread separada
        Task<Usuario> task = new Task<Usuario>() {
            @Override
            protected Usuario call() throws Exception {
                return usuarioService.autenticar(email, senha);
            }
            
            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    esconderLoading();
                    Usuario usuario = getValue();
                    if (usuario != null) {
                        salvarDadosSeLembrar();
                        abrirDashboard(usuario);
                    } else {
                        mostrarErro("Falha na autenticação", "E-mail ou senha inválidos");
                    }
                });
            }
            
            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    esconderLoading();
                    mostrarErro("Erro de conexão", "Não foi possível conectar ao banco de dados");
                    logger.log(Level.SEVERE, "Erro ao autenticar usuário", getException());
                });
            }
        };
        
        new Thread(task).start();
    }
    
    /**
     * Manipula o evento de cancelar
     */
    @FXML
    private void handleCancel() {
        if (confirmarSaida()) {
            Platform.exit();
        }
    }
    
    /**
     * Manipula o evento de esqueci senha
     */
    @FXML
    private void handleForgotPassword() {
        mostrarInfo("Recuperação de Senha", 
                   "Entre em contato com o administrador do sistema para redefinir sua senha.\n\n" +
                   "E-mail: admin@apapeq.com\n" +
                   "Telefone: (85) 99999-9999");
    }
    
    /**
     * Valida os campos obrigatórios
     * @return true se válidos, false caso contrário
     */
    private boolean validarCampos() {
        StringBuilder erros = new StringBuilder();
        
        if (txtEmail.getText().trim().isEmpty()) {
            erros.append("• E-mail é obrigatório\n");
        } else if (!isEmailValido(txtEmail.getText().trim())) {
            erros.append("• E-mail inválido\n");
        }
        
        if (txtSenha.getText().isEmpty()) {
            erros.append("• Senha é obrigatória\n");
        }
        
        if (erros.length() > 0) {
            mostrarErro("Campos obrigatórios", erros.toString());
            return false;
        }
        
        return true;
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
     * Abre a tela do dashboard
     * @param usuario usuário logado
     */
    private void abrirDashboard(Usuario usuario) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/planoaee/view/fxml/dashboard.fxml"));
            Scene dashboardScene = new Scene(loader.load());
            
            // Aplica CSS
            String cssPath = getClass().getResource("/com/planoaee/view/css/style.css").toExternalForm();
            dashboardScene.getStylesheets().add(cssPath);
            
            // Configura o controller do dashboard
            DashboardController dashboardController = loader.getController();
            dashboardController.setUsuarioLogado(usuario);
            
            // Atualiza a cena
            stage.setScene(dashboardScene);
            stage.setTitle("Plano AEE - Dashboard (" + usuario.getNome() + ")");
            stage.centerOnScreen();
            
            logger.info("Usuário " + usuario.getNome() + " logado com sucesso");
            
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erro ao carregar dashboard", e);
            mostrarErro("Erro", "Não foi possível carregar a tela principal");
        }
    }
    
    /**
     * Salva dados do usuário se "Lembrar" estiver marcado
     */
    private void salvarDadosSeLembrar() {
        if (chkLembrar.isSelected()) {
            // TODO: Implementar persistência das preferências
            logger.info("Dados do usuário salvos para próxima sessão");
        }
    }
    
    /**
     * Carrega dados salvos do usuário
     */
    private void carregarDadosSalvos() {
        // TODO: Implementar carregamento das preferências
        logger.info("Dados do usuário carregados");
    }
    
    /**
     * Mostra tela de loading
     * @param mensagem mensagem a ser exibida
     */
    private void mostrarLoading(String mensagem) {
        btnEntrar.setDisable(true);
        btnCancelar.setDisable(true);
        // TODO: Implementar loading visual
    }
    
    /**
     * Esconde tela de loading
     */
    private void esconderLoading() {
        btnEntrar.setDisable(false);
        btnCancelar.setDisable(false);
        // TODO: Esconder loading visual
    }
    
    /**
     * Mostra dialog de erro
     * @param titulo título do dialog
     * @param mensagem mensagem de erro
     */
    private void mostrarErro(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
    
    /**
     * Mostra dialog de informação
     * @param titulo título do dialog
     * @param mensagem mensagem informativa
     */
    private void mostrarInfo(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
    
    /**
     * Confirma se o usuário deseja sair
     * @return true se confirmado, false caso contrário
     */
    private boolean confirmarSaida() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Saída");
        alert.setHeaderText("Deseja realmente sair do sistema?");
        alert.setContentText("Todos os dados não salvos serão perdidos.");
        
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
}

