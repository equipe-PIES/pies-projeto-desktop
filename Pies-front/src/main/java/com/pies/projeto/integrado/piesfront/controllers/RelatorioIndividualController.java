package com.pies.projeto.integrado.piesfront.controllers;

import com.pies.projeto.integrado.piesfront.dto.CreateRelatorioIndividualDTO;
import com.pies.projeto.integrado.piesfront.dto.EducandoDTO;
import com.pies.projeto.integrado.piesfront.dto.RelatorioIndividualRequestDTO;
import com.pies.projeto.integrado.piesfront.services.AuthService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.StageStyle;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Label;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import com.utils.Janelas;
import javafx.event.ActionEvent;

public class RelatorioIndividualController {
    @FXML
    private BorderPane anamnese;
    @FXML
    private Label indicadorDeTela;
    @FXML
    private Label validationMsg;
    @FXML
    private TextArea dificuldadesRaciocinio;
    @FXML
    private TextArea dificuldadesRaciocinio1;
    @FXML
    private TextArea dificuldadesRaciocinio11;

    private EducandoDTO educando;
    private RelatorioIndividualRequestDTO formData = new RelatorioIndividualRequestDTO();
    private final AuthService authService = AuthService.getInstance();
    private int currentStep = 1;

    public void setEducando(EducandoDTO educando) {
        this.educando = educando;
        atualizarIndicador();
        carregarRelatorioExistente();
        populateFromFormData();
    }

    public void setFormData(RelatorioIndividualRequestDTO data) {
        if (data != null) {
            this.formData = data;
        }
    }

    public void setStep(int step) {
        this.currentStep = step;
        populateFromFormData();
        atualizarIndicador();
    }

    @FXML
    private void initialize() {
        if (validationMsg != null) {
            validationMsg.setVisible(false);
            validationMsg.setManaged(true);
        }
        atualizarIndicador();
        populateFromFormData();
    }

    private void atualizarIndicador() {
        if (indicadorDeTela != null) {
            String nome = educando != null && educando.nome() != null ? educando.nome() : "";
            indicadorDeTela.setText("Relatório Individual do aluno(a) " + nome);
        }
    }

    @FXML
    private void handleTurmasButtonAction() {
        if (anamnese != null) {
            Janelas.carregarTela(new ActionEvent(anamnese, null),
                    "/com/pies/projeto/integrado/piesfront/screens/tela-inicio-professor.fxml",
                    "Início - Professor(a)");
        }
    }

    @FXML
    private void handleSairButtonAction() {
        authService.logout();
        if (anamnese != null) {
            Janelas.carregarTela(new ActionEvent(anamnese, null),
                    "/com/pies/projeto/integrado/piesfront/screens/tela-de-login.fxml",
                    "Amparo Edu - Login");
        }
    }

    @FXML
    private void handleCancelAction() {
        if (anamnese != null) {
            Janelas.carregarTela(new ActionEvent(anamnese, null),
                    "/com/pies/projeto/integrado/piesfront/screens/view-turma.fxml",
                    "Visualizar Turma",
                    controller -> {
                        if (controller instanceof ViewTurmaController c && educando != null && educando.turmaId() != null) {
                            c.setTurmaId(educando.turmaId());
                        }
                    });
        }
    }

    @FXML
    private void handleBackAction() {
        if (currentStep == 2) {
            abrir("/com/pies/projeto/integrado/piesfront/screens/relatorio-individual-1.fxml", 1);
        } else if (currentStep == 3) {
            abrir("/com/pies/projeto/integrado/piesfront/screens/relatorio-individual-2.fxml", 2);
        }
    }

    @FXML
    private void handleGoToPdi2() {
        if (currentStep == 1) {
            if (validateStep1()) {
                abrir("/com/pies/projeto/integrado/piesfront/screens/relatorio-individual-2.fxml", 2);
            } else {
                showValidation();
            }
        } else if (currentStep == 2) {
            if (validateStep2()) {
                abrir("/com/pies/projeto/integrado/piesfront/screens/relatorio-individual-3.fxml", 3);
            } else {
                showValidation();
            }
        }
    }

    @FXML
    private void handleConcluirAction() {
        captureCurrentStepData();
        if (educando == null || educando.id() == null) {
            showValidation();
            return;
        }
        CreateRelatorioIndividualDTO dto = new CreateRelatorioIndividualDTO(
                educando.id(),
                formData.dadosFuncionais,
                formData.funcionalidadeCognitiva,
                formData.alfabetizacaoLetramento,
                formData.adaptacoesCurriculares,
                formData.participacaoAtividades,
                formData.autonomia,
                formData.interacaoProfessora,
                formData.atividadesVidaDiaria
        );
        var created = authService.criarRelatorioIndividual(dto);
        if (created != null) {
            showPopup("Diagnóstico Inicial registrado com sucesso!", true);
            handleCancelAction();
        } else {
            showPopup("Falha ao enviar Diagnóstico Inicial.", false);
            showValidation();
        }
    }

    private void abrir(String resource, int step) {
        captureCurrentStepData();
        if (anamnese != null) {
            Janelas.carregarTela(new ActionEvent(anamnese, null), resource, null, controller -> {
                if (controller instanceof RelatorioIndividualController c) {
                    c.setEducando(educando);
                    c.setFormData(formData);
                    c.setStep(step);
                }
            });
        }
    }

    private void showValidation() {
        if (validationMsg != null) {
            validationMsg.setText("Algum campo está em branco. Preencha para prosseguir.");
            validationMsg.setVisible(true);
        }
    }

    private void showPopup(String mensagem, boolean sucesso) {
        Label msg = new Label(mensagem);
        String style = sucesso ? "-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-padding: 10 16; -fx-background-radius: 8; -fx-font-weight: bold;"
                : "-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-padding: 10 16; -fx-background-radius: 8; -fx-font-weight: bold;";
        msg.setStyle(style);
        javafx.scene.layout.StackPane overlay = new javafx.scene.layout.StackPane(msg);
        overlay.setStyle("-fx-background-color: transparent;");
        overlay.setMouseTransparent(true);
        javafx.scene.layout.StackPane.setAlignment(msg, javafx.geometry.Pos.CENTER);
        overlay.prefWidthProperty().bind(anamnese.widthProperty());
        overlay.prefHeightProperty().bind(anamnese.heightProperty());
        anamnese.getChildren().add(overlay);
        PauseTransition pt = new PauseTransition(Duration.seconds(5));
        pt.setOnFinished(e -> anamnese.getChildren().remove(overlay));
        pt.play();
    }

    private boolean validateStep1() {
        return !isEmpty(dificuldadesRaciocinio) && !isEmpty(dificuldadesRaciocinio1) && !isEmpty(dificuldadesRaciocinio11);
    }

    private boolean validateStep2() {
        return !isEmpty(dificuldadesRaciocinio) && !isEmpty(dificuldadesRaciocinio1) && !isEmpty(dificuldadesRaciocinio11);
    }

    private boolean isEmpty(TextArea ta) {
        if (ta == null) return false;
        if (!ta.isVisible()) return false;
        String t = ta.getText() != null ? ta.getText().trim() : null;
        return t == null || t.isEmpty();
    }

    private void captureCurrentStepData() {
        if (currentStep == 1) {
            if (dificuldadesRaciocinio != null) formData.dadosFuncionais = safeText(dificuldadesRaciocinio);
            if (dificuldadesRaciocinio1 != null) formData.funcionalidadeCognitiva = safeText(dificuldadesRaciocinio1);
            if (dificuldadesRaciocinio11 != null) formData.alfabetizacaoLetramento = safeText(dificuldadesRaciocinio11);
        } else if (currentStep == 2) {
            if (dificuldadesRaciocinio != null) formData.adaptacoesCurriculares = safeText(dificuldadesRaciocinio);
            if (dificuldadesRaciocinio1 != null) formData.participacaoAtividades = safeText(dificuldadesRaciocinio1);
            if (dificuldadesRaciocinio11 != null) formData.autonomia = safeText(dificuldadesRaciocinio11);
        } else if (currentStep == 3) {
            if (dificuldadesRaciocinio != null) formData.interacaoProfessora = safeText(dificuldadesRaciocinio);
            if (dificuldadesRaciocinio1 != null) formData.atividadesVidaDiaria = safeText(dificuldadesRaciocinio1);
        }
    }

    private void populateFromFormData() {
        if (currentStep == 1) {
            if (dificuldadesRaciocinio != null) dificuldadesRaciocinio.setText(val(formData.dadosFuncionais));
            if (dificuldadesRaciocinio1 != null) dificuldadesRaciocinio1.setText(val(formData.funcionalidadeCognitiva));
            if (dificuldadesRaciocinio11 != null) dificuldadesRaciocinio11.setText(val(formData.alfabetizacaoLetramento));
        } else if (currentStep == 2) {
            if (dificuldadesRaciocinio != null) dificuldadesRaciocinio.setText(val(formData.adaptacoesCurriculares));
            if (dificuldadesRaciocinio1 != null) dificuldadesRaciocinio1.setText(val(formData.participacaoAtividades));
            if (dificuldadesRaciocinio11 != null) dificuldadesRaciocinio11.setText(val(formData.autonomia));
        } else if (currentStep == 3) {
            if (dificuldadesRaciocinio != null) dificuldadesRaciocinio.setText(val(formData.interacaoProfessora));
            if (dificuldadesRaciocinio1 != null) dificuldadesRaciocinio1.setText(val(formData.atividadesVidaDiaria));
        }
    }

    private String safeText(TextArea ta) {
        return ta.getText() != null ? ta.getText().trim() : null;
    }

    private String val(String s) {
        return s == null ? "" : s;
    }

    private void carregarRelatorioExistente() {
        if (educando == null || educando.id() == null) return;
        var lista = authService.getRelatoriosIndividuaisPorEducando(educando.id());
        if (lista == null || lista.isEmpty()) return;
        var dto = lista.get(lista.size() - 1);
        formData.dadosFuncionais = dto.dadosFuncionais();
        formData.funcionalidadeCognitiva = dto.funcionalidadeCognitiva();
        formData.alfabetizacaoLetramento = dto.alfabetizacaoLetramento();
        formData.adaptacoesCurriculares = dto.adaptacoesCurriculares();
        formData.participacaoAtividades = dto.participacaoAtividades();
        formData.autonomia = dto.autonomia();
        formData.interacaoProfessora = dto.interacaoProfessora();
        formData.atividadesVidaDiaria = dto.atividadesVidaDiaria();
    }
}
