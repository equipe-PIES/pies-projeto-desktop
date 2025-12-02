package com.pies.projeto.integrado.piesfront.controllers;

import com.pies.projeto.integrado.piesfront.dto.EducandoDTO;
import com.pies.projeto.integrado.piesfront.dto.CreateRelatorioIndividualDTO;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import com.pies.projeto.integrado.piesfront.services.AtendimentoFlowService;
import com.pies.projeto.integrado.piesfront.services.AuthService;

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
    private Button iniciarRelatorioIndividual;

    @FXML
    private Button baixarRelatorioIndividual;
    @FXML
    private Button excluirRelatorioIndividual;
    @FXML
    private Button editarRelatorioIndividual1;
    
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
    private Button closeProgressoAtd;
    
    private EducandoDTO educando;
    private final AuthService authService = AuthService.getInstance();
    
    /**
     * Define os dados do educando
     * @param educando DTO com as informações do educando
     */
    public void setEducando(EducandoDTO educando) {
        this.educando = educando;
        atualizarDados();
    }
    
    /**
     * Atualiza os dados da tela com base no educando
     */
    private void atualizarDados() {
        if (educando == null) {
            return;
        }
        boolean hasAnamnese = false;
        boolean hasDI = false;
        boolean hasPDI = false;
        boolean hasPAEE = false;
        if (educando.id() != null) {
            var a = authService.getAnamnesePorEducando(educando.id());
            hasAnamnese = a != null;
            var di = authService.getDiagnosticoInicialPorEducandoRaw(educando.id());
            hasDI = di != null;
            var pdis = authService.getPdisPorEducandoRaw(educando.id());
            hasPDI = pdis != null && !pdis.isEmpty();
            var paees = authService.getPaeesPorEducandoRaw(educando.id());
            hasPAEE = paees != null && !paees.isEmpty();
        }
        if (statusAnamnese != null) {
            boolean concluido = hasAnamnese;
            statusAnamnese.setText(concluido ? "Concluído" : "Iniciar");
            statusAnamnese.setStyle(concluido ? "-fx-background-color: #2ecc71; -fx-text-fill: white;" : "");
            statusAnamnese.setDisable(concluido);
        }
        if (statusDI != null) {
            boolean concluido = hasDI;
            statusDI.setText(concluido ? "Concluído" : "Iniciar");
            statusDI.setStyle(concluido ? "-fx-background-color: #2ecc71; -fx-text-fill: white;" : "");
            statusDI.setDisable(concluido);
        }
        if (statusPDI != null) {
            boolean existeAlgum = hasPDI;
            statusPDI.setText(existeAlgum ? "Novo" : "Iniciar");
            statusPDI.setStyle("");
            statusPDI.setDisable(false);
        }
        if (statusPAEE != null) {
            boolean existeAlgum = hasPAEE;
            statusPAEE.setText(existeAlgum ? "Novo" : "Iniciar");
            statusPAEE.setStyle("");
            statusPAEE.setDisable(false);
        }
        atualizarVisibilidadePorExistencia();
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
        navegarNoStagePai("/com/pies/projeto/integrado/piesfront/screens/anamnese-1.fxml", "Anamnese");
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
        navegarNoStagePai("/com/pies/projeto/integrado/piesfront/screens/anamnese-1.fxml", "Anamnese");
    }

    @FXML
    private void handleStatusDIAction() {
        if (educando == null) {
            return;
        }
        navegarNoStagePai("/com/pies/projeto/integrado/piesfront/screens/diagnostico-1.fxml", "Diagnóstico Inicial");
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
        navegarNoStagePai("/com/pies/projeto/integrado/piesfront/screens/paee-1.fxml", "PAEE");
    }
    
    /**
     * Handler para o botão de status do PDI
     */
    @FXML
    private void handleStatusPDIAction() {
        if (educando == null) {
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/pies/projeto/integrado/piesfront/screens/pdi-1.fxml"));
            Parent root = loader.load();
            Object controller = loader.getController();
            if (controller instanceof PDIController c) {
                c.setNovoRegistro(true);
                c.setEducando(educando);
            }
            Stage popupStage = getStageFromAnyNode();
            Stage parentStage = (Stage) popupStage.getOwner();
            if (parentStage == null) parentStage = popupStage;
            parentStage.setTitle("PDI");
            parentStage.setScene(new Scene(root));
            parentStage.setMaximized(false);
            parentStage.setMaximized(true);
            parentStage.show();
            popupStage.close();
        } catch (Exception e) {
            System.err.println("Erro ao iniciar novo PDI: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Handler para o botão de status do PAEE
     */
    @FXML
    private void handleStatusPAEEAction() {
        if (educando == null) {
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/pies/projeto/integrado/piesfront/screens/paee-1.fxml"));
            Parent root = loader.load();
            Object controller = loader.getController();
            if (controller instanceof PAEEController c) {
                c.setNovoRegistro(true);
                c.setEducando(educando);
            }
            Stage popupStage = getStageFromAnyNode();
            Stage parentStage = (Stage) popupStage.getOwner();
            if (parentStage == null) parentStage = popupStage;
            parentStage.setTitle("PAEE");
            parentStage.setScene(new Scene(root));
            parentStage.setMaximized(false);
            parentStage.setMaximized(true);
            parentStage.show();
            popupStage.close();
        } catch (Exception e) {
            System.err.println("Erro ao iniciar novo PAEE: " + e.getMessage());
            e.printStackTrace();
        }
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
        // Inicialização
        if (educando != null) {
            atualizarDados();
        }
        if (closeProgressoAtd != null) { closeProgressoAtd.setVisible(false); closeProgressoAtd.setManaged(false); }
        if (editarAnamnese != null) editarAnamnese.setOnAction(e -> handleEditAnamneseAction());
        if (verAnamnese != null) verAnamnese.setOnAction(e -> handleEditAnamneseAction());
        if (excluirAnamnese != null) {
            excluirAnamnese.setOnAction(e -> {
                if (educando == null) return;
                var a = authService.getAnamnesePorEducando(educando.id());
                if (a != null) {
                    atualizarVisibilidadePorExistencia();
                    atualizarDados();
                }
            });
        }
        if (editarDiagnosticoInicial != null) editarDiagnosticoInicial.setOnAction(e -> handleStatusDIAction());
        if (verDiagnosticoInicial != null) verDiagnosticoInicial.setOnAction(e -> handleStatusDIAction());
        if (excluirDiagnosticoInicial != null) excluirDiagnosticoInicial.setOnAction(e -> {
            if (educando == null) return;
            java.util.Map<String, Object> di = authService.getDiagnosticoInicialPorEducandoRaw(educando.id());
            String id = getIdFromMap(di);
            if (id != null && authService.deletarDiagnosticoInicial(id)) {
                atualizarVisibilidadePorExistencia();
                atualizarDados();
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
                    atualizarDados();
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
                    atualizarDados();
                }
            }
        });
        if (editarRelatorioIndividual1 != null) editarRelatorioIndividual1.setOnAction(e -> {
            if (educando == null) return;
            navegarNoStagePai("/com/pies/projeto/integrado/piesfront/screens/relatorio-individual-1.fxml", "Relatório Individual");
        });
        if (excluirRelatorioIndividual != null) excluirRelatorioIndividual.setOnAction(e -> {
            if (educando == null) return;
            java.util.List<com.pies.projeto.integrado.piesfront.dto.RelatorioIndividualDTO> rels = authService.getRelatoriosIndividuaisPorEducando(educando.id());
            if (rels != null && !rels.isEmpty()) {
                String id = rels.get(rels.size() - 1).id();
                if (id != null && authService.deletarRelatorioIndividual(id)) {
                    atualizarVisibilidadePorExistencia();
                    atualizarDados();
                }
            }
        });
    }

    private void navegarNoStagePai(String resource, String titulo) {
        try {
            System.out.println("=== navegarNoStagePai ===");
            System.out.println("Resource: " + resource);
            System.out.println("Título: " + titulo);
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource(resource));
            Parent root = loader.load();
            System.out.println("FXML carregado com sucesso");
            
            Object controller = loader.getController();
            if (controller instanceof AnamneseController c) {
                System.out.println("Controller é AnamneseController, setando educando...");
                c.setEducando(educando);
            } else if (controller instanceof PDIController c) {
                System.out.println("Controller é PDIController, setando educando...");
                c.setEducando(educando);
            } else if (controller instanceof PAEEController c) {
                c.setEducando(educando);
            } else if (controller instanceof RelatorioIndividualController c) {
                c.setEducando(educando);
            } else if (controller instanceof DIController c) {
                c.setEducando(educando);
            }
            
            Stage popupStage = getStageFromAnyNode();
            System.out.println("Popup stage: " + popupStage);
            
            Stage parentStage = (Stage) popupStage.getOwner();
            System.out.println("Parent stage: " + parentStage);
            
            if (parentStage == null) {
                System.out.println("Parent stage é null, usando popupStage como fallback");
                parentStage = popupStage; // fallback
            }
            
            parentStage.setTitle(titulo);
            parentStage.setScene(new Scene(root));
            
            // Força a maximização
            parentStage.setMaximized(false);
            parentStage.setMaximized(true);
            
            parentStage.show();
            System.out.println("Parent stage exibido");
            
            popupStage.close();
            System.out.println("Popup fechado");
            System.out.println("=== Navegação concluída ===");
        } catch (Exception e) {
            System.err.println("=== ERRO ao navegar ===");
            System.err.println("Mensagem: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Stage getStageFromAnyNode() {
        Button[] candidates = new Button[]{
                statusAnamnese, statusDI, statusPDI, statusPAEE,
                iniciarAtendimento, viewRelatorios,
                iniciarRelatorioIndividual, baixarRelatorioIndividual,
                editarRelatorioIndividual1, excluirRelatorioIndividual,
                editarAnamnese, verAnamnese,
                editarDiagnosticoInicial, verDiagnosticoInicial,
                editarPDI, verPDI,
                editarPAEE, verPAEE,
                closeProgressoAtd
        };
        for (Button b : candidates) {
            if (b != null && b.getScene() != null) {
                return (Stage) b.getScene().getWindow();
            }
        }
        return null;
    }

    private void atualizarVisibilidadePorExistencia() {
        boolean hasAnamnese = false;
        boolean hasDI = false;
        boolean hasPDI = false;
        boolean hasPAEE = false;
        boolean hasRelatorioIndividual = false;
        if (educando != null && educando.id() != null) {
            var a = authService.getAnamnesePorEducando(educando.id());
            hasAnamnese = a != null;
            var di = authService.getDiagnosticoInicialPorEducandoRaw(educando.id());
            hasDI = di != null;
            var pdis = authService.getPdisPorEducandoRaw(educando.id());
            hasPDI = pdis != null && !pdis.isEmpty();
            var paees = authService.getPaeesPorEducandoRaw(educando.id());
            hasPAEE = paees != null && !paees.isEmpty();
            var rels = authService.getRelatoriosIndividuaisPorEducando(educando.id());
            hasRelatorioIndividual = rels != null && !rels.isEmpty();
        }
        if (editarAnamnese != null) { editarAnamnese.setVisible(hasAnamnese); editarAnamnese.setManaged(hasAnamnese); }
        if (verAnamnese != null) { verAnamnese.setVisible(hasAnamnese); verAnamnese.setManaged(hasAnamnese); }
        if (excluirAnamnese != null) { excluirAnamnese.setVisible(hasAnamnese); excluirAnamnese.setManaged(hasAnamnese); }

        if (editarDiagnosticoInicial != null) { editarDiagnosticoInicial.setVisible(hasDI); editarDiagnosticoInicial.setManaged(hasDI); }
        if (verDiagnosticoInicial != null) { verDiagnosticoInicial.setVisible(hasDI); verDiagnosticoInicial.setManaged(hasDI); }
        if (excluirDiagnosticoInicial != null) { excluirDiagnosticoInicial.setVisible(hasDI); excluirDiagnosticoInicial.setManaged(hasDI); }

        if (editarPDI != null) { editarPDI.setVisible(hasPDI); editarPDI.setManaged(hasPDI); }
        if (verPDI != null) { verPDI.setVisible(hasPDI); verPDI.setManaged(hasPDI); }
        if (excluirPDI != null) { excluirPDI.setVisible(hasPDI); excluirPDI.setManaged(hasPDI); }

        if (editarPAEE != null) { editarPAEE.setVisible(hasPAEE); editarPAEE.setManaged(hasPAEE); }
        if (verPAEE != null) { verPAEE.setVisible(hasPAEE); verPAEE.setManaged(hasPAEE); }
        if (excluirPAEE != null) { excluirPAEE.setVisible(hasPAEE); excluirPAEE.setManaged(hasPAEE); }

        if (editarRelatorioIndividual1 != null) { editarRelatorioIndividual1.setVisible(hasRelatorioIndividual); editarRelatorioIndividual1.setManaged(hasRelatorioIndividual); }
        if (excluirRelatorioIndividual != null) { excluirRelatorioIndividual.setVisible(hasRelatorioIndividual); excluirRelatorioIndividual.setManaged(hasRelatorioIndividual); }
        if (baixarRelatorioIndividual != null) { baixarRelatorioIndividual.setVisible(hasRelatorioIndividual); baixarRelatorioIndividual.setManaged(hasRelatorioIndividual); }
    }

    @FXML
    private void handleIniciarRelatorioIndividualAction() {
        if (educando == null) {
            return;
        }
        var dto = new CreateRelatorioIndividualDTO(
                educando.id(),
                null, null, null, null, null, null, null, null
        );
        authService.criarRelatorioIndividual(dto);
        navegarNoStagePai("/com/pies/projeto/integrado/piesfront/screens/relatorio-individual-1.fxml", "Relatório Individual");
    }
}

