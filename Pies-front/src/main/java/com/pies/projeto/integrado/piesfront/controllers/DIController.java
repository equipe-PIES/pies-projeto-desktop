package com.pies.projeto.integrado.piesfront.controllers;
import com.pies.projeto.integrado.piesfront.dto.EducandoDTO;
import com.pies.projeto.integrado.piesfront.services.AtendimentoFlowService;
import com.pies.projeto.integrado.piesfront.services.AuthService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import java.net.URL;
import java.util.ResourceBundle;

public class DIController implements Initializable {
    @FXML private BorderPane anamnese;
    @FXML private Label indicadorDeTela;
    @FXML private Label validationMsg;

    @FXML private CheckBox falaSeuNome;
    @FXML private CheckBox dizDataNascimento;
    @FXML private CheckBox lePalavras;
    @FXML private CheckBox informaNumeroTelefone;
    @FXML private CheckBox emiteRespostas;
    @FXML private CheckBox transmiteRecado;
    @FXML private CheckBox informaEndereco;
    @FXML private CheckBox informaNomePais;
    @FXML private CheckBox compreendeOrdens;
    @FXML private CheckBox expoeIdeias;
    @FXML private CheckBox recontaHistorias;
    @FXML private CheckBox usaSistemaCA;
    @FXML private CheckBox relataFatosComCoerencia;
    @FXML private CheckBox pronunciaLetrasAlfabeto;
    @FXML private CheckBox verbalizaMusicas;
    @FXML private CheckBox interpretaHistorias;
    @FXML private CheckBox formulaPerguntas;
    @FXML private CheckBox utilizaGestosParaSeComunicar;

    @FXML private CheckBox demonstraCooperacao;
    @FXML private CheckBox timidoInseguro;
    @FXML private CheckBox fazBirra;
    @FXML private CheckBox solicitaOfereceAjuda;
    @FXML private CheckBox riComFrequencia;
    @FXML private CheckBox compartilhaOQueESeu;
    @FXML private CheckBox demonstraAmorGentilezaAtencao;
    @FXML private CheckBox choraComFrequencia;
    @FXML private CheckBox interageComColegas;

    @FXML private CheckBox captaDetalhesGravura;
    @FXML private CheckBox reconheceVozes;
    @FXML private CheckBox reconheceCancoes;
    @FXML private CheckBox percebeTexturas;
    @FXML private CheckBox percepcaoCores;
    @FXML private CheckBox discriminaSons;
    @FXML private CheckBox discriminaOdores;
    @FXML private CheckBox aceitaDiferentesTexturas;
    @FXML private CheckBox percepcaoFormas;
    @FXML private CheckBox identificaDirecaoSom;
    @FXML private CheckBox percebeDiscriminaSabores;
    @FXML private CheckBox acompanhaFocoLuminoso;

    @FXML private CheckBox movimentoPincaComTesoura;
    @FXML private CheckBox amassaPapel;
    @FXML private CheckBox caiComFacilidade;
    @FXML private CheckBox encaixaPecas;
    @FXML private CheckBox recorta;
    @FXML private CheckBox unePontos;
    @FXML private CheckBox consegueCorrer;
    @FXML private CheckBox empilha;
    @FXML private CheckBox agitacaoMotora;
    @FXML private CheckBox andaLinhaReta;
    @FXML private CheckBox sobeDesceEscadas;
    @FXML private CheckBox arremessaBola;

    @FXML private CheckBox usaSanitarioSemAjuda;
    @FXML private CheckBox penteiaSeSo;
    @FXML private CheckBox consegueVestirDespirSe;
    @FXML private CheckBox lavaSecaAsMaos;
    @FXML private CheckBox banhoComModeracao;
    @FXML private CheckBox calcaSeSo;
    @FXML private CheckBox reconheceRoupas;
    @FXML private CheckBox abreFechaTorneira;
    @FXML private CheckBox escovaDentesSemAjuda;
    @FXML private CheckBox consegueDarNosLacos;
    @FXML private CheckBox abotoaDesabotoaRoupas;
    @FXML private CheckBox identificaPartesDoCorpo;

    @FXML private CheckBox garatujas;
    @FXML private CheckBox preSilabico;
    @FXML private CheckBox silabico;
    @FXML private CheckBox silabicoAlfabetico;
    @FXML private CheckBox alfabetico;

    @FXML private TextArea observacoes;

    private int currentStep = 1;
    private EducandoDTO educando;
    private final AuthService authService = AuthService.getInstance();
    private DIFormData formData = new DIFormData();
    private boolean novoRegistro = false;
    private String diagnosticoId = null;

    public void setEducando(EducandoDTO educando) {
        this.educando = educando;
        atualizarIndicadorDeTela();
        if (!novoRegistro) {
            carregarExistente();
        }
        preencherCampos();
    }

    public void setNovoRegistro(boolean novo) {
        this.novoRegistro = novo;
        if (novo) {
            this.formData = new DIFormData();
            this.diagnosticoId = null;
        }
    }

    public void setFormData(DIFormData data) {
        if (data != null) {
            this.formData = data;
            preencherCampos();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (validationMsg != null) {
            validationMsg.setVisible(false);
            validationMsg.setManaged(true);
        }
        detectarEtapa(url);
        atualizarIndicadorDeTela();
        preencherCampos();
    }

    @FXML private void handleTurmasButtonAction() {
        navegar("/com/pies/projeto/integrado/piesfront/screens/tela-inicio-professor.fxml", null);
    }

    @FXML private void handleSairButtonAction() {
        authService.logout();
        navegar("/com/pies/projeto/integrado/piesfront/screens/tela-de-login.fxml", null);
    }

    @FXML private void handleCancelAction() {
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
            currentStage.show();
        } catch (Exception e) {
            handleSairButtonAction();
        }
    }

    @FXML private void handleBackAction() {
        captureCurrentStepData();
        if (currentStep <= 1) {
            handleCancelAction();
            return;
        }
        if (currentStep == 2) abrirDI("/com/pies/projeto/integrado/piesfront/screens/diagnostico-1.fxml", 1);
        else if (currentStep == 3) abrirDI("/com/pies/projeto/integrado/piesfront/screens/diagnostico-2.fxml", 2);
    }

    @FXML private void handleGoToDI2() {
        captureCurrentStepData();
        abrirDI("/com/pies/projeto/integrado/piesfront/screens/diagnostico-2.fxml", 2);
    }

    @FXML private void handleGoToDI3() {
        captureCurrentStepData();
        abrirDI("/com/pies/projeto/integrado/piesfront/screens/diagnostico-3.fxml", 3);
    }

    @FXML private void handleConcluirAction() {
        captureCurrentStepData();
        if (educando == null || educando.id() == null) {
            showValidation("Educando inválido.");
            return;
        }
        
        System.out.println("=== Concluir Diagnóstico Inicial ===");
        System.out.println("Educando ID: " + educando.id());
        System.out.println("Diagnóstico ID: " + diagnosticoId);
        System.out.println("Modo: " + (diagnosticoId != null ? "Edição" : "Criação"));
        
        CreateDiagnosticoInicialDTO dto = new CreateDiagnosticoInicialDTO(
                formData.falaSeuNome,
                formData.dizDataNascimento,
                formData.lePalavras,
                formData.informaNumeroTelefone,
                formData.emiteRespostas,
                formData.transmiteRecado,
                formData.informaEndereco,
                formData.informaNomePais,
                formData.compreendeOrdens,
                formData.expoeIdeias,
                formData.recontaHistorias,
                formData.usaSistemaCA,
                formData.relataFatosComCoerencia,
                formData.pronunciaLetrasAlfabeto,
                formData.verbalizaMusicas,
                formData.interpretaHistorias,
                formData.formulaPerguntas,
                formData.utilizaGestosParaSeComunicar,
                formData.demonstraCooperacao,
                formData.timidoInseguro,
                formData.fazBirra,
                formData.solicitaOfereceAjuda,
                formData.riComFrequencia,
                formData.compartilhaOQueESeu,
                formData.demonstraAmorGentilezaAtencao,
                formData.choraComFrequencia,
                formData.interageComColegas,
                formData.captaDetalhesGravura,
                formData.reconheceVozes,
                formData.reconheceCancoes,
                formData.percebeTexturas,
                formData.percepcaoCores,
                formData.discriminaSons,
                formData.discriminaOdores,
                formData.aceitaDiferentesTexturas,
                formData.percepcaoFormas,
                formData.identificaDirecaoSom,
                formData.percebeDiscriminaSabores,
                formData.acompanhaFocoLuminoso,
                formData.movimentoPincaComTesoura,
                formData.amassaPapel,
                formData.caiComFacilidade,
                formData.encaixaPecas,
                formData.recorta,
                formData.unePontos,
                formData.consegueCorrer,
                formData.empilha,
                formData.agitacaoMotora,
                formData.andaLinhaReta,
                formData.sobeDesceEscadas,
                formData.arremessaBola,
                formData.usaSanitarioSemAjuda,
                formData.penteiaSeSo,
                formData.consegueVestirDespirSe,
                formData.lavaSecaAsMaos,
                formData.banhoComModeracao,
                formData.calcaSeSo,
                formData.reconheceRoupas,
                formData.abreFechaTorneira,
                formData.escovaDentesSemAjuda,
                formData.consegueDarNosLacos,
                formData.abotoaDesabotoaRoupas,
                formData.identificaPartesDoCorpo,
                formData.garatujas,
                formData.preSilabico,
                formData.silabico,
                formData.silabicoAlfabetico,
                formData.alfabetico,
                formData.observacoes
        );
        
        boolean ok;
        try {
            if (diagnosticoId != null) {
                // Modo edição - atualizar existente
                System.out.println("Atualizando diagnóstico existente...");
                ok = authService.atualizarDiagnosticoInicial(diagnosticoId, educando.id(), dto);
            } else {
                // Modo criação - criar novo
                System.out.println("Criando novo diagnóstico...");
                ok = authService.criarDiagnosticoInicial(dto, educando.id());
            }
            System.out.println("Resultado da operação: " + ok);
        } catch (Exception e) {
            System.err.println("ERRO ao salvar diagnóstico: " + e.getMessage());
            e.printStackTrace();
            ok = false;
        }
        
        if (ok) {
            if (diagnosticoId == null) {
                AtendimentoFlowService.getInstance().concluirDI(educando.id());
            }
            showPopup("Diagnóstico Inicial " + (diagnosticoId != null ? "atualizado" : "registrado") + " com sucesso!", true);
            handleCancelAction();
        } else {
            String errorMsg = "Falha ao " + (diagnosticoId != null ? "atualizar" : "enviar") + " Diagnóstico Inicial. Verifique se o backend está rodando.";
            showPopup(errorMsg, false);
            showValidation(errorMsg);
        }
    }

    private void abrirDI(String resource, int step) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(resource));
            Parent root = loader.load();
            DIController controller = loader.getController();
            controller.setEducando(educando);
            controller.currentStep = step;
            controller.setFormData(formData);
            Stage stage;
            if (anamnese != null && anamnese.getScene() != null) {
                stage = (Stage) anamnese.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Diagnóstico Inicial");
            } else {
                stage = new Stage();
                stage.setTitle("Diagnóstico Inicial");
                stage.setScene(new Scene(root));
                stage.setResizable(false);
                stage.show();
            }
        } catch (Exception e) {
            if (validationMsg != null) {
                validationMsg.setVisible(true);
            }
        }
    }

    private void navegar(String resource, String titulo) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(resource));
            Stage currentStage = (Stage) anamnese.getScene().getWindow();
            currentStage.setScene(new Scene(root));
            if (titulo != null) currentStage.setTitle(titulo);
            currentStage.show();
        } catch (Exception e) {
        }
    }

    private void detectarEtapa(URL url) {
        if (url == null) return;
        String p = url.getPath();
        if (p.contains("diagnostico-1.fxml")) currentStep = 1;
        else if (p.contains("diagnostico-2.fxml")) currentStep = 2;
        else if (p.contains("diagnostico-3.fxml")) currentStep = 3;
    }

    private void atualizarIndicadorDeTela() {
        if (indicadorDeTela == null) return;
        String nome = educando != null ? educando.nome() : "Aluno(a)";
        indicadorDeTela.setText("Diagnóstico Inicial do aluno(a) " + nome);
    }

    private void captureCurrentStepData() {
        if (currentStep == 1) {
            formData.falaSeuNome = isSelected(falaSeuNome, formData.falaSeuNome);
            formData.dizDataNascimento = isSelected(dizDataNascimento, formData.dizDataNascimento);
            formData.lePalavras = isSelected(lePalavras, formData.lePalavras);
            formData.informaNumeroTelefone = isSelected(informaNumeroTelefone, formData.informaNumeroTelefone);
            formData.emiteRespostas = isSelected(emiteRespostas, formData.emiteRespostas);
            formData.transmiteRecado = isSelected(transmiteRecado, formData.transmiteRecado);
            formData.informaEndereco = isSelected(informaEndereco, formData.informaEndereco);
            formData.informaNomePais = isSelected(informaNomePais, formData.informaNomePais);
            formData.compreendeOrdens = isSelected(compreendeOrdens, formData.compreendeOrdens);
            formData.expoeIdeias = isSelected(expoeIdeias, formData.expoeIdeias);
            formData.recontaHistorias = isSelected(recontaHistorias, formData.recontaHistorias);
            formData.usaSistemaCA = isSelected(usaSistemaCA, formData.usaSistemaCA);
            formData.relataFatosComCoerencia = isSelected(relataFatosComCoerencia, formData.relataFatosComCoerencia);
            formData.pronunciaLetrasAlfabeto = isSelected(pronunciaLetrasAlfabeto, formData.pronunciaLetrasAlfabeto);
            formData.verbalizaMusicas = isSelected(verbalizaMusicas, formData.verbalizaMusicas);
            formData.interpretaHistorias = isSelected(interpretaHistorias, formData.interpretaHistorias);
            formData.formulaPerguntas = isSelected(formulaPerguntas, formData.formulaPerguntas);
            formData.utilizaGestosParaSeComunicar = isSelected(utilizaGestosParaSeComunicar, formData.utilizaGestosParaSeComunicar);
            formData.demonstraCooperacao = isSelected(demonstraCooperacao, formData.demonstraCooperacao);
            formData.timidoInseguro = isSelected(timidoInseguro, formData.timidoInseguro);
            formData.fazBirra = isSelected(fazBirra, formData.fazBirra);
            formData.solicitaOfereceAjuda = isSelected(solicitaOfereceAjuda, formData.solicitaOfereceAjuda);
            formData.riComFrequencia = isSelected(riComFrequencia, formData.riComFrequencia);
            formData.compartilhaOQueESeu = isSelected(compartilhaOQueESeu, formData.compartilhaOQueESeu);
            formData.demonstraAmorGentilezaAtencao = isSelected(demonstraAmorGentilezaAtencao, formData.demonstraAmorGentilezaAtencao);
            formData.choraComFrequencia = isSelected(choraComFrequencia, formData.choraComFrequencia);
            formData.interageComColegas = isSelected(interageComColegas, formData.interageComColegas);
        } else if (currentStep == 2) {
            formData.captaDetalhesGravura = isSelected(captaDetalhesGravura, formData.captaDetalhesGravura);
            formData.reconheceVozes = isSelected(reconheceVozes, formData.reconheceVozes);
            formData.reconheceCancoes = isSelected(reconheceCancoes, formData.reconheceCancoes);
            formData.percebeTexturas = isSelected(percebeTexturas, formData.percebeTexturas);
            formData.percepcaoCores = isSelected(percepcaoCores, formData.percepcaoCores);
            formData.discriminaSons = isSelected(discriminaSons, formData.discriminaSons);
            formData.discriminaOdores = isSelected(discriminaOdores, formData.discriminaOdores);
            formData.aceitaDiferentesTexturas = isSelected(aceitaDiferentesTexturas, formData.aceitaDiferentesTexturas);
            formData.percepcaoFormas = isSelected(percepcaoFormas, formData.percepcaoFormas);
            formData.identificaDirecaoSom = isSelected(identificaDirecaoSom, formData.identificaDirecaoSom);
            formData.percebeDiscriminaSabores = isSelected(percebeDiscriminaSabores, formData.percebeDiscriminaSabores);
            formData.acompanhaFocoLuminoso = isSelected(acompanhaFocoLuminoso, formData.acompanhaFocoLuminoso);
            formData.movimentoPincaComTesoura = isSelected(movimentoPincaComTesoura, formData.movimentoPincaComTesoura);
            formData.amassaPapel = isSelected(amassaPapel, formData.amassaPapel);
            formData.caiComFacilidade = isSelected(caiComFacilidade, formData.caiComFacilidade);
            formData.encaixaPecas = isSelected(encaixaPecas, formData.encaixaPecas);
            formData.recorta = isSelected(recorta, formData.recorta);
            formData.unePontos = isSelected(unePontos, formData.unePontos);
            formData.consegueCorrer = isSelected(consegueCorrer, formData.consegueCorrer);
            formData.empilha = isSelected(empilha, formData.empilha);
            formData.agitacaoMotora = isSelected(agitacaoMotora, formData.agitacaoMotora);
            formData.andaLinhaReta = isSelected(andaLinhaReta, formData.andaLinhaReta);
            formData.sobeDesceEscadas = isSelected(sobeDesceEscadas, formData.sobeDesceEscadas);
            formData.arremessaBola = isSelected(arremessaBola, formData.arremessaBola);
        } else if (currentStep == 3) {
            formData.usaSanitarioSemAjuda = isSelected(usaSanitarioSemAjuda, formData.usaSanitarioSemAjuda);
            formData.penteiaSeSo = isSelected(penteiaSeSo, formData.penteiaSeSo);
            formData.consegueVestirDespirSe = isSelected(consegueVestirDespirSe, formData.consegueVestirDespirSe);
            formData.lavaSecaAsMaos = isSelected(lavaSecaAsMaos, formData.lavaSecaAsMaos);
            formData.banhoComModeracao = isSelected(banhoComModeracao, formData.banhoComModeracao);
            formData.calcaSeSo = isSelected(calcaSeSo, formData.calcaSeSo);
            formData.reconheceRoupas = isSelected(reconheceRoupas, formData.reconheceRoupas);
            formData.abreFechaTorneira = isSelected(abreFechaTorneira, formData.abreFechaTorneira);
            formData.escovaDentesSemAjuda = isSelected(escovaDentesSemAjuda, formData.escovaDentesSemAjuda);
            formData.consegueDarNosLacos = isSelected(consegueDarNosLacos, formData.consegueDarNosLacos);
            formData.abotoaDesabotoaRoupas = isSelected(abotoaDesabotoaRoupas, formData.abotoaDesabotoaRoupas);
            formData.identificaPartesDoCorpo = isSelected(identificaPartesDoCorpo, formData.identificaPartesDoCorpo);
            formData.garatujas = isSelected(garatujas, formData.garatujas);
            formData.preSilabico = isSelected(preSilabico, formData.preSilabico);
            formData.silabico = isSelected(silabico, formData.silabico);
            formData.silabicoAlfabetico = isSelected(silabicoAlfabetico, formData.silabicoAlfabetico);
            formData.alfabetico = isSelected(alfabetico, formData.alfabetico);
            formData.observacoes = getText(observacoes, formData.observacoes);
        }
    }

    private void preencherCampos() {
        if (currentStep == 1) {
            setSelected(falaSeuNome, formData.falaSeuNome);
            setSelected(dizDataNascimento, formData.dizDataNascimento);
            setSelected(lePalavras, formData.lePalavras);
            setSelected(informaNumeroTelefone, formData.informaNumeroTelefone);
            setSelected(emiteRespostas, formData.emiteRespostas);
            setSelected(transmiteRecado, formData.transmiteRecado);
            setSelected(informaEndereco, formData.informaEndereco);
            setSelected(informaNomePais, formData.informaNomePais);
            setSelected(compreendeOrdens, formData.compreendeOrdens);
            setSelected(expoeIdeias, formData.expoeIdeias);
            setSelected(recontaHistorias, formData.recontaHistorias);
            setSelected(usaSistemaCA, formData.usaSistemaCA);
            setSelected(relataFatosComCoerencia, formData.relataFatosComCoerencia);
            setSelected(pronunciaLetrasAlfabeto, formData.pronunciaLetrasAlfabeto);
            setSelected(verbalizaMusicas, formData.verbalizaMusicas);
            setSelected(interpretaHistorias, formData.interpretaHistorias);
            setSelected(formulaPerguntas, formData.formulaPerguntas);
            setSelected(utilizaGestosParaSeComunicar, formData.utilizaGestosParaSeComunicar);
            setSelected(demonstraCooperacao, formData.demonstraCooperacao);
            setSelected(timidoInseguro, formData.timidoInseguro);
            setSelected(fazBirra, formData.fazBirra);
            setSelected(solicitaOfereceAjuda, formData.solicitaOfereceAjuda);
            setSelected(riComFrequencia, formData.riComFrequencia);
            setSelected(compartilhaOQueESeu, formData.compartilhaOQueESeu);
            setSelected(demonstraAmorGentilezaAtencao, formData.demonstraAmorGentilezaAtencao);
            setSelected(choraComFrequencia, formData.choraComFrequencia);
            setSelected(interageComColegas, formData.interageComColegas);
        } else if (currentStep == 2) {
            setSelected(captaDetalhesGravura, formData.captaDetalhesGravura);
            setSelected(reconheceVozes, formData.reconheceVozes);
            setSelected(reconheceCancoes, formData.reconheceCancoes);
            setSelected(percebeTexturas, formData.percebeTexturas);
            setSelected(percepcaoCores, formData.percepcaoCores);
            setSelected(discriminaSons, formData.discriminaSons);
            setSelected(discriminaOdores, formData.discriminaOdores);
            setSelected(aceitaDiferentesTexturas, formData.aceitaDiferentesTexturas);
            setSelected(percepcaoFormas, formData.percepcaoFormas);
            setSelected(identificaDirecaoSom, formData.identificaDirecaoSom);
            setSelected(percebeDiscriminaSabores, formData.percebeDiscriminaSabores);
            setSelected(acompanhaFocoLuminoso, formData.acompanhaFocoLuminoso);
            setSelected(movimentoPincaComTesoura, formData.movimentoPincaComTesoura);
            setSelected(amassaPapel, formData.amassaPapel);
            setSelected(caiComFacilidade, formData.caiComFacilidade);
            setSelected(encaixaPecas, formData.encaixaPecas);
            setSelected(recorta, formData.recorta);
            setSelected(unePontos, formData.unePontos);
            setSelected(consegueCorrer, formData.consegueCorrer);
            setSelected(empilha, formData.empilha);
            setSelected(agitacaoMotora, formData.agitacaoMotora);
            setSelected(andaLinhaReta, formData.andaLinhaReta);
            setSelected(sobeDesceEscadas, formData.sobeDesceEscadas);
            setSelected(arremessaBola, formData.arremessaBola);
        } else if (currentStep == 3) {
            setSelected(usaSanitarioSemAjuda, formData.usaSanitarioSemAjuda);
            setSelected(penteiaSeSo, formData.penteiaSeSo);
            setSelected(consegueVestirDespirSe, formData.consegueVestirDespirSe);
            setSelected(lavaSecaAsMaos, formData.lavaSecaAsMaos);
            setSelected(banhoComModeracao, formData.banhoComModeracao);
            setSelected(calcaSeSo, formData.calcaSeSo);
            setSelected(reconheceRoupas, formData.reconheceRoupas);
            setSelected(abreFechaTorneira, formData.abreFechaTorneira);
            setSelected(escovaDentesSemAjuda, formData.escovaDentesSemAjuda);
            setSelected(consegueDarNosLacos, formData.consegueDarNosLacos);
            setSelected(abotoaDesabotoaRoupas, formData.abotoaDesabotoaRoupas);
            setSelected(identificaPartesDoCorpo, formData.identificaPartesDoCorpo);
            setSelected(garatujas, formData.garatujas);
            setSelected(preSilabico, formData.preSilabico);
            setSelected(silabico, formData.silabico);
            setSelected(silabicoAlfabetico, formData.silabicoAlfabetico);
            setSelected(alfabetico, formData.alfabetico);
            if (observacoes != null && formData.observacoes != null) observacoes.setText(formData.observacoes);
        }
    }

    private Boolean isSelected(CheckBox cb, Boolean fallback) {
        if (cb == null) return fallback;
        return cb.isSelected();
    }

    private void setSelected(CheckBox cb, Boolean v) {
        if (cb == null || v == null) return;
        cb.setSelected(Boolean.TRUE.equals(v));
    }

    private String getText(TextArea ta, String fallback) {
        if (ta == null) return fallback;
        String t = ta.getText();
        return t == null ? fallback : t;
    }

    private void carregarExistente() {
        if (educando == null || educando.id() == null) return;
        java.util.Map<String, Object> dto = authService.getDiagnosticoInicialPorEducandoRaw(educando.id());
        if (dto == null) return;
        Object o;
        o = dto.get("falaSeuNome"); if (o instanceof Boolean b) formData.falaSeuNome = b;
        o = dto.get("dizDataNascimento"); if (o instanceof Boolean b) formData.dizDataNascimento = b;
        o = dto.get("lePalavras"); if (o instanceof Boolean b) formData.lePalavras = b;
        o = dto.get("informaNumeroTelefone"); if (o instanceof Boolean b) formData.informaNumeroTelefone = b;
        o = dto.get("emiteRespostas"); if (o instanceof Boolean b) formData.emiteRespostas = b;
        o = dto.get("transmiteRecado"); if (o instanceof Boolean b) formData.transmiteRecado = b;
        o = dto.get("informaEndereco"); if (o instanceof Boolean b) formData.informaEndereco = b;
        o = dto.get("informaNomePais"); if (o instanceof Boolean b) formData.informaNomePais = b;
        o = dto.get("compreendeOrdens"); if (o instanceof Boolean b) formData.compreendeOrdens = b;
        o = dto.get("expoeIdeias"); if (o instanceof Boolean b) formData.expoeIdeias = b;
        o = dto.get("recontaHistorias"); if (o instanceof Boolean b) formData.recontaHistorias = b;
        o = dto.get("usaSistemaCA"); if (o instanceof Boolean b) formData.usaSistemaCA = b;
        o = dto.get("relataFatosComCoerencia"); if (o instanceof Boolean b) formData.relataFatosComCoerencia = b;
        o = dto.get("pronunciaLetrasAlfabeto"); if (o instanceof Boolean b) formData.pronunciaLetrasAlfabeto = b;
        o = dto.get("verbalizaMusicas"); if (o instanceof Boolean b) formData.verbalizaMusicas = b;
        o = dto.get("interpretaHistorias"); if (o instanceof Boolean b) formData.interpretaHistorias = b;
        o = dto.get("formulaPerguntas"); if (o instanceof Boolean b) formData.formulaPerguntas = b;
        o = dto.get("utilizaGestosParaSeComunicar"); if (o instanceof Boolean b) formData.utilizaGestosParaSeComunicar = b;
        o = dto.get("demonstraCooperacao"); if (o instanceof Boolean b) formData.demonstraCooperacao = b;
        o = dto.get("timidoInseguro"); if (o instanceof Boolean b) formData.timidoInseguro = b;
        o = dto.get("fazBirra"); if (o instanceof Boolean b) formData.fazBirra = b;
        o = dto.get("solicitaOfereceAjuda"); if (o instanceof Boolean b) formData.solicitaOfereceAjuda = b;
        o = dto.get("riComFrequencia"); if (o instanceof Boolean b) formData.riComFrequencia = b;
        o = dto.get("compartilhaOQueESeu"); if (o instanceof Boolean b) formData.compartilhaOQueESeu = b;
        o = dto.get("demonstraAmorGentilezaAtencao"); if (o instanceof Boolean b) formData.demonstraAmorGentilezaAtencao = b;
        o = dto.get("choraComFrequencia"); if (o instanceof Boolean b) formData.choraComFrequencia = b;
        o = dto.get("interageComColegas"); if (o instanceof Boolean b) formData.interageComColegas = b;
        o = dto.get("captaDetalhesGravura"); if (o instanceof Boolean b) formData.captaDetalhesGravura = b;
        o = dto.get("reconheceVozes"); if (o instanceof Boolean b) formData.reconheceVozes = b;
        o = dto.get("reconheceCancoes"); if (o instanceof Boolean b) formData.reconheceCancoes = b;
        o = dto.get("percebeTexturas"); if (o instanceof Boolean b) formData.percebeTexturas = b;
        o = dto.get("percepcaoCores"); if (o instanceof Boolean b) formData.percepcaoCores = b;
        o = dto.get("discriminaSons"); if (o instanceof Boolean b) formData.discriminaSons = b;
        o = dto.get("discriminaOdores"); if (o instanceof Boolean b) formData.discriminaOdores = b;
        o = dto.get("aceitaDiferentesTexturas"); if (o instanceof Boolean b) formData.aceitaDiferentesTexturas = b;
        o = dto.get("percepcaoFormas"); if (o instanceof Boolean b) formData.percepcaoFormas = b;
        o = dto.get("identificaDirecaoSom"); if (o instanceof Boolean b) formData.identificaDirecaoSom = b;
        o = dto.get("percebeDiscriminaSabores"); if (o instanceof Boolean b) formData.percebeDiscriminaSabores = b;
        o = dto.get("acompanhaFocoLuminoso"); if (o instanceof Boolean b) formData.acompanhaFocoLuminoso = b;
        o = dto.get("movimentoPincaComTesoura"); if (o instanceof Boolean b) formData.movimentoPincaComTesoura = b;
        o = dto.get("amassaPapel"); if (o instanceof Boolean b) formData.amassaPapel = b;
        o = dto.get("caiComFacilidade"); if (o instanceof Boolean b) formData.caiComFacilidade = b;
        o = dto.get("encaixaPecas"); if (o instanceof Boolean b) formData.encaixaPecas = b;
        o = dto.get("recorta"); if (o instanceof Boolean b) formData.recorta = b;
        o = dto.get("unePontos"); if (o instanceof Boolean b) formData.unePontos = b;
        o = dto.get("consegueCorrer"); if (o instanceof Boolean b) formData.consegueCorrer = b;
        o = dto.get("empilha"); if (o instanceof Boolean b) formData.empilha = b;
        o = dto.get("agitacaoMotora"); if (o instanceof Boolean b) formData.agitacaoMotora = b;
        o = dto.get("andaLinhaReta"); if (o instanceof Boolean b) formData.andaLinhaReta = b;
        o = dto.get("sobeDesceEscadas"); if (o instanceof Boolean b) formData.sobeDesceEscadas = b;
        o = dto.get("arremessaBola"); if (o instanceof Boolean b) formData.arremessaBola = b;
        o = dto.get("usaSanitarioSemAjuda"); if (o instanceof Boolean b) formData.usaSanitarioSemAjuda = b;
        o = dto.get("penteiaSeSo"); if (o instanceof Boolean b) formData.penteiaSeSo = b;
        o = dto.get("consegueVestirDespirSe"); if (o instanceof Boolean b) formData.consegueVestirDespirSe = b;
        o = dto.get("lavaSecaAsMaos"); if (o instanceof Boolean b) formData.lavaSecaAsMaos = b;
        o = dto.get("banhoComModeracao"); if (o instanceof Boolean b) formData.banhoComModeracao = b;
        o = dto.get("calcaSeSo"); if (o instanceof Boolean b) formData.calcaSeSo = b;
        o = dto.get("reconheceRoupas"); if (o instanceof Boolean b) formData.reconheceRoupas = b;
        o = dto.get("abreFechaTorneira"); if (o instanceof Boolean b) formData.abreFechaTorneira = b;
        o = dto.get("escovaDentesSemAjuda"); if (o instanceof Boolean b) formData.escovaDentesSemAjuda = b;
        o = dto.get("consegueDarNosLacos"); if (o instanceof Boolean b) formData.consegueDarNosLacos = b;
        o = dto.get("abotoaDesabotoaRoupas"); if (o instanceof Boolean b) formData.abotoaDesabotoaRoupas = b;
        o = dto.get("identificaPartesDoCorpo"); if (o instanceof Boolean b) formData.identificaPartesDoCorpo = b;
        o = dto.get("garatujas"); if (o instanceof Boolean b) formData.garatujas = b;
        o = dto.get("preSilabico"); if (o instanceof Boolean b) formData.preSilabico = b;
        o = dto.get("silabico"); if (o instanceof Boolean b) formData.silabico = b;
        o = dto.get("silabicoAlfabetico"); if (o instanceof Boolean b) formData.silabicoAlfabetico = b;
        o = dto.get("alfabetico"); if (o instanceof Boolean b) formData.alfabetico = b;
        o = dto.get("observacoes"); if (o instanceof String s) formData.observacoes = s;
        
        // Captura o ID para permitir edição posterior
        o = dto.get("id");
        if (o instanceof String s) {
            diagnosticoId = s;
        }
    }

    private void showValidation(String msg) {
        if (validationMsg != null) {
            validationMsg.setText(msg);
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
        javafx.animation.PauseTransition pt = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(5));
        pt.setOnFinished(e -> anamnese.getChildren().remove(overlay));
        pt.play();
    }

    public static class CreateDiagnosticoInicialDTO {
        public Boolean falaSeuNome;
        public Boolean dizDataNascimento;
        public Boolean lePalavras;
        public Boolean informaNumeroTelefone;
        public Boolean emiteRespostas;
        public Boolean transmiteRecado;
        public Boolean informaEndereco;
        public Boolean informaNomePais;
        public Boolean compreendeOrdens;
        public Boolean expoeIdeias;
        public Boolean recontaHistorias;
        public Boolean usaSistemaCA;
        public Boolean relataFatosComCoerencia;
        public Boolean pronunciaLetrasAlfabeto;
        public Boolean verbalizaMusicas;
        public Boolean interpretaHistorias;
        public Boolean formulaPerguntas;
        public Boolean utilizaGestosParaSeComunicar;
        public Boolean demonstraCooperacao;
        public Boolean timidoInseguro;
        public Boolean fazBirra;
        public Boolean solicitaOfereceAjuda;
        public Boolean riComFrequencia;
        public Boolean compartilhaOQueESeu;
        public Boolean demonstraAmorGentilezaAtencao;
        public Boolean choraComFrequencia;
        public Boolean interageComColegas;
        public Boolean captaDetalhesGravura;
        public Boolean reconheceVozes;
        public Boolean reconheceCancoes;
        public Boolean percebeTexturas;
        public Boolean percepcaoCores;
        public Boolean discriminaSons;
        public Boolean discriminaOdores;
        public Boolean aceitaDiferentesTexturas;
        public Boolean percepcaoFormas;
        public Boolean identificaDirecaoSom;
        public Boolean percebeDiscriminaSabores;
        public Boolean acompanhaFocoLuminoso;
        public Boolean movimentoPincaComTesoura;
        public Boolean amassaPapel;
        public Boolean caiComFacilidade;
        public Boolean encaixaPecas;
        public Boolean recorta;
        public Boolean unePontos;
        public Boolean consegueCorrer;
        public Boolean empilha;
        public Boolean agitacaoMotora;
        public Boolean andaLinhaReta;
        public Boolean sobeDesceEscadas;
        public Boolean arremessaBola;
        public Boolean usaSanitarioSemAjuda;
        public Boolean penteiaSeSo;
        public Boolean consegueVestirDespirSe;
        public Boolean lavaSecaAsMaos;
        public Boolean banhoComModeracao;
        public Boolean calcaSeSo;
        public Boolean reconheceRoupas;
        public Boolean abreFechaTorneira;
        public Boolean escovaDentesSemAjuda;
        public Boolean consegueDarNosLacos;
        public Boolean abotoaDesabotoaRoupas;
        public Boolean identificaPartesDoCorpo;
        public Boolean garatujas;
        public Boolean preSilabico;
        public Boolean silabico;
        public Boolean silabicoAlfabetico;
        public Boolean alfabetico;
        public String observacoes;
        public CreateDiagnosticoInicialDTO(Boolean falaSeuNome,
                                           Boolean dizDataNascimento,
                                           Boolean lePalavras,
                                           Boolean informaNumeroTelefone,
                                           Boolean emiteRespostas,
                                           Boolean transmiteRecado,
                                           Boolean informaEndereco,
                                           Boolean informaNomePais,
                                           Boolean compreendeOrdens,
                                           Boolean expoeIdeias,
                                           Boolean recontaHistorias,
                                           Boolean usaSistemaCA,
                                           Boolean relataFatosComCoerencia,
                                           Boolean pronunciaLetrasAlfabeto,
                                           Boolean verbalizaMusicas,
                                           Boolean interpretaHistorias,
                                           Boolean formulaPerguntas,
                                           Boolean utilizaGestosParaSeComunicar,
                                           Boolean demonstraCooperacao,
                                           Boolean timidoInseguro,
                                           Boolean fazBirra,
                                           Boolean solicitaOfereceAjuda,
                                           Boolean riComFrequencia,
                                           Boolean compartilhaOQueESeu,
                                           Boolean demonstraAmorGentilezaAtencao,
                                           Boolean choraComFrequencia,
                                           Boolean interageComColegas,
                                           Boolean captaDetalhesGravura,
                                           Boolean reconheceVozes,
                                           Boolean reconheceCancoes,
                                           Boolean percebeTexturas,
                                           Boolean percepcaoCores,
                                           Boolean discriminaSons,
                                           Boolean discriminaOdores,
                                           Boolean aceitaDiferentesTexturas,
                                           Boolean percepcaoFormas,
                                           Boolean identificaDirecaoSom,
                                           Boolean percebeDiscriminaSabores,
                                           Boolean acompanhaFocoLuminoso,
                                           Boolean movimentoPincaComTesoura,
                                           Boolean amassaPapel,
                                           Boolean caiComFacilidade,
                                           Boolean encaixaPecas,
                                           Boolean recorta,
                                           Boolean unePontos,
                                           Boolean consegueCorrer,
                                           Boolean empilha,
                                           Boolean agitacaoMotora,
                                           Boolean andaLinhaReta,
                                           Boolean sobeDesceEscadas,
                                           Boolean arremessaBola,
                                           Boolean usaSanitarioSemAjuda,
                                           Boolean penteiaSeSo,
                                           Boolean consegueVestirDespirSe,
                                           Boolean lavaSecaAsMaos,
                                           Boolean banhoComModeracao,
                                           Boolean calcaSeSo,
                                           Boolean reconheceRoupas,
                                           Boolean abreFechaTorneira,
                                           Boolean escovaDentesSemAjuda,
                                           Boolean consegueDarNosLacos,
                                           Boolean abotoaDesabotoaRoupas,
                                           Boolean identificaPartesDoCorpo,
                                           Boolean garatujas,
                                           Boolean preSilabico,
                                           Boolean silabico,
                                           Boolean silabicoAlfabetico,
                                           Boolean alfabetico,
                                           String observacoes) {
            this.falaSeuNome = falaSeuNome;
            this.dizDataNascimento = dizDataNascimento;
            this.lePalavras = lePalavras;
            this.informaNumeroTelefone = informaNumeroTelefone;
            this.emiteRespostas = emiteRespostas;
            this.transmiteRecado = transmiteRecado;
            this.informaEndereco = informaEndereco;
            this.informaNomePais = informaNomePais;
            this.compreendeOrdens = compreendeOrdens;
            this.expoeIdeias = expoeIdeias;
            this.recontaHistorias = recontaHistorias;
            this.usaSistemaCA = usaSistemaCA;
            this.relataFatosComCoerencia = relataFatosComCoerencia;
            this.pronunciaLetrasAlfabeto = pronunciaLetrasAlfabeto;
            this.verbalizaMusicas = verbalizaMusicas;
            this.interpretaHistorias = interpretaHistorias;
            this.formulaPerguntas = formulaPerguntas;
            this.utilizaGestosParaSeComunicar = utilizaGestosParaSeComunicar;
            this.demonstraCooperacao = demonstraCooperacao;
            this.timidoInseguro = timidoInseguro;
            this.fazBirra = fazBirra;
            this.solicitaOfereceAjuda = solicitaOfereceAjuda;
            this.riComFrequencia = riComFrequencia;
            this.compartilhaOQueESeu = compartilhaOQueESeu;
            this.demonstraAmorGentilezaAtencao = demonstraAmorGentilezaAtencao;
            this.choraComFrequencia = choraComFrequencia;
            this.interageComColegas = interageComColegas;
            this.captaDetalhesGravura = captaDetalhesGravura;
            this.reconheceVozes = reconheceVozes;
            this.reconheceCancoes = reconheceCancoes;
            this.percebeTexturas = percebeTexturas;
            this.percepcaoCores = percepcaoCores;
            this.discriminaSons = discriminaSons;
            this.discriminaOdores = discriminaOdores;
            this.aceitaDiferentesTexturas = aceitaDiferentesTexturas;
            this.percepcaoFormas = percepcaoFormas;
            this.identificaDirecaoSom = identificaDirecaoSom;
            this.percebeDiscriminaSabores = percebeDiscriminaSabores;
            this.acompanhaFocoLuminoso = acompanhaFocoLuminoso;
            this.movimentoPincaComTesoura = movimentoPincaComTesoura;
            this.amassaPapel = amassaPapel;
            this.caiComFacilidade = caiComFacilidade;
            this.encaixaPecas = encaixaPecas;
            this.recorta = recorta;
            this.unePontos = unePontos;
            this.consegueCorrer = consegueCorrer;
            this.empilha = empilha;
            this.agitacaoMotora = agitacaoMotora;
            this.andaLinhaReta = andaLinhaReta;
            this.sobeDesceEscadas = sobeDesceEscadas;
            this.arremessaBola = arremessaBola;
            this.usaSanitarioSemAjuda = usaSanitarioSemAjuda;
            this.penteiaSeSo = penteiaSeSo;
            this.consegueVestirDespirSe = consegueVestirDespirSe;
            this.lavaSecaAsMaos = lavaSecaAsMaos;
            this.banhoComModeracao = banhoComModeracao;
            this.calcaSeSo = calcaSeSo;
            this.reconheceRoupas = reconheceRoupas;
            this.abreFechaTorneira = abreFechaTorneira;
            this.escovaDentesSemAjuda = escovaDentesSemAjuda;
            this.consegueDarNosLacos = consegueDarNosLacos;
            this.abotoaDesabotoaRoupas = abotoaDesabotoaRoupas;
            this.identificaPartesDoCorpo = identificaPartesDoCorpo;
            this.garatujas = garatujas;
            this.preSilabico = preSilabico;
            this.silabico = silabico;
            this.silabicoAlfabetico = silabicoAlfabetico;
            this.alfabetico = alfabetico;
            this.observacoes = observacoes;
        }
    }

    public static class DIFormData {
        public Boolean falaSeuNome;
        public Boolean dizDataNascimento;
        public Boolean lePalavras;
        public Boolean informaNumeroTelefone;
        public Boolean emiteRespostas;
        public Boolean transmiteRecado;
        public Boolean informaEndereco;
        public Boolean informaNomePais;
        public Boolean compreendeOrdens;
        public Boolean expoeIdeias;
        public Boolean recontaHistorias;
        public Boolean usaSistemaCA;
        public Boolean relataFatosComCoerencia;
        public Boolean pronunciaLetrasAlfabeto;
        public Boolean verbalizaMusicas;
        public Boolean interpretaHistorias;
        public Boolean formulaPerguntas;
        public Boolean utilizaGestosParaSeComunicar;
        public Boolean demonstraCooperacao;
        public Boolean timidoInseguro;
        public Boolean fazBirra;
        public Boolean solicitaOfereceAjuda;
        public Boolean riComFrequencia;
        public Boolean compartilhaOQueESeu;
        public Boolean demonstraAmorGentilezaAtencao;
        public Boolean choraComFrequencia;
        public Boolean interageComColegas;
        public Boolean captaDetalhesGravura;
        public Boolean reconheceVozes;
        public Boolean reconheceCancoes;
        public Boolean percebeTexturas;
        public Boolean percepcaoCores;
        public Boolean discriminaSons;
        public Boolean discriminaOdores;
        public Boolean aceitaDiferentesTexturas;
        public Boolean percepcaoFormas;
        public Boolean identificaDirecaoSom;
        public Boolean percebeDiscriminaSabores;
        public Boolean acompanhaFocoLuminoso;
        public Boolean movimentoPincaComTesoura;
        public Boolean amassaPapel;
        public Boolean caiComFacilidade;
        public Boolean encaixaPecas;
        public Boolean recorta;
        public Boolean unePontos;
        public Boolean consegueCorrer;
        public Boolean empilha;
        public Boolean agitacaoMotora;
        public Boolean andaLinhaReta;
        public Boolean sobeDesceEscadas;
        public Boolean arremessaBola;
        public Boolean usaSanitarioSemAjuda;
        public Boolean penteiaSeSo;
        public Boolean consegueVestirDespirSe;
        public Boolean lavaSecaAsMaos;
        public Boolean banhoComModeracao;
        public Boolean calcaSeSo;
        public Boolean reconheceRoupas;
        public Boolean abreFechaTorneira;
        public Boolean escovaDentesSemAjuda;
        public Boolean consegueDarNosLacos;
        public Boolean abotoaDesabotoaRoupas;
        public Boolean identificaPartesDoCorpo;
        public Boolean garatujas;
        public Boolean preSilabico;
        public Boolean silabico;
        public Boolean silabicoAlfabetico;
        public Boolean alfabetico;
        public String observacoes;
    }
}
