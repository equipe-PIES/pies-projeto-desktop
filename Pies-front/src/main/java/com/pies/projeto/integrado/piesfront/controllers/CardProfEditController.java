package com.pies.projeto.integrado.piesfront.controllers;

import com.pies.projeto.integrado.piesfront.dto.ProfessorDTO;
import com.pies.projeto.integrado.piesfront.services.AuthService;
import javafx.fxml.FXMLLoader;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region;
import com.utils.Janelas;
import com.pies.projeto.integrado.piesfront.controllers.CadastroProfController;

import java.net.URL;
import java.io.IOException;
import java.util.ResourceBundle;

public class CardProfEditController implements Initializable {

    @FXML
    private VBox cardProf;

    @FXML
    private Label nomeLabel;

    @FXML
    private Button excluirProf;

    @FXML
    private Button editarProf;

    @FXML
    private Button infoProfButton;

    private ProfessorDTO professor;
    private final AuthService authService = AuthService.getInstance();

    public void setProfessor(ProfessorDTO professor) {
        this.professor = professor;
        atualizarDados();
    }

    private void atualizarDados() {
        if (professor == null) return;
        if (nomeLabel != null) {
            String nome = professor.getNome() != null ? professor.getNome() : "Nome não informado";
            nomeLabel.setText(nome);
        }
    }

    @FXML
    private void handleExcluirProfAction() {
        if (cardProf == null || cardProf.getScene() == null || professor == null || professor.getId() == null) return;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/pies/projeto/integrado/piesfront/screens/excluir-confirm-card.fxml"));
            Parent conteudo = loader.load();
            ExcluirConfirmController ctrl = loader.getController();

            Pane root = cardProf.getScene().getRoot() instanceof Pane p ? p : null;
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
                boolean ok = authService.deletarProfessor(professor.getId());
                root.getChildren().remove(overlay);
                String nome = professor.getNome() != null ? professor.getNome() : "";
                String msg = ok ? ("Professor(a) " + nome + " foi excluído(a) com sucesso!") : "Falha ao excluir cadastro de professor(a)!";
                NotificacaoController.exibirTexto(root, msg, ok);
                if (ok && cardProf.getParent() instanceof Pane parent) {
                    parent.getChildren().remove(cardProf);
                }
            });
        } catch (IOException e) {
        }
    }

    @FXML
    public void handleInfoAction(javafx.event.ActionEvent event) {
        if (professor == null) return;
        String cpf = professor.getCpf() != null ? professor.getCpf() : "Não informado";
        String formacao = professor.getFormacao() != null ? professor.getFormacao() : "Não informado";
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informações do Professor");
        alert.setHeaderText(professor.getNome());
        alert.setContentText("CPF: " + cpf + "\nFormação: " + formacao);
        alert.showAndWait();
    }

    @FXML
    private void handleEditarProfAction(javafx.event.ActionEvent event) {
        if (professor == null || professor.getId() == null || editarProf == null) return;
        Janelas.carregarTela(event,
                "/com/pies/projeto/integrado/piesfront/screens/cadastro-de-prof.fxml",
                "Cadastro de Professor(a)",
                ctrl -> {
                    if (ctrl instanceof CadastroProfController c) {
                        c.setIndicadorDeTela("Cadastro de Professor(a)");
                        c.setProfessor(professor);
                    }
                });
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (professor != null) {
            atualizarDados();
        }
        if (excluirProf != null) excluirProf.setOnAction(e -> handleExcluirProfAction());
        if (editarProf != null) editarProf.setOnAction(this::handleEditarProfAction);
    }
}
