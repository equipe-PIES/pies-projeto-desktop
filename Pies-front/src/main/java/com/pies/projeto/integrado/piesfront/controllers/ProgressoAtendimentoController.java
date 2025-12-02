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
        var etapa = AtendimentoFlowService.getInstance().getEtapaAtual(educando.id());
        if (statusAnamnese != null) {
            boolean concluido = etapa != AtendimentoFlowService.Etapa.ANAMNESE;
            statusAnamnese.setText(concluido ? "Concluído" : "Iniciar");
            statusAnamnese.setStyle(concluido ? "-fx-background-color: #2ecc71; -fx-text-fill: white;" : "");
        }
        if (statusDI != null) {
            boolean concluido = etapa == AtendimentoFlowService.Etapa.PDI || etapa == AtendimentoFlowService.Etapa.PAEE || etapa == AtendimentoFlowService.Etapa.COMPLETO;
            statusDI.setText(concluido ? "Concluído" : "Iniciar");
            statusDI.setStyle(concluido ? "-fx-background-color: #2ecc71; -fx-text-fill: white;" : "");
        }
        if (statusPDI != null) {
            boolean concluido = etapa == AtendimentoFlowService.Etapa.PAEE || etapa == AtendimentoFlowService.Etapa.COMPLETO;
            statusPDI.setText(concluido ? "Concluído" : "Iniciar");
            statusPDI.setStyle(concluido ? "-fx-background-color: #2ecc71; -fx-text-fill: white;" : "");
        }
        if (statusPAEE != null) {
            boolean concluido = etapa == AtendimentoFlowService.Etapa.COMPLETO;
            statusPAEE.setText(concluido ? "Concluído" : "Iniciar");
            statusPAEE.setStyle(concluido ? "-fx-background-color: #2ecc71; -fx-text-fill: white;" : "");
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
        navegarNoStagePai("/com/pies/projeto/integrado/piesfront/screens/pdi-1.fxml", "PDI");
    }
    
    /**
     * Handler para o botão de status do PAEE
     */
    @FXML
    private void handleStatusPAEEAction() {
        if (educando == null) {
            return;
        }
        navegarNoStagePai("/com/pies/projeto/integrado/piesfront/screens/paee-1.fxml", "PAEE");
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
        if (editarAnamnese != null) editarAnamnese.setOnAction(e -> handleEditAnamneseAction());
        if (verAnamnese != null) verAnamnese.setOnAction(e -> handleEditAnamneseAction());
        if (excluirAnamnese != null) {
            excluirAnamnese.setOnAction(e -> {});
            excluirAnamnese.setVisible(false);
            excluirAnamnese.setManaged(false);
        }
        if (editarDiagnosticoInicial != null) editarDiagnosticoInicial.setOnAction(e -> handleStatusDIAction());
        if (verDiagnosticoInicial != null) verDiagnosticoInicial.setOnAction(e -> handleStatusDIAction());
        if (excluirDiagnosticoInicial != null) excluirDiagnosticoInicial.setOnAction(e -> {
            if (educando == null) return;
            java.util.Map<String, Object> di = authService.getDiagnosticoInicialPorEducandoRaw(educando.id());
            String id = getIdFromMap(di);
            if (id != null && authService.deletarDiagnosticoInicial(id)) {
                atualizarVisibilidadePorExistencia();
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
            
            Stage popupStage = (Stage) closeProgressoAtd.getScene().getWindow();
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

    private void atualizarVisibilidadePorExistencia() {
        boolean hasAnamnese = false;
        boolean hasDI = false;
        boolean hasPDI = false;
        boolean hasPAEE = false;
        if (educando != null && educando.id() != null) {
            var a = authService.getAnamnesePorEducando(educando.id());
            hasAnamnese = a != null;
            var di = authService.getDiagnosticoInicialPorEducandoRaw(educando.id());
            hasDI = di != null;
            var pdis = authService.getPdisPorEducandoRaw(educando.id());
            hasPDI = pdis != null && !pdis.isEmpty();
            var paees = authService.getPaeesPorEducandoRaw(educando.id());
            hasPAEE = paees != null && !paees.isEmpty();
        }
        if (editarAnamnese != null) { editarAnamnese.setVisible(hasAnamnese); editarAnamnese.setManaged(hasAnamnese); }
        if (verAnamnese != null) { verAnamnese.setVisible(hasAnamnese); verAnamnese.setManaged(hasAnamnese); }
        if (excluirAnamnese != null) { excluirAnamnese.setVisible(false); excluirAnamnese.setManaged(false); }

        if (editarDiagnosticoInicial != null) { editarDiagnosticoInicial.setVisible(hasDI); editarDiagnosticoInicial.setManaged(hasDI); }
        if (verDiagnosticoInicial != null) { verDiagnosticoInicial.setVisible(hasDI); verDiagnosticoInicial.setManaged(hasDI); }
        if (excluirDiagnosticoInicial != null) { excluirDiagnosticoInicial.setVisible(hasDI); excluirDiagnosticoInicial.setManaged(hasDI); }

        if (editarPDI != null) { editarPDI.setVisible(hasPDI); editarPDI.setManaged(hasPDI); }
        if (verPDI != null) { verPDI.setVisible(hasPDI); verPDI.setManaged(hasPDI); }
        if (excluirPDI != null) { excluirPDI.setVisible(hasPDI); excluirPDI.setManaged(hasPDI); }

        if (editarPAEE != null) { editarPAEE.setVisible(hasPAEE); editarPAEE.setManaged(hasPAEE); }
        if (verPAEE != null) { verPAEE.setVisible(hasPAEE); verPAEE.setManaged(hasPAEE); }
        if (excluirPAEE != null) { excluirPAEE.setVisible(hasPAEE); excluirPAEE.setManaged(hasPAEE); }
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

