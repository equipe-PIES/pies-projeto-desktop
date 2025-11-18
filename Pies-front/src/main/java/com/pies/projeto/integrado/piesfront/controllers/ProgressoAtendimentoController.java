package com.pies.projeto.integrado.piesfront.controllers;

import com.pies.projeto.integrado.piesfront.dto.EducandoDTO;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

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
        // TODO: Implementar lógica para iniciar atendimento
        System.out.println("Iniciar atendimento: " + (educando != null ? educando.id() : "null"));
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
        // TODO: Implementar lógica para status da anamnese
        System.out.println("Status anamnese: " + (educando != null ? educando.id() : "null"));
    }
    
    /**
     * Handler para o botão de status do diagnóstico inicial
     */
    @FXML
    private void handleStatusDIAction() {
        // TODO: Implementar lógica para status do diagnóstico inicial
        System.out.println("Status DI: " + (educando != null ? educando.id() : "null"));
    }
    
    /**
     * Handler para o botão de status do PDI
     */
    @FXML
    private void handleStatusPDIAction() {
        // TODO: Implementar lógica para status do PDI
        System.out.println("Status PDI: " + (educando != null ? educando.id() : "null"));
    }
    
    /**
     * Handler para o botão de status do PAEE
     */
    @FXML
    private void handleStatusPAEEAction() {
        // TODO: Implementar lógica para status do PAEE
        System.out.println("Status PAEE: " + (educando != null ? educando.id() : "null"));
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
}

