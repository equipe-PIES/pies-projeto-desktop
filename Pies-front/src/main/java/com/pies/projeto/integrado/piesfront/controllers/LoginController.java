package com.pies.projeto.integrado.piesfront.controllers;

import com.pies.projeto.integrado.piesfront.services.AuthService;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
 

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
    @FXML private ProgressBar loginProgressBar;
    
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
        loginButton.setDisable(true);
        emailField.setDisable(true);
        passwordField.setDisable(true);
        if (loginProgressBar != null) {
            loginProgressBar.setProgress(0);
        }

        Task<String> loginTask = new Task<>() {
            @Override
            protected String call() {
                return authService.authenticate(email, senha);
            }
        };

        Timeline timeline = new Timeline(new KeyFrame(javafx.util.Duration.millis(100), e -> {
            if (loginProgressBar != null) {
                double p = loginProgressBar.getProgress();
                if (p < 0.9) {
                    loginProgressBar.setProgress(p + 0.02);
                }
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        loginTask.setOnSucceeded(ev -> {
            timeline.stop();
            loginButton.setDisable(false);
            emailField.setDisable(false);
            passwordField.setDisable(false);

            String roleRecebidaDoServico = loginTask.getValue();
            if ("INVÁLIDO".equals(roleRecebidaDoServico)) {
                if (loginProgressBar != null) {
                    loginProgressBar.setProgress(0.7);
                }
                javafx.animation.PauseTransition ptErr = new javafx.animation.PauseTransition(javafx.util.Duration.millis(200));
                ptErr.setOnFinished(e1 -> {
                    if (loginProgressBar != null) {
                        loginProgressBar.setProgress(0);
                    }
                    errorMessageLabel.setText("Credenciais inválidas. Tente novamente.");
                });
                ptErr.play();
                return;
            }

            String nivelAcesso = roleRecebidaDoServico.toLowerCase();
            String fxmlDestino = null;
            switch (nivelAcesso) {
                case "coordenador":
                    fxmlDestino = "/com/pies/projeto/integrado/piesfront/screens/tela-inicio-coord.fxml";
                    break;
                case "professor":
                case "user":
                    fxmlDestino = "/com/pies/projeto/integrado/piesfront/screens/tela-inicio-professor.fxml";
                    break;
                default:
                    if (loginProgressBar != null) {
                        loginProgressBar.setProgress(0.7);
                    }
                    javafx.animation.PauseTransition ptErr = new javafx.animation.PauseTransition(javafx.util.Duration.millis(200));
                    ptErr.setOnFinished(e1 -> {
                        if (loginProgressBar != null) {
                            loginProgressBar.setProgress(0);
                        }
                        errorMessageLabel.setText("Acesso sem tela mapeada. Contate o suporte.");
                    });
                    ptErr.play();
                    return;
            }

            if (fxmlDestino != null) {
                if (loginProgressBar != null) {
                    loginProgressBar.setProgress(1.0);
                }
                final String destinoFXML = fxmlDestino;
                javafx.animation.PauseTransition wait = new javafx.animation.PauseTransition(javafx.util.Duration.millis(200));
                wait.setOnFinished(e0 -> {
                    try {
                        Parent root = FXMLLoader.load(getClass().getResource(destinoFXML));
                        Label msg = new Label("Bem-vindo a plataforma AmparoEdu!");
                        msg.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-padding: 10 16; -fx-background-radius: 8; -fx-font-weight: bold;");
                        javafx.scene.layout.StackPane overlay = new javafx.scene.layout.StackPane(msg);
                        overlay.setStyle("-fx-background-color: transparent;");
                        overlay.setPadding(new javafx.geometry.Insets(50, 50, 50, 50));
                        overlay.setMouseTransparent(true);
                        javafx.scene.layout.StackPane container = new javafx.scene.layout.StackPane(root, overlay);
                        javafx.scene.layout.StackPane.setAlignment(msg, javafx.geometry.Pos.CENTER);
                        Stage currentStage = (Stage) loginButton.getScene().getWindow();
                        currentStage.setScene(new Scene(container));
                        currentStage.show();
                        javafx.animation.PauseTransition pt = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(5));
                        pt.setOnFinished(e -> container.getChildren().remove(overlay));
                        pt.play();
                    } catch (IOException e) {
                        if (loginProgressBar != null) {
                            loginProgressBar.setProgress(0.7);
                        }
                        javafx.animation.PauseTransition ptErr2 = new javafx.animation.PauseTransition(javafx.util.Duration.millis(200));
                        ptErr2.setOnFinished(e2 -> {
                            if (loginProgressBar != null) {
                                loginProgressBar.setProgress(0);
                            }
                            errorMessageLabel.setText("Erro interno ao carregar a tela do sistema. FXML: " + destinoFXML);
                        });
                        ptErr2.play();
                    }
                });
                wait.play();
            }
        });

        loginTask.setOnFailed(ev -> {
            timeline.stop();
            if (loginProgressBar != null) {
                loginProgressBar.setProgress(0.7);
            }
            javafx.animation.PauseTransition ptErr = new javafx.animation.PauseTransition(javafx.util.Duration.millis(200));
            ptErr.setOnFinished(e1 -> {
                if (loginProgressBar != null) {
                    loginProgressBar.setProgress(0);
                }
                errorMessageLabel.setText("Erro ao processar login. Verifique sua conexão.");
            });
            ptErr.play();
            loginButton.setDisable(false);
            emailField.setDisable(false);
            passwordField.setDisable(false);
        });

        Thread t = new Thread(loginTask);
        t.setDaemon(true);
        t.start();
    }
}
