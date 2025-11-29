package com.pies.projeto.integrado.piesfront.controllers;

import com.pies.projeto.integrado.piesfront.services.AuthService;
import com.utils.Janelas;

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
import javafx.event.ActionEvent;

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
        this.authService = AuthService.getInstance(); // Usa a instância singleton
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
    private void handleLoginButtonAction(ActionEvent event) {
        String email = emailField.getText().trim();
        String senha = passwordField.getText();

        errorMessageLabel.setText("");
        errorMessageLabel.setStyle("-fx-text-fill: red;");

        // 1. ERRO: campos de login não estão preenchidos
        if (email.isEmpty() || senha.isEmpty()) {
            errorMessageLabel.setText("Por favor, preencha E-mail e Senha.");
            return;
        }

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
            errorMessageLabel.setText("Credenciais inválidas. Tente novamente.");
            return;
        }

        // Usa diretamente a role do backend (simplificado)
        String nivelAcesso = roleRecebidaDoServico.toLowerCase();


        // 3. Lógica de Mapeamento de Tela
        String fxmlDestino = null;
        String tituloJanela = "Amparo Edu";

        switch (nivelAcesso) {
            case "coordenador":
                fxmlDestino = "/com/pies/projeto/integrado/piesfront/screens/tela-inicio-coord.fxml";
                tituloJanela = "Início - Coordenador(a)";
                break;
            case "professor":
            case "user":  // Temporário: user também vai para tela de professor
                fxmlDestino = "/com/pies/projeto/integrado/piesfront/screens/tela-inicio-professor.fxml";
                tituloJanela = "Início - Professor(a)";
                break;
            default:
                errorMessageLabel.setText("Acesso sem tela mapeada. Contate o suporte.");
                return;
        }

        // 4. Carregar a Próxima Tela
        if (fxmlDestino != null) {
           Janelas.carregarTela(event, fxmlDestino, tituloJanela);
        }
    }
}