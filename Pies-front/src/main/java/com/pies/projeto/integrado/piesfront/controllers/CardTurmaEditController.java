package com.pies.projeto.integrado.piesfront.controllers;

import com.pies.projeto.integrado.piesfront.dto.EducandoDTO;
import com.pies.projeto.integrado.piesfront.dto.TurmaDTO;
import com.pies.projeto.integrado.piesfront.services.AuthService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class CardTurmaEditController implements Initializable {
    @FXML private VBox cardTurma;
    @FXML private Label nomeTurmaCard;
    @FXML private Label totalAlunoCard;
    @FXML private Label faixaEtariaCard;
    @FXML private Label grauTurmaCard;
    @FXML private Label turnoTurmaCard;
    @FXML private Button excluirTurma;
    @FXML private Button editarTurma;

    private TurmaDTO turma;
    private final AuthService authService = AuthService.getInstance();

    public void setTurma(TurmaDTO turma) {
        this.turma = turma;
        atualizarDados();
    }

    private void atualizarDados() {
        if (turma == null) return;
        if (nomeTurmaCard != null) nomeTurmaCard.setText(turma.nome() != null ? turma.nome() : "Sem nome");
        if (faixaEtariaCard != null) faixaEtariaCard.setText("Faixa Etária: " + (turma.faixaEtaria() != null ? turma.faixaEtaria() : "Não informado"));
        if (grauTurmaCard != null) grauTurmaCard.setText("Grau da turma: " + formatarGrauEscolar(turma.grauEscolar()));
        if (turnoTurmaCard != null) turnoTurmaCard.setText("Turno: " + formatarTurno(turma.turno()));
        if (totalAlunoCard != null && turma.id() != null) {
            try {
                List<EducandoDTO> alunos = authService.getEducandosPorTurma(turma.id());
                int total = alunos != null ? alunos.size() : 0;
                totalAlunoCard.setText("Total de alunos: " + total);
            } catch (Exception e) {
                totalAlunoCard.setText("Total de alunos: 0");
            }
        }
    }

    private String formatarGrauEscolar(String grauEscolar) {
        if (grauEscolar == null) return "Não informado";
        return switch (grauEscolar) {
            case "EDUCACAO_INFANTIL" -> "Educação Infantil";
            case "FUNDAMENTAL_I" -> "Fundamental I";
            case "FUNDAMENTAL_II" -> "Fundamental II";
            default -> grauEscolar;
        };
    }

    private String formatarTurno(String turno) {
        if (turno == null) return "Não informado";
        return switch (turno) {
            case "MATUTINO" -> "Matutino";
            case "VESPERTINO" -> "Vespertino";
            default -> turno;
        };
    }

    @FXML
    private void handleExcluirTurmaAction() {
        if (cardTurma == null || cardTurma.getScene() == null || turma == null || turma.id() == null) return;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/pies/projeto/integrado/piesfront/screens/excluir-confirm-card.fxml"));
            Parent conteudo = loader.load();
            ExcluirConfirmController ctrl = loader.getController();

            Pane root = cardTurma.getScene().getRoot() instanceof Pane p ? p : null;
            if (root == null) return;
            Pane overlay = new Pane(conteudo);
            overlay.setMouseTransparent(false);
            overlay.setManaged(false);
            overlay.prefWidthProperty().bind(root.widthProperty());
            overlay.prefHeightProperty().bind(root.heightProperty());
            root.getChildren().add(overlay);

            Runnable center = () -> {
                if (conteudo instanceof Region r) {
                    r.applyCss();
                    r.autosize();
                    double w = r.prefWidth(-1);
                    double h = r.prefHeight(-1);
                    double cw = root.getWidth();
                    double ch = root.getHeight();
                    r.setLayoutX((cw - w) / 2);
                    r.setLayoutY((ch - h) / 2);
                }
            };
            center.run();
            root.widthProperty().addListener((obs, o, n) -> center.run());
            root.heightProperty().addListener((obs, o, n) -> center.run());

            ctrl.setOnCancel(() -> root.getChildren().remove(overlay));
            ctrl.setOnConfirm(() -> {
                boolean ok = authService.deletarTurma(turma.id());
                root.getChildren().remove(overlay);
                String nome = turma.nome() != null ? turma.nome() : "";
                String msg = ok ? ("A turma " + nome + " foi excluída com sucesso!") : "Falha ao excluir turma!";
                NotificacaoController.exibirTexto(root, msg, ok);
                if (ok && cardTurma.getParent() instanceof Pane parent) {
                    parent.getChildren().remove(cardTurma);
                }
            });
        } catch (IOException e) {
        }
    }

    @FXML
    private void handleEditarTurmaAction(javafx.event.ActionEvent event) {
        if (turma == null || turma.id() == null || editarTurma == null) return;
        com.utils.Janelas.carregarTela(event,
                "/com/pies/projeto/integrado/piesfront/screens/cadastro-de-turma.fxml",
                "Cadastro de Turma",
                ctrl -> {
                    if (ctrl instanceof CadastroTurmaController c) {
                        TurmaDTO dados = authService.getTurmaById(turma.id());
                        if (dados != null) {
                            c.setTurma(dados);
                        }
                    }
                });
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (excluirTurma != null) excluirTurma.setOnAction(e -> handleExcluirTurmaAction());
        if (editarTurma != null) editarTurma.setOnAction(this::handleEditarTurmaAction);
        if (turma != null) atualizarDados();
    }

    public String getTurmaId() {
        return turma != null ? turma.id() : null;
    }
}
