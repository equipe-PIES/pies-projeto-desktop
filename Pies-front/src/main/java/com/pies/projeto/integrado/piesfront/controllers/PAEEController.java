package com.pies.projeto.integrado.piesfront.controllers;

import com.pies.projeto.integrado.piesfront.dto.EducandoDTO;
import com.pies.projeto.integrado.piesfront.dto.UserInfoDTO;
import com.pies.projeto.integrado.piesfront.dto.ProfessorDTO;
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
    private Label nameUser;
    @FXML
    private Label cargoUser;
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
    private boolean novoRegistro = false;
    private boolean somenteLeitura = false;

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
        }
        // Aplica o modo somente leitura após carregar os dados
        if (somenteLeitura) {
            aplicarModoSomenteLeitura();
        }
        // Força o preenchimento dos campos após garantir que o formData está correto
        javafx.application.Platform.runLater(() -> {
            System.out.println("=== Preenchendo campos via Platform.runLater ===");
            preencherCamposComFormData();
        });
        System.out.println("=== Fim setEducando ===");
    }

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
        if (novo) {
            this.formData = new PaeeFormData();
        }
    }

    /**
     * Método para ativar o modo de visualização somente leitura
     * Deve ser chamado antes de carregar a tela quando for apenas para ver o PAEE
     */
    public void ativarModoVisualizacao() {
        this.somenteLeitura = true;
        this.novoRegistro = false;
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
        // Aplica o modo somente leitura se estiver ativo
        if (somenteLeitura) {
            System.out.println("Aplicando modo somente leitura...");
            aplicarModoSomenteLeitura();
        }
        javafx.application.Platform.runLater(() -> {
            atualizarNomeUsuarioAsync();
        });
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
        // Verifica se está no modo de visualização
        if (somenteLeitura) {
            showValidation("Modo de visualização - Ação não permitida");
            return;
        }
        
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
                    authService.getProfessorId(),
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
        System.out.println("=== abrirPaee ===");
        System.out.println("Resource: " + resource);
        System.out.println("Step: " + step);
        System.out.println("FormData.resumoCaso: " + (formData.resumoCaso != null ? "presente" : "null"));
        System.out.println("FormData.desenvolvimentoMotoresPsicomotoresDificuldades: " + (formData.desenvolvimentoMotoresPsicomotoresDificuldades != null ? "presente" : "null"));
        if (anamnese != null) {
            boolean novoAtual = this.novoRegistro;
            boolean leituraAtual = this.somenteLeitura;
            Janelas.carregarTela(new javafx.event.ActionEvent(anamnese, null), resource, "PAEE", controller -> {
                if (controller instanceof PAEEController c) {
                    c.setNovoRegistro(novoAtual);
                    c.currentStep = step;
                    c.setFormData(formData);
                    c.setEducando(educando);
                    c.setSomenteLeitura(leituraAtual); // Passa o estado de somente leitura
                    // Força o preenchimento dos campos da página aberta
                    c.preencherCamposComFormData();
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
        String modo = somenteLeitura ? " - Modo Visualização" : "";
        indicadorDeTela.setText("PAEE (Plano de Atendimento Educacional Especializado) do aluno(a) " + nome + modo);
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
            System.out.println("Preenchendo Step 1:");
            System.out.println("  resumoCaso: " + (formData.resumoCaso != null ? "presente (" + formData.resumoCaso.length() + " chars)" : "null"));
            if (resumoCaso != null && formData.resumoCaso != null) {
                resumoCaso.setText(formData.resumoCaso);
                System.out.println("  -> resumoCaso preenchido na UI");
            }
            setChoice(dificuldadesMotoresPsicomotoresCb, formData.dificuldadesMotoresPsicomotores);
            setChoice(dificuldadesCognitivoCb, formData.dificuldadesCognitivo);
            setChoice(dificuldadesSensorialCb, formData.dificuldadesSensorial);
            setChoice(dificuldadesLinguagemComunicacaoCb, formData.dificuldadesLinguagemComunicacao);
            setChoice(dificuldadesFamiliarCb, formData.dificuldadesFamiliar);
            setChoice(dificuldadesAfetivoInterpessoaisCb, formData.dificuldadesAfetivoInterpessoais);
            setChoice(dificuldadesRaciocinioLogicoMatematicoCb, formData.dificuldadesRaciocinioLogicoMatematico);
            setChoice(dificuldadesAVAsCb, formData.dificuldadesAVAs);
        } else if (currentStep == 2) {
            System.out.println("Preenchendo Step 2:");
            System.out.println("  desenvolvimentoMotoresPsicomotoresDificuldades: " + (formData.desenvolvimentoMotoresPsicomotoresDificuldades != null ? "presente" : "null"));
            if (desenvolvimentoMotoresPsicomotoresDificuldadesTa != null && formData.desenvolvimentoMotoresPsicomotoresDificuldades != null) {
                desenvolvimentoMotoresPsicomotoresDificuldadesTa.setText(formData.desenvolvimentoMotoresPsicomotoresDificuldades);
                System.out.println("  -> desenvolvimentoMotoresPsicomotoresDificuldadesTa preenchido");
            } else {
                System.out.println("  -> Campo ou valor null: ta=" + (desenvolvimentoMotoresPsicomotoresDificuldadesTa != null) + ", valor=" + (formData.desenvolvimentoMotoresPsicomotoresDificuldades != null));
            }
            if (desenvolvimentoMotoresPsicomotoresIntervencoesTa != null && formData.desenvolvimentoMotoresPsicomotoresIntervencoes != null) desenvolvimentoMotoresPsicomotoresIntervencoesTa.setText(formData.desenvolvimentoMotoresPsicomotoresIntervencoes);
            if (comunicacaoLinguagemDificuldadesTa != null && formData.comunicacaoLinguagemDificuldades != null) comunicacaoLinguagemDificuldadesTa.setText(formData.comunicacaoLinguagemDificuldades);
            if (comunicacaoLinguagemIntervencoesTa != null && formData.comunicacaoLinguagemIntervencoes != null) comunicacaoLinguagemIntervencoesTa.setText(formData.comunicacaoLinguagemIntervencoes);
        } else if (currentStep == 3) {
            System.out.println("Preenchendo Step 3:");
            System.out.println("  dificuldadesRaciocinio: " + (formData.dificuldadesRaciocinio != null ? "presente" : "null"));
            System.out.println("  dificuldadesRaciocinioTa null? " + (dificuldadesRaciocinioTa == null));
            if (dificuldadesRaciocinioTa != null && formData.dificuldadesRaciocinio != null) {
                dificuldadesRaciocinioTa.setText(formData.dificuldadesRaciocinio);
                System.out.println("  -> dificuldadesRaciocinioTa preenchido com: " + formData.dificuldadesRaciocinio.substring(0, Math.min(50, formData.dificuldadesRaciocinio.length())));
            } else {
                System.out.println("  -> Não preenchido. ta null? " + (dificuldadesRaciocinioTa == null) + ", valor null? " + (formData.dificuldadesRaciocinio == null));
            }
            if (intervencoesRaciocinioTa != null && formData.intervencoesRaciocinio != null) {
                intervencoesRaciocinioTa.setText(formData.intervencoesRaciocinio);
                System.out.println("  -> intervencoesRaciocinioTa preenchido");
            }
            if (dificuldadesAtencaoTa != null && formData.dificuldadesAtencao != null) {
                dificuldadesAtencaoTa.setText(formData.dificuldadesAtencao);
                System.out.println("  -> dificuldadesAtencaoTa preenchido");
            }
            if (intervencoesAtencaoTa != null && formData.intervencoesAtencao != null) {
                intervencoesAtencaoTa.setText(formData.intervencoesAtencao);
                System.out.println("  -> intervencoesAtencaoTa preenchido");
            }
        } else if (currentStep == 4) {
            System.out.println("Preenchendo Step 4:");
            if (dificuldadesMemoriaTa != null && formData.dificuldadesMemoria != null) dificuldadesMemoriaTa.setText(formData.dificuldadesMemoria);
            if (intervencoesMemoriaTa != null && formData.intervencoesMemoria != null) intervencoesMemoriaTa.setText(formData.intervencoesMemoria);
            if (dificuldadesPercepcaoTa != null && formData.dificuldadesPercepcao != null) dificuldadesPercepcaoTa.setText(formData.dificuldadesPercepcao);
            if (intervencoesPercepcaoTa != null && formData.intervencoesPercepcao != null) intervencoesPercepcaoTa.setText(formData.intervencoesPercepcao);
        } else if (currentStep == 5) {
            System.out.println("Preenchendo Step 5:");
            if (dificuldadesSociabilidadeTa != null && formData.dificuldadesSociabilidade != null) dificuldadesSociabilidadeTa.setText(formData.dificuldadesSociabilidade);
            if (intervencoesSociabilidadeTa != null && formData.intervencoesSociabilidade != null) intervencoesSociabilidadeTa.setText(formData.intervencoesSociabilidade);
            if (dificuldadesAVATa != null && formData.dificuldadesAVA != null) dificuldadesAVATa.setText(formData.dificuldadesAVA);
            if (intervencoesAVATa != null && formData.intervencoesAVA != null) intervencoesAVATa.setText(formData.intervencoesAVA);
        } else if (currentStep == 6) {
            System.out.println("Preenchendo Step 6:");
            if (objetivosPlano != null && formData.objetivosAEE != null) objetivosPlano.setText(formData.objetivosAEE);
            setChoice(cbAEE, formData.envAEE);
            setChoice(cbPsicologo, formData.envPsicologo);
            setChoice(cbFisioterapeuta, formData.envFisioterapeuta);
            setChoice(cbPsicopedagogo, formData.envPsicopedagogo);
            setChoice(cbTO, formData.envTO);
            setChoice(cbEducacaoFisica, formData.envEducacaoFisica);
            setChoice(cbEstimulacaoPrecoce, formData.envEstimulacaoPrecoce);
        }
        System.out.println("=== Fim preencherCamposComFormData ===");
    }

    private void atualizarNomeUsuarioAsync() {
        Thread t = new Thread(() -> {
            ProfessorDTO prof = authService.getProfessorLogado();
            UserInfoDTO userInfo = authService.getUserInfo();
            javafx.application.Platform.runLater(() -> {
                if (prof != null && prof.getNome() != null && !prof.getNome().isEmpty()) {
                    if (nameUser != null) nameUser.setText(prof.getNome());
                } else if (userInfo != null && userInfo.name() != null && !userInfo.name().isEmpty()) {
                    if (nameUser != null) nameUser.setText(userInfo.name());
                } else if (nameUser != null) {
                    nameUser.setText("Usuário");
                }
                if (cargoUser != null && userInfo != null && userInfo.role() != null) {
                    String cargo = switch (userInfo.role().toUpperCase()) {
                        case "PROFESSOR" -> "Professor(a)";
                        case "COORDENADOR" -> "Coordenador(a)";
                        case "ADMIN" -> "Administrador(a)";
                        default -> "Usuário";
                    };
                    cargoUser.setText(cargo);
                }
            });
        });
        t.setDaemon(true);
        t.start();
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
    
    // LOG DETALHADO: Mostra TODOS os campos e valores retornados
    System.out.println("=== CAMPOS RETORNADOS DO BACKEND ===");
    dto.forEach((key, value) -> {
        String valorStr = value != null ? value.toString() : "null";
        System.out.println(String.format("  %-40s = %s", key, 
            valorStr.length() > 100 ? valorStr.substring(0, 100) + "..." : valorStr));
    });
    System.out.println("=== FIM CAMPOS RETORNADOS ===");
    
    // Carrega TODOS os campos do PAEE de uma vez
        carregarTodosOsCamposDoDTO(dto); 
    
    System.out.println("=== Fim carregarPaeeExistente ===");
    System.out.println("FormData após carregamento:");
    System.out.println("  resumoCaso: " + (formData.resumoCaso != null ? "presente" : "null"));
    System.out.println("  desenvolvimentoMotoresPsicomotoresDificuldades: " + (formData.desenvolvimentoMotoresPsicomotoresDificuldades != null ? "presente" : "null"));
    System.out.println("  dificuldadesRaciocinio: " + (formData.dificuldadesRaciocinio != null ? "presente" : "null"));
    System.out.println("  objetivosAEE: " + (formData.objetivosAEE != null ? "presente" : "null"));
    }

        /**
         * Preenche o formData com os dados vindos do backend (dto).
         */
        private void carregarTodosOsCamposDoDTO(java.util.Map<String, Object> dto) {
            if (dto == null) return;
            formData.resumoCaso = getString(dto, "resumoCaso");
            formData.dificuldadesMotoresPsicomotores = getString(dto, "dificuldadesMotoresPsicomotores");
            formData.dificuldadesCognitivo = getString(dto, "dificuldadesCognitivo");
            formData.dificuldadesSensorial = getString(dto, "dificuldadesSensorial");
            formData.dificuldadesLinguagemComunicacao = getString(dto, "dificuldadesLinguagemComunicacao");
            formData.dificuldadesFamiliar = getString(dto, "dificuldadesFamiliar");
            formData.dificuldadesAfetivoInterpessoais = getString(dto, "dificuldadesAfetivoInterpessoais");
            formData.dificuldadesRaciocinioLogicoMatematico = getString(dto, "dificuldadesRaciocinioLogicoMatematico");
            formData.dificuldadesAVAs = getString(dto, "dificuldadesAVAs");
            formData.desenvolvimentoMotoresPsicomotoresDificuldades = getString(dto, "desenvolvimentoMotoresPsicomotoresDificuldades");
            formData.desenvolvimentoMotoresPsicomotoresIntervencoes = getString(dto, "desenvolvimentoMotoresPsicomotoresIntervencoes");
            formData.comunicacaoLinguagemDificuldades = getString(dto, "comunicacaoLinguagemDificuldades");
            formData.comunicacaoLinguagemIntervencoes = getString(dto, "comunicacaoLinguagemIntervencoes");
            formData.dificuldadesRaciocinio = getString(dto, "dificuldadesRaciocinio");
            formData.intervencoesRaciocinio = getString(dto, "intervencoesRaciocinio");
            formData.dificuldadesAtencao = getString(dto, "dificuldadesAtencao");
            formData.intervencoesAtencao = getString(dto, "intervencoesAtencao");
            formData.dificuldadesMemoria = getString(dto, "dificuldadesMemoria");
            formData.intervencoesMemoria = getString(dto, "intervencoesMemoria");
            formData.dificuldadesPercepcao = getString(dto, "dificuldadesPercepcao");
            formData.intervencoesPercepcao = getString(dto, "intervencoesPercepcao");
            formData.dificuldadesSociabilidade = getString(dto, "dificuldadesSociabilidade");
            formData.intervencoesSociabilidade = getString(dto, "intervencoesSociabilidade");
            formData.dificuldadesAVA = getString(dto, "dificuldadesAVA");
            formData.intervencoesAVA = getString(dto, "intervencoesAVA");
            formData.objetivosAEE = getString(dto, "objetivosAEE");
            formData.envAEE = getString(dto, "envAEE");
            formData.envPsicologo = getString(dto, "envPsicologo");
            formData.envFisioterapeuta = getString(dto, "envFisioterapeuta");
            formData.envPsicopedagogo = getString(dto, "envPsicopedagogo");
            formData.envTO = getString(dto, "envTO");
            formData.envEducacaoFisica = getString(dto, "envEducacaoFisica");
            formData.envEstimulacaoPrecoce = getString(dto, "envEstimulacaoPrecoce");
        }

        /**
         * Helper para converter Object em String.
         */
        private String getString(java.util.Map<String, Object> dto, String key) {
            Object value = dto.get(key);
            return value != null ? value.toString() : null;
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

    public void setSomenteLeitura(boolean sl) {
        this.somenteLeitura = sl;
        if (sl) {
            aplicarModoSomenteLeitura();
        }
    }

    private void aplicarModoSomenteLeitura() {
        if (anamnese == null) return;
        
        System.out.println("Aplicando modo somente leitura para PAEE step " + currentStep);
        
        // Desabilita/remove o botão Concluir
        javafx.scene.control.Button concluirButton = (javafx.scene.control.Button) anamnese.lookup("#concluirButton");
        if (concluirButton != null) {
            concluirButton.setDisable(true);
            concluirButton.setVisible(false);
            concluirButton.setManaged(false);
        }
        
        // Aplica a desabilitação em todos os campos
        disableInputs(anamnese);
        
        // Desabilita campos específicos baseados no currentStep
        desabilitarCamposPorStep();
    }

    public void verPAEE() {
        setSomenteLeitura(true);
    }

    private void disableInputs(javafx.scene.Parent root) {
        if (root == null) return;
        
        System.out.println("Desabilitando inputs para PAEE: " + root.getClass().getSimpleName());
        
        // Percorre todos os nós da hierarquia
        for (javafx.scene.Node node : root.getChildrenUnmodifiable()) {
            if (node instanceof javafx.scene.control.TextInputControl tic) {
                tic.setEditable(false);
                tic.setStyle("-fx-background-color: #f0f0f0; -fx-text-fill: #666;");
                System.out.println("Desabilitado TextInputControl: " + tic.getId());
            } else if (node instanceof javafx.scene.control.ComboBox<?> cb) {
                cb.setDisable(true);
                cb.setStyle("-fx-opacity: 1; -fx-background-color: #f0f0f0;");
                System.out.println("Desabilitado ComboBox: " + cb.getId());
            } else if (node instanceof javafx.scene.control.ChoiceBox<?> ch) {
                ch.setDisable(true);
                ch.setStyle("-fx-opacity: 1; -fx-background-color: #f0f0f0;");
                System.out.println("Desabilitado ChoiceBox: " + ch.getId());
            } else if (node instanceof javafx.scene.control.Button btn) {
                // Mantém habilitados apenas:
                // 1. Botões de navegação (cancelar, voltar)
                // 2. Botões de etapas
                // 3. Botões de navegação geral
                String btnId = btn.getId();
                if (btnId != null) {
                    // Botões que devem permanecer habilitados
                    boolean manterHabilitado = 
                        btnId.equals("cancelarButton") || 
                        btnId.equals("voltarButton") ||
                        btnId.equals("seguinteButton") || // Botão "Seguinte" das telas intermediárias
                        btnId.equals("turmasButton") ||
                        btnId.equals("alunosButton") ||
                        btnId.equals("sairButton") ||
                        btnId.contains("paee"); // Botões específicos do PAEE
                    
                    if (!manterHabilitado) {
                        btn.setDisable(true);
                        btn.setStyle("-fx-opacity: 0.5;");
                        System.out.println("Desabilitado botão: " + btnId);
                    } else {
                        System.out.println("Mantido habilitado botão: " + btnId);
                    }
                }
            } else if (node instanceof javafx.scene.Parent p) {
                disableInputs(p);
            }
        }
    }

    private void desabilitarCamposPorStep() {
        // Desabilita campos específicos baseados no currentStep
        
        if (currentStep == 1) {
            // Campos da tela 1
            if (resumoCaso != null) {
                resumoCaso.setEditable(false);
                resumoCaso.setStyle("-fx-background-color: #f0f0f0;");
            }
            if (dificuldadesMotoresPsicomotoresCb != null) dificuldadesMotoresPsicomotoresCb.setDisable(true);
            if (dificuldadesCognitivoCb != null) dificuldadesCognitivoCb.setDisable(true);
            if (dificuldadesSensorialCb != null) dificuldadesSensorialCb.setDisable(true);
            if (dificuldadesLinguagemComunicacaoCb != null) dificuldadesLinguagemComunicacaoCb.setDisable(true);
            if (dificuldadesFamiliarCb != null) dificuldadesFamiliarCb.setDisable(true);
            if (dificuldadesAfetivoInterpessoaisCb != null) dificuldadesAfetivoInterpessoaisCb.setDisable(true);
            if (dificuldadesRaciocinioLogicoMatematicoCb != null) dificuldadesRaciocinioLogicoMatematicoCb.setDisable(true);
            if (dificuldadesAVAsCb != null) dificuldadesAVAsCb.setDisable(true);
        } 
        else if (currentStep == 2) {
            // Campos da tela 2
            if (desenvolvimentoMotoresPsicomotoresDificuldadesTa != null) {
                desenvolvimentoMotoresPsicomotoresDificuldadesTa.setEditable(false);
                desenvolvimentoMotoresPsicomotoresDificuldadesTa.setStyle("-fx-background-color: #f0f0f0;");
            }
            if (desenvolvimentoMotoresPsicomotoresIntervencoesTa != null) {
                desenvolvimentoMotoresPsicomotoresIntervencoesTa.setEditable(false);
                desenvolvimentoMotoresPsicomotoresIntervencoesTa.setStyle("-fx-background-color: #f0f0f0;");
            }
            if (comunicacaoLinguagemDificuldadesTa != null) {
                comunicacaoLinguagemDificuldadesTa.setEditable(false);
                comunicacaoLinguagemDificuldadesTa.setStyle("-fx-background-color: #f0f0f0;");
            }
            if (comunicacaoLinguagemIntervencoesTa != null) {
                comunicacaoLinguagemIntervencoesTa.setEditable(false);
                comunicacaoLinguagemIntervencoesTa.setStyle("-fx-background-color: #f0f0f0;");
            }
        } 
        else if (currentStep == 3) {
            // Campos da tela 3
            if (dificuldadesRaciocinioTa != null) {
                dificuldadesRaciocinioTa.setEditable(false);
                dificuldadesRaciocinioTa.setStyle("-fx-background-color: #f0f0f0;");
            }
            if (intervencoesRaciocinioTa != null) {
                intervencoesRaciocinioTa.setEditable(false);
                intervencoesRaciocinioTa.setStyle("-fx-background-color: #f0f0f0;");
            }
            if (dificuldadesAtencaoTa != null) {
                dificuldadesAtencaoTa.setEditable(false);
                dificuldadesAtencaoTa.setStyle("-fx-background-color: #f0f0f0;");
            }
            if (intervencoesAtencaoTa != null) {
                intervencoesAtencaoTa.setEditable(false);
                intervencoesAtencaoTa.setStyle("-fx-background-color: #f0f0f0;");
            }
        } 
        else if (currentStep == 4) {
            // Campos da tela 4
            if (dificuldadesMemoriaTa != null) {
                dificuldadesMemoriaTa.setEditable(false);
                dificuldadesMemoriaTa.setStyle("-fx-background-color: #f0f0f0;");
            }
            if (intervencoesMemoriaTa != null) {
                intervencoesMemoriaTa.setEditable(false);
                intervencoesMemoriaTa.setStyle("-fx-background-color: #f0f0f0;");
            }
            if (dificuldadesPercepcaoTa != null) {
                dificuldadesPercepcaoTa.setEditable(false);
                dificuldadesPercepcaoTa.setStyle("-fx-background-color: #f0f0f0;");
            }
            if (intervencoesPercepcaoTa != null) {
                intervencoesPercepcaoTa.setEditable(false);
                intervencoesPercepcaoTa.setStyle("-fx-background-color: #f0f0f0;");
            }
        } 
        else if (currentStep == 5) {
            // Campos da tela 5
            if (dificuldadesSociabilidadeTa != null) {
                dificuldadesSociabilidadeTa.setEditable(false);
                dificuldadesSociabilidadeTa.setStyle("-fx-background-color: #f0f0f0;");
            }
            if (intervencoesSociabilidadeTa != null) {
                intervencoesSociabilidadeTa.setEditable(false);
                intervencoesSociabilidadeTa.setStyle("-fx-background-color: #f0f0f0;");
            }
            if (dificuldadesAVATa != null) {
                dificuldadesAVATa.setEditable(false);
                dificuldadesAVATa.setStyle("-fx-background-color: #f0f0f0;");
            }
            if (intervencoesAVATa != null) {
                intervencoesAVATa.setEditable(false);
                intervencoesAVATa.setStyle("-fx-background-color: #f0f0f0;");
            }
        } 
        else if (currentStep == 6) {
            // Campos da tela 6
            if (objetivosPlano != null) {
                objetivosPlano.setEditable(false);
                objetivosPlano.setStyle("-fx-background-color: #f0f0f0;");
            }
            if (cbAEE != null) cbAEE.setDisable(true);
            if (cbPsicologo != null) cbPsicologo.setDisable(true);
            if (cbFisioterapeuta != null) cbFisioterapeuta.setDisable(true);
            if (cbPsicopedagogo != null) cbPsicopedagogo.setDisable(true);
            if (cbTO != null) cbTO.setDisable(true);
            if (cbEducacaoFisica != null) cbEducacaoFisica.setDisable(true);
            if (cbEstimulacaoPrecoce != null) cbEstimulacaoPrecoce.setDisable(true);
        }
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
        public String professorId;
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
                             String professorId,
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
            this.professorId = professorId;
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