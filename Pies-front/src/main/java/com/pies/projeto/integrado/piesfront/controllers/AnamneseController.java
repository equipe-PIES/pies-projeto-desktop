package com.pies.projeto.integrado.piesfront.controllers;

import com.pies.projeto.integrado.piesfront.dto.EducandoDTO;
import com.pies.projeto.integrado.piesfront.services.AtendimentoFlowService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class AnamneseController {
    @FXML
    private BorderPane anamnese;

    private EducandoDTO educando;

    public void setEducando(EducandoDTO educando) {
        this.educando = educando;
    }

    @FXML
    private void handleTurmasButtonAction() {}

    @FXML
    private void handleSairButtonAction() {
        if (anamnese != null && anamnese.getScene() != null) {
            Stage stage = (Stage) anamnese.getScene().getWindow();
            stage.close();
        }
    }

    @FXML
    private void handleCancelAction() {
        handleSairButtonAction();
    }

    @FXML
    private void handleGoToAnamnese1() {
        abrir("/com/pies/projeto/integrado/piesfront/screens/anamnese-1.fxml", "Anamnese");
    }

    @FXML
    private void handleGoToAnamnese2() {
        abrir("/com/pies/projeto/integrado/piesfront/screens/anamnese-2.fxml", "Anamnese");
    }

    @FXML
    private void handleGoToAnamnese3() {
        abrir("/com/pies/projeto/integrado/piesfront/screens/anamnese-3.fxml", "Anamnese");
    }

    @FXML
    private void handleConcluirAction() {
        if (educando != null) {
            AtendimentoFlowService.getInstance().concluirAnamnese(educando.id());
        }
        handleSairButtonAction();
    }

    private void abrir(String resource, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(resource));
            Parent root = loader.load();
            AnamneseController controller = loader.getController();
            controller.setEducando(educando);
            Stage stage;
            if (anamnese != null && anamnese.getScene() != null) {
                stage = (Stage) anamnese.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle(titulo);
            } else {
                stage = new Stage();
                stage.setTitle(titulo);
                stage.setScene(new Scene(root));
                stage.setResizable(false);
                stage.show();
            }
        } catch (Exception e) {
            System.err.println("Erro ao abrir anamnese: " + e.getMessage());
        }
    }
}
