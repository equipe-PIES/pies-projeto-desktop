package com.pies.projeto.integrado.piesfront.controllers;

import com.pies.projeto.integrado.piesfront.dto.TurmaDTO;
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

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ViewTurmasCoordController implements Initializable {

    @FXML private Label indicadorDeTela;
    @FXML private Label nameUser;
    @FXML private Label cargoUser;
    @FXML private Button sairButton;
    @FXML private Button inicioButton;
    @FXML private Button professoresButton;
    @FXML private Button alunosButton;
    @FXML private FlowPane containerCards;
    @FXML private ScrollPane turmasScrollPane;
    @FXML private TextField buscarTurma;
    @FXML private Button buscarTurmaButton;

    private final AuthService authService = AuthService.getInstance();
    private List<TurmaDTO> todasTurmas;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (indicadorDeTela != null) indicadorDeTela.setText("Turmas");

        if (turmasScrollPane != null && containerCards != null) {
            turmasScrollPane.setFitToWidth(true);
            turmasScrollPane.viewportBoundsProperty().addListener((obs, oldB, newB) -> {
                double cardWidth = 350.0;
                double hgap = containerCards.getHgap();
                double threeColsWidth = cardWidth * 3 + hgap * 2;
                double wrap = Math.min(newB.getWidth(), threeColsWidth);
                containerCards.setPrefWrapLength(wrap);
            });
        }

        if (buscarTurmaButton != null) {
            buscarTurmaButton.setOnAction(e -> filtrarPorNome());
        }

        javafx.application.Platform.runLater(() -> {
            atualizarNomeCargo();
            carregarTurmas();
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

    private void carregarTurmas() {
        if (containerCards == null) return;
        Label loading = new Label("Carregando turmas...");
        loading.setStyle("-fx-font-size: 16px; -fx-text-fill: #666;");
        containerCards.getChildren().setAll(loading);

        java.util.concurrent.CompletableFuture<java.util.List<TurmaDTO>> turmasFuture =
                java.util.concurrent.CompletableFuture.supplyAsync(authService::getTurmas);

        turmasFuture.thenAccept(lista -> javafx.application.Platform.runLater(() -> {
            todasTurmas = lista != null ? lista : java.util.List.of();
            exibirLista(todasTurmas);
        }));
    }

    private void exibirLista(List<TurmaDTO> lista) {
        containerCards.getChildren().clear();
        if (lista == null || lista.isEmpty()) {
            Label vazio = new Label("Nenhuma turma cadastrada.");
            vazio.setStyle("-fx-font-size: 16px; -fx-text-fill: #666;");
            containerCards.getChildren().add(vazio);
            return;
        }
        
        // Carregar cards em lotes para evitar travamento da UI
        final int BATCH_SIZE = 10;
        carregarCardsEmLotes(lista, 0, BATCH_SIZE);
    }
    
    private void carregarCardsEmLotes(List<TurmaDTO> lista, int start, int batchSize) {
        if (start >= lista.size()) return;
        
        int end = Math.min(start + batchSize, lista.size());
        List<TurmaDTO> lote = lista.subList(start, end);
        
        // Carregar FXML em background thread
        java.util.concurrent.CompletableFuture.supplyAsync(() -> {
            List<VBox> cards = new java.util.ArrayList<>();
            for (TurmaDTO t : lote) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource(
                            "/com/pies/projeto/integrado/piesfront/screens/card-turma-edit.fxml"));
                    VBox node = loader.load();
                    CardTurmaController controller = loader.getController();
                    controller.setTurma(t);
                    cards.add(node);
                } catch (IOException e) {
                    System.err.println("Erro ao carregar card de turma: " + e.getMessage());
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
        String termo = buscarTurma != null && buscarTurma.getText() != null ? buscarTurma.getText().trim() : "";
        if (termo.isEmpty()) {
            exibirLista(todasTurmas);
            return;
        }
        List<TurmaDTO> filtradas = todasTurmas.stream()
                .filter(t -> t.nome() != null && t.nome().toLowerCase().contains(termo.toLowerCase()))
                .toList();
        exibirLista(filtradas);
    }

    @FXML
    private void handleInicioButtonAction(javafx.event.ActionEvent event) {
        Janelas.carregarTela(event,
                "/com/pies/projeto/integrado/piesfront/screens/tela-inicio-coord.fxml",
                "Início - Coordenador(a)");
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
    private void handleTurmasButtonAction(javafx.event.ActionEvent event) {
        Janelas.carregarTela(event,
                "/com/pies/projeto/integrado/piesfront/screens/view-turmas-coord.fxml",
                "Turmas");
    }

    @FXML
    private void handleAlunosButtonAction(javafx.event.ActionEvent event) {
        Janelas.carregarTela(event,
                "/com/pies/projeto/integrado/piesfront/screens/view-alunos-coord.fxml",
                "Alunos");
    }
}
