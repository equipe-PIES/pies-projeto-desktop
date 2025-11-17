package com.pies.projeto.integrado.piesfront.controllers;

import com.pies.projeto.integrado.piesfront.dto.EducandoDTO;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.time.LocalDate;
import java.time.Period;
import java.util.ResourceBundle;

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
    private Label idadeLabel;
    
    @FXML
    private Label cidLabel;
    
    @FXML
    private Label grauEscolaridadeLabel;
    
    @FXML
    private Button statusAtendimentoButton;
    
    @FXML
    private Button verProgressoButton;
    
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
     */
    @FXML
    private void handleStatusAtendimentoAction() {
        // TODO: Implementar navegação para status de atendimento
        System.out.println("Status de atendimento: " + (educando != null ? educando.id() : "null"));
    }
    
    /**
     * Handler para o botão "Ver Progresso"
     */
    @FXML
    private void handleVerProgressoAction() {
        // TODO: Implementar navegação para progresso do aluno
        System.out.println("Ver progresso: " + (educando != null ? educando.id() : "null"));
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
