package com.pies.projeto.integrado.piesfront.controllers;

import com.pies.projeto.integrado.piesfront.dto.EducandoDTO;
import com.pies.projeto.integrado.piesfront.dto.AnamneseRequestDTO;
import com.pies.projeto.integrado.piesfront.services.AtendimentoFlowService;
import com.pies.projeto.integrado.piesfront.services.AnamneseService;
import com.pies.projeto.integrado.piesfront.services.AuthService;
import com.utils.Janelas;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.collections.FXCollections;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class AnamneseController {
    @FXML
    private BorderPane anamnese;
    @FXML
    private Label indicadorDeTela;
    @FXML
    private Label validationMsg;

    private EducandoDTO educando;
    private AnamneseRequestDTO formData = new AnamneseRequestDTO();
    private final AnamneseService anamneseService = new AnamneseService();
    private final AuthService authService = AuthService.getInstance();

    public void setEducando(EducandoDTO educando) {
        this.educando = educando;
    }

    public void setFormData(AnamneseRequestDTO data) {
        if (data != null) {
            this.formData = data;
        }
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
    private void handleGoToAnamnese1() {
        abrir("/com/pies/projeto/integrado/piesfront/screens/anamnese-1.fxml", "Anamnese");
        if (validationMsg != null) {
            validationMsg.setVisible(false);
        }
    }

    @FXML
    private void handleGoToAnamnese2() {
        if (validateAnamnese1()) {
            abrir("/com/pies/projeto/integrado/piesfront/screens/anamnese-2.fxml", "Anamnese");
        } else {
            showValidation();
        }
    }

    @FXML
    private void handleGoToAnamnese3() {
        if (validateAnamnese2()) {
            abrir("/com/pies/projeto/integrado/piesfront/screens/anamnese-3.fxml", "Anamnese");
        } else {
            showValidation();
        }
    }

    @FXML
    private void handleConcluirAction() {
        captureCurrentStepData();
        if (educando != null) {
            String token = authService.getCurrentToken();
            boolean ok = anamneseService.submit(educando.id(), formData, token);
            if (ok) {
                AtendimentoFlowService.getInstance().concluirAnamnese(educando.id());
            } else {
                System.err.println("Falha ao enviar anamnese");
            }
        }
        handleSairButtonAction();
    }

    private void abrir(String resource, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(resource));
            Parent root = loader.load();
            AnamneseController controller = loader.getController();
            captureCurrentStepData();
            controller.setEducando(educando);
            controller.setFormData(formData);
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

    @FXML
    private CheckBox convenioSim, convenioNao, doencaContagiosaSim, doencaContagiosaNao, medicacaoSim, medicacaoNao;
    @FXML
    private CheckBox convulsaoSim, convulsaoNao, vacinacaoSim, vacinacaoNao;
    @FXML
    private CheckBox dificuldadesSim, dificuldadesNao, apoioPedagogicoSim, apoioPedagogicoNao;
    @FXML
    private CheckBox sustentouCabecaSim, sustentouCabecaNao, engatinhouSim, engatinhouNao, sentouSim, sentouNao, andouSim, andouNao, terapiaSim, terapiaNao, falouSim, falouNao;
    @FXML
    private CheckBox chorouSim, chorouNao, ficouRoxoSim, ficouRoxoNao, incubadoraSim, incubadoraNao, amamentadoSim, amamentadoNao;
    @FXML
    private CheckBox prematuridadeSim, prematuridadeNao;
    @FXML
    private Label questConvenio, questDoencaContagiosa, questMedicacao, questDificuldade, questApioPedagogico;
    @FXML
    private Label questCausaPrematuridade1;
    @FXML
    private Label lblSustentouCabecaMeses, lblEngatinhouMeses, lblSentouMeses, lblAndouMeses, lblTerapiaMotivo, lblFalouMeses;
    @FXML
    private TextField convenio, doencaContagiosa, medicacoes, servicosFrequentados1,
            inicioEscolarizacao, dificuldades, apoioPedagogico,
            cidadeNascimento, maternidade,
            sustentouCabeca, engatinhou, sentou, andou, terapia, falou;
    @FXML
    private TextField prematuridade1;
    @FXML
    private ChoiceBox<String> tipoParto;

    @FXML
    private void initialize() {
        setupConditionalVisibility();
        populateFromFormData();
        if (indicadorDeTela != null) {
            indicadorDeTela.setText("Anamnese");
        }
        if (validationMsg != null) {
            validationMsg.setVisible(false);
            validationMsg.setManaged(true);
        }
        if (tipoParto != null && (tipoParto.getItems() == null || tipoParto.getItems().isEmpty())) {
            tipoParto.setItems(FXCollections.observableArrayList("Normal", "Cesáreo", "Fórceps"));
        }
    }

    private void showValidation() {
        if (validationMsg != null) {
            validationMsg.setText("Algum campo está em branco. Preencha para prosseguir.");
            validationMsg.setVisible(true);
        }
    }

    private boolean anyEmpty(TextField... fields) {
        if (fields == null) return false;
        for (TextField f : fields) {
            if (f == null) continue;
            if (!f.isVisible()) continue;
            String t = safeText(f);
            if (t == null || t.isEmpty()) return true;
        }
        return false;
    }

    private boolean anyUnselected(CheckBox... pairs) {
        if (pairs == null) return false;
        for (int i = 0; i + 1 < pairs.length; i += 2) {
            CheckBox a = pairs[i], b = pairs[i + 1];
            if (a == null || b == null) continue;
            if (!a.isVisible() && !b.isVisible()) continue;
            if (!a.isSelected() && !b.isSelected()) return true;
        }
        return false;
    }

    private boolean choiceEmpty(ChoiceBox<?>... boxes) {
        if (boxes == null) return false;
        for (ChoiceBox<?> cb : boxes) {
            if (cb == null) continue;
            if (!cb.isVisible()) continue;
            if (cb.getValue() == null) return true;
        }
        return false;
    }

    private boolean validateAnamnese1() {
        boolean emptyText = anyEmpty(servicosFrequentados1, inicioEscolarizacao,
                convenioSim != null && convenioSim.isSelected() ? convenio : null,
                doencaContagiosaSim != null && doencaContagiosaSim.isSelected() ? doencaContagiosa : null,
                medicacaoSim != null && medicacaoSim.isSelected() ? medicacoes : null,
                dificuldadesSim != null && dificuldadesSim.isSelected() ? dificuldades : null,
                apoioPedagogicoSim != null && apoioPedagogicoSim.isSelected() ? apoioPedagogico : null,
                prematuridadeSim != null && prematuridadeSim.isSelected() ? prematuridade1 : null);

        boolean missingChecks = anyUnselected(convulsaoSim, convulsaoNao,
                convenioSim, convenioNao,
                vacinacaoSim, vacinacaoNao,
                doencaContagiosaSim, doencaContagiosaNao,
                medicacaoSim, medicacaoNao,
                dificuldadesSim, dificuldadesNao,
                apoioPedagogicoSim, apoioPedagogicoNao,
                prematuridadeSim, prematuridadeNao);

        return !emptyText && !missingChecks;
    }

    private boolean validateAnamnese2() {
        boolean emptyText = anyEmpty(cidadeNascimento, maternidade,
                sustentouCabecaSim != null && sustentouCabecaSim.isSelected() ? sustentouCabeca : null,
                engatinhouSim != null && engatinhouSim.isSelected() ? engatinhou : null,
                sentouSim != null && sentouSim.isSelected() ? sentou : null,
                andouSim != null && andouSim.isSelected() ? andou : null,
                terapiaSim != null && terapiaSim.isSelected() ? terapia : null,
                falouSim != null && falouSim.isSelected() ? falou : null);

        boolean missingChecks = anyUnselected(chorouSim, chorouNao,
                ficouRoxoSim, ficouRoxoNao,
                incubadoraSim, incubadoraNao,
                amamentadoSim, amamentadoNao,
                sustentouCabecaSim, sustentouCabecaNao,
                engatinhouSim, engatinhouNao,
                sentouSim, sentouNao,
                andouSim, andouNao,
                terapiaSim, terapiaNao,
                falouSim, falouNao);

        boolean emptyChoices = choiceEmpty(tipoParto);
        return !emptyText && !missingChecks && !emptyChoices;
    }

    private void setupConditionalVisibility() {
        toggle(false, questConvenio, convenio);
        toggle(false, questDoencaContagiosa, doencaContagiosa);
        toggle(false, questMedicacao, medicacoes);
        toggle(false, questDificuldade, dificuldades);
        toggle(false, questApioPedagogico, apoioPedagogico);
        toggle(false, questCausaPrematuridade1, prematuridade1);
        toggle(false, lblSustentouCabecaMeses, sustentouCabeca);
        toggle(false, lblEngatinhouMeses, engatinhou);
        toggle(false, lblSentouMeses, sentou);
        toggle(false, lblAndouMeses, andou);
        toggle(false, lblTerapiaMotivo, terapia);
        toggle(false, lblFalouMeses, falou);

        wire(convenioSim, convenioNao, questConvenio, convenio);
        wire(doencaContagiosaSim, doencaContagiosaNao, questDoencaContagiosa, doencaContagiosa);
        wire(medicacaoSim, medicacaoNao, questMedicacao, medicacoes);
        wire(dificuldadesSim, dificuldadesNao, questDificuldade, dificuldades);
        wire(apoioPedagogicoSim, apoioPedagogicoNao, questApioPedagogico, apoioPedagogico);
        wire(prematuridadeSim, prematuridadeNao, questCausaPrematuridade1, prematuridade1);
        wire(sustentouCabecaSim, sustentouCabecaNao, lblSustentouCabecaMeses, sustentouCabeca);
        wire(engatinhouSim, engatinhouNao, lblEngatinhouMeses, engatinhou);
        wire(sentouSim, sentouNao, lblSentouMeses, sentou);
        wire(andouSim, andouNao, lblAndouMeses, andou);
        wire(terapiaSim, terapiaNao, lblTerapiaMotivo, terapia);
        wire(falouSim, falouNao, lblFalouMeses, falou);
    }

    private void wire(CheckBox sim, CheckBox nao, Node... dependents) {
        if (sim == null || nao == null) return;
        sim.selectedProperty().addListener((obs, was, is) -> toggle(is, dependents));
        nao.selectedProperty().addListener((obs, was, is) -> {
            if (is) toggle(false, dependents);
        });
    }

    private void toggle(boolean show, Node... nodes) {
        if (nodes == null) return;
        for (Node n : nodes) {
            if (n == null) continue;
            n.setVisible(show);
            n.setManaged(show);
        }
    }

    private void captureCurrentStepData() {
        if (convulsaoSim != null) formData.convulsao = convulsaoSim.isSelected();
        if (vacinacaoSim != null) formData.vacinacaoEmDia = vacinacaoSim.isSelected();
        if (convenioSim != null) formData.possuiConvenio = convenioSim.isSelected();
        if (convenio != null && convenio.isVisible()) formData.convenio = safeText(convenio);
        if (doencaContagiosaSim != null) formData.teveDoencaContagiosa = doencaContagiosaSim.isSelected();
        if (doencaContagiosa != null && doencaContagiosa.isVisible()) formData.doencaContagiosa = safeText(doencaContagiosa);
        if (medicacaoSim != null) formData.fazUsoMedicacoes = medicacaoSim.isSelected();
        if (medicacoes != null && medicacoes.isVisible()) formData.medicacoes = safeText(medicacoes);

        if (servicosFrequentados1 != null) formData.servicosFrequentados = safeText(servicosFrequentados1);
        if (inicioEscolarizacao != null) formData.inicioEscolarizacao = safeText(inicioEscolarizacao);
        if (dificuldadesSim != null) formData.apresentaDificuldades = dificuldadesSim.isSelected();
        if (dificuldades != null && dificuldades.isVisible()) formData.dificuldades = safeText(dificuldades);
        if (apoioPedagogicoSim != null) formData.apoioPedagogicoEmCasa = apoioPedagogicoSim.isSelected();
        if (apoioPedagogico != null && apoioPedagogico.isVisible()) formData.apoioPedagogico = safeText(apoioPedagogico);

        if (cidadeNascimento != null) formData.cidadeNascimento = safeText(cidadeNascimento);
        if (maternidade != null) formData.maternidade = safeText(maternidade);
        if (tipoParto != null && tipoParto.getValue() != null) formData.tipoParto = tipoParto.getValue();

        if (chorouSim != null) formData.chorouAoNascer = chorouSim.isSelected();
        if (ficouRoxoSim != null) formData.ficouRoxo = ficouRoxoSim.isSelected();
        if (incubadoraSim != null) formData.usoIncubadora = incubadoraSim.isSelected();
        if (amamentadoSim != null) formData.foiAmamentado = amamentadoSim.isSelected();

        if (sustentouCabecaSim != null) formData.sustentouCabeca = sustentouCabecaSim.isSelected();
        if (sustentouCabeca != null && sustentouCabeca.isVisible()) formData.sustentouCabecaMeses = safeText(sustentouCabeca);
        if (engatinhouSim != null) formData.engatinhou = engatinhouSim.isSelected();
        if (engatinhou != null && engatinhou.isVisible()) formData.engatinhouMeses = safeText(engatinhou);
        if (sentouSim != null) formData.sentou = sentouSim.isSelected();
        if (sentou != null && sentou.isVisible()) formData.sentouMeses = safeText(sentou);
        if (andouSim != null) formData.andou = andouSim.isSelected();
        if (andou != null && andou.isVisible()) formData.andouMeses = safeText(andou);
        if (terapiaSim != null) formData.precisouTerapia = terapiaSim.isSelected();
        if (terapia != null && terapia.isVisible()) formData.terapiaMotivo = safeText(terapia);
        if (falouSim != null) formData.falou = falouSim.isSelected();
        if (falou != null && falou.isVisible()) formData.falouMeses = safeText(falou);
    }

    private String safeText(TextField tf) {
        return tf.getText() != null ? tf.getText().trim() : null;
    }

    private void populateFromFormData() {
        if (formData == null) return;
        if (convenio != null) convenio.setText(val(formData.convenio));
        if (doencaContagiosa != null) doencaContagiosa.setText(val(formData.doencaContagiosa));
        if (medicacoes != null) medicacoes.setText(val(formData.medicacoes));
        if (servicosFrequentados1 != null) servicosFrequentados1.setText(val(formData.servicosFrequentados));
        if (inicioEscolarizacao != null) inicioEscolarizacao.setText(val(formData.inicioEscolarizacao));
        if (dificuldades != null) dificuldades.setText(val(formData.dificuldades));
        if (apoioPedagogico != null) apoioPedagogico.setText(val(formData.apoioPedagogico));
        if (cidadeNascimento != null) cidadeNascimento.setText(val(formData.cidadeNascimento));
        if (maternidade != null) maternidade.setText(val(formData.maternidade));
        if (tipoParto != null && formData.tipoParto != null) tipoParto.setValue(formData.tipoParto);
        if (sustentouCabeca != null) sustentouCabeca.setText(val(formData.sustentouCabecaMeses));
        if (engatinhou != null) engatinhou.setText(val(formData.engatinhouMeses));
        if (sentou != null) sentou.setText(val(formData.sentouMeses));
        if (andou != null) andou.setText(val(formData.andouMeses));
        if (terapia != null) terapia.setText(val(formData.terapiaMotivo));
        if (falou != null) falou.setText(val(formData.falouMeses));
    }

    private String val(String s) { return s == null ? "" : s; }
}
