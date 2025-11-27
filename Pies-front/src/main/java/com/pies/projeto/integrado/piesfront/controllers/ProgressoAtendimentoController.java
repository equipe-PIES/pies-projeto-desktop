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
        
        // TODO: Implementar lógica para carregar o progresso do educando
        // Por enquanto, os botões mantêm seus estados padrão
    }
    
    /**
     * Handler para o botão de iniciar atendimento
     */
    @FXML
    private void handleIniciarAtendimentoAction() {
        if (educando == null) {
            return;
        }
        AtendimentoFlowService.Etapa etapa = AtendimentoFlowService.getInstance()
                .getEtapaAtual(educando.id());
        if (etapa == AtendimentoFlowService.Etapa.ANAMNESE) {
            abrirTela("/com/pies/projeto/integrado/piesfront/screens/anamnese-1.fxml", "Anamnese");
        } else if (etapa == AtendimentoFlowService.Etapa.PDI) {
            abrirTela("/com/pies/projeto/integrado/piesfront/screens/pdi-1.fxml", "PDI");
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
        abrirTela("/com/pies/projeto/integrado/piesfront/screens/anamnese-1.fxml", "Anamnese");
    }
    
    /**
     * Handler para o botão de status do diagnóstico inicial
     */
    @FXML
    private void handleStatusDIAction() {
        if (educando == null) {
            return;
        }
    }
    
    /**
     * Handler para o botão de status do PDI
     */
    @FXML
    private void handleStatusPDIAction() {
        if (educando == null) {
            return;
        }
        abrirTela("/com/pies/projeto/integrado/piesfront/screens/pdi-1.fxml", "PDI");
    }
    
    /**
     * Handler para o botão de status do PAEE
     */
    @FXML
    private void handleStatusPAEEAction() {
        if (educando == null) {
            return;
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
    }

    private void abrirTela(String resource, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(resource));
            Parent root = loader.load();
            Object controller = loader.getController();
            if (controller instanceof AnamneseController c) {
                c.setEducando(educando);
            } else if (controller instanceof PDIController c) {
                c.setEducando(educando);
            }
            Stage stage = new Stage();
            stage.setTitle(titulo);
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            System.err.println("Erro ao abrir tela: " + e.getMessage());
        }
    }
}

