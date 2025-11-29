package com.pies.projeto.integrado.piesfront.controllers;

import com.pies.projeto.integrado.piesfront.dto.EducandoDTO;
import com.pies.projeto.integrado.piesfront.services.AtendimentoFlowService;
import com.utils.Janelas;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class PDIController {
    @FXML
    private BorderPane anamnese;
    @FXML
    private Label indicadorDeTela;
    @FXML
    private Label validationMsg;
    @FXML
    private TextField periodoPlano, horarioAtendimento;
    @FXML
    private ChoiceBox<String> frequenciaSemana, diasSemana, composicaoGrupo;
    @FXML
    private TextArea objetivosPlano;

    private EducandoDTO educando;

    public void setEducando(EducandoDTO educando) {
        this.educando = educando;
    }

    @FXML
    private void handleTurmasButtonAction(javafx.event.ActionEvent event) {
        Janelas.carregarTela(event, "/com/pies/projeto/integrado/piesfront/screens/tela-inicio-professor.fxml", "Início - Professor(a)");
    }

    @FXML
    private void handleSairButtonAction() {
        if (anamnese != null && anamnese.getScene() != null) {
            Stage stage = (Stage) anamnese.getScene().getWindow();
            stage.close();
        }
    }

    @FXML
    private void handleCancelAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/pies/projeto/integrado/piesfront/screens/view-turma.fxml"));
            Parent root = loader.load();
            ViewTurmaController controller = loader.getController();
            if (educando != null && educando.turmaId() != null) {
                controller.setTurmaId(educando.turmaId());
            }
            Stage currentStage = (Stage) anamnese.getScene().getWindow();
            currentStage.setScene(new Scene(root));
            currentStage.setTitle("Visualizar Turma");
            
            // Força a maximização
            currentStage.setMaximized(false);
            currentStage.setMaximized(true);
            
            currentStage.show();
        } catch (Exception e) {
            System.err.println("Erro ao voltar para View Turma: " + e.getMessage());
            handleSairButtonAction();
        }
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
        if (!canStartPDI()) {
            showValidation("Só é possível fazer PDI após Anamnese concluída.");
            return;
        }
        if (validatePdi1()) {
            abrir("/com/pies/projeto/integrado/piesfront/screens/pdi-2.fxml", "PDI");
        } else {
            showValidation("Algum campo está em branco. Preencha para prosseguir.");
        }
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

    @FXML
    private void initialize() {
        if (indicadorDeTela != null) {
            indicadorDeTela.setText("PDI");
        }
        if (validationMsg != null) {
            validationMsg.setVisible(false);
            validationMsg.setManaged(true);
        }
    }

    private void showValidation(String msg) {
        if (validationMsg != null) {
            validationMsg.setText(msg);
            validationMsg.setVisible(true);
        }
    }

    private boolean validatePdi1() {
        boolean textEmpty = isEmpty(periodoPlano) || isEmpty(horarioAtendimento) || isEmpty(objetivosPlano);
        boolean choiceEmpty = isChoiceEmpty(frequenciaSemana) || isChoiceEmpty(diasSemana) || isChoiceEmpty(composicaoGrupo);
        return !textEmpty && !choiceEmpty;
    }

    private boolean isEmpty(TextField tf) {
        if (tf == null) return false;
        if (!tf.isVisible()) return false;
        String t = tf.getText() != null ? tf.getText().trim() : null;
        return t == null || t.isEmpty();
    }

    private boolean isEmpty(TextArea ta) {
        if (ta == null) return false;
        if (!ta.isVisible()) return false;
        String t = ta.getText() != null ? ta.getText().trim() : null;
        return t == null || t.isEmpty();
    }

    private boolean isChoiceEmpty(ChoiceBox<?> cb) {
        if (cb == null) return false;
        if (!cb.isVisible()) return false;
        return cb.getValue() == null;
    }

    private boolean canStartPDI() {
        if (educando == null) return false;
        AtendimentoFlowService.Etapa etapa = AtendimentoFlowService.getInstance().getEtapaAtual(educando.id());
        return etapa == AtendimentoFlowService.Etapa.PDI;
    }
}
