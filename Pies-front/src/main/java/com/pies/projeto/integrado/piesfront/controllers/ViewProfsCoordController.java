package com.pies.projeto.integrado.piesfront.controllers;

import com.pies.projeto.integrado.piesfront.dto.ProfessorDTO;
import com.pies.projeto.integrado.piesfront.dto.UserInfoDTO;
import com.pies.projeto.integrado.piesfront.services.AuthService;
import com.utils.Janelas;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ViewProfsCoordController implements Initializable {

    @FXML private Label indicadorDeTela;
    @FXML private Label nameUser;
    @FXML private Label cargoUser;
    @FXML private Button sairButton;
    @FXML private Button inicioButton;
    @FXML private FlowPane containerCards;
    @FXML private ScrollPane turmasScrollPane;
    @FXML private TextField buscarProf;
    @FXML private Button buscarProfButton;

    private final AuthService authService = AuthService.getInstance();
    private List<ProfessorDTO> todosProfessores;
    private PauseTransition searchDebounce;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (indicadorDeTela != null) indicadorDeTela.setText("Professores");

        if (turmasScrollPane != null && containerCards != null) {
            turmasScrollPane.setFitToWidth(true);
            turmasScrollPane.viewportBoundsProperty().addListener((obs, oldB, newB) -> {
                double cardWidth = 850.0;
                double hgap = containerCards.getHgap();
                double twoColsWidth = cardWidth * 1 + hgap * 0; // cards são largura 850
                double wrap = Math.min(newB.getWidth(), twoColsWidth);
                containerCards.setPrefWrapLength(wrap);
            });
        }

        if (buscarProfButton != null) {
            buscarProfButton.setOnAction(e -> filtrarPorNome());
        }

        if (buscarProf != null) {
            searchDebounce = new PauseTransition(Duration.millis(300));
            searchDebounce.setOnFinished(e -> filtrarPorNome());
            buscarProf.textProperty().addListener((obs, ov, nv) -> {
                if (searchDebounce != null) {
                    searchDebounce.stop();
                    searchDebounce.playFromStart();
                }
            });
        }

        javafx.application.Platform.runLater(() -> {
            atualizarNomeCargo();
            carregarProfessores();
        });
    }

    private void atualizarNomeCargo() {
        UserInfoDTO userInfo = authService.getUserInfo();
        if (userInfo != null) {
            if (nameUser != null && userInfo.name() != null) nameUser.setText(userInfo.name());
            if (cargoUser != null && userInfo.role() != null) {
                String cargo = switch (userInfo.role().toUpperCase()) {
                    case "COORDENADOR" -> "Coordenadora";
                    case "ADMIN" -> "Administrador(a)";
                    case "PROFESSOR" -> "Professor(a)";
                    default -> "Usuário";
                };
                cargoUser.setText(cargo);
            }
        }
    }

    private void carregarProfessores() {
        if (containerCards == null) return;
        Label loading = new Label("Carregando professores...");
        loading.setStyle("-fx-font-size: 16px; -fx-text-fill: #666;");
        containerCards.getChildren().setAll(loading);

        java.util.concurrent.CompletableFuture<java.util.List<ProfessorDTO>> profsFuture =
                java.util.concurrent.CompletableFuture.supplyAsync(authService::getProfessores);

        profsFuture.thenAccept(lista -> javafx.application.Platform.runLater(() -> {
            todosProfessores = lista != null ? lista : java.util.List.of();
            exibirLista(todosProfessores);
        }));
    }

    private void exibirLista(List<ProfessorDTO> lista) {
        containerCards.getChildren().clear();
        if (lista == null || lista.isEmpty()) {
            Label vazio = new Label("Nenhum professor cadastrado.");
            vazio.setStyle("-fx-font-size: 16px; -fx-text-fill: #666;");
            containerCards.getChildren().add(vazio);
            return;
        }
        
        // Carregar cards em lotes para evitar travamento da UI
        final int BATCH_SIZE = 10;
        carregarCardsEmLotes(lista, 0, BATCH_SIZE);
    }
    
    private void carregarCardsEmLotes(List<ProfessorDTO> lista, int start, int batchSize) {
        if (start >= lista.size()) return;
        
        int end = Math.min(start + batchSize, lista.size());
        List<ProfessorDTO> lote = lista.subList(start, end);
        
        // Carregar FXML em background thread
        java.util.concurrent.CompletableFuture.supplyAsync(() -> {
            List<VBox> cards = new java.util.ArrayList<>();
            for (ProfessorDTO p : lote) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource(
                            "/com/pies/projeto/integrado/piesfront/screens/card-prof-edit.fxml"));
                    VBox node = loader.load();
                    CardProfEditController controller = loader.getController();
                    controller.setProfessor(p);
                    cards.add(node);
                } catch (IOException e) {
                    System.err.println("Erro ao carregar card de professor: " + e.getMessage());
                }
            }
            return cards;
        }).thenAccept(cards -> javafx.application.Platform.runLater(() -> {
            containerCards.getChildren().addAll(cards);
            // Carregar próximo lote
            carregarCardsEmLotes(lista, end, batchSize);
        }));
    }

    private void filtrarPorNome() {
        String termo = buscarProf != null && buscarProf.getText() != null ? buscarProf.getText().trim() : "";
        if (termo.isEmpty()) {
            exibirLista(todosProfessores);
            return;
        }
        List<ProfessorDTO> filtrados = todosProfessores.stream()
                .filter(p -> p.getNome() != null && p.getNome().toLowerCase().contains(termo.toLowerCase()))
                .toList();
        exibirLista(filtrados);
    }

    @FXML
    private void handleInicioButtonAction(javafx.event.ActionEvent event) {
        Janelas.carregarTela(event, 
                "/com/pies/projeto/integrado/piesfront/screens/tela-inicio-coord.fxml", 
                "Início - Coordenador(a)");
    }

    @FXML
    private void handleTurmasButtonAction(javafx.event.ActionEvent event) {
        Janelas.carregarTela(event,
                "/com/pies/projeto/integrado/piesfront/screens/view-turmas-coord.fxml",
                "Turmas");
    }

    @FXML
    private void handleSairButtonAction(javafx.event.ActionEvent event) {
        authService.logout();
        Janelas.carregarTela(event, 
                "/com/pies/projeto/integrado/piesfront/screens/tela-de-login.fxml", 
                "Amparo Edu - Login");
    }

    @FXML
    private void handleProfessoresButtonAction(javafx.event.ActionEvent event) {
        Janelas.carregarTela(event,
                "/com/pies/projeto/integrado/piesfront/screens/view-profs-coord.fxml",
                "Professores");
    }

    @FXML
    private void handleAlunosButtonAction(javafx.event.ActionEvent event) {
        Janelas.carregarTela(event,
                "/com/pies/projeto/integrado/piesfront/screens/view-alunos-coord.fxml",
                "Alunos");
    }
}
