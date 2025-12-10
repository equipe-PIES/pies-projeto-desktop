package com.pies.projeto.integrado.piesfront.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import com.pies.projeto.integrado.piesfront.dto.EducandoDTO;
import com.pies.projeto.integrado.piesfront.dto.RelatorioIndividualDTO;
import com.pies.projeto.integrado.piesfront.services.AtendimentoFlowService;
import com.pies.projeto.integrado.piesfront.services.AuthService;
import com.utils.Janelas;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import com.pies.projeto.integrado.piesfront.controllers.NotificacaoController;

/**
 * Controller para a tela de progresso de atendimento
 * Exibe o progresso do atendimento do educando em formato de popup
 */
public class ProgressoAtendimentoController implements Initializable {
    
    @FXML
    private Button iniciarAtendimento;
    
    @FXML
    private Button viewRelatorios;
    
    @FXML
    private Button statusAnamnese;
    
    @FXML
    private Button statusDI;
    
    @FXML
    private Button statusPDI;
    
    @FXML
    private Button statusPAEE;
    @FXML
    private Button editarAnamnese;
    @FXML
    private Button verAnamnese;
    @FXML
    private Button excluirAnamnese;
    @FXML
    private Button editarDiagnosticoInicial;
    @FXML
    private Button verDiagnosticoInicial;
    @FXML
    private Button excluirDiagnosticoInicial;
    @FXML
    private Button editarPDI;
    @FXML
    private Button verPDI;
    @FXML
    private Button excluirPDI;
    @FXML
    private Button editarPAEE;
    @FXML
    private Button verPAEE;
    @FXML
    private Button excluirPAEE;
    @FXML
    private Button editarRelatorioIndividual;
    @FXML
    private Button verRelatorioIndividual;
    @FXML
    private Button excluirRelatorioIndividual;
    @FXML
    private Button editarRelatorioIndividual1;
    @FXML
    private Button baixarRelatorioIndividual;
    
    @FXML
    private Button closeProgressoAtd;

    @FXML
    private javafx.scene.layout.BorderPane progressoAtendimentoRoot;
    
    private EducandoDTO educando;
    private final AuthService authService = AuthService.getInstance();
    private boolean hasAnamneseCached;
    private boolean hasDICached;
    private boolean hasPDICached;
    private boolean hasPAEECached;
    private boolean hasRICached;
    
    /**
     * Define os dados do educando
     * @param educando DTO com as informações do educando
     */
    public void setEducando(EducandoDTO educando) {
        this.educando = educando;
        prepararUIParaCarregamento();
        javafx.application.Platform.runLater(() -> carregarDadosAsync());
    }
    
    /**
     * Atualiza os dados da tela com base no educando
     */
    private void prepararUIParaCarregamento() {
        if (statusAnamnese != null) { statusAnamnese.setText("Carregando..."); statusAnamnese.setDisable(true); }
        if (statusDI != null) { statusDI.setText("Carregando..."); statusDI.setDisable(true); }
        if (statusPDI != null) { statusPDI.setText("Carregando..."); statusPDI.setDisable(true); }
        if (statusPAEE != null) { statusPAEE.setText("Carregando..."); statusPAEE.setDisable(true); }
    }

    private void carregarDadosAsync() {
        if (educando == null || educando.id() == null) {
            atualizarUIComResultados(false, false, false, false, false);
            return;
        }
        java.util.concurrent.CompletableFuture.runAsync(() -> {
            boolean a = false;
            boolean di = false;
            boolean pdi = false;
            boolean paee = false;
            boolean ri = false;
            try {
                java.util.Map<String, Object> m = authService.getProgressoPorEducando(educando.id());
                Object oa = m.get("anamnese");
                a = oa instanceof Boolean ? (Boolean) oa : false;
                Object odi = m.get("diagnosticoInicial");
                di = odi instanceof Boolean ? (Boolean) odi : false;
                Object opdi = m.get("pdiCount");
                pdi = opdi instanceof Number ? ((Number) opdi).intValue() > 0 : false;
                Object opaee = m.get("paeeCount");
                paee = opaee instanceof Number ? ((Number) opaee).intValue() > 0 : false;
                Object ori = m.get("relatorioCount");
                ri = ori instanceof Number ? ((Number) ori).intValue() > 0 : false;
            } catch (Exception ignored) {}
            final boolean fa = a, fdi = di, fpdi = pdi, fpaee = paee, fri = ri;
            javafx.application.Platform.runLater(() -> atualizarUIComResultados(fa, fdi, fpdi, fpaee, fri));
        });
    }

    private void atualizarUIComResultados(boolean a, boolean di, boolean pdi, boolean paee, boolean ri) {
        hasAnamneseCached = a;
        hasDICached = di;
        hasPDICached = pdi;
        hasPAEECached = paee;
        hasRICached = ri;
        if (statusAnamnese != null) { statusAnamnese.setText(a ? "Novo" : "Iniciar"); statusAnamnese.setDisable(false); }
        if (statusDI != null) { statusDI.setText(di ? "Novo" : "Iniciar"); statusDI.setDisable(false); }
        if (statusPDI != null) { statusPDI.setText(pdi ? "Novo" : "Iniciar"); statusPDI.setDisable(false); }
        if (statusPAEE != null) { statusPAEE.setText(paee ? "Novo" : "Iniciar"); statusPAEE.setDisable(false); }
        atualizarVisibilidadePorExistenciaFromCache();
    }
    
    /**
     * Handler para o botão de iniciar atendimento
     */
    @FXML
    private void handleIniciarAtendimentoAction() {
        if (educando == null) {
            return;
        }
        AtendimentoFlowService.Etapa etapa = AtendimentoFlowService.getInstance().getEtapaAtual(educando.id());
        if (etapa == AtendimentoFlowService.Etapa.ANAMNESE) {
            navegarNoStagePai("/com/pies/projeto/integrado/piesfront/screens/anamnese-1.fxml", "Anamnese");
        } else if (etapa == AtendimentoFlowService.Etapa.DI) {
            navegarNoStagePai("/com/pies/projeto/integrado/piesfront/screens/diagnostico-1.fxml", "Diagnóstico Inicial");
        } else if (etapa == AtendimentoFlowService.Etapa.PDI) {
            navegarNoStagePai("/com/pies/projeto/integrado/piesfront/screens/pdi-1.fxml", "PDI");
        } else if (etapa == AtendimentoFlowService.Etapa.PAEE) {
            navegarNoStagePai("/com/pies/projeto/integrado/piesfront/screens/paee-1.fxml", "PAEE");
        }
    }
    
    /**
     * Handler para o botão de visualizar relatórios
     */
    @FXML
    private void handleViewRelatoriosAction() {
        // TODO: Implementar lógica para visualizar relatórios
        System.out.println("Visualizar relatórios: " + (educando != null ? educando.id() : "null"));
    }
    
    /**
     * Handler para o botão de status da anamnese
     */
    @FXML
    private void handleStatusAnamneseAction() {
        System.out.println("=== handleStatusAnamneseAction chamado ===");
        if (educando == null) {
            System.err.println("Educando é null!");
            return;
        }
        System.out.println("Educando: " + educando.nome() + " (ID: " + educando.id() + ")");
        // Verifica se já existe anamnese - se não existir, abre em modo novo
        var anamnese = authService.getAnamnesePorEducando(educando.id());
        boolean carregar = anamnese != null;
        navegarNoStagePaiComModo("/com/pies/projeto/integrado/piesfront/screens/anamnese-1.fxml", "Anamnese", carregar);
    }
    
    /**
     * Handler para o botão de status do diagnóstico inicial
     */
    private String getIdFromMap(java.util.Map<String, Object> m) {
        if (m == null) return null;
        Object id = m.get("id");
        return id instanceof String s ? s : null;
    }

    @FXML
    private void handleEditAnamneseAction() {
        if (educando == null) {
            return;
        }
        // Botão Editar sempre carrega dados existentes
        navegarNoStagePaiComModo("/com/pies/projeto/integrado/piesfront/screens/anamnese-1.fxml", "Anamnese", true);
    }

    @FXML
    private void handleStatusDIAction() {
        System.out.println("=== handleStatusDIAction chamado ===");
        if (educando == null) {
            System.err.println("Educando é null!");
            return;
        }
        System.out.println("Educando: " + educando.nome() + " (ID: " + educando.id() + ")");
        // Verifica se já existe DI - se não existir, abre em modo novo
        var di = authService.getDiagnosticoInicialPorEducandoRaw(educando.id());
        boolean carregar = di != null;
        navegarNoStagePaiComModo("/com/pies/projeto/integrado/piesfront/screens/diagnostico-1.fxml", "Diagnóstico Inicial", carregar);
    }

    @FXML
    private void handleEditDIAction() {
        if (educando == null) {
            return;
        }
        // Botão Editar sempre carrega dados existentes
        navegarNoStagePaiComModo("/com/pies/projeto/integrado/piesfront/screens/diagnostico-1.fxml", "Diagnóstico Inicial", true);
    }

    @FXML
    private void handleEditPDIAction() {
        if (educando == null) {
            return;
        }
        navegarNoStagePai("/com/pies/projeto/integrado/piesfront/screens/pdi-1.fxml", "PDI");
    }

    @FXML
    private void handleEditPAEEAction() {
        if (educando == null) {
            return;
        }
        // Botão Editar sempre carrega dados existentes
        navegarNoStagePaiComModo("/com/pies/projeto/integrado/piesfront/screens/paee-1.fxml", "PAEE", true);
    }
    
    /**
     * Handler para o botão de status do PDI
     */
    @FXML
    private void handleStatusPDIAction() {
        if (educando == null) {
            return;
        }
        navegarNoStagePaiComModo("/com/pies/projeto/integrado/piesfront/screens/pdi-1.fxml", "PDI", false);
    }
    
    /**
     * Handler para o botão de status do PAEE
     */
    @FXML
    private void handleStatusPAEEAction() {
        System.out.println("=== handleStatusPAEEAction chamado ===");
        if (educando == null) {
            System.err.println("Educando é null!");
            return;
        }
        System.out.println("Educando: " + educando.nome() + " (ID: " + educando.id() + ")");
        // Verifica se já existe PAEE - se não existir, abre em modo novo
        var paees = authService.getPaeesPorEducandoRaw(educando.id());
        boolean carregar = paees != null && !paees.isEmpty();
        navegarNoStagePaiComModo("/com/pies/projeto/integrado/piesfront/screens/paee-1.fxml", "PAEE", carregar);
    }
    
    /**
     * Handler para o botão de fechar
     */
    @FXML
    private void handleCloseAction() {
        Stage stage = (Stage) closeProgressoAtd.getScene().getWindow();
        stage.close();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (educando != null) {
            prepararUIParaCarregamento();
            javafx.application.Platform.runLater(() -> carregarDadosAsync());
        }
        if (editarAnamnese != null) editarAnamnese.setOnAction(e -> handleEditAnamneseAction());
        if (verAnamnese != null) verAnamnese.setOnAction(e -> handleEditAnamneseAction());
        if (excluirAnamnese != null) {
            excluirAnamnese.setOnAction(e -> {
                if (educando == null) return;
                if (educando.id() != null && authService.deletarAnamnesePorEducando(educando.id())) {
                    atualizarVisibilidadePorExistencia();
                    prepararUIParaCarregamento();
                    carregarDadosAsync();
                }
            });
        }
        if (editarDiagnosticoInicial != null) editarDiagnosticoInicial.setOnAction(e -> handleEditDIAction());
        if (verDiagnosticoInicial != null) verDiagnosticoInicial.setOnAction(e -> handleEditDIAction());
        if (excluirDiagnosticoInicial != null) excluirDiagnosticoInicial.setOnAction(e -> {
            if (educando == null) return;
            java.util.Map<String, Object> di = authService.getDiagnosticoInicialPorEducandoRaw(educando.id());
            String id = getIdFromMap(di);
            if (id != null && authService.deletarDiagnosticoInicial(id)) {
                atualizarVisibilidadePorExistencia();
                prepararUIParaCarregamento();
                carregarDadosAsync();
            }
        });
        if (editarPDI != null) editarPDI.setOnAction(e -> handleEditPDIAction());
        if (verPDI != null) verPDI.setOnAction(e -> handleEditPDIAction());
        if (excluirPDI != null) excluirPDI.setOnAction(e -> {
            if (educando == null) return;
            java.util.List<java.util.Map<String, Object>> pdis = authService.getPdisPorEducandoRaw(educando.id());
            if (pdis != null && !pdis.isEmpty()) {
                String id = getIdFromMap(pdis.get(pdis.size() - 1));
                if (id != null && authService.deletarPDI(id)) {
                    atualizarVisibilidadePorExistencia();
                    prepararUIParaCarregamento();
                    carregarDadosAsync();
                }
            }
        });
        if (editarPAEE != null) editarPAEE.setOnAction(e -> handleEditPAEEAction());
        if (verPAEE != null) verPAEE.setOnAction(e -> handleEditPAEEAction());
        if (excluirPAEE != null) excluirPAEE.setOnAction(e -> {

            if (educando == null) return;
            java.util.List<java.util.Map<String, Object>> paees = authService.getPaeesPorEducandoRaw(educando.id());
            if (paees != null && !paees.isEmpty()) {
                String id = getIdFromMap(paees.get(paees.size() - 1));
                if (id != null && authService.deletarPAEE(id)) {
                    atualizarVisibilidadePorExistencia();
                    prepararUIParaCarregamento();
                    carregarDadosAsync();
                }
            }
        });
        if (editarRelatorioIndividual != null) editarRelatorioIndividual.setOnAction(e -> {
            if (educando == null) return;
            // Botão Editar sempre carrega dados existentes
            navegarNoStagePaiComModo("/com/pies/projeto/integrado/piesfront/screens/relatorio-individual-1.fxml", "Relatório Individual", true);
        });
        if (editarRelatorioIndividual1 != null) editarRelatorioIndividual1.setOnAction(e -> {
            if (educando == null) return;
            // Botão Editar sempre carrega dados existentes
            navegarNoStagePaiComModo("/com/pies/projeto/integrado/piesfront/screens/relatorio-individual-1.fxml", "Relatório Individual", true);
        });
        if (verRelatorioIndividual != null) verRelatorioIndividual.setOnAction(e -> {
            if (educando == null) return;
            // Botão Ver sempre carrega dados existentes
            navegarNoStagePaiComModo("/com/pies/projeto/integrado/piesfront/screens/relatorio-individual-1.fxml", "Relatório Individual", true);
        });
        if (baixarRelatorioIndividual != null) baixarRelatorioIndividual.setOnAction(e -> {
            if (educando == null || educando.id() == null) return;
            java.util.List<RelatorioIndividualDTO> ris = authService.getRelatoriosIndividuaisPorEducando(educando.id());
            if (ris == null || ris.isEmpty()) {
                NotificacaoController.exibirTexto(progressoAtendimentoRoot, "Nenhum Relatório Individual encontrado para este educando.", false);
                return;
            }
            RelatorioIndividualDTO ri = ris.get(ris.size() - 1);
            String id = ri.id();
            byte[] pdfBytes = authService.baixarRelatorioIndividualPDF(id);
            if (pdfBytes == null || pdfBytes.length == 0) {
                NotificacaoController.exibirTexto(progressoAtendimentoRoot, "Falha ao baixar PDF do Relatório Individual.", false);
                return;
            }
            String nomeBase = educando.nome() != null ? educando.nome().replaceAll("\\s+", "_").replaceAll("[^a-zA-Z0-9_]", "") : "Educando";
            String shortId = id != null ? id.substring(0, Math.min(8, id.length())) : "RI";
            String nomeArquivo = "Relatorio_Final_" + nomeBase + "_" + shortId + ".pdf";
            FileChooser chooser = new FileChooser();
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF (*.pdf)", "*.pdf"));
            chooser.setInitialFileName(nomeArquivo);
            Stage stage = (Stage) closeProgressoAtd.getScene().getWindow();
            java.io.File destino = chooser.showSaveDialog(stage);
            if (destino == null) {
                return;
            }
            try {
                java.nio.file.Files.write(destino.toPath(), pdfBytes);
                NotificacaoController.exibirTexto(progressoAtendimentoRoot, "Relatório Individual baixado com sucesso!", true);
            } catch (Exception ex) {
                NotificacaoController.exibirTexto(progressoAtendimentoRoot, "Erro ao salvar o arquivo PDF.", false);
            }
        });
        if (excluirRelatorioIndividual != null) excluirRelatorioIndividual.setOnAction(e -> {
            if (educando == null) return;
            java.util.List<RelatorioIndividualDTO> ris = authService.getRelatoriosIndividuaisPorEducando(educando.id());
            if (ris != null && !ris.isEmpty()) {
                String id = ris.get(ris.size() - 1).id();
                if (id != null && authService.deletarRelatorioIndividual(id)) {
                    atualizarVisibilidadePorExistencia();
                    prepararUIParaCarregamento();
                    carregarDadosAsync();
                }
            }
        });
    }

    private void navegarNoStagePai(String resource, String titulo) {
        Stage popupStage = (Stage) closeProgressoAtd.getScene().getWindow();
        Stage parentStage = (Stage) popupStage.getOwner();
        if (parentStage == null) {
            parentStage = popupStage;
        }
        var sourceNode = parentStage.getScene() != null ? parentStage.getScene().getRoot() : closeProgressoAtd;
        Janelas.carregarTela(new ActionEvent(sourceNode, null), resource, titulo, controller -> {
            if (controller instanceof AnamneseController c) {
                c.setEducando(educando);
            } else if (controller instanceof PDIController c) {
                c.setEducando(educando);
            } else if (controller instanceof PAEEController c) {
                c.setEducando(educando);
            } else if (controller instanceof RelatorioIndividualController c) {
                c.setEducando(educando);
            } else if (controller instanceof DIController c) {
                c.setEducando(educando);
            }
        });
        popupStage.close();
    }

    private void navegarNoStagePaiComModo(String resource, String titulo, boolean carregarDadosExistentes) {
        System.out.println("=== navegarNoStagePaiComModo ===");
        System.out.println("Resource: " + resource);
        System.out.println("Titulo: " + titulo);
        System.out.println("carregarDadosExistentes: " + carregarDadosExistentes);
        System.out.println("Educando: " + (educando != null ? educando.nome() + " (ID: " + educando.id() + ")" : "null"));
        
        Stage popupStage = (Stage) closeProgressoAtd.getScene().getWindow();
        Stage parentStage = (Stage) popupStage.getOwner();
        if (parentStage == null) {
            parentStage = popupStage;
        }
        var sourceNode = parentStage.getScene() != null ? parentStage.getScene().getRoot() : closeProgressoAtd;
        Janelas.carregarTela(new ActionEvent(sourceNode, null), resource, titulo, controller -> {
            if (controller instanceof AnamneseController c) {
                System.out.println("Controller é AnamneseController, setando educando...");
                if (!carregarDadosExistentes) {
                    c.setModoNovo();
                }
                c.setEducando(educando);
            } else if (controller instanceof PDIController c) {
                System.out.println("Controller é PDIController, setando educando...");
                c.setNovoRegistro(!carregarDadosExistentes);
                c.setEducando(educando);
            } else if (controller instanceof PAEEController c) {
                System.out.println("Controller é PAEEController, setando educando...");
                c.setNovoRegistro(!carregarDadosExistentes);
                c.setEducando(educando);
            } else if (controller instanceof DIController c) {
                System.out.println("Controller é DIController, setando educando...");
                c.setNovoRegistro(!carregarDadosExistentes);
                c.setEducando(educando);
            } else if (controller instanceof RelatorioIndividualController c) {
                System.out.println("Controller é RelatorioIndividualController, setando educando...");
                System.out.println("Setando novoRegistro = " + !carregarDadosExistentes);
                c.setNovoRegistro(!carregarDadosExistentes);
                c.setEducando(educando);
            }
        });
        popupStage.close();
    }

    private void atualizarVisibilidadePorExistencia() {
        boolean hasAnamnese = false;
        boolean hasDI = false;
        boolean hasPDI = false;
        boolean hasPAEE = false;
        boolean hasRI = false;
        if (educando != null && educando.id() != null) {
            java.util.Map<String, Object> m = authService.getProgressoPorEducando(educando.id());
            Object oa = m.get("anamnese");
            hasAnamnese = oa instanceof Boolean ? (Boolean) oa : false;
            Object odi = m.get("diagnosticoInicial");
            hasDI = odi instanceof Boolean ? (Boolean) odi : false;
            Object opdi = m.get("pdiCount");
            hasPDI = opdi instanceof Number ? ((Number) opdi).intValue() > 0 : false;
            Object opaee = m.get("paeeCount");
            hasPAEE = opaee instanceof Number ? ((Number) opaee).intValue() > 0 : false;
            Object ori = m.get("relatorioCount");
            hasRI = ori instanceof Number ? ((Number) ori).intValue() > 0 : false;
        }
        if (editarAnamnese != null) { editarAnamnese.setVisible(true); editarAnamnese.setManaged(true); editarAnamnese.setDisable(!hasAnamnese); }
        if (verAnamnese != null) { verAnamnese.setVisible(true); verAnamnese.setManaged(true); verAnamnese.setDisable(!hasAnamnese); }
        if (excluirAnamnese != null) { excluirAnamnese.setVisible(true); excluirAnamnese.setManaged(true); excluirAnamnese.setDisable(!hasAnamnese); }

        if (editarDiagnosticoInicial != null) { editarDiagnosticoInicial.setVisible(true); editarDiagnosticoInicial.setManaged(true); editarDiagnosticoInicial.setDisable(!hasDI); }
        if (verDiagnosticoInicial != null) { verDiagnosticoInicial.setVisible(true); verDiagnosticoInicial.setManaged(true); verDiagnosticoInicial.setDisable(!hasDI); }
        if (excluirDiagnosticoInicial != null) { excluirDiagnosticoInicial.setVisible(true); excluirDiagnosticoInicial.setManaged(true); excluirDiagnosticoInicial.setDisable(!hasDI); }

        if (editarPDI != null) { editarPDI.setVisible(true); editarPDI.setManaged(true); editarPDI.setDisable(!hasPDI); }
        if (verPDI != null) { verPDI.setVisible(true); verPDI.setManaged(true); verPDI.setDisable(!hasPDI); }
        if (excluirPDI != null) { excluirPDI.setVisible(true); excluirPDI.setManaged(true); excluirPDI.setDisable(!hasPDI); }

        if (editarPAEE != null) { editarPAEE.setVisible(true); editarPAEE.setManaged(true); editarPAEE.setDisable(!hasPAEE); }
        if (verPAEE != null) { verPAEE.setVisible(true); verPAEE.setManaged(true); verPAEE.setDisable(!hasPAEE); }
        if (excluirPAEE != null) { excluirPAEE.setVisible(true); excluirPAEE.setManaged(true); excluirPAEE.setDisable(!hasPAEE); }

        if (editarRelatorioIndividual != null) { editarRelatorioIndividual.setVisible(true); editarRelatorioIndividual.setManaged(true); editarRelatorioIndividual.setDisable(!hasRI); }
        if (verRelatorioIndividual != null) { verRelatorioIndividual.setVisible(true); verRelatorioIndividual.setManaged(true); verRelatorioIndividual.setDisable(!hasRI); }
        if (excluirRelatorioIndividual != null) { excluirRelatorioIndividual.setVisible(true); excluirRelatorioIndividual.setManaged(true); excluirRelatorioIndividual.setDisable(!hasRI); }
        if (editarRelatorioIndividual1 != null) { editarRelatorioIndividual1.setVisible(true); editarRelatorioIndividual1.setManaged(true); editarRelatorioIndividual1.setDisable(!hasRI); }
        if (baixarRelatorioIndividual != null) { baixarRelatorioIndividual.setVisible(true); baixarRelatorioIndividual.setManaged(true); baixarRelatorioIndividual.setDisable(!hasRI); }
    }

    private void atualizarVisibilidadePorExistenciaFromCache() {
        if (editarAnamnese != null) { editarAnamnese.setVisible(true); editarAnamnese.setManaged(true); editarAnamnese.setDisable(!hasAnamneseCached); }
        if (verAnamnese != null) { verAnamnese.setVisible(true); verAnamnese.setManaged(true); verAnamnese.setDisable(!hasAnamneseCached); }
        if (excluirAnamnese != null) { excluirAnamnese.setVisible(true); excluirAnamnese.setManaged(true); excluirAnamnese.setDisable(!hasAnamneseCached); }

        if (editarDiagnosticoInicial != null) { editarDiagnosticoInicial.setVisible(true); editarDiagnosticoInicial.setManaged(true); editarDiagnosticoInicial.setDisable(!hasDICached); }
        if (verDiagnosticoInicial != null) { verDiagnosticoInicial.setVisible(true); verDiagnosticoInicial.setManaged(true); verDiagnosticoInicial.setDisable(!hasDICached); }
        if (excluirDiagnosticoInicial != null) { excluirDiagnosticoInicial.setVisible(true); excluirDiagnosticoInicial.setManaged(true); excluirDiagnosticoInicial.setDisable(!hasDICached); }

        if (editarPDI != null) { editarPDI.setVisible(true); editarPDI.setManaged(true); editarPDI.setDisable(!hasPDICached); }
        if (verPDI != null) { verPDI.setVisible(true); verPDI.setManaged(true); verPDI.setDisable(!hasPDICached); }
        if (excluirPDI != null) { excluirPDI.setVisible(true); excluirPDI.setManaged(true); excluirPDI.setDisable(!hasPDICached); }

        if (editarPAEE != null) { editarPAEE.setVisible(true); editarPAEE.setManaged(true); editarPAEE.setDisable(!hasPAEECached); }
        if (verPAEE != null) { verPAEE.setVisible(true); verPAEE.setManaged(true); verPAEE.setDisable(!hasPAEECached); }

        if (editarRelatorioIndividual != null) { editarRelatorioIndividual.setVisible(true); editarRelatorioIndividual.setManaged(true); editarRelatorioIndividual.setDisable(!hasRICached); }
        if (verRelatorioIndividual != null) { verRelatorioIndividual.setVisible(true); verRelatorioIndividual.setManaged(true); verRelatorioIndividual.setDisable(!hasRICached); }
        if (excluirRelatorioIndividual != null) { excluirRelatorioIndividual.setVisible(true); excluirRelatorioIndividual.setManaged(true); excluirRelatorioIndividual.setDisable(!hasRICached); }
        if (editarRelatorioIndividual1 != null) { editarRelatorioIndividual1.setVisible(true); editarRelatorioIndividual1.setManaged(true); editarRelatorioIndividual1.setDisable(!hasRICached); }
        if (baixarRelatorioIndividual != null) { baixarRelatorioIndividual.setVisible(true); baixarRelatorioIndividual.setManaged(true); baixarRelatorioIndividual.setDisable(!hasRICached); }
    }

    @FXML
    private void handleIniciarRelatorioIndividualAction() {
        if (educando == null) {
            return;
        }
        // Não cria relatório vazio - apenas abre a tela em modo novo
        navegarNoStagePaiComModo("/com/pies/projeto/integrado/piesfront/screens/relatorio-individual-1.fxml", "Relatório Individual", false);
    }
}

