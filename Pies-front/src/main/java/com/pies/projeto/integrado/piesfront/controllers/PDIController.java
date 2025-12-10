package com.pies.projeto.integrado.piesfront.controllers;

import com.pies.projeto.integrado.piesfront.dto.EducandoDTO;
import com.pies.projeto.integrado.piesfront.dto.CreatePDIDTO;
import com.pies.projeto.integrado.piesfront.dto.UserInfoDTO;
import com.pies.projeto.integrado.piesfront.dto.ProfessorDTO;
import com.pies.projeto.integrado.piesfront.services.AuthService;
import com.pies.projeto.integrado.piesfront.services.AtendimentoFlowService;
import com.utils.Janelas;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.StageStyle;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Label;
import javafx.scene.control.ChoiceBox;
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
    private Label nameUser;
    @FXML
    private Label cargoUser;
    @FXML
    private TextField periodoPlano, horarioAtendimento;
    @FXML
    private ChoiceBox<String> frequenciaSemana, diasSemana, composicaoGrupo;
    @FXML
    private TextArea objetivosPlano;
    @FXML
    private TextArea potencialidadesTextArea;
    @FXML
    private TextArea necessidadesTextArea;
    @FXML
    private TextArea habilidadesTextArea;
    @FXML
    private TextArea atividadesTextArea;
    @FXML
    private TextArea recursosMateriaisTextArea;
    @FXML
    private TextArea recursosAdequacaoTextArea;
    @FXML
    private TextArea recursosProduzidosTextArea;
    @FXML
    private TextArea parceriasTextArea;

    private EducandoDTO educando;
    private int currentStep = 1;
    private final AuthService authService = AuthService.getInstance();
    private PDIFormData formData = new PDIFormData();
    // private boolean modoNovo = false;

    // public void setEducando(EducandoDTO educando) {
    //     this.educando = educando;
    //     // Se já foi inicializado E formData está vazio, carrega agora
    //     if (anamnese != null && formData.periodoPlanoAEE == null && !modoNovo) {
    //         carregarPdiExistente();
    //         preencherCamposComFormData();
    //     }
    // }

    // /**
    //  * Define que o controller está em modo de novo cadastro.
    //  * Neste modo, não carrega dados existentes.
    //  */
    // public void setModoNovo() {
    //     this.modoNovo = true;
    //     this.formData = new PDIFormData(); // Limpa os dados
    private boolean novoRegistro = false;
    private boolean somenteLeitura = false;

    

    public void setEducando(EducandoDTO educando) {
        this.educando = educando;
        atualizarIndicadorDeTela();
        if (!novoRegistro) {
            carregarPdiExistente();
        } else {
            this.formData = new PDIFormData();
        }
        preencherCamposComFormData();
    }

    public void setFormData(PDIFormData data) {
        if (data != null) {
            this.formData = data;
            preencherCamposComFormData();
        }
    }

    public void setNovoRegistro(boolean novo) {
        this.novoRegistro = novo;
        if (novo) {
            this.formData = new PDIFormData();
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
    private void handleBackAction() {
        captureCurrentStepData();
        if (currentStep <= 1) {
            handleCancelAction();
            return;
        }
        if (currentStep == 2) abrir("/com/pies/projeto/integrado/piesfront/screens/pdi-1.fxml", "PDI", 1);
        else if (currentStep == 3) abrir("/com/pies/projeto/integrado/piesfront/screens/pdi-2.fxml", "PDI", 2);
        else if (currentStep == 4) abrir("/com/pies/projeto/integrado/piesfront/screens/pdi-3.fxml", "PDI", 3);
    }

    @FXML
    private void handleGoToPdi1() {
        abrir("/com/pies/projeto/integrado/piesfront/screens/pdi-1.fxml", "PDI", 1);
    }

    @FXML
    private void handleGoToPdi2() {
        captureCurrentStepData();
        if (validatePdi1()) {
            abrir("/com/pies/projeto/integrado/piesfront/screens/pdi-2.fxml", "PDI", 2);
        } else {
            showValidation("Algum campo está em branco. Preencha para prosseguir.");
        }
    }

    @FXML
    private void handleGoToPdi3() {
        captureCurrentStepData();
        if (validatePdi2()) {
            abrir("/com/pies/projeto/integrado/piesfront/screens/pdi-3.fxml", "PDI", 3);
        } else {
            showValidation("Algum campo está em branco. Preencha para prosseguir.");
        }
    }

    @FXML
    private void handleGoToPdi4() {
        captureCurrentStepData();
        if (validatePdi3()) {
            abrir("/com/pies/projeto/integrado/piesfront/screens/pdi-4.fxml", "PDI", 4);
        } else {
            showValidation("Algum campo está em branco. Preencha para prosseguir.");
        }
    }

    @FXML
    private void handleConcluirAction() {
        captureCurrentStepData();
        if (somenteLeitura) {
            showValidation("Modo de visualização");
            return;
        }
        if (educando == null || educando.id() == null) {
            showValidation("Educando inválido.");
            return;
        }
        if (!validateAll()) {
            showValidation("Preencha todos os campos obrigatórios.");
            return;
        }
        try {
            String token = authService.getCurrentToken();
            if (token == null || token.isEmpty()) {
                showValidation("Sessão expirada.");
                return;
            }
            CreatePDIDTO dto = new CreatePDIDTO(
                    formData.periodoPlanoAEE,
                    formData.horarioTempoAtendimento,
                    mapFrequencia(formData.frequenciaAtendimento),
                    FXCollections.observableArrayList(mapDia(formData.diaSemana)),
                    mapComposicao(formData.composicaoAtendimento),
                    formData.objetivosPlano,
                    formData.potencialidades,
                    formData.necessidadesEducacionaisEspeciais,
                    formData.habilidades,
                    formData.atividadesASeremDesenvolvidas,
                    formData.recursosMateriais,
                    formData.recursosQueNecessitamAdequacao,
                    formData.recursosMateriaisASeremProduzidos,
                    formData.parceriasNecessarias,
                    authService.getProfessorId(),
                    educando.id()
            );
            boolean ok = authService.criarPDI(dto);
            if (ok) {
                com.pies.projeto.integrado.piesfront.services.AtendimentoFlowService.getInstance().concluirPDI(educando.id());
                NotificacaoController.agendar("PDI registrado com sucesso!", true);
                handleCancelAction();
            } else {
                showPopup("Falha ao enviar PDI.", false);
                showValidation("Falha ao enviar PDI.");
            }
        } catch (Exception e) {
            showPopup("Falha ao enviar PDI.", false);
            showValidation("Falha ao enviar PDI.");
        }
    }

    private void abrir(String resource, String titulo, int step) {
        if (anamnese != null) {
            boolean novoAtual = this.novoRegistro;
            Janelas.carregarTela(new javafx.event.ActionEvent(anamnese, null), resource, titulo, controller -> {
                if (controller instanceof PDIController c) {
                    c.setNovoRegistro(novoAtual);
                    c.setEducando(educando);
                    c.currentStep = step;
                    c.setFormData(formData);
                }
            });
        }
    }

    @FXML
    private void initialize() {
        inicializarChoiceBoxes();
        atualizarIndicadorDeTela();
        if (validationMsg != null) {
            validationMsg.setVisible(false);
            validationMsg.setManaged(true);
        }
        System.out.println("=== PDI INITIALIZE ===");
        System.out.println("Educando: " + (educando != null ? educando.nome() : "null"));
        System.out.println("FormData.periodoPlanoAEE: " + formData.periodoPlanoAEE);
        System.out.println("FormData.potencialidades: " + formData.potencialidades);
        // Se o educando já foi definido E o formData está vazio (primeira abertura)
        if (educando != null && formData.periodoPlanoAEE == null) {
            System.out.println("Carregando PDI do backend...");
            carregarPdiExistente();
        }
        // Sempre preenche os campos com o formData (seja recém carregado ou de navegação entre telas)
        preencherCamposComFormData();
        javafx.application.Platform.runLater(() -> {
            atualizarNomeUsuarioAsync();
        });
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

    private boolean validatePdi1() {
        boolean textEmpty = isEmpty(periodoPlano) || isEmpty(horarioAtendimento) || isEmpty(objetivosPlano);
        boolean choiceEmpty = isChoiceEmpty(frequenciaSemana) || isChoiceEmpty(diasSemana) || isChoiceEmpty(composicaoGrupo);
        return !textEmpty && !choiceEmpty;
    }

    private boolean validatePdi2() {
        boolean p2Empty = isEmpty(potencialidadesTextArea) || isEmpty(necessidadesTextArea) || isEmpty(habilidadesTextArea);
        return !p2Empty;
    }

    private boolean validatePdi3() {
        boolean p3Empty = isEmpty(atividadesTextArea) || isEmpty(recursosMateriaisTextArea) || isEmpty(recursosAdequacaoTextArea);
        return !p3Empty;
    }

    private boolean validateAll() {
        if (!validatePdi1()) return false;
        boolean p2Empty = !validatePdi2();
        boolean p3Empty = !validatePdi3();
        boolean p4Empty = isEmpty(recursosProduzidosTextArea) || isEmpty(parceriasTextArea);
        return !p2Empty && !p3Empty && !p4Empty;
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
        // if (educando == null) return false;
        // // Verifica no backend se existe anamnese cadastrada
        // try {
        //     com.pies.projeto.integrado.piesfront.dto.AnamneseDTO anamnese = authService.getAnamnesePorEducando(educando.id());
        //     return anamnese != null;
        // } catch (Exception e) {
        //     System.err.println("Erro ao verificar anamnese: " + e.getMessage());
        //     return false;
        // }
        if (educando == null || educando.id() == null) return false;
        var a = authService.getAnamnesePorEducando(educando.id());
        return a != null;
    }

    private void inicializarChoiceBoxes() {
        if (frequenciaSemana != null && frequenciaSemana.getItems().isEmpty()) {
            frequenciaSemana.getItems().addAll("Uma vez", "Duas vezes");
        }
        if (diasSemana != null && diasSemana.getItems().isEmpty()) {
            ObservableList<String> dias = FXCollections.observableArrayList(
                    "Segunda-feira", "Terça-feira", "Quarta-feira", "Quinta-feira");
            diasSemana.setItems(dias);
        }
        if (composicaoGrupo != null && composicaoGrupo.getItems().isEmpty()) {
            composicaoGrupo.getItems().addAll("Individual", "Coletivo");
        }
    }

    private void atualizarIndicadorDeTela() {
        if (indicadorDeTela == null) return;
        String nome = educando != null ? educando.nome() : "Aluno(a)";
        indicadorDeTela.setText("PDI (Plano de Desenvolvimento Individual) do aluno(a) " + nome);
    }

    private void captureCurrentStepData() {
        if (currentStep == 1) {
            formData.periodoPlanoAEE = getText(periodoPlano, formData.periodoPlanoAEE);
            formData.horarioTempoAtendimento = getText(horarioAtendimento, formData.horarioTempoAtendimento);
            formData.frequenciaAtendimento = getValue(frequenciaSemana, formData.frequenciaAtendimento);
            formData.diaSemana = getValue(diasSemana, formData.diaSemana);
            formData.composicaoAtendimento = getValue(composicaoGrupo, formData.composicaoAtendimento);
            formData.objetivosPlano = getText(objetivosPlano, formData.objetivosPlano);
        } else if (currentStep == 2) {
            formData.potencialidades = getText(potencialidadesTextArea, formData.potencialidades);
            formData.necessidadesEducacionaisEspeciais = getText(necessidadesTextArea, formData.necessidadesEducacionaisEspeciais);
            formData.habilidades = getText(habilidadesTextArea, formData.habilidades);
        } else if (currentStep == 3) {
            formData.atividadesASeremDesenvolvidas = getText(atividadesTextArea, formData.atividadesASeremDesenvolvidas);
            formData.recursosMateriais = getText(recursosMateriaisTextArea, formData.recursosMateriais);
            formData.recursosQueNecessitamAdequacao = getText(recursosAdequacaoTextArea, formData.recursosQueNecessitamAdequacao);
        } else if (currentStep == 4) {
            formData.recursosMateriaisASeremProduzidos = getText(recursosProduzidosTextArea, formData.recursosMateriaisASeremProduzidos);
            formData.parceriasNecessarias = getText(parceriasTextArea, formData.parceriasNecessarias);
        }
    }

    private void preencherCamposComFormData() {
        if (currentStep == 1) {
            if (periodoPlano != null && formData.periodoPlanoAEE != null) periodoPlano.setText(formData.periodoPlanoAEE);
            if (horarioAtendimento != null && formData.horarioTempoAtendimento != null) horarioAtendimento.setText(formData.horarioTempoAtendimento);
            setChoice(frequenciaSemana, formData.frequenciaAtendimento);
            setChoice(diasSemana, formData.diaSemana);
            setChoice(composicaoGrupo, formData.composicaoAtendimento);
            if (objetivosPlano != null && formData.objetivosPlano != null) objetivosPlano.setText(formData.objetivosPlano);
        } else if (currentStep == 2) {
            if (potencialidadesTextArea != null && formData.potencialidades != null) potencialidadesTextArea.setText(formData.potencialidades);
            if (necessidadesTextArea != null && formData.necessidadesEducacionaisEspeciais != null) necessidadesTextArea.setText(formData.necessidadesEducacionaisEspeciais);
            if (habilidadesTextArea != null && formData.habilidades != null) habilidadesTextArea.setText(formData.habilidades);
        } else if (currentStep == 3) {
            if (atividadesTextArea != null && formData.atividadesASeremDesenvolvidas != null) atividadesTextArea.setText(formData.atividadesASeremDesenvolvidas);
            if (recursosMateriaisTextArea != null && formData.recursosMateriais != null) recursosMateriaisTextArea.setText(formData.recursosMateriais);
            if (recursosAdequacaoTextArea != null && formData.recursosQueNecessitamAdequacao != null) recursosAdequacaoTextArea.setText(formData.recursosQueNecessitamAdequacao);
        } else if (currentStep == 4) {
            if (recursosProduzidosTextArea != null && formData.recursosMateriaisASeremProduzidos != null) recursosProduzidosTextArea.setText(formData.recursosMateriaisASeremProduzidos);
            if (parceriasTextArea != null && formData.parceriasNecessarias != null) parceriasTextArea.setText(formData.parceriasNecessarias);
        }
    }

    private String getText(TextField tf, String current) {
        if (tf == null) return current;
        String t = tf.getText() != null ? tf.getText().trim() : null;
        return t != null && !t.isEmpty() ? t : current;
    }

    private String getText(TextArea ta, String current) {
        if (ta == null) return current;
        String t = ta.getText() != null ? ta.getText().trim() : null;
        return t != null && !t.isEmpty() ? t : current;
    }

    private String getValue(ChoiceBox<String> cb, String current) {
        if (cb == null) return current;
        Object v = cb.getValue();
        String s = v != null ? v.toString() : null;
        return s != null && !s.isEmpty() ? s : current;
    }

    private void setChoice(ChoiceBox<String> cb, String value) {
        if (cb == null || value == null) return;
        if (cb.getItems() != null && !cb.getItems().isEmpty()) {
            for (String item : cb.getItems()) {
                if (item.equals(value)) {
                    cb.setValue(item);
                    break;
                }
            }
        }
    }

    private String mapFrequencia(String label) {
        if (label == null) return null;
        return switch (label) {
            case "Uma vez" -> "UMA_VEZ";
            case "Duas vezes" -> "DUAS_VEZES";
            default -> null;
        };
    }

    private String mapComposicao(String label) {
        if (label == null) return null;
        return switch (label) {
            case "Individual" -> "INDIVIDUAL";
            case "Coletivo" -> "COLETIVO";
            default -> null;
        };
    }

    private String mapDia(String label) {
        if (label == null) return null;
        return switch (label) {
            case "Segunda-feira" -> "SEGUNDA_FEIRA";
            case "Terça-feira" -> "TERCA_FEIRA";
            case "Quarta-feira" -> "QUARTA_FEIRA";
            case "Quinta-feira" -> "QUINTA_FEIRA";
            default -> null;
        };
    }

    private String toLabelFrequencia(String code) {
        if (code == null) return null;
        return switch (code) {
            case "UMA_VEZ" -> "Uma vez";
            case "DUAS_VEZES" -> "Duas vezes";
            default -> code;
        };
    }

    private String toLabelComposicao(String code) {
        if (code == null) return null;
        return switch (code) {
            case "INDIVIDUAL" -> "Individual";
            case "COLETIVO" -> "Coletivo";
            default -> code;
        };
    }

    private String toLabelDia(String code) {
        if (code == null) return null;
        return switch (code) {
            case "SEGUNDA_FEIRA" -> "Segunda-feira";
            case "TERCA_FEIRA" -> "Terça-feira";
            case "QUARTA_FEIRA" -> "Quarta-feira";
            case "QUINTA_FEIRA" -> "Quinta-feira";
            default -> code;
        };
    }

    private void carregarPdiExistente() {
        if (educando == null || educando.id() == null) return;
        java.util.List<java.util.Map<String, Object>> lista = authService.getPdisPorEducandoRaw(educando.id());
        if (lista == null || lista.isEmpty()) return;
        java.util.Map<String, Object> dto = lista.get(lista.size() - 1);
        Object o;
        o = dto.get("periodoPlanoAEE");
        if (o instanceof String s) formData.periodoPlanoAEE = s;
        o = dto.get("horarioTempoAtendimento");
        if (o instanceof String s) formData.horarioTempoAtendimento = s;
        o = dto.get("frequenciaAtendimento");
        if (o instanceof String s) formData.frequenciaAtendimento = toLabelFrequencia(s);
        o = dto.get("diasSemana");
        if (o instanceof java.util.List<?> l && !l.isEmpty()) {
            Object d = l.get(0);
            if (d instanceof String ds) formData.diaSemana = toLabelDia(ds);
        }
        o = dto.get("composicaoAtendimento");
        if (o instanceof String s) formData.composicaoAtendimento = toLabelComposicao(s);
        o = dto.get("objetivosPlano");
        if (o instanceof String s) formData.objetivosPlano = s;
        o = dto.get("potencialidades");
        if (o instanceof String s) formData.potencialidades = s;
        o = dto.get("necessidadesEducacionaisEspeciais");
        if (o instanceof String s) formData.necessidadesEducacionaisEspeciais = s;
        o = dto.get("habilidades");
        if (o instanceof String s) formData.habilidades = s;
        o = dto.get("atividadesASeremDesenvolvidas");
        if (o instanceof String s) formData.atividadesASeremDesenvolvidas = s;
        o = dto.get("recursosMateriais");
        if (o instanceof String s) formData.recursosMateriais = s;
        o = dto.get("recursosQueNecessitamAdequacao");
        if (o instanceof String s) formData.recursosQueNecessitamAdequacao = s;
        o = dto.get("recursosMateriaisASeremProduzidos");
        if (o instanceof String s) formData.recursosMateriaisASeremProduzidos = s;
        o = dto.get("parceriasNecessarias");
        if (o instanceof String s) formData.parceriasNecessarias = s;
    }

    public static class PDIFormData {
        public String periodoPlanoAEE;
        public String horarioTempoAtendimento;
        public String frequenciaAtendimento;
        public String diaSemana;
        public String composicaoAtendimento;
        public String objetivosPlano;
        public String potencialidades;
        public String necessidadesEducacionaisEspeciais;
        public String habilidades;
        public String atividadesASeremDesenvolvidas;
        public String recursosMateriais;
        public String recursosQueNecessitamAdequacao;
        public String recursosMateriaisASeremProduzidos;
        public String parceriasNecessarias;
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
}
