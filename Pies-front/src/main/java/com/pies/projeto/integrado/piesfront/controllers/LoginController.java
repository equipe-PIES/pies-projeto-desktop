package com.pies.projeto.integrado.piesfront.controllers;

import com.pies.projeto.integrado.piesfront.enums.UserRole;
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

    // Opcional: Implementar initialize se for necessário
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Inicialização se necessária
    }

    // ----------------------------------------------------
    // MÉTODOS DE AÇÃO
    // ----------------------------------------------------

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

        // ----------------------------------------------------------------------
        // SIMULAÇÃO DA CHAMADA DE AUTENTICAÇÃO
        // ----------------------------------------------------------------------
        // ESTE É O PONTO ONDE VOCÊ FARIA UMA CHAMADA AO SEU SERVIÇO SPRING
        // E RECEBERIA A STRING DO PAPEL, EX: "professor", "admin", "INVÁLIDO"
        // ----------------------------------------------------------------------

        // **SUBSTITUA A LINHA ABAIXO PELA CHAMADA AO SEU BACKEND/SERVIÇO DE AUTENTICAÇÃO**
        // Por enquanto, vamos simular que o email "coord@a.com" loga como coordenador
        String roleRecebidaDoServico;
        if ("coord@a.com".equals(email) && "123".equals(senha)) {
            roleRecebidaDoServico = "coordenador"; // String retornada do login bem-sucedido
        } else if ("prof@a.com".equals(email) && "123".equals(senha)) {
            roleRecebidaDoServico = "professor"; // String retornada do login bem-sucedido
        } else {
            roleRecebidaDoServico = "INVÁLIDO"; // String retornada quando credenciais falham
        }

        // ----------------------------------------------------------------------
        // 2. Autenticação e Verificação de Nível (Usando o Enum UserRole)
        // ----------------------------------------------------------------------

        // Se as credenciais falharem, encerra aqui.
        if ("INVÁLIDO".equals(roleRecebidaDoServico)) {
            errorMessageLabel.setText("Credenciais inválidas. Tente novamente.");
            return;
        }

        String nivelAcesso = null;
        try {
            // CONVERSÃO CORRETA: String -> UserRole -> String
            UserRole userRoleEnum = UserRole.fromString(roleRecebidaDoServico);

            // Aqui pegamos o valor da String
            nivelAcesso = userRoleEnum.getRole();

        } catch (IllegalArgumentException e) {
            // Este catch lida com o caso onde o backend retorna uma role válida, mas desconhecida
            // pelo frontend (ex: "gerente").
            e.printStackTrace();
            errorMessageLabel.setText("Erro de permissão: Papel de usuário desconhecido. Contate o suporte.");
            return;
        }


        // 3. Lógica de Mapeamento de Tela
        String fxmlDestino = null;

        switch (nivelAcesso) {
            case "coordenador":
                fxmlDestino = "tela-inicio-coord.fxml";
                break;
            case "professor":
                fxmlDestino = "tela-inicio-professor.fxml";
                break;
            default:
                errorMessageLabel.setText("Acesso sem tela mapeada. Contate o suporte.");
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
                errorMessageLabel.setText("Erro interno ao carregar a tela do sistema. FXML: " + fxmlDestino);
            }
        }
    }
}