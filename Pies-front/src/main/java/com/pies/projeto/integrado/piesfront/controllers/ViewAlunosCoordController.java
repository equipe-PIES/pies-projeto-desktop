package com.pies.projeto.integrado.piesfront.controllers;

import com.pies.projeto.integrado.piesfront.dto.EducandoDTO;
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
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ViewAlunosCoordController implements Initializable {
    @FXML private Label indicadorDeTela;
    @FXML private Label nameUser;
    @FXML private Label cargoUser;
    @FXML private Button sairButton;
    @FXML private Button inicioButton;
    @FXML private FlowPane containerCards;
    @FXML private ScrollPane turmasScrollPane;
    @FXML private TextField buscarAluno;
    @FXML private Button buscarAlunoButton;
    @FXML private ChoiceBox<String> filterTipo;
    @FXML private ChoiceBox<String> filterOpcoes;

    private final AuthService authService = AuthService.getInstance();
    private List<EducandoDTO> todosAlunos;
    private PauseTransition searchDebounce;
    private String currentQuery = "";
    private Map<String, com.pies.projeto.integrado.piesfront.dto.TurmaDTO> turmasPorId;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (indicadorDeTela != null) indicadorDeTela.setText("Alunos");

        if (turmasScrollPane != null && containerCards != null) {
            turmasScrollPane.setFitToWidth(true);
            turmasScrollPane.viewportBoundsProperty().addListener((obs, oldB, newB) -> {
                double cardWidth = 850.0;
                double hgap = containerCards.getHgap();
                double wrap = Math.min(newB.getWidth(), cardWidth + hgap);
                containerCards.setPrefWrapLength(wrap);
            });
        }

        if (buscarAlunoButton != null) {
            buscarAlunoButton.setOnAction(e -> atualizarFiltro());
        }

        if (filterTipo != null) {
            filterTipo.setItems(FXCollections.observableArrayList("Nenhum", "Nome", "Grau de Escolaridade"));
            filterTipo.setValue("Nenhum");
            filterTipo.valueProperty().addListener((obs, ov, nv) -> {
                boolean porEscolaridade = "Grau de Escolaridade".equalsIgnoreCase(nv);
                if (filterOpcoes != null) {
                    filterOpcoes.setDisable(!porEscolaridade);
                    if (!porEscolaridade) {
                        filterOpcoes.getItems().clear();
                        filterOpcoes.setValue(null);
                    } else {
                        popularEscolaridadePadrao();
                    }
                }
                atualizarFiltro();
            });
        }

        if (filterOpcoes != null) {
            filterOpcoes.setDisable(true);
            filterOpcoes.valueProperty().addListener((obs, ov, nv) -> atualizarFiltro());
        }

        if (buscarAluno != null) {
            searchDebounce = new PauseTransition(Duration.millis(300));
            searchDebounce.setOnFinished(e -> atualizarFiltro());
            buscarAluno.textProperty().addListener((obs, oldVal, newVal) -> {
                currentQuery = newVal != null ? newVal : "";
                if (searchDebounce != null) {
                    searchDebounce.stop();
                    searchDebounce.playFromStart();
                }
            });
        }

        javafx.application.Platform.runLater(() -> {
            atualizarNomeCargo();
            carregarAlunos();
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

    private void carregarAlunos() {
        if (containerCards == null) return;
        Label loading = new Label("Carregando alunos...");
        loading.setStyle("-fx-font-size: 16px; -fx-text-fill: #666;");
        containerCards.getChildren().setAll(loading);

        java.util.concurrent.CompletableFuture<java.util.List<com.pies.projeto.integrado.piesfront.dto.EducandoDTO>> alunosFuture =
                java.util.concurrent.CompletableFuture.supplyAsync(this::buscarEducandos);
        java.util.concurrent.CompletableFuture<java.util.List<com.pies.projeto.integrado.piesfront.dto.TurmaDTO>> turmasFuture =
                java.util.concurrent.CompletableFuture.supplyAsync(authService::getTurmas);

        alunosFuture.thenAcceptBoth(turmasFuture, (alunos, turmas) -> {
            this.todosAlunos = alunos != null ? alunos : java.util.List.of();
            java.util.List<com.pies.projeto.integrado.piesfront.dto.TurmaDTO> listaTurmas = turmas != null ? turmas : java.util.List.of();
            this.turmasPorId = listaTurmas.stream()
                    .filter(t -> t.id() != null)
                    .collect(Collectors.toMap(t -> t.id(), t -> t, (a, b) -> a));
            javafx.application.Platform.runLater(() -> {
                popularEscolaridadePadrao();
                exibirLista(todosAlunos);
            });
        });
    }

    private void exibirLista(List<EducandoDTO> lista) {
        containerCards.getChildren().clear();
        if (lista == null || lista.isEmpty()) {
            Label vazio = new Label("Nenhum aluno cadastrado.");
            vazio.setStyle("-fx-font-size: 16px; -fx-text-fill: #666;");
            containerCards.getChildren().add(vazio);
            return;
        }
        
        // Carregar cards em lotes para evitar travamento da UI
        final int BATCH_SIZE = 10;
        carregarCardsEmLotes(lista, 0, BATCH_SIZE);
    }
    
    private void carregarCardsEmLotes(List<EducandoDTO> lista, int start, int batchSize) {
        if (start >= lista.size()) return;
        
        int end = Math.min(start + batchSize, lista.size());
        List<EducandoDTO> lote = lista.subList(start, end);
        
        // Carregar FXML em background thread
        java.util.concurrent.CompletableFuture.supplyAsync(() -> {
            List<VBox> cards = new java.util.ArrayList<>();
            for (EducandoDTO aluno : lote) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource(
                            "/com/pies/projeto/integrado/piesfront/screens/card-aluno-edit.fxml"));
                    VBox node = loader.load();
                    CardAlunoController controller = loader.getController();
                    controller.setEducando(aluno);
                    if (turmasPorId != null && aluno.turmaId() != null) {
                        var turma = turmasPorId.get(aluno.turmaId());
                        controller.setTurmaInfo(turma);
                    }
                    cards.add(node);
                } catch (IOException e) {
                    System.err.println("Erro ao carregar card de aluno: " + e.getMessage());
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
        String termo = buscarAluno != null && buscarAluno.getText() != null ? buscarAluno.getText().trim() : "";
        if (termo.isEmpty()) {
            exibirLista(todosAlunos);
            return;
        }
        List<EducandoDTO> filtrados = todosAlunos.stream()
                .filter(a -> a.nome() != null && a.nome().toLowerCase().contains(termo.toLowerCase()))
                .toList();
        exibirLista(filtrados);
    }

    private void atualizarFiltro() {
        String termo = buscarAluno != null && buscarAluno.getText() != null ? buscarAluno.getText().trim() : "";
        String tipo = filterTipo != null ? filterTipo.getValue() : "Nome";
        String opcao = filterOpcoes != null ? filterOpcoes.getValue() : null;

        if (todosAlunos == null) {
            exibirLista(java.util.List.of());
            return;
        }

        if ("Grau de Escolaridade".equalsIgnoreCase(tipo)) {
            String codigoEscolaridade = (opcao != null && !"Nenhuma opção".equalsIgnoreCase(opcao)) ? mapEscolaridadeLabelToBackend(opcao) : null;
            List<EducandoDTO> filtrados = todosAlunos.stream()
                    .filter(a -> codigoEscolaridade == null || (a.escolaridade() != null && a.escolaridade().equalsIgnoreCase(codigoEscolaridade)))
                    .filter(a -> termo.isEmpty() || (a.nome() != null && a.nome().toLowerCase().contains(termo.toLowerCase())))
                    .toList();
            exibirLista(filtrados);
        } else {
            filtrarPorNome();
        }
    }

    private void popularEscolaridadePadrao() {
        if (filterOpcoes == null || filterTipo == null) return;
        if (!"Grau de Escolaridade".equalsIgnoreCase(filterTipo.getValue())) return;
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

    private String formatarEscolaridade(String escolaridade) {
        if (escolaridade == null) return "Não informado";
        return switch (escolaridade) {
            case "EDUCACAO_INFANTIL" -> "Educação Infantil";
            case "ESTIMULACAO_PRECOCE" -> "Estimulação Precoce";
            case "FUNDAMENTAL_I" -> "Fundamental I";
            case "FUNDAMENTAL_II" -> "Fundamental II";
            case "MEDIO" -> "Ensino Médio";
            case "OUTRO" -> "Outro";
            case "PREFIRO_NAO_INFORMAR" -> "Prefiro não informar";
            default -> escolaridade;
        };
    }

    private String mapEscolaridadeLabelToBackend(String label) {
        if (label == null) return null;
        if ("Nenhuma opção".equalsIgnoreCase(label)) return null;
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

    private java.util.List<EducandoDTO> buscarEducandos() {
        java.util.List<EducandoDTO> lista = authService.getEducandosSimplificados();
        if (lista == null || lista.isEmpty()) {
            lista = authService.getEducandos();
        }
        return lista != null ? lista : java.util.List.of();
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
