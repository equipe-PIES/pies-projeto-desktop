package com.pies.projeto.integrado.piesfront.controllers;

import com.pies.projeto.integrado.piesfront.dto.EducandoDTO;
import com.pies.projeto.integrado.piesfront.dto.AnamneseDTO;
import com.pies.projeto.integrado.piesfront.dto.AnamneseRequestDTO;
import com.pies.projeto.integrado.piesfront.services.AtendimentoFlowService;
import com.pies.projeto.integrado.piesfront.services.AnamneseService;
import com.pies.projeto.integrado.piesfront.services.AuthService;
import com.utils.Janelas;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.StageStyle;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Label;
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
    private final AuthService authService = AuthService.getInstance();

    public void setEducando(EducandoDTO educando) {
        this.educando = educando;
        carregarAnamneseExistente();
        populateFromFormData();
    }

    public void setFormData(AnamneseRequestDTO data) {
        if (data != null) {
            this.formData = data;
            populateFromFormData();
        }
    }

    @FXML
    private void handleTurmasButtonAction(javafx.event.ActionEvent event) {
        Janelas.carregarTela(event, "/com/pies/projeto/integrado/piesfront/screens/tela-inicio-professor.fxml", "Início - Professor(a)");
    }

    @FXML
    private void handleSairButtonAction() {
        try {
            authService.logout();
            if (anamnese != null) {
                javafx.event.ActionEvent fakeEvent = new javafx.event.ActionEvent(anamnese, null);
                Janelas.carregarTela(fakeEvent,
                        "/com/pies/projeto/integrado/piesfront/screens/tela-de-login.fxml",
                        "Amparo Edu - Login");
            }
        } catch (Exception e) {
            if (anamnese != null && anamnese.getScene() != null) {
                Stage stage = (Stage) anamnese.getScene().getWindow();
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource(
                            "/com/pies/projeto/integrado/piesfront/screens/tela-de-login.fxml"));
                    Parent root = loader.load();
                    stage.setScene(new Scene(root));
                    stage.setTitle("Amparo Edu - Login");
                    stage.setMaximized(true);
                    stage.show();
                } catch (Exception ex) {
                    stage.close();
                }
            }
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
        captureCurrentStepData();
        abrir("/com/pies/projeto/integrado/piesfront/screens/anamnese-1.fxml", "Anamnese");
        if (validationMsg != null) {
            validationMsg.setVisible(false);
        }
    }

    @FXML
    private void handleGoToAnamnese2() {
        captureCurrentStepData();
        if (validateAnamnese1()) {
            abrir("/com/pies/projeto/integrado/piesfront/screens/anamnese-2.fxml", "Anamnese");
        } else {
            showValidation();
        }
    }

    @FXML
    private void handleGoToAnamnese3() {
        captureCurrentStepData();
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
            AnamneseDTO dto = toAnamneseDTO();
            var created = authService.criarAnamnese(educando.id(), dto);
            if (created != null) {
                AtendimentoFlowService.getInstance().concluirAnamnese(educando.id());
                showPopup("Anamnese registrada com sucesso!", true);
                handleCancelAction();
            } else {
                System.err.println("Falha ao enviar anamnese");
                showPopup("Falha ao enviar anamnese.", false);
            }
        }
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
                stage.setMaximized(false);
                stage.setMaximized(true);
            } else {
                stage = new Stage();
                stage.setTitle(titulo);
                stage.setScene(new Scene(root));
                stage.setMaximized(true);
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
    private TextField duracaoGestacao1;
    @FXML
    private CheckBox preNatalSim, preNatalNao;
    @FXML
    private TextField servicos;
    @FXML
    private ChoiceBox<String> balbucio;
    @FXML
    private TextField primeiraPalavra;
    @FXML
    private TextField primeiraFrase;
    @FXML
    private ChoiceBox<String> tipoFala;
    @FXML
    private TextField disturbio;
    @FXML
    private ChoiceBox<String> dormeSozinho;
    @FXML
    private ChoiceBox<String> temQuarto;
    @FXML
    private ChoiceBox<String> sono;
    @FXML
    private ChoiceBox<String> respeitaRegras;
    @FXML
    private ChoiceBox<String> desmotivado;
    @FXML
    private ChoiceBox<String> agressivo;
    @FXML
    private ChoiceBox<String> inquietacao;
    @FXML
    private CheckBox disturbioSim, disturbioNao;
    @FXML
    private Label questDisturbio;

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
        if (balbucio != null && (balbucio.getItems() == null || balbucio.getItems().isEmpty())) {
            balbucio.setItems(FXCollections.observableArrayList(
                    "1-3 meses", "4-6 meses", "7-9 meses", "10-12 meses"));
        }
        if (tipoFala != null && (tipoFala.getItems() == null || tipoFala.getItems().isEmpty())) {
            tipoFala.setItems(FXCollections.observableArrayList("Natural", "Inibido"));
        }
        var simNao = FXCollections.observableArrayList("Sim", "Não");
        if (dormeSozinho != null && (dormeSozinho.getItems() == null || dormeSozinho.getItems().isEmpty())) {
            dormeSozinho.setItems(simNao);
        }
        if (temQuarto != null && (temQuarto.getItems() == null || temQuarto.getItems().isEmpty())) {
            temQuarto.setItems(simNao);
        }
        if (respeitaRegras != null && (respeitaRegras.getItems() == null || respeitaRegras.getItems().isEmpty())) {
            respeitaRegras.setItems(simNao);
        }
        if (desmotivado != null && (desmotivado.getItems() == null || desmotivado.getItems().isEmpty())) {
            desmotivado.setItems(simNao);
        }
        if (agressivo != null && (agressivo.getItems() == null || agressivo.getItems().isEmpty())) {
            agressivo.setItems(simNao);
        }
        if (inquietacao != null && (inquietacao.getItems() == null || inquietacao.getItems().isEmpty())) {
            inquietacao.setItems(simNao);
        }
        if (sono != null && (sono.getItems() == null || sono.getItems().isEmpty())) {
            sono.setItems(FXCollections.observableArrayList("Calmo", "Agitado"));
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
                preNatalSim, preNatalNao,
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
        toggle(false, questDisturbio, disturbio);

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
        wire(disturbioSim, disturbioNao, questDisturbio, disturbio);
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
        if (servicos != null) {
            String s = safeText(servicos);
            if (s != null && !s.isEmpty()) formData.servicosFrequentados = s;
        }
        if (inicioEscolarizacao != null) formData.inicioEscolarizacao = safeText(inicioEscolarizacao);
        if (dificuldadesSim != null) formData.apresentaDificuldades = dificuldadesSim.isSelected();
        if (dificuldades != null && dificuldades.isVisible()) formData.dificuldades = safeText(dificuldades);
        if (apoioPedagogicoSim != null) formData.apoioPedagogicoEmCasa = apoioPedagogicoSim.isSelected();
        if (apoioPedagogico != null && apoioPedagogico.isVisible()) formData.apoioPedagogico = safeText(apoioPedagogico);
        if (duracaoGestacao1 != null) formData.duracaoGestacao = safeText(duracaoGestacao1);
        if (preNatalSim != null) formData.fezPreNatal = preNatalSim.isSelected();
        if (prematuridadeSim != null) {
            if (prematuridadeSim.isSelected() && prematuridade1 != null && prematuridade1.isVisible()) {
                formData.prematuridade = safeText(prematuridade1);
            } else {
                formData.prematuridade = null;
            }
        }

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

        if (balbucio != null && balbucio.getValue() != null) formData.primeiroBalbucioMeses = balbucio.getValue();
        if (primeiraPalavra != null) formData.primeiraPalavraQuando = safeText(primeiraPalavra);
        if (primeiraFrase != null) formData.primeiraFraseQuando = safeText(primeiraFrase);
        if (tipoFala != null && tipoFala.getValue() != null) formData.falaNaturalOuInibido = tipoFala.getValue();
        if (disturbioSim != null && disturbioSim.isSelected() && disturbio != null && disturbio.isVisible()) {
            formData.disturbioFala = safeText(disturbio);
        } else {
            formData.disturbioFala = null;
        }

        if (dormeSozinho != null && dormeSozinho.getValue() != null) formData.dormeSozinho = dormeSozinho.getValue();
        if (temQuarto != null && temQuarto.getValue() != null) formData.temQuartoProprio = temQuarto.getValue();
        if (sono != null && sono.getValue() != null) formData.sonoCalmoOuAgitado = sono.getValue();

        if (respeitaRegras != null && respeitaRegras.getValue() != null) formData.respeitaRegras = respeitaRegras.getValue();
        if (desmotivado != null && desmotivado.getValue() != null) formData.desmotivado = desmotivado.getValue();
        if (agressivo != null && agressivo.getValue() != null) formData.agressivo = agressivo.getValue();
        if (inquietacao != null && inquietacao.getValue() != null) formData.apresentaInquietacao = inquietacao.getValue();
    }

    private String convertSimNao(String s) {
        if (s == null) return null;
        return "Não".equalsIgnoreCase(s) ? "NAO" : ("Sim".equalsIgnoreCase(s) ? "SIM" : s);
    }

    private AnamneseDTO toAnamneseDTO() {
        String temConvulsao = formData.convulsao ? "SIM" : "NAO";
        String convenioMedico = formData.possuiConvenio ? val(formData.convenio) : null;
        String vacinacaoEmDia = formData.vacinacaoEmDia ? "SIM" : "NAO";
        String doencaContagiosa = formData.teveDoencaContagiosa ? val(formData.doencaContagiosa) : null;
        String usoMedicacoes = formData.fazUsoMedicacoes ? val(formData.medicacoes) : null;
        String servicosSaudeOuEducacao = val(formData.servicosFrequentados);
        String inicioEscolarizacao = val(formData.inicioEscolarizacao);
        String dificuldadesEscolares = formData.apresentaDificuldades ? val(formData.dificuldades) : null;
        String apoioPedagogicoEmCasa = formData.apoioPedagogicoEmCasa ? val(formData.apoioPedagogico) : null;
        String duracaoGestacao = val(formData.duracaoGestacao);
        String fezPreNatal = formData.fezPreNatal ? "SIM" : "NAO";
        String prematuridade = val(formData.prematuridade);

        String cidadeNascimento = val(formData.cidadeNascimento);
        String maternidadeNascimento = val(formData.maternidade);
        String tipoParto = formData.tipoParto;

        String chorouAoNascer = formData.chorouAoNascer ? "SIM" : "NAO";
        String ficouRoxo = formData.ficouRoxo ? "SIM" : "NAO";
        String usoIncubadora = formData.usoIncubadora ? "SIM" : "NAO";
        String foiAmamentado = formData.foiAmamentado ? "SIM" : "NAO";

        String sustentouCabecaMeses = formData.sustentouCabeca ? val(formData.sustentouCabecaMeses) : null;
        String engatinhouMeses = formData.engatinhou ? val(formData.engatinhouMeses) : null;
        String sentouMeses = formData.sentou ? val(formData.sentouMeses) : null;
        String andouMeses = formData.andou ? val(formData.andouMeses) : null;
        String precisouTerapiaMotivo = formData.precisouTerapia ? val(formData.terapiaMotivo) : null;
        String falouMeses = formData.falou ? val(formData.falouMeses) : null;

        String primeiroBalbucioMeses = val(formData.primeiroBalbucioMeses);
        String primeiraPalavraQuando = val(formData.primeiraPalavraQuando);
        String primeiraFraseQuando = val(formData.primeiraFraseQuando);
        String falaNaturalOuInibido = val(formData.falaNaturalOuInibido);
        String disturbioFala = val(formData.disturbioFala);

        String dormeSozinho = convertSimNao(formData.dormeSozinho);
        String temQuartoProprio = convertSimNao(formData.temQuartoProprio);
        String sonoCalmoOuAgitado = val(formData.sonoCalmoOuAgitado);
        String respeitaRegras = convertSimNao(formData.respeitaRegras);
        String desmotivado = convertSimNao(formData.desmotivado);
        String agressivo = convertSimNao(formData.agressivo);
        String apresentaInquietacao = convertSimNao(formData.apresentaInquietacao);

        return new AnamneseDTO(
                null,
                temConvulsao,
                convenioMedico,
                vacinacaoEmDia,
                doencaContagiosa,
                usoMedicacoes,
                servicosSaudeOuEducacao,
                inicioEscolarizacao,
                dificuldadesEscolares,
                apoioPedagogicoEmCasa,
                duracaoGestacao,
                fezPreNatal,
                prematuridade,
                cidadeNascimento,
                maternidadeNascimento,
                tipoParto,
                chorouAoNascer,
                ficouRoxo,
                usoIncubadora,
                foiAmamentado,
                sustentouCabecaMeses,
                engatinhouMeses,
                sentouMeses,
                andouMeses,
                precisouTerapiaMotivo,
                falouMeses,
                primeiroBalbucioMeses,
                primeiraPalavraQuando,
                primeiraFraseQuando,
                falaNaturalOuInibido,
                disturbioFala,
                dormeSozinho,
                temQuartoProprio,
                sonoCalmoOuAgitado,
                respeitaRegras,
                desmotivado,
                agressivo,
                apresentaInquietacao
        );
    }

    private String safeText(TextField tf) {
        return tf.getText() != null ? tf.getText().trim() : null;
    }

    private void populateFromFormData() {
        if (formData == null) return;
        if (convulsaoSim != null && convulsaoNao != null) { convulsaoSim.setSelected(formData.convulsao); convulsaoNao.setSelected(!formData.convulsao); }
        if (vacinacaoSim != null && vacinacaoNao != null) { vacinacaoSim.setSelected(formData.vacinacaoEmDia); vacinacaoNao.setSelected(!formData.vacinacaoEmDia); }
        if (convenioSim != null && convenioNao != null) { convenioSim.setSelected(formData.possuiConvenio); convenioNao.setSelected(!formData.possuiConvenio); }
        if (doencaContagiosaSim != null && doencaContagiosaNao != null) { doencaContagiosaSim.setSelected(formData.teveDoencaContagiosa); doencaContagiosaNao.setSelected(!formData.teveDoencaContagiosa); }
        if (medicacaoSim != null && medicacaoNao != null) { medicacaoSim.setSelected(formData.fazUsoMedicacoes); medicacaoNao.setSelected(!formData.fazUsoMedicacoes); }
        if (dificuldadesSim != null && dificuldadesNao != null) { dificuldadesSim.setSelected(formData.apresentaDificuldades); dificuldadesNao.setSelected(!formData.apresentaDificuldades); }
        if (apoioPedagogicoSim != null && apoioPedagogicoNao != null) { apoioPedagogicoSim.setSelected(formData.apoioPedagogicoEmCasa); apoioPedagogicoNao.setSelected(!formData.apoioPedagogicoEmCasa); }
        if (preNatalSim != null && preNatalNao != null) { preNatalSim.setSelected(formData.fezPreNatal); preNatalNao.setSelected(!formData.fezPreNatal); }
        if (prematuridadeSim != null && prematuridadeNao != null) { boolean b = formData.prematuridade != null && !formData.prematuridade.isEmpty(); prematuridadeSim.setSelected(b); prematuridadeNao.setSelected(!b); }

        if (chorouSim != null && chorouNao != null) { chorouSim.setSelected(formData.chorouAoNascer); chorouNao.setSelected(!formData.chorouAoNascer); }
        if (ficouRoxoSim != null && ficouRoxoNao != null) { ficouRoxoSim.setSelected(formData.ficouRoxo); ficouRoxoNao.setSelected(!formData.ficouRoxo); }
        if (incubadoraSim != null && incubadoraNao != null) { incubadoraSim.setSelected(formData.usoIncubadora); incubadoraNao.setSelected(!formData.usoIncubadora); }
        if (amamentadoSim != null && amamentadoNao != null) { amamentadoSim.setSelected(formData.foiAmamentado); amamentadoNao.setSelected(!formData.foiAmamentado); }

        if (sustentouCabecaSim != null && sustentouCabecaNao != null) { sustentouCabecaSim.setSelected(formData.sustentouCabeca); sustentouCabecaNao.setSelected(!formData.sustentouCabeca); }
        if (engatinhouSim != null && engatinhouNao != null) { engatinhouSim.setSelected(formData.engatinhou); engatinhouNao.setSelected(!formData.engatinhou); }
        if (sentouSim != null && sentouNao != null) { sentouSim.setSelected(formData.sentou); sentouNao.setSelected(!formData.sentou); }
        if (andouSim != null && andouNao != null) { andouSim.setSelected(formData.andou); andouNao.setSelected(!formData.andou); }
        if (terapiaSim != null && terapiaNao != null) { terapiaSim.setSelected(formData.precisouTerapia); terapiaNao.setSelected(!formData.precisouTerapia); }
        if (falouSim != null && falouNao != null) { falouSim.setSelected(formData.falou); falouNao.setSelected(!formData.falou); }

        if (convenio != null) convenio.setText(val(formData.convenio));
        if (doencaContagiosa != null) doencaContagiosa.setText(val(formData.doencaContagiosa));
        if (medicacoes != null) medicacoes.setText(val(formData.medicacoes));
        if (servicosFrequentados1 != null) servicosFrequentados1.setText(val(formData.servicosFrequentados));
        if (inicioEscolarizacao != null) inicioEscolarizacao.setText(val(formData.inicioEscolarizacao));
        if (dificuldades != null) dificuldades.setText(val(formData.dificuldades));
        if (apoioPedagogico != null) apoioPedagogico.setText(val(formData.apoioPedagogico));
        if (duracaoGestacao1 != null) duracaoGestacao1.setText(val(formData.duracaoGestacao));
        if (prematuridade1 != null) prematuridade1.setText(val(formData.prematuridade));
        if (cidadeNascimento != null) cidadeNascimento.setText(val(formData.cidadeNascimento));
        if (maternidade != null) maternidade.setText(val(formData.maternidade));
        if (tipoParto != null && formData.tipoParto != null) tipoParto.setValue(formData.tipoParto);
        if (sustentouCabeca != null) sustentouCabeca.setText(val(formData.sustentouCabecaMeses));
        if (engatinhou != null) engatinhou.setText(val(formData.engatinhouMeses));
        if (sentou != null) sentou.setText(val(formData.sentouMeses));
        if (andou != null) andou.setText(val(formData.andouMeses));
        if (terapia != null) terapia.setText(val(formData.terapiaMotivo));
        if (falou != null) falou.setText(val(formData.falouMeses));

        if (balbucio != null && formData.primeiroBalbucioMeses != null) balbucio.setValue(formData.primeiroBalbucioMeses);
        if (primeiraPalavra != null) primeiraPalavra.setText(val(formData.primeiraPalavraQuando));
        if (primeiraFrase != null) primeiraFrase.setText(val(formData.primeiraFraseQuando));
        if (tipoFala != null && formData.falaNaturalOuInibido != null) tipoFala.setValue(formData.falaNaturalOuInibido);
        if (disturbio != null) disturbio.setText(val(formData.disturbioFala));
        if (disturbioSim != null && disturbioNao != null) {
            String d = val(formData.disturbioFala);
            boolean has = d != null && !d.isEmpty();
            disturbioSim.setSelected(has);
            disturbioNao.setSelected(!has);
        }

        if (dormeSozinho != null && formData.dormeSozinho != null) dormeSozinho.setValue(toDisplaySimNao(formData.dormeSozinho));
        if (temQuarto != null && formData.temQuartoProprio != null) temQuarto.setValue(toDisplaySimNao(formData.temQuartoProprio));
        if (sono != null && formData.sonoCalmoOuAgitado != null) sono.setValue(formData.sonoCalmoOuAgitado);
        if (respeitaRegras != null && formData.respeitaRegras != null) respeitaRegras.setValue(toDisplaySimNao(formData.respeitaRegras));
        if (desmotivado != null && formData.desmotivado != null) desmotivado.setValue(toDisplaySimNao(formData.desmotivado));
        if (agressivo != null && formData.agressivo != null) agressivo.setValue(toDisplaySimNao(formData.agressivo));
        if (inquietacao != null && formData.apresentaInquietacao != null) inquietacao.setValue(toDisplaySimNao(formData.apresentaInquietacao));
    }

    private String toDisplaySimNao(String s) {
        if (s == null) return null;
        if ("SIM".equalsIgnoreCase(s)) return "Sim";
        if ("NAO".equalsIgnoreCase(s) || "NÃO".equalsIgnoreCase(s)) return "Não";
        return s;
    }

    @FXML
    private void handleBackToAnamnese2() {
        captureCurrentStepData();
        abrir("/com/pies/projeto/integrado/piesfront/screens/anamnese-2.fxml", "Anamnese");
    }

    private String val(String s) { return s == null ? "" : s; }

    private boolean parseBool(String s) {
        if (s == null) return false;
        String t = s.trim();
        return t.equalsIgnoreCase("true") || t.equalsIgnoreCase("sim") || t.equalsIgnoreCase("s") || t.equals("1");
    }

    private void carregarAnamneseExistente() {
        if (educando == null || educando.id() == null) return;
        AnamneseDTO dto = authService.getAnamnesePorEducando(educando.id());
        if (dto == null) return;
        formData.convulsao = parseBool(dto.temConvulsao());
        formData.possuiConvenio = dto.convenioMedico() != null && !dto.convenioMedico().isEmpty();
        formData.convenio = dto.convenioMedico();
        formData.vacinacaoEmDia = parseBool(dto.vacinacaoEmDia());
        formData.teveDoencaContagiosa = dto.doencaContagiosa() != null && !dto.doencaContagiosa().isEmpty();
        formData.doencaContagiosa = dto.doencaContagiosa();
        formData.fazUsoMedicacoes = dto.usoMedicacoes() != null && !dto.usoMedicacoes().isEmpty();
        formData.medicacoes = dto.usoMedicacoes();
        formData.servicosFrequentados = dto.servicosSaudeOuEducacao();
        formData.inicioEscolarizacao = dto.inicioEscolarizacao();
        formData.apresentaDificuldades = dto.dificuldadesEscolares() != null && !dto.dificuldadesEscolares().isEmpty();
        formData.dificuldades = dto.dificuldadesEscolares();
        formData.apoioPedagogicoEmCasa = parseBool(dto.apoioPedagogicoEmCasa());
        formData.apoioPedagogico = null;
        formData.duracaoGestacao = dto.duracaoGestacao();
        formData.fezPreNatal = parseBool(dto.fezPreNatal());
        formData.prematuridade = dto.prematuridade();
        formData.cidadeNascimento = dto.cidadeNascimento();
        formData.maternidade = dto.maternidadeNascimento();
        formData.tipoParto = dto.tipoParto();
        formData.chorouAoNascer = parseBool(dto.chorouAoNascer());
        formData.ficouRoxo = parseBool(dto.ficouRoxo());
        formData.usoIncubadora = parseBool(dto.usoIncubadora());
        formData.foiAmamentado = parseBool(dto.foiAmamentado());
        formData.sustentouCabeca = dto.sustentouCabecaMeses() != null && !dto.sustentouCabecaMeses().isEmpty();
        formData.sustentouCabecaMeses = dto.sustentouCabecaMeses();
        formData.engatinhou = dto.engatinhouMeses() != null && !dto.engatinhouMeses().isEmpty();
        formData.engatinhouMeses = dto.engatinhouMeses();
        formData.sentou = dto.sentouMeses() != null && !dto.sentouMeses().isEmpty();
        formData.sentouMeses = dto.sentouMeses();
        formData.andou = dto.andouMeses() != null && !dto.andouMeses().isEmpty();
        formData.andouMeses = dto.andouMeses();
        formData.precisouTerapia = dto.precisouTerapiaMotivo() != null && !dto.precisouTerapiaMotivo().isEmpty();
        formData.terapiaMotivo = dto.precisouTerapiaMotivo();
        formData.falou = dto.falouMeses() != null && !dto.falouMeses().isEmpty();
        formData.falouMeses = dto.falouMeses();
        formData.primeiroBalbucioMeses = dto.primeiroBalbucioMeses();
        formData.primeiraPalavraQuando = dto.primeiraPalavraQuando();
        formData.primeiraFraseQuando = dto.primeiraFraseQuando();
        formData.falaNaturalOuInibido = dto.falaNaturalOuInibido();
        formData.disturbioFala = dto.disturbioFala();
        formData.dormeSozinho = dto.dormeSozinho();
        formData.temQuartoProprio = dto.temQuartoProprio();
        formData.sonoCalmoOuAgitado = dto.sonoCalmoOuAgitado();
        formData.respeitaRegras = dto.respeitaRegras();
        formData.desmotivado = dto.desmotivado();
        formData.agressivo = dto.agressivo();
        formData.apresentaInquietacao = dto.apresentaInquietacao();
    }
}
