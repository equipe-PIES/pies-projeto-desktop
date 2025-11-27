package com.pies.projeto.integrado.piesfront.controllers;

import com.pies.projeto.integrado.piesfront.dto.EducandoDTO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.Period;
import java.util.ResourceBundle;

import com.pies.projeto.integrado.piesfront.services.AtendimentoFlowService;

/**
 * Controller para o card de aluno (educando)
 * Gerencia a exibição de informações de um educando em formato de card
 */
public class CardAlunoController implements Initializable {
    
    @FXML
    private VBox cardAluno;
    
    @FXML
    private ImageView imgAlunoCard;
    
    @FXML
    private Label nomeLabel;
    
    @FXML
    private Label idadeLabel;
    
    @FXML
    private Label cidLabel;
    
    @FXML
    private Label grauEscolaridadeLabel;
    
    @FXML
    private Button statusAtendimentoButton;
    
    @FXML
    private Button verProgressoButton;
    
    @FXML
    private Button infoButton;
    
    private EducandoDTO educando;
    
    /**
     * Define os dados do educando a serem exibidos no card
     * @param educando DTO com as informações do educando
     */
    public void setEducando(EducandoDTO educando) {
        this.educando = educando;
        atualizarDados();
    }
    
    /**
     * Atualiza os campos do card com os dados do educando
     */
    private void atualizarDados() {
        if (educando == null) {
            return;
        }
        
        // Nome do aluno
        if (nomeLabel != null) {
            String nome = educando.nome() != null ? educando.nome() : "Nome não informado";
            nomeLabel.setText(nome);
        }
        
        // Calcula a idade
        if (idadeLabel != null && educando.dataNascimento() != null) {
            int idade = calcularIdade(educando.dataNascimento());
            idadeLabel.setText("Idade: " + idade + " anos");
        } else if (idadeLabel != null) {
            idadeLabel.setText("Idade: Não informado");
        }
        
        // CID
        if (cidLabel != null) {
            String cid = educando.cid() != null ? educando.cid() : "Não informado";
            cidLabel.setText("CID: " + cid);
        }
        
        // Grau de Escolaridade
        if (grauEscolaridadeLabel != null) {
            String escolaridade = educando.escolaridade() != null ? 
                    formatarEscolaridade(educando.escolaridade()) : "Não informado";
            grauEscolaridadeLabel.setText("Grau de Escolaridade: " + escolaridade);
        }

        if (statusAtendimentoButton != null) {
            AtendimentoFlowService.Etapa etapa = AtendimentoFlowService.getInstance()
                    .getEtapaAtual(educando.id());
            String texto = switch (etapa) {
                case ANAMNESE -> "Anamnese";
                case PDI -> "PDI";
                case COMPLETO -> "Concluído";
            };
            statusAtendimentoButton.setText(texto);
        }
    }
    
    /**
     * Calcula a idade baseada na data de nascimento
     */
    private int calcularIdade(LocalDate dataNascimento) {
        if (dataNascimento == null) {
            return 0;
        }
        return Period.between(dataNascimento, LocalDate.now()).getYears();
    }
    
    /**
     * Formata o grau de escolaridade para exibição mais amigável
     */
    private String formatarEscolaridade(String escolaridade) {
        if (escolaridade == null) {
            return "Não informado";
        }
        
        return switch (escolaridade) {
            case "EDUCACAO_INFANTIL" -> "Educação Infantil";
            case "FUNDAMENTAL_I" -> "Fundamental I";
            case "FUNDAMENTAL_II" -> "Fundamental II";
            default -> escolaridade;
        };
    }
    
    /**
     * Handler para o botão "Status de Atendimento"
     * TODO: Implementar navegação para status de atendimento
     */
    @FXML
    private void handleStatusAtendimentoAction() {
        if (educando == null) {
            return;
        }
        AtendimentoFlowService.Etapa etapa = AtendimentoFlowService.getInstance()
                .getEtapaAtual(educando.id());
        if (etapa == AtendimentoFlowService.Etapa.ANAMNESE) {
            abrirTelaAnamnese("/com/pies/projeto/integrado/piesfront/screens/anamnese-1.fxml", "Anamnese");
        } else if (etapa == AtendimentoFlowService.Etapa.PDI) {
            abrirTelaPdi("/com/pies/projeto/integrado/piesfront/screens/pdi-1.fxml", "PDI");
        }
    }
    
    /**
     * Handler para o botão "Info"
     * Abre a tela de informações do aluno como popup
     */
    @FXML
    private void handleInfoAction() {
        if (educando == null) {
            System.err.println("Educando não definido!");
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/pies/projeto/integrado/piesfront/screens/infos-aluno.fxml"));
            Parent root = loader.load();
            
            // Obtém o controller e define o educando
            InfosAlunoController controller = loader.getController();
            controller.setEducando(educando);
            
            // Cria a janela popup
            Stage popupStage = new Stage();
            popupStage.setTitle("Informações do Aluno");
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.initStyle(StageStyle.UTILITY);
            popupStage.setScene(new Scene(root));
            popupStage.setResizable(false);
            
            // Centraliza a janela
            Stage parentStage = (Stage) infoButton.getScene().getWindow();
            popupStage.initOwner(parentStage);
            popupStage.setX(parentStage.getX() + (parentStage.getWidth() - popupStage.getWidth()) / 2);
            popupStage.setY(parentStage.getY() + (parentStage.getHeight() - popupStage.getHeight()) / 2);
            
            popupStage.showAndWait();
            
        } catch (IOException e) {
            System.err.println("Erro ao carregar tela de informações do aluno: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Handler para o botão "Ver Progresso"
     * Abre a tela de progresso de atendimento como popup
     */
    @FXML
    private void handleVerProgressoAction() {
        if (educando == null) {
            System.err.println("Educando não definido!");
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/pies/projeto/integrado/piesfront/screens/progresso-atendimento.fxml"));
            Parent root = loader.load();
            
            // Obtém o controller e define o educando
            ProgressoAtendimentoController controller = loader.getController();
            controller.setEducando(educando);
            
            // Cria a janela popup
            Stage popupStage = new Stage();
            popupStage.setTitle("Progresso de Atendimento");
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.initStyle(StageStyle.UTILITY);
            popupStage.setScene(new Scene(root));
            popupStage.setResizable(false);
            
            // Centraliza a janela
            Stage parentStage = (Stage) verProgressoButton.getScene().getWindow();
            popupStage.initOwner(parentStage);
            popupStage.setX(parentStage.getX() + (parentStage.getWidth() - popupStage.getWidth()) / 2);
            popupStage.setY(parentStage.getY() + (parentStage.getHeight() - popupStage.getHeight()) / 2);
            
            popupStage.showAndWait();
            
        } catch (IOException e) {
            System.err.println("Erro ao carregar tela de progresso: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void abrirTelaAnamnese(String resource, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(resource));
            Parent root = loader.load();
            AnamneseController controller = loader.getController();
            controller.setEducando(educando);
            Stage stage = (Stage) statusAtendimentoButton.getScene().getWindow();
            stage.setTitle(titulo);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println("Erro ao abrir anamnese: " + e.getMessage());
        }
    }

    private void abrirTelaPdi(String resource, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(resource));
            Parent root = loader.load();
            PDIController controller = loader.getController();
            controller.setEducando(educando);
            Stage stage = (Stage) statusAtendimentoButton.getScene().getWindow();
            stage.setTitle(titulo);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println("Erro ao abrir PDI: " + e.getMessage());
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Inicialização do card
        if (educando != null) {
            atualizarDados();
        }
    }
    
    /**
     * Retorna o ID do educando deste card
     */
    public String getEducandoId() {
        return educando != null ? educando.id() : null;
    }
    
    /**
     * Retorna o nome do educando deste card
     */
    public String getEducandoNome() {
        return educando != null ? educando.nome() : null;
    }
}
