package com.pies.projeto.integrado.piesfront.controllers;

import com.pies.projeto.integrado.piesfront.dto.ProfessorDTO;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.net.URL;
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (professor != null) {
            atualizarDados();
        }
    }
}
