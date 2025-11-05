package com.pies.projeto.integrado.piesfront.controllers;

import com.pies.projeto.integrado.piesfront.services.AuthService;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Hyperlink;
import javafx.fxml.Initializable;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    // ----------------------------------------------------
    // DECLARAÇÃO DOS fx:id
    // ----------------------------------------------------
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Label errorMessageLabel;
    @FXML private Hyperlink forgotPasswordLink;
    
    // Serviço de autenticação para comunicação com o backend
    private final AuthService authService;

    public LoginController() {
        this.authService = new AuthService();
    }

    // Opcional: Implementar initialize se for necessário
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Inicialização se necessária
    }

    // ----------------------------------------------------
    // MÉTODOS DE AÇÃO
    // ----------------------------------------------------

    @FXML
    private void handleForgotPasswordAction() {
        // TODO: Implementar funcionalidade de recuperação de senha
        // Por enquanto, apenas mostra uma mensagem informativa
        errorMessageLabel.setText("Funcionalidade de recuperação de senha será implementada em breve.");
        errorMessageLabel.setStyle("-fx-text-fill: blue;");
    }

    @FXML
    private void handleLoginButtonAction() {
        String email = emailField.getText().trim();
        String senha = passwordField.getText();

        errorMessageLabel.setText("");
        errorMessageLabel.setStyle("-fx-text-fill: red;");

        // 1. ERRO: campos de login não estão preenchidos
        if (email.isEmpty() || senha.isEmpty()) {
            errorMessageLabel.setText("Por favor, preencha E-mail e Senha.");
            return;
        }

        // Desabilita o botão durante o processo de login
        loginButton.setDisable(true);
        errorMessageLabel.setText("Autenticando...");
        errorMessageLabel.setStyle("-fx-text-fill: blue;");

        try {
            // ----------------------------------------------------------------------
            // CHAMADA REAL AO BACKEND SPRING BOOT
            // ----------------------------------------------------------------------
            // Faz a chamada HTTP para o endpoint /auth/login do backend
            String roleRecebidaDoServico = authService.authenticate(email, senha);

            // ----------------------------------------------------------------------
            // 2. Autenticação e Verificação de Nível (Usando o Enum UserRole)
            // ----------------------------------------------------------------------

            // Se as credenciais falharem, encerra aqui.
            if ("INVÁLIDO".equals(roleRecebidaDoServico)) {
                errorMessageLabel.setText("Credenciais inválidas ou servidor indisponível.");
                errorMessageLabel.setStyle("-fx-text-fill: red;");
                return;
            }

            if ("ERRO_CONEXAO".equals(roleRecebidaDoServico)) {
                errorMessageLabel.setText("Erro de conexão com o servidor. Verifique se o backend está rodando.");
                errorMessageLabel.setStyle("-fx-text-fill: red;");
                return;
            }

            // Usa diretamente a role do backend (simplificado)
            String nivelAcesso = roleRecebidaDoServico.toLowerCase();


            // 3. Lógica de Mapeamento de Tela
            String fxmlDestino = null;

            switch (nivelAcesso) {
                case "coordenador":
                    fxmlDestino = "/com/pies/projeto/integrado/piesfront/screens/tela-inicio-coord.fxml";
                    break;
                case "professor":
                case "user":  // Temporário: user também vai para tela de professor
                    fxmlDestino = "/com/pies/projeto/integrado/piesfront/screens/tela-inicio-professor.fxml";
                    break;
                default:
                    errorMessageLabel.setText("Acesso sem tela mapeada. Contate o suporte.");
                    errorMessageLabel.setStyle("-fx-text-fill: red;");
                    return;
            }

            // 4. Carregar a Próxima Tela
            if (fxmlDestino != null) {
                try {
                    Parent root = FXMLLoader.load(getClass().getResource(fxmlDestino));

                    Stage currentStage = (Stage) loginButton.getScene().getWindow();
                    currentStage.setScene(new Scene(root));
                    currentStage.show();

                } catch (IOException e) {
                    e.printStackTrace();
                    errorMessageLabel.setText("Erro ao carregar a tela: " + e.getMessage());
                    errorMessageLabel.setStyle("-fx-text-fill: red;");
                }
            }
        } finally {
            // Reabilita o botão
            loginButton.setDisable(false);
        }
    }
}