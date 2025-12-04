package com.pies.projeto.integrado.piesfront.controllers;
import com.pies.projeto.integrado.piesfront.dto.EducandoDTO;
import com.pies.projeto.integrado.piesfront.services.AuthService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.StageStyle;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Label;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import com.utils.Janelas;

import java.net.URL;
import java.util.ResourceBundle;

public class PAEEController implements Initializable {
    @FXML
    private BorderPane anamnese;
    @FXML
    private Label indicadorDeTela;
    @FXML
    private Label validationMsg;
    @FXML
    private TextArea objetivosPlano;
    @FXML
    private TextArea resumoCaso;
    @FXML
    private ChoiceBox<String> dificuldadesMotoresPsicomotoresCb;
    @FXML
    private ChoiceBox<String> dificuldadesCognitivoCb;
    @FXML
    private ChoiceBox<String> dificuldadesSensorialCb;
    @FXML
    private ChoiceBox<String> dificuldadesLinguagemComunicacaoCb;
    @FXML
    private ChoiceBox<String> dificuldadesFamiliarCb;
    @FXML
    private ChoiceBox<String> dificuldadesAfetivoInterpessoaisCb;
    @FXML
    private ChoiceBox<String> dificuldadesRaciocinioLogicoMatematicoCb;
    @FXML
    private ChoiceBox<String> dificuldadesAVAsCb;
    @FXML
    private TextArea desenvolvimentoMotoresPsicomotoresDificuldadesTa;
    @FXML
    private TextArea desenvolvimentoMotoresPsicomotoresIntervencoesTa;
    @FXML
    private TextArea comunicacaoLinguagemDificuldadesTa;
    @FXML
    private TextArea comunicacaoLinguagemIntervencoesTa;
    @FXML
    private TextArea dificuldadesRaciocinioTa;
    @FXML
    private TextArea intervencoesRaciocinioTa;
    @FXML
    private TextArea dificuldadesAtencaoTa;
    @FXML
    private TextArea intervencoesAtencaoTa;
    @FXML
    private TextArea dificuldadesMemoriaTa;
    @FXML
    private TextArea intervencoesMemoriaTa;
    @FXML
    private TextArea dificuldadesPercepcaoTa;
    @FXML
    private TextArea intervencoesPercepcaoTa;
    @FXML
    private TextArea dificuldadesSociabilidadeTa;
    @FXML
    private TextArea intervencoesSociabilidadeTa;
    @FXML
    private TextArea dificuldadesAVATa;
    @FXML
    private TextArea intervencoesAVATa;
    @FXML
    private ChoiceBox<String> cbAEE;
    @FXML
    private ChoiceBox<String> cbPsicologo;
    @FXML
    private ChoiceBox<String> cbFisioterapeuta;
    @FXML
    private ChoiceBox<String> cbPsicopedagogo;
    @FXML
    private ChoiceBox<String> cbTO;
    @FXML
    private ChoiceBox<String> cbEducacaoFisica;
    @FXML
    private ChoiceBox<String> cbEstimulacaoPrecoce;

    private int currentStep = 1;
    private EducandoDTO educando;
    private final AuthService authService = AuthService.getInstance();
    private PaeeFormData formData = new PaeeFormData();
    // private boolean modoNovo = false;
    private boolean novoRegistro = false;

    public void setEducando(EducandoDTO educando) {
        System.out.println("=== PAEEController.setEducando ===");
        System.out.println("Educando: " + (educando != null ? educando.nome() : "null"));
        System.out.println("novoRegistro: " + novoRegistro);
        System.out.println("currentStep: " + currentStep);
        this.educando = educando;
        atualizarIndicadorDeTela();
        if (!novoRegistro) {
            System.out.println("Carregando PAEE existente do backend...");
            carregarPaeeExistente();
            System.out.println("PAEE carregado. resumoCaso: " + (formData.resumoCaso != null ? formData.resumoCaso.substring(0, Math.min(50, formData.resumoCaso.length())) + "..." : "null"));
        } else {
            // Apenas cria novo formData se ainda não tiver dados (primeiro acesso)
            if (this.formData == null || isFormDataEmpty(this.formData)) {
                System.out.println("Criando novo formData vazio");
                this.formData = new PaeeFormData();
            } else {
                System.out.println("Mantendo formData existente (já tem dados)");
            }
        }
        preencherCamposComFormData();
        System.out.println("=== Fim setEducando ===");
    }

    /**
     * Define que o controller está em modo de novo cadastro.
     * Neste modo, não carrega dados existentes.
     */
    // public void setModoNovo() {
    //     this.modoNovo = true;
    //     this.formData = new PaeeFormData(); // Limpa os dados
    // }

    public void setFormData(PaeeFormData data) {
        System.out.println("=== setFormData ===");
        System.out.println("Data null? " + (data == null));
        if (data != null) {
            System.out.println("resumoCaso: " + (data.resumoCaso != null ? "presente" : "null"));
            System.out.println("desenvolvimentoMotoresPsicomotoresDificuldades: " + (data.desenvolvimentoMotoresPsicomotoresDificuldades != null ? "presente" : "null"));
            System.out.println("dificuldadesRaciocinio: " + (data.dificuldadesRaciocinio != null ? "presente" : "null"));
            this.formData = data;
            preencherCamposComFormData();
        }
    }

    public void setNovoRegistro(boolean novo) {
        this.novoRegistro = novo;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (validationMsg != null) {
            validationMsg.setVisible(false);
            validationMsg.setManaged(true);
        }
        inicializarChoiceBoxesSimNao();
        detectarEtapa(url);
        atualizarIndicadorDeTela();
        preencherCamposComFormData();
    }

    @FXML
    private void handleTurmasButtonAction() {
        if (anamnese != null) {
            Janelas.carregarTela(new javafx.event.ActionEvent(anamnese, null),
                    "/com/pies/projeto/integrado/piesfront/screens/tela-inicio-professor.fxml",
                    "Início - Professor(a)");
        }
    }

    @FXML
    private void handleSairButtonAction() {
        authService.logout();
        if (anamnese != null) {
            Janelas.carregarTela(new javafx.event.ActionEvent(anamnese, null),
                    "/com/pies/projeto/integrado/piesfront/screens/tela-de-login.fxml",
                    "Amparo Edu - Login");
        }
    }

    @FXML
    private void handleCancelAction() {
        if (anamnese != null) {
            Janelas.carregarTela(new javafx.event.ActionEvent(anamnese, null),
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
        captureCurrentStepData();
        if (currentStep <= 1) {
            handleCancelAction();
            return;
        }
        if (currentStep == 2) abrirPaee("/com/pies/projeto/integrado/piesfront/screens/paee-1.fxml", 1);
        else if (currentStep == 3) abrirPaee("/com/pies/projeto/integrado/piesfront/screens/paee-2.fxml", 2);
        else if (currentStep == 4) abrirPaee("/com/pies/projeto/integrado/piesfront/screens/paee-3.fxml", 3);
        else if (currentStep == 5) abrirPaee("/com/pies/projeto/integrado/piesfront/screens/paee-4.fxml", 4);
        else if (currentStep == 6) abrirPaee("/com/pies/projeto/integrado/piesfront/screens/paee-5.fxml", 5);
    }

    @FXML
    private void handleGoToPaee2() {
        captureCurrentStepData();
        if (!validateResumo()) {
            showValidation("Informe o resumo do caso para prosseguir.");
            return;
        }
        abrirPaee("/com/pies/projeto/integrado/piesfront/screens/paee-2.fxml", 2);
    }

    @FXML
    private void handleGoToPaee3() {
        captureCurrentStepData();
        if (validateStep2()) {
            abrirPaee("/com/pies/projeto/integrado/piesfront/screens/paee-3.fxml", 3);
        } else {
            showValidation("Algum campo está em branco. Preencha para prosseguir.");
        }
    }

    @FXML
    private void handleGoToPaee4() {
        captureCurrentStepData();
        if (validateStep3()) {
            abrirPaee("/com/pies/projeto/integrado/piesfront/screens/paee-4.fxml", 4);
        } else {
            showValidation("Algum campo está em branco. Preencha para prosseguir.");
        }
    }

    @FXML
    private void handleGoToPaee5() {
        captureCurrentStepData();
        if (validateStep4()) {
            abrirPaee("/com/pies/projeto/integrado/piesfront/screens/paee-5.fxml", 5);
        } else {
            showValidation("Algum campo está em branco. Preencha para prosseguir.");
        }
    }

    @FXML
    private void handleGoToPaee6() {
        captureCurrentStepData();
        if (validateStep5()) {
            abrirPaee("/com/pies/projeto/integrado/piesfront/screens/paee-6.fxml", 6);
        } else {
            showValidation("Algum campo está em branco. Preencha para prosseguir.");
        }
    }

    @FXML
    private void handleConcluirAction() {
        System.out.println("=== handleConcluirAction PAEE ===");
        captureCurrentStepData();
        if (educando == null || educando.id() == null) {
            System.err.println("Educando inválido!");
            showValidation("Educando inválido.");
            return;
        }
        System.out.println("Educando ID: " + educando.id());
        if (!validateResumo()) {
            System.err.println("Resumo do caso não preenchido!");
            showValidation("Informe o resumo do caso.");
            return;
        }
        System.out.println("Resumo validado!");
        try {
            String token = authService.getCurrentToken();
            System.out.println("Token presente: " + (token != null && !token.isEmpty()));
            if (token == null || token.isEmpty()) {
                showValidation("Sessão expirada.");
                return;
            }
            var dto = new CreatePAEEDTO(
                    formData.resumoCaso,
                    formData.dificuldadesMotoresPsicomotores,
                    formData.dificuldadesCognitivo,
                    formData.dificuldadesSensorial,
                    formData.dificuldadesLinguagemComunicacao,
                    formData.dificuldadesFamiliar,
                    formData.dificuldadesAfetivoInterpessoais,
                    formData.dificuldadesRaciocinioLogicoMatematico,
                    formData.dificuldadesAVAs,
                    formData.desenvolvimentoMotoresPsicomotoresDificuldades,
                    formData.desenvolvimentoMotoresPsicomotoresIntervencoes,
                    formData.comunicacaoLinguagemDificuldades,
                    formData.comunicacaoLinguagemIntervencoes,
                    formData.dificuldadesRaciocinio,
                    formData.intervencoesRaciocinio,
                    formData.dificuldadesAtencao,
                    formData.intervencoesAtencao,
                    formData.dificuldadesMemoria,
                    formData.intervencoesMemoria,
                    formData.dificuldadesPercepcao,
                    formData.intervencoesPercepcao,
                    formData.dificuldadesSociabilidade,
                    formData.intervencoesSociabilidade,
                    formData.dificuldadesAVA,
                    formData.intervencoesAVA,
                    formData.objetivosAEE,
                    formData.envAEE,
                    formData.envPsicologo,
                    formData.envFisioterapeuta,
                    formData.envPsicopedagogo,
                    formData.envTO,
                    formData.envEducacaoFisica,
                    formData.envEstimulacaoPrecoce,
                    educando.id()
            );
            System.out.println("DTO criado. Resumo: " + dto.resumoCaso);
            System.out.println("Educando ID no DTO: " + dto.educandoId);
            System.out.println("Chamando authService.criarPAEE...");
            boolean ok = authService.criarPAEE(dto);
            System.out.println("Resultado criarPAEE: " + ok);
            if (ok) {
                com.pies.projeto.integrado.piesfront.services.AtendimentoFlowService.getInstance().concluirPAEE(educando.id());
                NotificacaoController.agendar("PAEE registrado com sucesso!", true);
                handleCancelAction();
            } else {
                showPopup("Falha ao enviar PAEE.", false);
                showValidation("Falha ao enviar PAEE.");
            }
        } catch (Exception e) {
            System.err.println("Exceção ao criar PAEE: " + e.getMessage());
            e.printStackTrace();
            showPopup("Falha ao enviar PAEE.", false);
            showValidation("Falha ao enviar PAEE.");
        }
    }

    private void abrirPaee(String resource, int step) {
        if (anamnese != null) {
            Janelas.carregarTela(new javafx.event.ActionEvent(anamnese, null), resource, "PAEE", controller -> {
                if (controller instanceof PAEEController c) {
                    c.setNovoRegistro(true);
                    c.currentStep = step;
                    c.setFormData(formData);
                    c.setEducando(educando);
                }
            });
        }
    }

    private void navegar(String resource, String titulo) {
        if (anamnese != null) {
            Janelas.carregarTela(new javafx.event.ActionEvent(anamnese, null), resource, titulo);
        }
    }

    private void detectarEtapa(URL url) {
        if (url == null) return;
        String p = url.getPath();
        if (p.contains("paee-1.fxml")) currentStep = 1;
        else if (p.contains("paee-2.fxml")) currentStep = 2;
        else if (p.contains("paee-3.fxml")) currentStep = 3;
        else if (p.contains("paee-4.fxml")) currentStep = 4;
        else if (p.contains("paee-5.fxml")) currentStep = 5;
        else if (p.contains("paee-6.fxml")) currentStep = 6;
    }

    private void atualizarIndicadorDeTela() {
        if (indicadorDeTela == null) return;
        String nome = educando != null ? educando.nome() : "Aluno(a)";
        indicadorDeTela.setText("PAEE (Plano de Atendimento Educacional Especializado) do aluno(a) " + nome);
    }

    private void inicializarChoiceBoxesSimNao() {
        if (dificuldadesMotoresPsicomotoresCb != null && dificuldadesMotoresPsicomotoresCb.getItems().isEmpty()) dificuldadesMotoresPsicomotoresCb.getItems().addAll("Sim", "Não");
        if (dificuldadesCognitivoCb != null && dificuldadesCognitivoCb.getItems().isEmpty()) dificuldadesCognitivoCb.getItems().addAll("Sim", "Não");
        if (dificuldadesSensorialCb != null && dificuldadesSensorialCb.getItems().isEmpty()) dificuldadesSensorialCb.getItems().addAll("Sim", "Não");
        if (dificuldadesLinguagemComunicacaoCb != null && dificuldadesLinguagemComunicacaoCb.getItems().isEmpty()) dificuldadesLinguagemComunicacaoCb.getItems().addAll("Sim", "Não");
        if (dificuldadesFamiliarCb != null && dificuldadesFamiliarCb.getItems().isEmpty()) dificuldadesFamiliarCb.getItems().addAll("Sim", "Não");
        if (dificuldadesAfetivoInterpessoaisCb != null && dificuldadesAfetivoInterpessoaisCb.getItems().isEmpty()) dificuldadesAfetivoInterpessoaisCb.getItems().addAll("Sim", "Não");
        if (dificuldadesRaciocinioLogicoMatematicoCb != null && dificuldadesRaciocinioLogicoMatematicoCb.getItems().isEmpty()) dificuldadesRaciocinioLogicoMatematicoCb.getItems().addAll("Sim", "Não");
        if (dificuldadesAVAsCb != null && dificuldadesAVAsCb.getItems().isEmpty()) dificuldadesAVAsCb.getItems().addAll("Sim", "Não");
        if (cbAEE != null && cbAEE.getItems().isEmpty()) cbAEE.getItems().addAll("Sim", "Não");
        if (cbPsicologo != null && cbPsicologo.getItems().isEmpty()) cbPsicologo.getItems().addAll("Sim", "Não");
        if (cbFisioterapeuta != null && cbFisioterapeuta.getItems().isEmpty()) cbFisioterapeuta.getItems().addAll("Sim", "Não");
        if (cbPsicopedagogo != null && cbPsicopedagogo.getItems().isEmpty()) cbPsicopedagogo.getItems().addAll("Sim", "Não");
        if (cbTO != null && cbTO.getItems().isEmpty()) cbTO.getItems().addAll("Sim", "Não");
        if (cbEducacaoFisica != null && cbEducacaoFisica.getItems().isEmpty()) cbEducacaoFisica.getItems().addAll("Sim", "Não");
        if (cbEstimulacaoPrecoce != null && cbEstimulacaoPrecoce.getItems().isEmpty()) cbEstimulacaoPrecoce.getItems().addAll("Sim", "Não");
    }

    private void captureCurrentStepData() {
        System.out.println("=== captureCurrentStepData (Step " + currentStep + ") ===");
        if (currentStep == 1) {
            formData.resumoCaso = resumoCaso != null ? resumoCaso.getText() : formData.resumoCaso;
            System.out.println("Capturado resumoCaso: " + (formData.resumoCaso != null ? "presente" : "null"));
            formData.dificuldadesMotoresPsicomotores = getValue(dificuldadesMotoresPsicomotoresCb, formData.dificuldadesMotoresPsicomotores);
            formData.dificuldadesCognitivo = getValue(dificuldadesCognitivoCb, formData.dificuldadesCognitivo);
            formData.dificuldadesSensorial = getValue(dificuldadesSensorialCb, formData.dificuldadesSensorial);
            formData.dificuldadesLinguagemComunicacao = getValue(dificuldadesLinguagemComunicacaoCb, formData.dificuldadesLinguagemComunicacao);
            formData.dificuldadesFamiliar = getValue(dificuldadesFamiliarCb, formData.dificuldadesFamiliar);
            formData.dificuldadesAfetivoInterpessoais = getValue(dificuldadesAfetivoInterpessoaisCb, formData.dificuldadesAfetivoInterpessoais);
            formData.dificuldadesRaciocinioLogicoMatematico = getValue(dificuldadesRaciocinioLogicoMatematicoCb, formData.dificuldadesRaciocinioLogicoMatematico);
            formData.dificuldadesAVAs = getValue(dificuldadesAVAsCb, formData.dificuldadesAVAs);
        } else if (currentStep == 2) {
            formData.desenvolvimentoMotoresPsicomotoresDificuldades = getText(desenvolvimentoMotoresPsicomotoresDificuldadesTa, formData.desenvolvimentoMotoresPsicomotoresDificuldades);
            System.out.println("Capturado desenvolvimentoMotoresPsicomotoresDificuldades: " + (formData.desenvolvimentoMotoresPsicomotoresDificuldades != null ? "presente" : "null"));
            formData.desenvolvimentoMotoresPsicomotoresIntervencoes = getText(desenvolvimentoMotoresPsicomotoresIntervencoesTa, formData.desenvolvimentoMotoresPsicomotoresIntervencoes);
            formData.comunicacaoLinguagemDificuldades = getText(comunicacaoLinguagemDificuldadesTa, formData.comunicacaoLinguagemDificuldades);
            formData.comunicacaoLinguagemIntervencoes = getText(comunicacaoLinguagemIntervencoesTa, formData.comunicacaoLinguagemIntervencoes);
        } else if (currentStep == 3) {
            formData.dificuldadesRaciocinio = getText(dificuldadesRaciocinioTa, formData.dificuldadesRaciocinio);
            System.out.println("Capturado dificuldadesRaciocinio: " + (formData.dificuldadesRaciocinio != null ? "presente" : "null"));
            formData.intervencoesRaciocinio = getText(intervencoesRaciocinioTa, formData.intervencoesRaciocinio);
            formData.dificuldadesAtencao = getText(dificuldadesAtencaoTa, formData.dificuldadesAtencao);
            formData.intervencoesAtencao = getText(intervencoesAtencaoTa, formData.intervencoesAtencao);
        } else if (currentStep == 4) {
            formData.dificuldadesMemoria = getText(dificuldadesMemoriaTa, formData.dificuldadesMemoria);
            formData.intervencoesMemoria = getText(intervencoesMemoriaTa, formData.intervencoesMemoria);
            formData.dificuldadesPercepcao = getText(dificuldadesPercepcaoTa, formData.dificuldadesPercepcao);
            formData.intervencoesPercepcao = getText(intervencoesPercepcaoTa, formData.intervencoesPercepcao);
        } else if (currentStep == 5) {
            formData.dificuldadesSociabilidade = getText(dificuldadesSociabilidadeTa, formData.dificuldadesSociabilidade);
            formData.intervencoesSociabilidade = getText(intervencoesSociabilidadeTa, formData.intervencoesSociabilidade);
            formData.dificuldadesAVA = getText(dificuldadesAVATa, formData.dificuldadesAVA);
            formData.intervencoesAVA = getText(intervencoesAVATa, formData.intervencoesAVA);
        } else if (currentStep == 6) {
            formData.objetivosAEE = getText(objetivosPlano, formData.objetivosAEE);
            formData.envAEE = getValue(cbAEE, formData.envAEE);
            formData.envPsicologo = getValue(cbPsicologo, formData.envPsicologo);
            formData.envFisioterapeuta = getValue(cbFisioterapeuta, formData.envFisioterapeuta);
            formData.envPsicopedagogo = getValue(cbPsicopedagogo, formData.envPsicopedagogo);
            formData.envTO = getValue(cbTO, formData.envTO);
            formData.envEducacaoFisica = getValue(cbEducacaoFisica, formData.envEducacaoFisica);
            formData.envEstimulacaoPrecoce = getValue(cbEstimulacaoPrecoce, formData.envEstimulacaoPrecoce);
        }
    }

    private void preencherCamposComFormData() {
        System.out.println("=== preencherCamposComFormData (Step " + currentStep + ") ===");
        if (currentStep == 1) {
            if (resumoCaso != null && formData.resumoCaso != null) resumoCaso.setText(formData.resumoCaso);
            setChoice(dificuldadesMotoresPsicomotoresCb, formData.dificuldadesMotoresPsicomotores);
            setChoice(dificuldadesCognitivoCb, formData.dificuldadesCognitivo);
            setChoice(dificuldadesSensorialCb, formData.dificuldadesSensorial);
            setChoice(dificuldadesLinguagemComunicacaoCb, formData.dificuldadesLinguagemComunicacao);
            setChoice(dificuldadesFamiliarCb, formData.dificuldadesFamiliar);
            setChoice(dificuldadesAfetivoInterpessoaisCb, formData.dificuldadesAfetivoInterpessoais);
            setChoice(dificuldadesRaciocinioLogicoMatematicoCb, formData.dificuldadesRaciocinioLogicoMatematico);
            setChoice(dificuldadesAVAsCb, formData.dificuldadesAVAs);
        } else if (currentStep == 2) {
            if (desenvolvimentoMotoresPsicomotoresDificuldadesTa != null && formData.desenvolvimentoMotoresPsicomotoresDificuldades != null) desenvolvimentoMotoresPsicomotoresDificuldadesTa.setText(formData.desenvolvimentoMotoresPsicomotoresDificuldades);
            if (desenvolvimentoMotoresPsicomotoresIntervencoesTa != null && formData.desenvolvimentoMotoresPsicomotoresIntervencoes != null) desenvolvimentoMotoresPsicomotoresIntervencoesTa.setText(formData.desenvolvimentoMotoresPsicomotoresIntervencoes);
            if (comunicacaoLinguagemDificuldadesTa != null && formData.comunicacaoLinguagemDificuldades != null) comunicacaoLinguagemDificuldadesTa.setText(formData.comunicacaoLinguagemDificuldades);
            if (comunicacaoLinguagemIntervencoesTa != null && formData.comunicacaoLinguagemIntervencoes != null) comunicacaoLinguagemIntervencoesTa.setText(formData.comunicacaoLinguagemIntervencoes);
        } else if (currentStep == 3) {
            if (dificuldadesRaciocinioTa != null && formData.dificuldadesRaciocinio != null) dificuldadesRaciocinioTa.setText(formData.dificuldadesRaciocinio);
            if (intervencoesRaciocinioTa != null && formData.intervencoesRaciocinio != null) intervencoesRaciocinioTa.setText(formData.intervencoesRaciocinio);
            if (dificuldadesAtencaoTa != null && formData.dificuldadesAtencao != null) dificuldadesAtencaoTa.setText(formData.dificuldadesAtencao);
            if (intervencoesAtencaoTa != null && formData.intervencoesAtencao != null) intervencoesAtencaoTa.setText(formData.intervencoesAtencao);
        } else if (currentStep == 4) {
            if (dificuldadesMemoriaTa != null && formData.dificuldadesMemoria != null) dificuldadesMemoriaTa.setText(formData.dificuldadesMemoria);
            if (intervencoesMemoriaTa != null && formData.intervencoesMemoria != null) intervencoesMemoriaTa.setText(formData.intervencoesMemoria);
            if (dificuldadesPercepcaoTa != null && formData.dificuldadesPercepcao != null) dificuldadesPercepcaoTa.setText(formData.dificuldadesPercepcao);
            if (intervencoesPercepcaoTa != null && formData.intervencoesPercepcao != null) intervencoesPercepcaoTa.setText(formData.intervencoesPercepcao);
        } else if (currentStep == 5) {
            if (dificuldadesSociabilidadeTa != null && formData.dificuldadesSociabilidade != null) dificuldadesSociabilidadeTa.setText(formData.dificuldadesSociabilidade);
            if (intervencoesSociabilidadeTa != null && formData.intervencoesSociabilidade != null) intervencoesSociabilidadeTa.setText(formData.intervencoesSociabilidade);
            if (dificuldadesAVATa != null && formData.dificuldadesAVA != null) dificuldadesAVATa.setText(formData.dificuldadesAVA);
            if (intervencoesAVATa != null && formData.intervencoesAVA != null) intervencoesAVATa.setText(formData.intervencoesAVA);
        } else if (currentStep == 6) {
            if (objetivosPlano != null && formData.objetivosAEE != null) objetivosPlano.setText(formData.objetivosAEE);
            setChoice(cbAEE, formData.envAEE);
            setChoice(cbPsicologo, formData.envPsicologo);
            setChoice(cbFisioterapeuta, formData.envFisioterapeuta);
            setChoice(cbPsicopedagogo, formData.envPsicopedagogo);
            setChoice(cbTO, formData.envTO);
            setChoice(cbEducacaoFisica, formData.envEducacaoFisica);
            setChoice(cbEstimulacaoPrecoce, formData.envEstimulacaoPrecoce);
        }
    }

    private boolean validateResumo() {
        if (resumoCaso == null) return true;
        String t = resumoCaso.getText() != null ? resumoCaso.getText().trim() : "";
        return !t.isEmpty();
    }

    private boolean isEmpty(TextArea ta) {
        if (ta == null) return false;
        if (!ta.isVisible()) return false;
        String t = ta.getText() != null ? ta.getText().trim() : null;
        return t == null || t.isEmpty();
    }

    private boolean validateStep2() {
        boolean e = isEmpty(desenvolvimentoMotoresPsicomotoresDificuldadesTa)
                || isEmpty(desenvolvimentoMotoresPsicomotoresIntervencoesTa)
                || isEmpty(comunicacaoLinguagemDificuldadesTa)
                || isEmpty(comunicacaoLinguagemIntervencoesTa);
        return !e;
    }

    private boolean validateStep3() {
        boolean e = isEmpty(dificuldadesRaciocinioTa)
                || isEmpty(intervencoesRaciocinioTa)
                || isEmpty(dificuldadesAtencaoTa)
                || isEmpty(intervencoesAtencaoTa);
        return !e;
    }

    private boolean validateStep4() {
        boolean e = isEmpty(dificuldadesMemoriaTa)
                || isEmpty(intervencoesMemoriaTa)
                || isEmpty(dificuldadesPercepcaoTa)
                || isEmpty(intervencoesPercepcaoTa);
        return !e;
    }

    private boolean validateStep5() {
        boolean e = isEmpty(dificuldadesSociabilidadeTa)
                || isEmpty(intervencoesSociabilidadeTa)
                || isEmpty(dificuldadesAVATa)
                || isEmpty(intervencoesAVATa);
        return !e;
    }

    private String getValue(ChoiceBox<String> cb, String fallback) {
        if (cb == null) return fallback;
        Object v = cb.getValue();
        return v == null ? fallback : v.toString();
    }

    private String getText(TextArea ta, String fallback) {
        if (ta == null) return fallback;
        String t = ta.getText();
        return t == null ? fallback : t;
    }

    private void setChoice(ChoiceBox<String> cb, String value) {
        if (cb == null || value == null) return;
        if (!cb.getItems().contains(value)) cb.getItems().add(value);
        cb.setValue(value);
    }

    private String toSimNao(Object o) {
        if (o == null) return null;
        String s = o.toString();
        if (s.equalsIgnoreCase("true") || s.equalsIgnoreCase("sim") || s.equals("1")) return "Sim";
        if (s.equalsIgnoreCase("false") || s.equalsIgnoreCase("nao") || s.equalsIgnoreCase("não") || s.equals("0")) return "Não";
        return s;
    }

    private boolean isFormDataEmpty(PaeeFormData data) {
        if (data == null) return true;
        // Verifica se todos os campos principais estão vazios
        return data.resumoCaso == null &&
               data.dificuldadesMotoresPsicomotores == null &&
               data.desenvolvimentoMotoresPsicomotoresDificuldades == null &&
               data.desenvolvimentoMotoresPsicomotoresIntervencoes == null &&
               data.comunicacaoLinguagemDificuldades == null &&
               data.objetivosAEE == null;
    }

    private void carregarPaeeExistente() {
        System.out.println("=== carregarPaeeExistente ===");
        if (educando == null || educando.id() == null) {
            System.out.println("Educando null ou sem ID, abortando");
            return;
        }
        java.util.List<java.util.Map<String, Object>> lista = authService.getPaeesPorEducandoRaw(educando.id());
        System.out.println("Lista de PAEEs retornada: " + (lista != null ? lista.size() + " registros" : "null"));
        if (lista == null || lista.isEmpty()) {
            System.out.println("Nenhum PAEE encontrado");
            return;
        }
        java.util.Map<String, Object> dto = lista.get(lista.size() - 1);
        System.out.println("DTO recuperado com " + dto.size() + " campos");
        System.out.println("Campos no DTO: " + dto.keySet());
        System.out.println("Valores:");
        dto.forEach((key, value) -> {
            String valorStr = value != null ? value.toString() : "null";
            if (valorStr.length() > 50) valorStr = valorStr.substring(0, 50) + "...";
            System.out.println("  " + key + " = " + valorStr);
        });
        Object o;
        o = dto.get("resumoCaso");
        if (o instanceof String s) {
            formData.resumoCaso = s;
            System.out.println("resumoCaso carregado: " + s.substring(0, Math.min(50, s.length())) + "...");
        }
        o = dto.get("dificuldadesMotoresPsicomotores");
        if (o instanceof String s) formData.dificuldadesMotoresPsicomotores = toSimNao(s);
        o = dto.get("dificuldadesCognitivo");
        if (o instanceof String s) formData.dificuldadesCognitivo = toSimNao(s);
        o = dto.get("dificuldadesSensorial");
        if (o instanceof String s) formData.dificuldadesSensorial = toSimNao(s);
        o = dto.get("dificuldadesLinguagemComunicacao");
        if (o instanceof String s) formData.dificuldadesLinguagemComunicacao = toSimNao(s);
        o = dto.get("dificuldadesFamiliar");
        if (o instanceof String s) formData.dificuldadesFamiliar = toSimNao(s);
        o = dto.get("dificuldadesAfetivoInterpessoais");
        if (o instanceof String s) formData.dificuldadesAfetivoInterpessoais = toSimNao(s);
        o = dto.get("dificuldadesRaciocinioLogicoMatematico");
        if (o instanceof String s) formData.dificuldadesRaciocinioLogicoMatematico = toSimNao(s);
        o = dto.get("dificuldadesAVAs");
        if (o instanceof String s) formData.dificuldadesAVAs = toSimNao(s);
        o = dto.get("desenvolvimentoMotoresPsicomotoresDificuldades");
        if (o instanceof String s) {
            formData.desenvolvimentoMotoresPsicomotoresDificuldades = s;
            System.out.println("desenvolvimentoMotoresPsicomotoresDificuldades carregado: " + s.length() + " chars");
        }
        o = dto.get("desenvolvimentoMotoresPsicomotoresIntervencoes");
        if (o instanceof String s) {
            formData.desenvolvimentoMotoresPsicomotoresIntervencoes = s;
            System.out.println("desenvolvimentoMotoresPsicomotoresIntervencoes carregado: " + s.length() + " chars");
        }
        o = dto.get("comunicacaoLinguagemDificuldades");
        if (o instanceof String s) formData.comunicacaoLinguagemDificuldades = s;
        o = dto.get("comunicacaoLinguagemIntervencoes");
        if (o instanceof String s) formData.comunicacaoLinguagemIntervencoes = s;
        o = dto.get("dificuldadesRaciocinio");
        if (o instanceof String s) formData.dificuldadesRaciocinio = s;
        o = dto.get("intervencoesRaciocinio");
        if (o instanceof String s) formData.intervencoesRaciocinio = s;
        o = dto.get("dificuldadesAtencao");
        if (o instanceof String s) formData.dificuldadesAtencao = s;
        o = dto.get("intervencoesAtencao");
        if (o instanceof String s) formData.intervencoesAtencao = s;
        o = dto.get("dificuldadesMemoria");
        if (o instanceof String s) formData.dificuldadesMemoria = s;
        o = dto.get("intervencoesMemoria");
        if (o instanceof String s) formData.intervencoesMemoria = s;
        o = dto.get("dificuldadesPercepcao");
        if (o instanceof String s) formData.dificuldadesPercepcao = s;
        o = dto.get("intervencoesPercepcao");
        if (o instanceof String s) formData.intervencoesPercepcao = s;
        o = dto.get("dificuldadesSociabilidade");
        if (o instanceof String s) formData.dificuldadesSociabilidade = s;
        o = dto.get("intervencoesSociabilidade");
        if (o instanceof String s) formData.intervencoesSociabilidade = s;
        o = dto.get("dificuldadesAVA");
        if (o instanceof String s) formData.dificuldadesAVA = s;
        o = dto.get("intervencoesAVA");
        if (o instanceof String s) formData.intervencoesAVA = s;
        o = dto.get("objetivosAEE");
        if (o instanceof String s) formData.objetivosAEE = s;
        o = dto.get("envAEE");
        formData.envAEE = toSimNao(o);
        o = dto.get("envPsicologo");
        formData.envPsicologo = toSimNao(o);
        o = dto.get("envFisioterapeuta");
        formData.envFisioterapeuta = toSimNao(o);
        o = dto.get("envPsicopedagogo");
        formData.envPsicopedagogo = toSimNao(o);
        o = dto.get("envTO");
        formData.envTO = toSimNao(o);
        o = dto.get("envEducacaoFisica");
        formData.envEducacaoFisica = toSimNao(o);
        o = dto.get("envEstimulacaoPrecoce");
        formData.envEstimulacaoPrecoce = toSimNao(o);
    }

    private void showValidation(String msg) {
        if (validationMsg != null) {
            validationMsg.setText(msg);
            validationMsg.setVisible(true);
        }
    }

    private void showPopup(String mensagem, boolean sucesso) {
        NotificacaoController.exibir(anamnese, mensagem, sucesso);
    }

    public static class CreatePAEEDTO {
        public String resumoCaso;
        public String dificuldadesMotoresPsicomotores;
        public String dificuldadesCognitivo;
        public String dificuldadesSensorial;
        public String dificuldadesLinguagemComunicacao;
        public String dificuldadesFamiliar;
        public String dificuldadesAfetivoInterpessoais;
        public String dificuldadesRaciocinioLogicoMatematico;
        public String dificuldadesAVAs;
        public String desenvolvimentoMotoresPsicomotoresDificuldades;
        public String desenvolvimentoMotoresPsicomotoresIntervencoes;
        public String comunicacaoLinguagemDificuldades;
        public String comunicacaoLinguagemIntervencoes;
        public String dificuldadesRaciocinio;
        public String intervencoesRaciocinio;
        public String dificuldadesAtencao;
        public String intervencoesAtencao;
        public String dificuldadesMemoria;
        public String intervencoesMemoria;
        public String dificuldadesPercepcao;
        public String intervencoesPercepcao;
        public String dificuldadesSociabilidade;
        public String intervencoesSociabilidade;
        public String dificuldadesAVA;
        public String intervencoesAVA;
        public String objetivosAEE;
        public String envAEE;
        public String envPsicologo;
        public String envFisioterapeuta;
        public String envPsicopedagogo;
        public String envTO;
        public String envEducacaoFisica;
        public String envEstimulacaoPrecoce;
        public String educandoId;
        public CreatePAEEDTO(String resumoCaso,
                             String dificuldadesMotoresPsicomotores,
                             String dificuldadesCognitivo,
                             String dificuldadesSensorial,
                             String dificuldadesLinguagemComunicacao,
                             String dificuldadesFamiliar,
                             String dificuldadesAfetivoInterpessoais,
                             String dificuldadesRaciocinioLogicoMatematico,
                             String dificuldadesAVAs,
                             String desenvolvimentoMotoresPsicomotoresDificuldades,
                             String desenvolvimentoMotoresPsicomotoresIntervencoes,
                             String comunicacaoLinguagemDificuldades,
                             String comunicacaoLinguagemIntervencoes,
                             String dificuldadesRaciocinio,
                             String intervencoesRaciocinio,
                             String dificuldadesAtencao,
                             String intervencoesAtencao,
                             String dificuldadesMemoria,
                             String intervencoesMemoria,
                             String dificuldadesPercepcao,
                             String intervencoesPercepcao,
                             String dificuldadesSociabilidade,
                             String intervencoesSociabilidade,
                             String dificuldadesAVA,
                             String intervencoesAVA,
                             String objetivosAEE,
                             String envAEE,
                             String envPsicologo,
                             String envFisioterapeuta,
                             String envPsicopedagogo,
                             String envTO,
                             String envEducacaoFisica,
                             String envEstimulacaoPrecoce,
                             String educandoId) {
            this.resumoCaso = resumoCaso;
            this.dificuldadesMotoresPsicomotores = dificuldadesMotoresPsicomotores;
            this.dificuldadesCognitivo = dificuldadesCognitivo;
            this.dificuldadesSensorial = dificuldadesSensorial;
            this.dificuldadesLinguagemComunicacao = dificuldadesLinguagemComunicacao;
            this.dificuldadesFamiliar = dificuldadesFamiliar;
            this.dificuldadesAfetivoInterpessoais = dificuldadesAfetivoInterpessoais;
            this.dificuldadesRaciocinioLogicoMatematico = dificuldadesRaciocinioLogicoMatematico;
            this.dificuldadesAVAs = dificuldadesAVAs;
            this.desenvolvimentoMotoresPsicomotoresDificuldades = desenvolvimentoMotoresPsicomotoresDificuldades;
            this.desenvolvimentoMotoresPsicomotoresIntervencoes = desenvolvimentoMotoresPsicomotoresIntervencoes;
            this.comunicacaoLinguagemDificuldades = comunicacaoLinguagemDificuldades;
            this.comunicacaoLinguagemIntervencoes = comunicacaoLinguagemIntervencoes;
            this.dificuldadesRaciocinio = dificuldadesRaciocinio;
            this.intervencoesRaciocinio = intervencoesRaciocinio;
            this.dificuldadesAtencao = dificuldadesAtencao;
            this.intervencoesAtencao = intervencoesAtencao;
            this.dificuldadesMemoria = dificuldadesMemoria;
            this.intervencoesMemoria = intervencoesMemoria;
            this.dificuldadesPercepcao = dificuldadesPercepcao;
            this.intervencoesPercepcao = intervencoesPercepcao;
            this.dificuldadesSociabilidade = dificuldadesSociabilidade;
            this.intervencoesSociabilidade = intervencoesSociabilidade;
            this.dificuldadesAVA = dificuldadesAVA;
            this.intervencoesAVA = intervencoesAVA;
            this.objetivosAEE = objetivosAEE;
            this.envAEE = envAEE;
            this.envPsicologo = envPsicologo;
            this.envFisioterapeuta = envFisioterapeuta;
            this.envPsicopedagogo = envPsicopedagogo;
            this.envTO = envTO;
            this.envEducacaoFisica = envEducacaoFisica;
            this.envEstimulacaoPrecoce = envEstimulacaoPrecoce;
            this.educandoId = educandoId;
        }
    }

    public static class PaeeFormData {
        public String resumoCaso;
        public String dificuldadesMotoresPsicomotores;
        public String dificuldadesCognitivo;
        public String dificuldadesSensorial;
        public String dificuldadesLinguagemComunicacao;
        public String dificuldadesFamiliar;
        public String dificuldadesAfetivoInterpessoais;
        public String dificuldadesRaciocinioLogicoMatematico;
        public String dificuldadesAVAs;
        public String desenvolvimentoMotoresPsicomotoresDificuldades;
        public String desenvolvimentoMotoresPsicomotoresIntervencoes;
        public String comunicacaoLinguagemDificuldades;
        public String comunicacaoLinguagemIntervencoes;
        public String dificuldadesRaciocinio;
        public String intervencoesRaciocinio;
        public String dificuldadesAtencao;
        public String intervencoesAtencao;
        public String dificuldadesMemoria;
        public String intervencoesMemoria;
        public String dificuldadesPercepcao;
        public String intervencoesPercepcao;
        public String dificuldadesSociabilidade;
        public String intervencoesSociabilidade;
        public String dificuldadesAVA;
        public String intervencoesAVA;
        public String objetivosAEE;
        public String envAEE;
        public String envPsicologo;
        public String envFisioterapeuta;
        public String envPsicopedagogo;
        public String envTO;
        public String envEducacaoFisica;
        public String envEstimulacaoPrecoce;
    }
}
