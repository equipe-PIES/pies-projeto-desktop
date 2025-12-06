package com.pies.projeto.integrado.piesfront.controllers;

import com.pies.projeto.integrado.piesfront.dto.TurmaDTO;
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
import javafx.scene.layout.FlowPane;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class HomeProfController implements Initializable {
    // ----------------------------------------------------
    // DECLARAÇÃO DOS fx:id
    // ----------------------------------------------------
    @FXML
    private Label indicadorDeTela;
    @FXML
    private Label nameUser;
    @FXML
    private Label cargoUser;
    @FXML
    private Button sairButton;
    @FXML
    private Button turmasButton;
    @FXML
    private FlowPane containerCards;
    @FXML
    private ScrollPane turmasScrollPane;

    private final AuthService authService;

    public HomeProfController() {
        this.authService = AuthService.getInstance(); // Usa a instância singleton
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        atualizarIndicadorDeTela(url);
        if (turmasScrollPane != null && containerCards != null) {
            turmasScrollPane.setFitToWidth(true);
            turmasScrollPane.viewportBoundsProperty().addListener((obs, oldBounds, newBounds) -> {
                // Largura do card agora é 340px (ajustado para caber 3 colunas)
                double cardWidth = 340.0;
                double hgap = 30.0;
                double totalPadding = 40.0; // padding do FlowPane (20 * 2)
                
                // Largura necessária para 3 colunas: 340*3 + 30*2 + 40 = 1120px
                double threeColsNeeded = (cardWidth * 3) + (hgap * 2) + totalPadding;
                
                // Define o wrap para mostrar 3 colunas quando possível
                containerCards.setPrefWrapLength(threeColsNeeded);
            });
        }
        javafx.application.Platform.runLater(() -> {
            carregarDadosEmParalelo();
        });
    }

    // ----------------------------------------------------
    // MÉTODOS DE AÇÃO
    // ----------------------------------------------------

    /**
     * Atualiza o texto do indicador de tela baseado no arquivo FXML carregado.
     *
     * @param url URL do arquivo FXML que foi carregado
     */
    private void atualizarIndicadorDeTela(URL url) {
        if (indicadorDeTela == null || url == null) {
            return;
        }

        // Extrai o nome do arquivo do URL
        String arquivoFXML = url.getPath();

        // Determina o texto baseado no nome do arquivo
        //FALTA ADICIONAR MAIS INDICES
        String textoIndicador;

        if (arquivoFXML.contains("tela-inicio-professor.fxml")) {
            textoIndicador = "Turmas";
        } else if (arquivoFXML.contains("tela-inicio-prof.fxml")) {
            textoIndicador = "Turmas";
        } else {
            // Texto padrão caso não reconheça a tela
            textoIndicador = "Indicador de Tela";
        }

        indicadorDeTela.setText(textoIndicador);
    }

    /**
     * Busca as informações do usuário logado e atualiza o nome exibido.
     */
    private void atualizarNomeUsuarioAsync() {
        Thread t = new Thread(() -> {
            UserInfoDTO userInfo = authService.getUserInfo();
            javafx.application.Platform.runLater(() -> {
                if (userInfo != null) {
                    if (nameUser != null && userInfo.name() != null && !userInfo.name().isEmpty()) {
                        nameUser.setText(userInfo.name());
                    }
                    if (cargoUser != null && userInfo.role() != null) {
                        String cargo = switch (userInfo.role().toUpperCase()) {
                            case "PROFESSOR" -> "Professor(a)";
                            case "COORDENADOR" -> "Coordenador(a)";
                            case "ADMIN" -> "Administrador(a)";
                            default -> "Usuário";
                        };
                        cargoUser.setText(cargo);
                    }
                } else {
                    if (nameUser != null) {
                        nameUser.setText("Usuário");
                    }
                    System.err.println("Não foi possível carregar o nome do usuário.");
                }
            });
        });
        t.setDaemon(true);
        t.start();
    }
    
    /**
     * Carrega as turmas do professor logado e exibe como cards
     */
    private void carregarDadosEmParalelo() {
        if (containerCards == null) return;
        Label loading = new Label("Carregando turmas...");
        loading.setStyle("-fx-font-size: 16px; -fx-text-fill: #666;");
        containerCards.getChildren().setAll(loading);

        java.util.concurrent.CompletableFuture<UserInfoDTO> userFuture =
                java.util.concurrent.CompletableFuture.supplyAsync(authService::getUserInfo);
        java.util.concurrent.CompletableFuture<ProfessorDTO> professorFuture =
                java.util.concurrent.CompletableFuture.supplyAsync(authService::getProfessorLogado);
        java.util.concurrent.CompletableFuture<String> profIdFuture =
                java.util.concurrent.CompletableFuture.supplyAsync(authService::getProfessorId);
        java.util.concurrent.CompletableFuture<java.util.List<TurmaDTO>> turmasFuture =
                java.util.concurrent.CompletableFuture.supplyAsync(authService::getTurmas);

        userFuture.thenAccept(userInfo -> javafx.application.Platform.runLater(() -> atualizarNomeUsuarioUI(userInfo)));
        professorFuture.thenAccept(prof -> javafx.application.Platform.runLater(() -> {
            if (prof != null && prof.getNome() != null && !prof.getNome().isEmpty()) {
                if (nameUser != null) nameUser.setText(prof.getNome());
            }
        }));

        profIdFuture.thenCombine(turmasFuture, (profId, todasTurmas) -> {
            if (profId == null || todasTurmas == null) return java.util.List.<TurmaDTO>of();
            return todasTurmas.stream()
                    .filter(t -> profId.equals(t.professorId()))
                    .collect(java.util.stream.Collectors.toList());
        }).thenAccept(turmasDoProfessor -> javafx.application.Platform.runLater(() -> {
            containerCards.getChildren().clear();
            if (turmasDoProfessor.isEmpty()) {
                Label semTurmasLabel = new Label("Nenhuma turma designada a este professor.");
                semTurmasLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #666;");
                containerCards.getChildren().add(semTurmasLabel);
                return;
            }
            for (TurmaDTO turma : turmasDoProfessor) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource(
                            "/com/pies/projeto/integrado/piesfront/screens/card-turma.fxml"));
                    VBox cardNode = loader.load();
                    CardTurmaController cardController = loader.getController();
                    cardController.setTurma(turma);
                    containerCards.getChildren().add(cardNode);
                } catch (IOException e) {
                    System.err.println("Erro ao carregar card de turma: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }));
    }

    /**
     * Método público para atualizar o indicador de tela manualmente.
     * Pode ser usado quando há navegação entre telas.
     *
     * @param texto Texto a ser exibido no indicador
     */
    public void setIndicadorDeTela(String texto) {
        if (indicadorDeTela != null) {
            indicadorDeTela.setText(texto);
        }
    }

    /**
     * Handler para o botão de turmas.
     * Recarrega a tela de turmas (já é a tela atual, então apenas recarrega os cards)
     */
    @FXML
    private void handleTurmasButtonAction() {
        javafx.event.ActionEvent fakeEvent = new javafx.event.ActionEvent(turmasButton, null);
        Janelas.carregarTela(fakeEvent,
                "/com/pies/projeto/integrado/piesfront/screens/tela-inicio-prof.fxml",
                "Turmas");
    }

    private void atualizarNomeUsuarioUI(UserInfoDTO userInfo) {
        if (userInfo != null) {
            if (cargoUser != null && userInfo.role() != null) {
                String cargo = switch (userInfo.role().toUpperCase()) {
                    case "PROFESSOR" -> "Professor(a)";
                    case "COORDENADOR" -> "Coordenador(a)";
                    case "ADMIN" -> "Administrador(a)";
                    default -> "Usuário";
                };
                cargoUser.setText(cargo);
            }
        } else {
            System.err.println("Não foi possível carregar o nome do usuário.");
        }
    }


    /**
     * Handler para o botão de sair.
     * Faz logout do usuário e retorna para a tela de login.
     */
    @FXML
    private void handleSairButtonAction(javafx.event.ActionEvent event) {
        // Faz logout - limpa o token de autenticação
        authService.logout();

        // Carrega a tela de login
        Janelas.carregarTela(event, "/com/pies/projeto/integrado/piesfront/screens/tela-de-login.fxml", "Amparo Edu - Login");
    }

    @FXML
    private void handleProfessoresButtonAction(javafx.event.ActionEvent event) {
        Janelas.carregarTela(event, "/com/pies/projeto/integrado/piesfront/screens/view-profs-coord.fxml", "Professores");
    }
}
