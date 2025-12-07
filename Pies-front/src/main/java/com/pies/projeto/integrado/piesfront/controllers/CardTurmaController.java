package com.pies.projeto.integrado.piesfront.controllers;

import com.pies.projeto.integrado.piesfront.dto.TurmaDTO;
import com.pies.projeto.integrado.piesfront.dto.EducandoDTO;
import com.pies.projeto.integrado.piesfront.services.AuthService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import com.utils.Janelas;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller para o card de turma
 * Gerencia a exibição de informações de uma turma em formato de card
 */
public class CardTurmaController implements Initializable {
    
    @FXML
    private VBox cardTurma;
    
    @FXML
    private ImageView imgTurmaCard;
    
    @FXML
    private Label nomeTurmaCard;
    
    @FXML
    private Label totalAlunoCard;
    
    @FXML
    private Label faixaEtariaCard;
    
    @FXML
    private Label grauTurmaCard;
    
    @FXML
    private Label turnoTurmaCard;
    
    @FXML
    private Button verificarTurmaButton;
    
    private TurmaDTO turma;
    private final AuthService authService = AuthService.getInstance();
    
    /**
     * Define os dados da turma a serem exibidos no card
     * @param turma DTO com as informações da turma
     */
    public void setTurma(TurmaDTO turma) {
        this.turma = turma;
        atualizarDados();
    }
    
    /**
     * Atualiza os campos do card com os dados da turma
     */
    private void atualizarDados() {
        if (turma == null) {
            return;
        }
        
        if (nomeTurmaCard != null) {
            String nome = turma.nome() != null ? turma.nome() : "Sem nome";
            nomeTurmaCard.setText(nome);
        }
        
        if (faixaEtariaCard != null) {
            String faixaEtaria = turma.faixaEtaria() != null ? turma.faixaEtaria() : "Não informado";
            faixaEtariaCard.setText("Faixa Etária: " + faixaEtaria);
        }
        
        if (grauTurmaCard != null) {
            String grau = turma.grauEscolar() != null ? formatarGrauEscolar(turma.grauEscolar()) : "Não informado";
            grauTurmaCard.setText("Grau da turma: " + grau);
        }
        
        if (turnoTurmaCard != null) {
            String turno = turma.turno() != null ? formatarTurno(turma.turno()) : "Não informado";
            turnoTurmaCard.setText("Turno: " + turno);
        }
        
        // Busca o total de alunos da turma
       if (totalAlunoCard != null) {
            int total = turma.educandos() != null ? turma.educandos().size() : 0;
            totalAlunoCard.setText("Total de alunos: " + total);
        }
    }
    
    /**
     * Formata o grau escolar para exibição mais amigável
     */
    private String formatarGrauEscolar(String grauEscolar) {
        if (grauEscolar == null) {
            return "Não informado";
        }
        
        return switch (grauEscolar) {
            case "EDUCACAO_INFANTIL" -> "Educação Infantil";
            case "FUNDAMENTAL_I" -> "Fundamental I";
            case "FUNDAMENTAL_II" -> "Fundamental II";
            default -> grauEscolar;
        };
    }
    
    /**
     * Formata o turno para exibição mais amigável
     */
    private String formatarTurno(String turno) {
        if (turno == null) {
            return "Não informado";
        }
        
        return switch (turno) {
            case "MATUTINO" -> "Matutino";
            case "VESPERTINO" -> "Vespertino";
            default -> turno;
        };
    }
    
    /**
     * Handler para o botão "Verificar turma"
     */
    @FXML
    private void handleVerificarTurmaAction() {
        if (turma == null || turma.id() == null) {
            System.err.println("Turma não definida!");
            return;
        }
        Janelas.carregarTela(new ActionEvent(verificarTurmaButton, null),
                "/com/pies/projeto/integrado/piesfront/screens/view-turma.fxml",
                "Visualizar Turma",
                controller -> {
                    if (controller instanceof ViewTurmaController c) {
                        c.setTurmaId(turma.id());
                    }
                });
    }
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Inicialização do card
        if (turma != null) {
            atualizarDados();
        }
    }
    
    /**
     * Retorna o ID da turma deste card
     */
    public String getTurmaId() {
        return turma != null ? turma.id() : null;
    }
}
