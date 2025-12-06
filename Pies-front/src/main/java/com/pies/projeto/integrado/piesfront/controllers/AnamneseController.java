package com.pies.projeto.integrado.piesfront.controllers;

import com.pies.projeto.integrado.piesfront.dto.EducandoDTO;
import com.pies.projeto.integrado.piesfront.dto.AnamneseDTO;
import com.pies.projeto.integrado.piesfront.dto.AnamneseRequestDTO;
import com.pies.projeto.integrado.piesfront.dto.UserInfoDTO;
import com.pies.projeto.integrado.piesfront.dto.ProfessorDTO;
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
    @FXML
    private Label nameUser;
    @FXML
    private Label cargoUser;

    private EducandoDTO educando;
    private boolean temDadosExistentes = false;
    private AnamneseRequestDTO formData = new AnamneseRequestDTO();
    private final AuthService authService = AuthService.getInstance();
    private boolean carregarDadosExistentes = true; // Por padrão carrega dados existentes

    public void setEducando(EducandoDTO educando) {
        this.educando = educando;
        carregarAnamneseExistente();
        if (!isFormDataVazio()) {
            populateFromFormData();
        }
    }
    
    public void setModoNovo() {
        this.carregarDadosExistentes = false;
        this.formData = new AnamneseRequestDTO(); // Limpa os dados
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
            
            // Verificar se já existe uma anamnese
            System.out.println("=== SALVANDO ANAMNESE ===");
            AnamneseDTO existente = authService.getAnamnesePorEducando(educando.id());
            System.out.println("Anamnese existente: " + (existente != null ? existente.id() : "null"));
            
            AnamneseDTO resultado;
            
            if (existente != null) {
                // Se já existe, atualizar
                System.out.println("Anamnese já existe, atualizando...");
                System.out.println("Dados sendo enviados: " + dto);
                resultado = authService.atualizarAnamnese(educando.id(), dto);
            } else {
                // Se não existe, criar nova
                System.out.println("Criando nova anamnese...");
                resultado = authService.criarAnamnese(educando.id(), dto);
            }
            
            System.out.println("Resultado: " + (resultado != null ? "ID=" + resultado.id() : "null"));
            
            if (resultado != null) {
                AtendimentoFlowService.getInstance().concluirAnamnese(educando.id());
                NotificacaoController.agendar("Anamnese salva com sucesso!", true);
                handleCancelAction();
            } else {
                System.err.println("Falha ao salvar anamnese");
                showPopup("Falha ao salvar anamnese.", false);
            }
        }
    }

    private void abrir(String resource, String titulo) {
        captureCurrentStepData();
        if (anamnese != null) {
            Janelas.carregarTela(new javafx.event.ActionEvent(anamnese, null), resource, titulo, controller -> {
                if (controller instanceof AnamneseController c) {
                    c.setEducando(educando);
                    c.setFormData(formData);
                }
            });
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
        if (indicadorDeTela != null) {
            indicadorDeTela.setText("Anamnese");
        }
        if (validationMsg != null) {
            validationMsg.setVisible(false);
            validationMsg.setManaged(true);
        }
        javafx.application.Platform.runLater(() -> {
            atualizarNomeUsuarioAsync();
        });
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

    private void atualizarNomeUsuarioAsync() {
        Thread t = new Thread(() -> {
            ProfessorDTO prof = authService.getProfessorLogado();
            UserInfoDTO userInfo = authService.getUserInfo();
            javafx.application.Platform.runLater(() -> {
                if (prof != null && prof.getNome() != null && !prof.getNome().isEmpty()) {
                    if (nameUser != null) {
                        nameUser.setText(prof.getNome());
                    }
                } else if (userInfo != null && userInfo.name() != null && !userInfo.name().isEmpty()) {
                    if (nameUser != null) {
                        nameUser.setText(userInfo.name());
                    }
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
        NotificacaoController.exibir(anamnese, mensagem, sucesso);
    }

    private void captureCurrentStepData() {
        if (convulsaoSim != null && convulsaoNao != null) formData.convulsao = convulsaoSim.isSelected() ? Boolean.TRUE : (convulsaoNao.isSelected() ? Boolean.FALSE : null);
        if (vacinacaoSim != null && vacinacaoNao != null) formData.vacinacaoEmDia = vacinacaoSim.isSelected() ? Boolean.TRUE : (vacinacaoNao.isSelected() ? Boolean.FALSE : null);
        if (convenioSim != null && convenioNao != null) formData.possuiConvenio = convenioSim.isSelected() ? Boolean.TRUE : (convenioNao.isSelected() ? Boolean.FALSE : null);
        if (convenio != null && convenio.isVisible()) formData.convenio = safeText(convenio);
        if (doencaContagiosaSim != null && doencaContagiosaNao != null) formData.teveDoencaContagiosa = doencaContagiosaSim.isSelected() ? Boolean.TRUE : (doencaContagiosaNao.isSelected() ? Boolean.FALSE : null);
        if (doencaContagiosa != null && doencaContagiosa.isVisible()) formData.doencaContagiosa = safeText(doencaContagiosa);
        if (medicacaoSim != null && medicacaoNao != null) formData.fazUsoMedicacoes = medicacaoSim.isSelected() ? Boolean.TRUE : (medicacaoNao.isSelected() ? Boolean.FALSE : null);
        if (medicacoes != null && medicacoes.isVisible()) formData.medicacoes = safeText(medicacoes);

        if (servicosFrequentados1 != null) formData.servicosFrequentados = safeText(servicosFrequentados1);
        if (servicos != null) {
            String s = safeText(servicos);
            if (s != null && !s.isEmpty()) formData.servicosFrequentados = s;
        }
        if (inicioEscolarizacao != null) formData.inicioEscolarizacao = safeText(inicioEscolarizacao);
        if (dificuldadesSim != null && dificuldadesNao != null) formData.apresentaDificuldades = dificuldadesSim.isSelected() ? Boolean.TRUE : (dificuldadesNao.isSelected() ? Boolean.FALSE : null);
        if (dificuldades != null && dificuldades.isVisible()) formData.dificuldades = safeText(dificuldades);
        if (apoioPedagogicoSim != null && apoioPedagogicoNao != null) formData.apoioPedagogicoEmCasa = apoioPedagogicoSim.isSelected() ? Boolean.TRUE : (apoioPedagogicoNao.isSelected() ? Boolean.FALSE : null);
        if (apoioPedagogico != null && apoioPedagogico.isVisible()) formData.apoioPedagogico = safeText(apoioPedagogico);
        if (duracaoGestacao1 != null) formData.duracaoGestacao = safeText(duracaoGestacao1);
        if (preNatalSim != null && preNatalNao != null) formData.fezPreNatal = preNatalSim.isSelected() ? Boolean.TRUE : (preNatalNao.isSelected() ? Boolean.FALSE : null);
        if (prematuridadeSim != null && prematuridadeNao != null) {
            formData.prematuridadeOcorrida = prematuridadeSim.isSelected() ? Boolean.TRUE : (prematuridadeNao.isSelected() ? Boolean.FALSE : null);
            if (Boolean.TRUE.equals(formData.prematuridadeOcorrida) && prematuridade1 != null && prematuridade1.isVisible()) {
                formData.prematuridade = safeText(prematuridade1);
            } else {
                formData.prematuridade = null;
            }
        }

        if (cidadeNascimento != null) formData.cidadeNascimento = safeText(cidadeNascimento);
        if (maternidade != null) formData.maternidade = safeText(maternidade);
        if (tipoParto != null && tipoParto.getValue() != null) formData.tipoParto = tipoParto.getValue();

        if (chorouSim != null && chorouNao != null) formData.chorouAoNascer = chorouSim.isSelected() ? Boolean.TRUE : (chorouNao.isSelected() ? Boolean.FALSE : null);
        if (ficouRoxoSim != null && ficouRoxoNao != null) formData.ficouRoxo = ficouRoxoSim.isSelected() ? Boolean.TRUE : (ficouRoxoNao.isSelected() ? Boolean.FALSE : null);
        if (incubadoraSim != null && incubadoraNao != null) formData.usoIncubadora = incubadoraSim.isSelected() ? Boolean.TRUE : (incubadoraNao.isSelected() ? Boolean.FALSE : null);
        if (amamentadoSim != null && amamentadoNao != null) formData.foiAmamentado = amamentadoSim.isSelected() ? Boolean.TRUE : (amamentadoNao.isSelected() ? Boolean.FALSE : null);

        if (sustentouCabecaSim != null && sustentouCabecaNao != null) formData.sustentouCabeca = sustentouCabecaSim.isSelected() ? Boolean.TRUE : (sustentouCabecaNao.isSelected() ? Boolean.FALSE : null);
        if (sustentouCabeca != null && sustentouCabeca.isVisible()) formData.sustentouCabecaMeses = safeText(sustentouCabeca);
        if (engatinhouSim != null && engatinhouNao != null) formData.engatinhou = engatinhouSim.isSelected() ? Boolean.TRUE : (engatinhouNao.isSelected() ? Boolean.FALSE : null);
        if (engatinhou != null && engatinhou.isVisible()) formData.engatinhouMeses = safeText(engatinhou);
        if (sentouSim != null && sentouNao != null) formData.sentou = sentouSim.isSelected() ? Boolean.TRUE : (sentouNao.isSelected() ? Boolean.FALSE : null);
        if (sentou != null && sentou.isVisible()) formData.sentouMeses = safeText(sentou);
        if (andouSim != null && andouNao != null) formData.andou = andouSim.isSelected() ? Boolean.TRUE : (andouNao.isSelected() ? Boolean.FALSE : null);
        if (andou != null && andou.isVisible()) formData.andouMeses = safeText(andou);
        if (terapiaSim != null && terapiaNao != null) formData.precisouTerapia = terapiaSim.isSelected() ? Boolean.TRUE : (terapiaNao.isSelected() ? Boolean.FALSE : null);
        if (terapia != null && terapia.isVisible()) formData.terapiaMotivo = safeText(terapia);
        if (falouSim != null && falouNao != null) formData.falou = falouSim.isSelected() ? Boolean.TRUE : (falouNao.isSelected() ? Boolean.FALSE : null);
        if (falou != null && falou.isVisible()) formData.falouMeses = safeText(falou);

        if (balbucio != null && balbucio.getValue() != null) formData.primeiroBalbucioMeses = balbucio.getValue();
        if (primeiraPalavra != null) formData.primeiraPalavraQuando = safeText(primeiraPalavra);
        if (primeiraFrase != null) formData.primeiraFraseQuando = safeText(primeiraFrase);
        if (tipoFala != null && tipoFala.getValue() != null) formData.falaNaturalOuInibido = tipoFala.getValue();
        if (disturbioSim != null && disturbioNao != null) {
            formData.possuiDisturbio = disturbioSim.isSelected() ? Boolean.TRUE : (disturbioNao.isSelected() ? Boolean.FALSE : null);
        }
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

    private String tri(Boolean b) {
        if (b == null) return null;
        return Boolean.TRUE.equals(b) ? "SIM" : "NAO";
    }

    private AnamneseDTO toAnamneseDTO() {
        String temConvulsao = tri(formData.convulsao);
        String convenioMedico = Boolean.TRUE.equals(formData.possuiConvenio) ? val(formData.convenio) : null;
        String vacinacaoEmDia = tri(formData.vacinacaoEmDia);
        String doencaContagiosa = Boolean.TRUE.equals(formData.teveDoencaContagiosa) ? val(formData.doencaContagiosa) : null;
        String usoMedicacoes = Boolean.TRUE.equals(formData.fazUsoMedicacoes) ? val(formData.medicacoes) : null;
        String servicosSaudeOuEducacao = val(formData.servicosFrequentados);
        String inicioEscolarizacao = val(formData.inicioEscolarizacao);
        String dificuldadesEscolares = Boolean.TRUE.equals(formData.apresentaDificuldades) ? val(formData.dificuldades) : null;
        String apoioPedagogicoEmCasa = Boolean.TRUE.equals(formData.apoioPedagogicoEmCasa) ? val(formData.apoioPedagogico) : null;
        String duracaoGestacao = val(formData.duracaoGestacao);
        String fezPreNatal = tri(formData.fezPreNatal);
        String prematuridade = Boolean.TRUE.equals(formData.prematuridadeOcorrida) ? val(formData.prematuridade) : null;

        String cidadeNascimento = val(formData.cidadeNascimento);
        String maternidadeNascimento = val(formData.maternidade);
        String tipoParto = formData.tipoParto;

        String chorouAoNascer = tri(formData.chorouAoNascer);
        String ficouRoxo = tri(formData.ficouRoxo);
        String usoIncubadora = tri(formData.usoIncubadora);
        String foiAmamentado = tri(formData.foiAmamentado);

        String sustentouCabecaMeses = Boolean.TRUE.equals(formData.sustentouCabeca) ? val(formData.sustentouCabecaMeses) : null;
        String engatinhouMeses = Boolean.TRUE.equals(formData.engatinhou) ? val(formData.engatinhouMeses) : null;
        String sentouMeses = Boolean.TRUE.equals(formData.sentou) ? val(formData.sentouMeses) : null;
        String andouMeses = Boolean.TRUE.equals(formData.andou) ? val(formData.andouMeses) : null;
        String precisouTerapiaMotivo = Boolean.TRUE.equals(formData.precisouTerapia) ? val(formData.terapiaMotivo) : null;
        String falouMeses = Boolean.TRUE.equals(formData.falou) ? val(formData.falouMeses) : null;

        String primeiroBalbucioMeses = val(formData.primeiroBalbucioMeses);
        String primeiraPalavraQuando = val(formData.primeiraPalavraQuando);
        String primeiraFraseQuando = val(formData.primeiraFraseQuando);
        String falaNaturalOuInibido = val(formData.falaNaturalOuInibido);
        String disturbioFala = Boolean.TRUE.equals(formData.possuiDisturbio) ? val(formData.disturbioFala) : null;

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

    private boolean isFormDataVazio() {
        if (formData == null) return true;
        boolean algumTrue = Boolean.TRUE.equals(formData.convulsao) || Boolean.TRUE.equals(formData.possuiConvenio) || Boolean.TRUE.equals(formData.vacinacaoEmDia)
                || Boolean.TRUE.equals(formData.teveDoencaContagiosa) || Boolean.TRUE.equals(formData.fazUsoMedicacoes) || Boolean.TRUE.equals(formData.apresentaDificuldades)
                || Boolean.TRUE.equals(formData.apoioPedagogicoEmCasa) || Boolean.TRUE.equals(formData.fezPreNatal) || Boolean.TRUE.equals(formData.chorouAoNascer)
                || Boolean.TRUE.equals(formData.ficouRoxo) || Boolean.TRUE.equals(formData.usoIncubadora) || Boolean.TRUE.equals(formData.foiAmamentado)
                || Boolean.TRUE.equals(formData.sustentouCabeca) || Boolean.TRUE.equals(formData.engatinhou) || Boolean.TRUE.equals(formData.sentou) || Boolean.TRUE.equals(formData.andou)
                || Boolean.TRUE.equals(formData.precisouTerapia) || Boolean.TRUE.equals(formData.falou) || Boolean.TRUE.equals(formData.prematuridadeOcorrida) || Boolean.TRUE.equals(formData.possuiDisturbio);
        boolean algumTexto = notEmpty(formData.convenio) || notEmpty(formData.doencaContagiosa)
                || notEmpty(formData.medicacoes) || notEmpty(formData.servicosFrequentados)
                || notEmpty(formData.inicioEscolarizacao) || notEmpty(formData.dificuldades)
                || notEmpty(formData.apoioPedagogico) || notEmpty(formData.duracaoGestacao)
                || notEmpty(formData.prematuridade) || notEmpty(formData.cidadeNascimento)
                || notEmpty(formData.maternidade) || notEmpty(formData.tipoParto)
                || notEmpty(formData.sustentouCabecaMeses) || notEmpty(formData.engatinhouMeses)
                || notEmpty(formData.sentouMeses) || notEmpty(formData.andouMeses)
                || notEmpty(formData.terapiaMotivo) || notEmpty(formData.falouMeses)
                || notEmpty(formData.primeiroBalbucioMeses) || notEmpty(formData.primeiraPalavraQuando)
                || notEmpty(formData.primeiraFraseQuando) || notEmpty(formData.falaNaturalOuInibido)
                || notEmpty(formData.disturbioFala) || notEmpty(formData.dormeSozinho)
                || notEmpty(formData.temQuartoProprio) || notEmpty(formData.sonoCalmoOuAgitado)
                || notEmpty(formData.respeitaRegras) || notEmpty(formData.desmotivado)
                || notEmpty(formData.agressivo) || notEmpty(formData.apresentaInquietacao);
        return !algumTrue && !algumTexto;
    }

    private boolean notEmpty(String s) { return s != null && !s.isEmpty(); }

    private void populateFromFormData() {
        if (formData == null) return;
        boolean permiteSelecionarNao = temDadosExistentes || formData.preenchimentoEmAndamento;
        if (convulsaoSim != null && convulsaoNao != null) { Boolean b = formData.convulsao; convulsaoSim.setSelected(Boolean.TRUE.equals(b)); convulsaoNao.setSelected(Boolean.FALSE.equals(b) && permiteSelecionarNao); }
        if (vacinacaoSim != null && vacinacaoNao != null) { Boolean b = formData.vacinacaoEmDia; vacinacaoSim.setSelected(Boolean.TRUE.equals(b)); vacinacaoNao.setSelected(Boolean.FALSE.equals(b) && permiteSelecionarNao); }
        if (convenioSim != null && convenioNao != null) { Boolean b = formData.possuiConvenio; convenioSim.setSelected(Boolean.TRUE.equals(b)); convenioNao.setSelected(Boolean.FALSE.equals(b) && permiteSelecionarNao); }
        if (doencaContagiosaSim != null && doencaContagiosaNao != null) { Boolean b = formData.teveDoencaContagiosa; doencaContagiosaSim.setSelected(Boolean.TRUE.equals(b)); doencaContagiosaNao.setSelected(Boolean.FALSE.equals(b) && permiteSelecionarNao); }
        if (medicacaoSim != null && medicacaoNao != null) { Boolean b = formData.fazUsoMedicacoes; medicacaoSim.setSelected(Boolean.TRUE.equals(b)); medicacaoNao.setSelected(Boolean.FALSE.equals(b) && permiteSelecionarNao); }
        if (dificuldadesSim != null && dificuldadesNao != null) { Boolean b = formData.apresentaDificuldades; dificuldadesSim.setSelected(Boolean.TRUE.equals(b)); dificuldadesNao.setSelected(Boolean.FALSE.equals(b) && permiteSelecionarNao); }
        if (apoioPedagogicoSim != null && apoioPedagogicoNao != null) { Boolean b = formData.apoioPedagogicoEmCasa; apoioPedagogicoSim.setSelected(Boolean.TRUE.equals(b)); apoioPedagogicoNao.setSelected(Boolean.FALSE.equals(b) && permiteSelecionarNao); }
        if (preNatalSim != null && preNatalNao != null) { Boolean b = formData.fezPreNatal; preNatalSim.setSelected(Boolean.TRUE.equals(b)); preNatalNao.setSelected(Boolean.FALSE.equals(b) && permiteSelecionarNao); }
        if (prematuridadeSim != null && prematuridadeNao != null) { Boolean b = formData.prematuridadeOcorrida; prematuridadeSim.setSelected(Boolean.TRUE.equals(b)); prematuridadeNao.setSelected(Boolean.FALSE.equals(b) && permiteSelecionarNao); }

        if (chorouSim != null && chorouNao != null) { Boolean b = formData.chorouAoNascer; chorouSim.setSelected(Boolean.TRUE.equals(b)); chorouNao.setSelected(Boolean.FALSE.equals(b) && permiteSelecionarNao); }
        if (ficouRoxoSim != null && ficouRoxoNao != null) { Boolean b = formData.ficouRoxo; ficouRoxoSim.setSelected(Boolean.TRUE.equals(b)); ficouRoxoNao.setSelected(Boolean.FALSE.equals(b) && permiteSelecionarNao); }
        if (incubadoraSim != null && incubadoraNao != null) { Boolean b = formData.usoIncubadora; incubadoraSim.setSelected(Boolean.TRUE.equals(b)); incubadoraNao.setSelected(Boolean.FALSE.equals(b) && permiteSelecionarNao); }
        if (amamentadoSim != null && amamentadoNao != null) { Boolean b = formData.foiAmamentado; amamentadoSim.setSelected(Boolean.TRUE.equals(b)); amamentadoNao.setSelected(Boolean.FALSE.equals(b) && permiteSelecionarNao); }

        if (sustentouCabecaSim != null && sustentouCabecaNao != null) { Boolean b = formData.sustentouCabeca; sustentouCabecaSim.setSelected(Boolean.TRUE.equals(b)); sustentouCabecaNao.setSelected(Boolean.FALSE.equals(b) && permiteSelecionarNao); }
        if (engatinhouSim != null && engatinhouNao != null) { Boolean b = formData.engatinhou; engatinhouSim.setSelected(Boolean.TRUE.equals(b)); engatinhouNao.setSelected(Boolean.FALSE.equals(b) && permiteSelecionarNao); }
        if (sentouSim != null && sentouNao != null) { Boolean b = formData.sentou; sentouSim.setSelected(Boolean.TRUE.equals(b)); sentouNao.setSelected(Boolean.FALSE.equals(b) && permiteSelecionarNao); }
        if (andouSim != null && andouNao != null) { Boolean b = formData.andou; andouSim.setSelected(Boolean.TRUE.equals(b)); andouNao.setSelected(Boolean.FALSE.equals(b) && permiteSelecionarNao); }
        if (terapiaSim != null && terapiaNao != null) { Boolean b = formData.precisouTerapia; terapiaSim.setSelected(Boolean.TRUE.equals(b)); terapiaNao.setSelected(Boolean.FALSE.equals(b) && permiteSelecionarNao); }
        if (falouSim != null && falouNao != null) { Boolean b = formData.falou; falouSim.setSelected(Boolean.TRUE.equals(b)); falouNao.setSelected(Boolean.FALSE.equals(b) && permiteSelecionarNao); }

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
            Boolean b = formData.possuiDisturbio;
            disturbioSim.setSelected(Boolean.TRUE.equals(b));
            disturbioNao.setSelected(Boolean.FALSE.equals(b) && permiteSelecionarNao);
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

    private Boolean parseTri(String s) {
        if (s == null) return null;
        String t = s.trim();
        if (t.equalsIgnoreCase("true") || t.equalsIgnoreCase("sim") || t.equalsIgnoreCase("s") || t.equals("1") || t.equalsIgnoreCase("SIM")) return Boolean.TRUE;
        if (t.equalsIgnoreCase("false") || t.equalsIgnoreCase("nao") || t.equalsIgnoreCase("não") || t.equalsIgnoreCase("n") || t.equals("0") || t.equalsIgnoreCase("NAO")) return Boolean.FALSE;
        return null;
    }

    private Boolean parseBool(String s) {
        return parseTri(s);
    }

    private void carregarAnamneseExistente() {
        if (educando == null || educando.id() == null) {
            System.out.println("Educando é null ou não tem ID, não pode carregar anamnese");
            return;
        }
        System.out.println("Tentando carregar anamnese para educando ID: " + educando.id());
        
        // Força uma nova busca no backend
        AnamneseDTO dto = authService.getAnamnesePorEducando(educando.id());
        temDadosExistentes = dto != null;
        if (dto == null) {
            System.out.println("Nenhuma anamnese encontrada para o educando");
            return;
        }
        System.out.println("Anamnese encontrada! ID: " + dto.id() + " Carregando dados...");
        
        formData.convulsao = parseTri(dto.temConvulsao());
        formData.possuiConvenio = (dto.convenioMedico() != null && !dto.convenioMedico().isEmpty()) ? Boolean.TRUE : Boolean.FALSE;
        formData.convenio = dto.convenioMedico();
        formData.vacinacaoEmDia = parseTri(dto.vacinacaoEmDia());
        formData.teveDoencaContagiosa = (dto.doencaContagiosa() != null && !dto.doencaContagiosa().isEmpty()) ? Boolean.TRUE : Boolean.FALSE;
        formData.doencaContagiosa = dto.doencaContagiosa();
        formData.fazUsoMedicacoes = (dto.usoMedicacoes() != null && !dto.usoMedicacoes().isEmpty()) ? Boolean.TRUE : Boolean.FALSE;
        formData.medicacoes = dto.usoMedicacoes();
        formData.servicosFrequentados = dto.servicosSaudeOuEducacao();
        formData.inicioEscolarizacao = dto.inicioEscolarizacao();
        formData.apresentaDificuldades = (dto.dificuldadesEscolares() != null && !dto.dificuldadesEscolares().isEmpty()) ? Boolean.TRUE : Boolean.FALSE;
        formData.dificuldades = dto.dificuldadesEscolares();
        formData.apoioPedagogicoEmCasa = parseTri(dto.apoioPedagogicoEmCasa());
        formData.apoioPedagogico = null;
        formData.duracaoGestacao = dto.duracaoGestacao();
        formData.fezPreNatal = parseTri(dto.fezPreNatal());
        formData.prematuridade = dto.prematuridade();
        formData.prematuridadeOcorrida = (dto.prematuridade() != null && !dto.prematuridade().isEmpty()) ? Boolean.TRUE : Boolean.FALSE;
        formData.cidadeNascimento = dto.cidadeNascimento();
        formData.maternidade = dto.maternidadeNascimento();
        formData.tipoParto = dto.tipoParto();
        formData.chorouAoNascer = parseTri(dto.chorouAoNascer());
        formData.ficouRoxo = parseTri(dto.ficouRoxo());
        formData.usoIncubadora = parseTri(dto.usoIncubadora());
        formData.foiAmamentado = parseTri(dto.foiAmamentado());
        formData.sustentouCabeca = (dto.sustentouCabecaMeses() != null && !dto.sustentouCabecaMeses().isEmpty()) ? Boolean.TRUE : Boolean.FALSE;
        formData.sustentouCabecaMeses = dto.sustentouCabecaMeses();
        formData.engatinhou = (dto.engatinhouMeses() != null && !dto.engatinhouMeses().isEmpty()) ? Boolean.TRUE : Boolean.FALSE;
        formData.engatinhouMeses = dto.engatinhouMeses();
        formData.sentou = (dto.sentouMeses() != null && !dto.sentouMeses().isEmpty()) ? Boolean.TRUE : Boolean.FALSE;
        formData.sentouMeses = dto.sentouMeses();
        formData.andou = (dto.andouMeses() != null && !dto.andouMeses().isEmpty()) ? Boolean.TRUE : Boolean.FALSE;
        formData.andouMeses = dto.andouMeses();
        formData.precisouTerapia = (dto.precisouTerapiaMotivo() != null && !dto.precisouTerapiaMotivo().isEmpty()) ? Boolean.TRUE : Boolean.FALSE;
        formData.terapiaMotivo = dto.precisouTerapiaMotivo();
        formData.falou = (dto.falouMeses() != null && !dto.falouMeses().isEmpty()) ? Boolean.TRUE : Boolean.FALSE;
        formData.falouMeses = dto.falouMeses();
        formData.primeiroBalbucioMeses = dto.primeiroBalbucioMeses();
        formData.primeiraPalavraQuando = dto.primeiraPalavraQuando();
        formData.primeiraFraseQuando = dto.primeiraFraseQuando();
        formData.falaNaturalOuInibido = dto.falaNaturalOuInibido();
        formData.disturbioFala = dto.disturbioFala();
        formData.possuiDisturbio = (dto.disturbioFala() != null && !dto.disturbioFala().isEmpty()) ? Boolean.TRUE : Boolean.FALSE;
        formData.dormeSozinho = dto.dormeSozinho();
        formData.temQuartoProprio = dto.temQuartoProprio();
        formData.sonoCalmoOuAgitado = dto.sonoCalmoOuAgitado();
        formData.respeitaRegras = dto.respeitaRegras();
        formData.desmotivado = dto.desmotivado();
        formData.agressivo = dto.agressivo();
        formData.apresentaInquietacao = dto.apresentaInquietacao();
    }
}
