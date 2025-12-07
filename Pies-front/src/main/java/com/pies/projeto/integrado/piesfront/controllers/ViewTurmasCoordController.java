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
import javafx.scene.control.ChoiceBox;
import javafx.collections.FXCollections;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

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
    @FXML private ChoiceBox<String> filterTipo;
    @FXML private ChoiceBox<String> filterOpcoes;

    private final AuthService authService = AuthService.getInstance();
    private List<TurmaDTO> todasTurmas;
    private java.util.List<com.pies.projeto.integrado.piesfront.dto.ProfessorDTO> todosProfessores;
    private PauseTransition searchDebounce;

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
            buscarTurmaButton.setOnAction(e -> atualizarFiltro());
        }

        if (filterTipo != null) {
            filterTipo.setItems(FXCollections.observableArrayList("Nenhum", "Professor", "Grau de Escolaridade"));
            filterTipo.setValue("Nenhum");
            filterTipo.valueProperty().addListener((obs, ov, nv) -> {
                if (filterOpcoes != null) {
                    if ("Professor".equalsIgnoreCase(nv)) {
                        filterOpcoes.setDisable(false);
                        popularProfessoresOpcoes();
                    } else if ("Grau de Escolaridade".equalsIgnoreCase(nv)) {
                        filterOpcoes.setDisable(false);
                        popularEscolaridadePadrao();
                    } else {
                        filterOpcoes.getItems().clear();
                        filterOpcoes.setValue(null);
                        filterOpcoes.setDisable(true);
                    }
                }
                atualizarFiltro();
            });
        }

        if (filterOpcoes != null) {
            filterOpcoes.valueProperty().addListener((obs, ov, nv) -> atualizarFiltro());
        }

        if (buscarTurma != null) {
            searchDebounce = new PauseTransition(Duration.millis(300));
            searchDebounce.setOnFinished(e -> atualizarFiltro());
            buscarTurma.textProperty().addListener((obs, ov, nv) -> {
                if (searchDebounce != null) {
                    searchDebounce.stop();
                    searchDebounce.playFromStart();
                }
            });
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
        java.util.concurrent.CompletableFuture<java.util.List<com.pies.projeto.integrado.piesfront.dto.ProfessorDTO>> profsFuture =
                java.util.concurrent.CompletableFuture.supplyAsync(authService::getProfessores);

        turmasFuture.thenAcceptBoth(profsFuture, (listaTurmas, listaProfs) -> {
            todasTurmas = listaTurmas != null ? listaTurmas : java.util.List.of();
            todosProfessores = listaProfs != null ? listaProfs : java.util.List.of();
            javafx.application.Platform.runLater(() -> {
                if (filterTipo != null && "Professor".equalsIgnoreCase(filterTipo.getValue())) {
                    popularProfessoresOpcoes();
                } else if (filterTipo != null && "Grau de Escolaridade".equalsIgnoreCase(filterTipo.getValue())) {
                    popularEscolaridadePadrao();
                }
                exibirLista(todasTurmas);
            });
        });
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
                    CardTurmaEditController controller = loader.getController();
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

    private void atualizarFiltro() {
        String termo = buscarTurma != null && buscarTurma.getText() != null ? buscarTurma.getText().trim() : "";
        String tipo = filterTipo != null ? filterTipo.getValue() : null;
        String opcao = filterOpcoes != null ? filterOpcoes.getValue() : null;

        if (todasTurmas == null) {
            exibirLista(java.util.List.of());
            return;
        }

        if ("Professor".equalsIgnoreCase(tipo)) {
            String professorNome = (opcao != null && !"Nenhuma opção".equalsIgnoreCase(opcao)) ? opcao : null;
            List<TurmaDTO> filtradas = todasTurmas.stream()
                    .filter(t -> professorNome == null || (t.professorNome() != null && t.professorNome().equalsIgnoreCase(professorNome)))
                    .filter(t -> termo.isEmpty() || (t.nome() != null && t.nome().toLowerCase().contains(termo.toLowerCase())))
                    .toList();
            exibirLista(filtradas);
        } else if ("Grau de Escolaridade".equalsIgnoreCase(tipo)) {
            String codigo = (opcao != null && !"Nenhuma opção".equalsIgnoreCase(opcao)) ? mapEscolaridadeLabelToBackend(opcao) : null;
            List<TurmaDTO> filtradas = todasTurmas.stream()
                    .filter(t -> codigo == null || (t.grauEscolar() != null && t.grauEscolar().equalsIgnoreCase(codigo)))
                    .filter(t -> termo.isEmpty() || (t.nome() != null && t.nome().toLowerCase().contains(termo.toLowerCase())))
                    .toList();
            exibirLista(filtradas);
        } else {
            List<TurmaDTO> filtradas = todasTurmas.stream()
                    .filter(t -> termo.isEmpty() || (t.nome() != null && t.nome().toLowerCase().contains(termo.toLowerCase())))
                    .toList();
            exibirLista(filtradas);
        }
    }

    private void popularProfessoresOpcoes() {
        if (filterOpcoes == null) return;
        java.util.List<String> nomes = todosProfessores != null ? todosProfessores.stream()
                .map(com.pies.projeto.integrado.piesfront.dto.ProfessorDTO::getNome)
                .filter(n -> n != null && !n.isBlank())
                .distinct()
                .sorted(String::compareToIgnoreCase)
                .toList() : java.util.List.of();
        java.util.List<String> itens = new java.util.ArrayList<>();
        itens.add("Nenhuma opção");
        itens.addAll(nomes);
        filterOpcoes.setItems(FXCollections.observableArrayList(itens));
        filterOpcoes.setValue("Nenhuma opção");
    }

    private void popularEscolaridadePadrao() {
        if (filterOpcoes == null) return;
        filterOpcoes.setItems(FXCollections.observableArrayList(
                "Nenhuma opção",
                "Educação Infantil",
                "Estimulação Precoce",
                "Fundamental I",
                "Fundamental II",
                "Ensino Médio",
                "Outro",
                "Prefiro não informar"
        ));
        filterOpcoes.setValue("Nenhuma opção");
    }

    private String mapEscolaridadeLabelToBackend(String label) {
        if (label == null) return null;
        String v = label.trim();
        if (v.equalsIgnoreCase("Educação Infantil") || v.equalsIgnoreCase("Educacao Infantil")) return "EDUCACAO_INFANTIL";
        if (v.equalsIgnoreCase("Estimulação Precoce") || v.equalsIgnoreCase("Estimulacao Precoce")) return "ESTIMULACAO_PRECOCE";
        if (v.equalsIgnoreCase("Fundamental I")) return "FUNDAMENTAL_I";
        if (v.equalsIgnoreCase("Fundamental II")) return "FUNDAMENTAL_II";
        if (v.equalsIgnoreCase("Ensino Médio") || v.equalsIgnoreCase("Ensino Medio")) return "MEDIO";
        if (v.equalsIgnoreCase("Outro")) return "OUTRO";
        if (v.equalsIgnoreCase("Prefiro não informar") || v.equalsIgnoreCase("Prefiro nao informar")) return "PREFIRO_NAO_INFORMAR";
        return v;
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
