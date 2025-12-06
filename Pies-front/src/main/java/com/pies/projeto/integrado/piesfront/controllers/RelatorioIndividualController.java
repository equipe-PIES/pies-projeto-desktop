package com.pies.projeto.integrado.piesfront.controllers;

import com.pies.projeto.integrado.piesfront.dto.CreateRelatorioIndividualDTO;
import com.pies.projeto.integrado.piesfront.dto.EducandoDTO;
import com.pies.projeto.integrado.piesfront.dto.RelatorioIndividualRequestDTO;
import com.pies.projeto.integrado.piesfront.dto.UserInfoDTO;
import com.pies.projeto.integrado.piesfront.dto.ProfessorDTO;
import com.pies.projeto.integrado.piesfront.services.AuthService;
import javafx.fxml.FXML;
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
    private Label nameUser;
    @FXML
    private Label cargoUser;
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
    private boolean novoRegistro = false;

    public void setEducando(EducandoDTO educando) {
        System.out.println("=== RelatorioIndividualController.setEducando ===");
        System.out.println("Educando: " + (educando != null ? educando.nome() + " (ID: " + educando.id() + ")" : "null"));
        System.out.println("novoRegistro: " + novoRegistro);
        this.educando = educando;
        atualizarIndicador();
        if (!novoRegistro) {
            System.out.println("Chamando carregarRelatorioExistente...");
            carregarRelatorioExistente();
        } else {
            System.out.println("Modo novo registro - não carregando dados existentes");
        }
        populateFromFormData();
    }

    public void setNovoRegistro(boolean novo) {
        this.novoRegistro = novo;
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
        javafx.application.Platform.runLater(() -> {
            atualizarNomeUsuarioAsync();
        });
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
            com.pies.projeto.integrado.piesfront.services.AtendimentoFlowService.getInstance().concluirRelatorioIndividual(educando.id());
            NotificacaoController.agendar("Relatório Final registrado com sucesso!", true);
            handleCancelAction();
        } else {
            showPopup("Falha ao registrar Relatório Final.", false);
            showValidation();
        }
    }

    private void abrir(String resource, int step) {
        captureCurrentStepData();
        if (anamnese != null) {
            Janelas.carregarTela(new javafx.event.ActionEvent(anamnese, null), resource, "Relatório Individual", controller -> {
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
        NotificacaoController.exibir(anamnese, mensagem, sucesso);
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
        System.out.println("=== populateFromFormData (Step " + currentStep + ") ===");
        if (currentStep == 1) {
            System.out.println("Populando step 1:");
            System.out.println("  dadosFuncionais: " + (formData.dadosFuncionais != null ? formData.dadosFuncionais.substring(0, Math.min(50, formData.dadosFuncionais.length())) + "..." : "null"));
            System.out.println("  funcionalidadeCognitiva: " + (formData.funcionalidadeCognitiva != null ? formData.funcionalidadeCognitiva.substring(0, Math.min(50, formData.funcionalidadeCognitiva.length())) + "..." : "null"));
            if (dificuldadesRaciocinio != null) dificuldadesRaciocinio.setText(val(formData.dadosFuncionais));
            if (dificuldadesRaciocinio1 != null) dificuldadesRaciocinio1.setText(val(formData.funcionalidadeCognitiva));
            if (dificuldadesRaciocinio11 != null) dificuldadesRaciocinio11.setText(val(formData.alfabetizacaoLetramento));
        } else if (currentStep == 2) {
            System.out.println("Populando step 2:");
            System.out.println("  adaptacoesCurriculares: " + (formData.adaptacoesCurriculares != null ? formData.adaptacoesCurriculares.substring(0, Math.min(50, formData.adaptacoesCurriculares.length())) + "..." : "null"));
            if (dificuldadesRaciocinio != null) dificuldadesRaciocinio.setText(val(formData.adaptacoesCurriculares));
            if (dificuldadesRaciocinio1 != null) dificuldadesRaciocinio1.setText(val(formData.participacaoAtividades));
            if (dificuldadesRaciocinio11 != null) dificuldadesRaciocinio11.setText(val(formData.autonomia));
        } else if (currentStep == 3) {
            System.out.println("Populando step 3:");
            System.out.println("  interacaoProfessora: " + (formData.interacaoProfessora != null ? formData.interacaoProfessora.substring(0, Math.min(50, formData.interacaoProfessora.length())) + "..." : "null"));
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
        System.out.println("=== carregarRelatorioExistente ===");
        if (educando == null || educando.id() == null) {
            System.out.println("Educando ou ID é null, retornando");
            return;
        }
        System.out.println("Buscando relatórios para educando ID: " + educando.id());
        var lista = authService.getRelatoriosIndividuaisPorEducando(educando.id());
        System.out.println("Relatórios encontrados: " + (lista != null ? lista.size() : "null"));
        if (lista == null || lista.isEmpty()) {
            System.out.println("Nenhum relatório encontrado");
            return;
        }
        var dto = lista.get(lista.size() - 1);
        System.out.println("Carregando último relatório (ID: " + dto.id() + ")");
        System.out.println("Dados funcionais: " + (dto.dadosFuncionais() != null ? dto.dadosFuncionais().substring(0, Math.min(50, dto.dadosFuncionais().length())) + "..." : "null"));
        formData.dadosFuncionais = dto.dadosFuncionais();
        formData.funcionalidadeCognitiva = dto.funcionalidadeCognitiva();
        formData.alfabetizacaoLetramento = dto.alfabetizacaoLetramento();
        formData.adaptacoesCurriculares = dto.adaptacoesCurriculares();
        formData.participacaoAtividades = dto.participacaoAtividades();
        formData.autonomia = dto.autonomia();
        formData.interacaoProfessora = dto.interacaoProfessora();
        formData.atividadesVidaDiaria = dto.atividadesVidaDiaria();
        System.out.println("Dados carregados no formData");
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
