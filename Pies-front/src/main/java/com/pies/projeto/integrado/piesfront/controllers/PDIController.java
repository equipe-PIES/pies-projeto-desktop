package com.pies.projeto.integrado.piesfront.controllers;

import com.pies.projeto.integrado.piesfront.dto.EducandoDTO;
import com.pies.projeto.integrado.piesfront.services.AtendimentoFlowService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class PDIController {
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
    private void handleBackAction() {
        handleSairButtonAction();
    }

    @FXML
    private void handleGoToPdi1() {
        abrir("/com/pies/projeto/integrado/piesfront/screens/pdi-1.fxml", "PDI");
    }

    @FXML
    private void handleGoToPdi2() {
        abrir("/com/pies/projeto/integrado/piesfront/screens/pdi-2.fxml", "PDI");
    }

    @FXML
    private void handleGoToPdi3() {
        abrir("/com/pies/projeto/integrado/piesfront/screens/pdi-3.fxml", "PDI");
    }

    @FXML
    private void handleGoToPdi4() {
        abrir("/com/pies/projeto/integrado/piesfront/screens/pdi-4.fxml", "PDI");
    }

    @FXML
    private void handleConcluirAction() {
        if (educando != null) {
            AtendimentoFlowService.getInstance().concluirPDI(educando.id());
        }
        handleSairButtonAction();
    }

    private void abrir(String resource, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(resource));
            Parent root = loader.load();
            PDIController controller = loader.getController();
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
            System.err.println("Erro ao abrir PDI: " + e.getMessage());
        }
    }
}
