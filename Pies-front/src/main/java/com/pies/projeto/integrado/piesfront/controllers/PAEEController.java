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
    // private boolean modoNovo = false;
    private boolean novoRegistro = false;
    private boolean somenteLeitura = false;
    private String currentPaeeId = null;

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
        // Força o preenchimento dos campos após garantir que o formData está correto
        javafx.application.Platform.runLater(() -> {
            System.out.println("=== Preenchendo campos via Platform.runLater ===");
            preencherCamposComFormData();
        });
        System.out.println("=== Fim setEducando ===");
    }

    /**
     * Define que o controller está em modo de novo cadastro.
     * Neste modo, não carrega dados existentes.
     */
    // public void setModoNovo() {
    //    this.modoNovo = true;
    //    this.formData = new PaeeFormData(); // Limpa os dados
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
        if (novo) {
            this.formData = new PaeeFormData();
        }
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

    // --- MÉTODOS AUXILIARES DE CONVERSÃO ---
    private Boolean converterSimNao(String valor) {
        if (valor == null) return false;
        return valor.equalsIgnoreCase("Sim") || valor.equalsIgnoreCase("true") || valor.equals("1");
    }

    @FXML
    private void handleConcluirAction() {
        System.out.println("=== handleConcluirAction PAEE ===");
        captureCurrentStepData(); // Garante que os dados da tela 6 foram salvos no formData

        if (somenteLeitura) {
            showValidation("Modo de visualização");
            return;
        }
        if (educando == null || educando.id() == null) {
            System.err.println("Educando inválido!");
            showValidation("Educando inválido.");
            return;
        }
        if (!validateResumo()) {
            System.err.println("Resumo do caso não preenchido!");
            showValidation("Informe o resumo do caso.");
            return;
        }

        try {
            String token = authService.getCurrentToken();
            if (token == null || token.isEmpty()) {
                showValidation("Sessão expirada.");
                return;
            }

            // --- CRIAÇÃO E MAPEAMENTO DO DTO PARA O BACKEND ---
            CreatePAEEDTO dto = new CreatePAEEDTO();

            dto.alunoId = educando.id();
            dto.professorId = authService.getProfessorId();

            // Resumo
            dto.resumoCaso = formData.resumoCaso;

            // Checkboxes (Conversão String -> Boolean)
            dto.apresentaDificuldadeMotora = converterSimNao(formData.dificuldadesMotoresPsicomotores);
            dto.apresentaDificuldadeCognitiva = converterSimNao(formData.dificuldadesCognitivo);
            dto.apresentaDificuldadeSensorial = converterSimNao(formData.dificuldadesSensorial);
            dto.apresentaDificuldadeLinguagem = converterSimNao(formData.dificuldadesLinguagemComunicacao);
            dto.apresentaDificuldadeFamiliar = converterSimNao(formData.dificuldadesFamiliar);
            dto.apresentaDificuldadeAfetiva = converterSimNao(formData.dificuldadesAfetivoInterpessoais);
            dto.apresentaDificuldadeLogica = converterSimNao(formData.dificuldadesRaciocinioLogicoMatematico);
            dto.apresentaDificuldadeAVA = converterSimNao(formData.dificuldadesAVAs);

            // Detalhes - Mapeando nomes do Form para nomes do Backend
            dto.desenvolvimentoMotorDificuldades = formData.desenvolvimentoMotoresPsicomotoresDificuldades;
            dto.desenvolvimentoMotorIntervencoes = formData.desenvolvimentoMotoresPsicomotoresIntervencoes;

            dto.comunicacaoLinguagemDificuldades = formData.comunicacaoLinguagemDificuldades;
            dto.comunicacaoLinguagemIntervencoes = formData.comunicacaoLinguagemIntervencoes;

            dto.raciocinioLogicoDificuldades = formData.dificuldadesRaciocinio;
            dto.raciocinioLogicoIntervencoes = formData.intervencoesRaciocinio;

            dto.atencaoConcentracaoDificuldades = formData.dificuldadesAtencao;
            dto.atencaoConcentracaoIntervencoes = formData.intervencoesAtencao;

            dto.memoriaDificuldades = formData.dificuldadesMemoria;
            dto.memoriaIntervencoes = formData.intervencoesMemoria;

            dto.percepcaoDificuldades = formData.dificuldadesPercepcao;
            dto.percepcaoIntervencoes = formData.intervencoesPercepcao;

            dto.sociabilidadeDificuldades = formData.dificuldadesSociabilidade;
            dto.sociabilidadeIntervencoes = formData.intervencoesSociabilidade;

            dto.avasDificuldades = formData.dificuldadesAVA;
            dto.avasIntervencoes = formData.intervencoesAVA;

            // Final
            dto.objetivosGerais = formData.objetivosAEE;
            
            dto.apoioAEE = converterSimNao(formData.envAEE);
            dto.apoioPsicologo = converterSimNao(formData.envPsicologo);
            dto.apoioFisioterapeuta = converterSimNao(formData.envFisioterapeuta);
            dto.apoioPsicopedagogo = converterSimNao(formData.envPsicopedagogo);
            dto.apoioTO = converterSimNao(formData.envTO);
            dto.apoioEdFisica = converterSimNao(formData.envEducacaoFisica);
            dto.apoioEstimulacaoPrecoce = converterSimNao(formData.envEstimulacaoPrecoce);

            System.out.println("Enviando PAEE para o backend...");
            System.out.println("ResumoCaso len: " + (dto.resumoCaso != null ? dto.resumoCaso.length() : "null"));
            System.out.println("DevMotor Dific len: " + (dto.desenvolvimentoMotorDificuldades != null ? dto.desenvolvimentoMotorDificuldades.length() : "null"));
            System.out.println("DevMotor Interv len: " + (dto.desenvolvimentoMotorIntervencoes != null ? dto.desenvolvimentoMotorIntervencoes.length() : "null"));
            System.out.println("Comunicacao Dific len: " + (dto.comunicacaoLinguagemDificuldades != null ? dto.comunicacaoLinguagemDificuldades.length() : "null"));
            System.out.println("Comunicacao Interv len: " + (dto.comunicacaoLinguagemIntervencoes != null ? dto.comunicacaoLinguagemIntervencoes.length() : "null"));
            
            // Certifique-se que seu AuthService foi atualizado para aceitar este objeto CreatePAEEDTO
            boolean ok;
            if (!novoRegistro && currentPaeeId != null && !currentPaeeId.isEmpty()) {
                System.out.println("Atualizando PAEE existente. ID: " + currentPaeeId);
                ok = authService.atualizarPAEE(currentPaeeId, dto);
            } else {
                ok = authService.criarPAEE(dto);
            }
            
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
            Janelas.carregarTela(new javafx.event.ActionEvent(anamnese, null), resource, "PAEE", controller -> {
                if (controller instanceof PAEEController c) {
                    c.setNovoRegistro(novoAtual);
                    c.currentStep = step;
                    c.setFormData(formData);
                    c.setEducando(educando);
                    c.setSomenteLeitura(this.somenteLeitura);
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
    
    private Object getFirst(java.util.Map<String, Object> m, String... keys) {
        if (m == null || keys == null) return null;
        for (String k : keys) {
            if (m.containsKey(k)) {
                Object v = m.get(k);
                if (v != null) return v;
            }
        }
        return null;
    }
    
    private String getString(java.util.Map<String, Object> m, String... keys) {
        Object v = getFirst(m, keys);
        if (v == null) return null;
        return v instanceof String ? (String) v : v.toString();
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
        System.out.println("Valores completos do DTO:");
        dto.forEach((key, value) -> {
            String valorStr = value != null ? value.toString() : "null";
            System.out.println("  " + key + " = " + valorStr);
        });
        System.out.println("--- Fim do log dos dados do PAEE ---");
        Object idObj = dto.get("id");
        if (idObj instanceof String sId) {
            currentPaeeId = sId;
            System.out.println("PAEE atual ID: " + currentPaeeId);
        }
        Object o;
        o = getFirst(dto, "resumoCaso");
        if (o instanceof String s) {
            formData.resumoCaso = s;
            System.out.println("resumoCaso carregado: " + s.substring(0, Math.min(50, s.length())) + "...");
        }
        o = getFirst(dto, "apresentaDificuldadeMotora", "dificuldadesMotoresPsicomotores");
        formData.dificuldadesMotoresPsicomotores = toSimNao(o);
        o = getFirst(dto, "apresentaDificuldadeCognitiva", "dificuldadesCognitivo");
        formData.dificuldadesCognitivo = toSimNao(o);
        o = getFirst(dto, "apresentaDificuldadeSensorial", "dificuldadesSensorial");
        formData.dificuldadesSensorial = toSimNao(o);
        o = getFirst(dto, "apresentaDificuldadeLinguagem", "dificuldadesLinguagemComunicacao");
        formData.dificuldadesLinguagemComunicacao = toSimNao(o);
        o = getFirst(dto, "apresentaDificuldadeFamiliar", "dificuldadesFamiliar");
        formData.dificuldadesFamiliar = toSimNao(o);
        o = getFirst(dto, "apresentaDificuldadeAfetiva", "dificuldadesAfetivoInterpessoais");
        formData.dificuldadesAfetivoInterpessoais = toSimNao(o);
        o = getFirst(dto, "apresentaDificuldadeLogica", "dificuldadesRaciocinioLogicoMatematico");
        formData.dificuldadesRaciocinioLogicoMatematico = toSimNao(o);
        o = getFirst(dto, "apresentaDificuldadeAVA", "dificuldadesAVAs");
        formData.dificuldadesAVAs = toSimNao(o);
        String s;
        s = getString(dto, "desenvolvimentoMotorDificuldades", "desenvolvimentoMotoresPsicomotoresDificuldades");
        if (s != null) {
            formData.desenvolvimentoMotoresPsicomotoresDificuldades = s;
            System.out.println("desenvolvimentoMotoresPsicomotoresDificuldades carregado: " + s.length() + " chars");
        }
        s = getString(dto, "desenvolvimentoMotorIntervencoes", "desenvolvimentoMotoresPsicomotoresIntervencoes");
        if (s != null) {
            formData.desenvolvimentoMotoresPsicomotoresIntervencoes = s;
            System.out.println("desenvolvimentoMotoresPsicomotoresIntervencoes carregado: " + s.length() + " chars");
        }
        s = getString(dto, "comunicacaoLinguagemDificuldades");
        if (s != null) formData.comunicacaoLinguagemDificuldades = s;
        s = getString(dto, "comunicacaoLinguagemIntervencoes");
        if (s != null) formData.comunicacaoLinguagemIntervencoes = s;
        s = getString(dto, "raciocinioLogicoDificuldades", "dificuldadesRaciocinio");
        if (s != null) formData.dificuldadesRaciocinio = s;
        s = getString(dto, "raciocinioLogicoIntervencoes", "intervencoesRaciocinio");
        if (s != null) formData.intervencoesRaciocinio = s;
        s = getString(dto, "atencaoConcentracaoDificuldades", "dificuldadesAtencao");
        if (s != null) formData.dificuldadesAtencao = s;
        s = getString(dto, "atencaoConcentracaoIntervencoes", "intervencoesAtencao");
        if (s != null) formData.intervencoesAtencao = s;
        s = getString(dto, "memoriaDificuldades", "dificuldadesMemoria");
        if (s != null) formData.dificuldadesMemoria = s;
        s = getString(dto, "memoriaIntervencoes", "intervencoesMemoria");
        if (s != null) formData.intervencoesMemoria = s;
        s = getString(dto, "percepcaoDificuldades", "dificuldadesPercepcao");
        if (s != null) formData.dificuldadesPercepcao = s;
        s = getString(dto, "percepcaoIntervencoes", "intervencoesPercepcao");
        if (s != null) formData.intervencoesPercepcao = s;
        s = getString(dto, "sociabilidadeDificuldades", "dificuldadesSociabilidade");
        if (s != null) formData.dificuldadesSociabilidade = s;
        s = getString(dto, "sociabilidadeIntervencoes", "intervencoesSociabilidade");
        if (s != null) formData.intervencoesSociabilidade = s;
        s = getString(dto, "avasDificuldades", "dificuldadesAVA");
        if (s != null) formData.dificuldadesAVA = s;
        s = getString(dto, "avasIntervencoes", "intervencoesAVA");
        if (s != null) formData.intervencoesAVA = s;
        s = getString(dto, "objetivosGerais", "objetivosAEE");
        if (s != null) formData.objetivosAEE = s;
        o = getFirst(dto, "apoioAEE", "envAEE");
        formData.envAEE = toSimNao(o);
        o = getFirst(dto, "apoioPsicologo", "envPsicologo");
        formData.envPsicologo = toSimNao(o);
        o = getFirst(dto, "apoioFisioterapeuta", "envFisioterapeuta");
        formData.envFisioterapeuta = toSimNao(o);
        o = getFirst(dto, "apoioPsicopedagogo", "envPsicopedagogo");
        formData.envPsicopedagogo = toSimNao(o);
        o = getFirst(dto, "apoioTO", "envTO");
        formData.envTO = toSimNao(o);
        o = getFirst(dto, "apoioEdFisica", "envEducacaoFisica");
        formData.envEducacaoFisica = toSimNao(o);
        o = getFirst(dto, "apoioEstimulacaoPrecoce", "envEstimulacaoPrecoce");
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

    public void setSomenteLeitura(boolean sl) {
        this.somenteLeitura = sl;
        aplicarSomenteLeitura();
    }

    private void aplicarSomenteLeitura() {
        if (!somenteLeitura) return;
        javafx.application.Platform.runLater(() -> {
            disableInputs(anamnese);
        });
    }

    private void disableInputs(javafx.scene.Parent root) {
        if (root == null) return;
        for (javafx.scene.Node node : root.getChildrenUnmodifiable()) {
            if (node instanceof javafx.scene.control.TextInputControl tic) {
                tic.setEditable(false);
            } else if (node instanceof javafx.scene.control.CheckBox cb) {
                cb.setDisable(true);
            } else if (node instanceof javafx.scene.control.ChoiceBox<?> ch) {
                ch.setDisable(true);
            } else if (node instanceof javafx.scene.Parent p) {
                disableInputs(p);
            }
        }
    }

    // --- NOVA CLASSE DTO COMPATÍVEL COM O BACKEND SPRING BOOT ---
    public static class CreatePAEEDTO {
        public String alunoId;
        public String professorId;
        public String resumoCaso;

        // Checkboxes Iniciais (Boolean)
        public Boolean apresentaDificuldadeMotora;
        public Boolean apresentaDificuldadeCognitiva;
        public Boolean apresentaDificuldadeSensorial;
        public Boolean apresentaDificuldadeLinguagem;
        public Boolean apresentaDificuldadeFamiliar;
        public Boolean apresentaDificuldadeAfetiva;
        public Boolean apresentaDificuldadeLogica;
        public Boolean apresentaDificuldadeAVA;

        // Detalhes (Strings)
        public String desenvolvimentoMotorDificuldades;
        public String desenvolvimentoMotorIntervencoes;
        
        public String comunicacaoLinguagemDificuldades;
        public String comunicacaoLinguagemIntervencoes;
        
        public String raciocinioLogicoDificuldades;
        public String raciocinioLogicoIntervencoes;
        
        public String atencaoConcentracaoDificuldades;
        public String atencaoConcentracaoIntervencoes;
        
        public String memoriaDificuldades;
        public String memoriaIntervencoes;
        
        public String percepcaoDificuldades;
        public String percepcaoIntervencoes;
        
        public String sociabilidadeDificuldades;
        public String sociabilidadeIntervencoes;
        
        public String avasDificuldades;
        public String avasIntervencoes;

        // Final
        public String objetivosGerais;
        public Boolean apoioAEE;
        public Boolean apoioPsicologo;
        public Boolean apoioFisioterapeuta;
        public Boolean apoioPsicopedagogo;
        public Boolean apoioTO;
        public Boolean apoioEdFisica;
        public Boolean apoioEstimulacaoPrecoce;

        public CreatePAEEDTO() {}
    }

    // Mantida para preservar o estado do formulário (bindings dos campos de texto)
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
