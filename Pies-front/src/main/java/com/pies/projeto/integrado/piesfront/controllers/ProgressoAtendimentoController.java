package com.pies.projeto.integrado.piesfront.controllers;

import com.pies.projeto.integrado.piesfront.dto.EducandoDTO;
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
    private Button editAnamnese;
    @FXML
    private Button editDI;
    @FXML
    private Button editPDI;
    @FXML
    private Button editPAEE;
    
    @FXML
    private Button closeProgressoAtd;
    
    private EducandoDTO educando;
    
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
            if (editAnamnese != null) {
                editAnamnese.setVisible(concluido);
                editAnamnese.setManaged(concluido);
            }
        }
        if (statusDI != null) {
            boolean concluido = etapa == AtendimentoFlowService.Etapa.PDI || etapa == AtendimentoFlowService.Etapa.PAEE || etapa == AtendimentoFlowService.Etapa.COMPLETO;
            statusDI.setText(concluido ? "Concluído" : "Iniciar");
            statusDI.setStyle(concluido ? "-fx-background-color: #2ecc71; -fx-text-fill: white;" : "");
            if (editDI != null) {
                editDI.setVisible(concluido);
                editDI.setManaged(concluido);
            }
        }
        if (statusPDI != null) {
            boolean concluido = etapa == AtendimentoFlowService.Etapa.PAEE || etapa == AtendimentoFlowService.Etapa.COMPLETO;
            statusPDI.setText(concluido ? "Concluído" : "Iniciar");
            statusPDI.setStyle(concluido ? "-fx-background-color: #2ecc71; -fx-text-fill: white;" : "");
            if (editPDI != null) {
                editPDI.setVisible(concluido);
                editPDI.setManaged(concluido);
            }
        }
        if (statusPAEE != null) {
            boolean concluido = etapa == AtendimentoFlowService.Etapa.COMPLETO;
            statusPAEE.setText(concluido ? "Concluído" : "Iniciar");
            statusPAEE.setStyle(concluido ? "-fx-background-color: #2ecc71; -fx-text-fill: white;" : "");
            if (editPAEE != null) {
                editPAEE.setVisible(concluido);
                editPAEE.setManaged(concluido);
            }
        }
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
            navegarNoStagePai("/com/pies/projeto/integrado/piesfront/screens/relatorio-individual-1.fxml", "Diagnóstico Inicial");
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
        if (educando == null) {
            return;
        }
        navegarNoStagePai("/com/pies/projeto/integrado/piesfront/screens/anamnese-1.fxml", "Anamnese");
    }
    
    /**
     * Handler para o botão de status do diagnóstico inicial
     */
    @FXML
    private void handleStatusDIAction() {
        if (educando == null) {
            return;
        }
        navegarNoStagePai("/com/pies/projeto/integrado/piesfront/screens/relatorio-individual-1.fxml", "Diagnóstico Inicial");
    }

    @FXML
    private void handleEditAnamneseAction() {
        if (educando == null) {
            return;
        }
        navegarNoStagePai("/com/pies/projeto/integrado/piesfront/screens/anamnese-1.fxml", "Anamnese");
    }

    @FXML
    private void handleEditDIAction() {
        if (educando == null) {
            return;
        }
        navegarNoStagePai("/com/pies/projeto/integrado/piesfront/screens/relatorio-individual-1.fxml", "Diagnóstico Inicial");
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
    }

    private void navegarNoStagePai(String resource, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(resource));
            Parent root = loader.load();
            Object controller = loader.getController();
            if (controller instanceof AnamneseController c) {
                c.setEducando(educando);
            } else if (controller instanceof PDIController c) {
                c.setEducando(educando);
            } else if (controller instanceof PAEEController c) {
                c.setEducando(educando);
            } else if (controller instanceof RelatorioIndividualController c) {
                c.setEducando(educando);
            }
            Stage popupStage = (Stage) closeProgressoAtd.getScene().getWindow();
            Stage parentStage = (Stage) popupStage.getOwner();
            if (parentStage == null) {
                parentStage = popupStage; // fallback
            }
            parentStage.setTitle(titulo);
            parentStage.setScene(new Scene(root));
            parentStage.show();
            popupStage.close();
        } catch (Exception e) {
            System.err.println("Erro ao navegar: " + e.getMessage());
        }
    }
}

