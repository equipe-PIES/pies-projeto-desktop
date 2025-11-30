package com.pies.projeto.integrado.piesfront.controllers;

import com.pies.projeto.integrado.piesfront.dto.EducandoDTO;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

/**
 * Controller para a tela de informações do aluno
 * Exibe informações detalhadas do educando em formato de popup
 */
public class InfosAlunoController implements Initializable {
    
    @FXML
    private Label nomeLabel;
    
    @FXML
    private Label dataNascimentoLabel;
    
    @FXML
    private Label cpfLabel;
    
    @FXML
    private Label generoLabel;
    
    @FXML
    private Label cidLabel;
    
    @FXML
    private Label nisLabel;
    
    @FXML
    private Label grauEscolaridadeLabel;
    
    @FXML
    private Label escolaLabel;
    
    @FXML
    private Label observacoesLabel;
    
    @FXML
    private Label nomeResponsavelLabel;
    
    @FXML
    private Label parentescoLabel;
    
    @FXML
    private Label cpfResponsavelLabel;
    
    @FXML
    private Label contatoLabel;
    
    @FXML
    private Label enderecoLabel;
    
    @FXML
    private Label turmaLabel;
    
    @FXML
    private Label professoraResponsavelLabel;
    
    @FXML
    private Label grauEscolaridadeTurmaLabel;
    
    @FXML
    private Label turnoLabel;
    
    @FXML
    private Label horarioAtendimentoLabel;
    
    @FXML
    private Button closeButton;
    
    private EducandoDTO educando;
    
    /**
     * Define os dados do educando a serem exibidos
     * @param educando DTO com as informações do educando
     */
    public void setEducando(EducandoDTO educando) {
        this.educando = educando;
        atualizarDados();
    }
    
    /**
     * Atualiza os campos com os dados do educando
     */
    private void atualizarDados() {
        if (educando == null) {
            return;
        }
        
        // Informações do Aluno
        if (nomeLabel != null) {
            nomeLabel.setText(educando.nome() != null ? educando.nome() : "Não informado");
        }
        
        if (dataNascimentoLabel != null) {
            if (educando.dataNascimento() != null) {
                String dataFormatada = educando.dataNascimento()
                        .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                dataNascimentoLabel.setText(dataFormatada);
            } else {
                dataNascimentoLabel.setText("Não informado");
            }
        }
        
        if (cpfLabel != null) {
            cpfLabel.setText(educando.cpf() != null ? educando.cpf() : "Não informado");
        }
        
        if (generoLabel != null) {
            String genero = educando.genero() != null ? 
                    formatarGenero(educando.genero()) : "Não informado";
            generoLabel.setText(genero);
        }
        
        if (cidLabel != null) {
            cidLabel.setText(educando.cid() != null ? educando.cid() : "Não informado");
        }
        
        if (nisLabel != null) {
            nisLabel.setText(educando.nis() != null ? educando.nis() : "Não informado");
        }
        
        if (grauEscolaridadeLabel != null) {
            String escolaridade = educando.escolaridade() != null ? 
                    formatarEscolaridade(educando.escolaridade()) : "Não informado";
            grauEscolaridadeLabel.setText(escolaridade);
        }
        
        if (escolaLabel != null) {
            escolaLabel.setText(educando.escola() != null ? educando.escola() : "Não informado");
        }
        
        if (observacoesLabel != null) {
            // O DTO não tem campo de observações, então deixamos como "Não informado"
            observacoesLabel.setText("Não informado");
        }
        
        // Informações do Responsável (não disponíveis no DTO atual)
        if (nomeResponsavelLabel != null) {
            nomeResponsavelLabel.setText("Não informado");
        }
        
        if (parentescoLabel != null) {
            parentescoLabel.setText("Não informado");
        }
        
        if (cpfResponsavelLabel != null) {
            cpfResponsavelLabel.setText("Não informado");
        }
        
        if (contatoLabel != null) {
            String telefoneFormatado = "Não informado";
            if (educando.responsaveis() != null && !educando.responsaveis().isEmpty()) {
                String contato = educando.responsaveis().get(0).contato();
                telefoneFormatado = formatarTelefone(contato);
            }
            contatoLabel.setText(telefoneFormatado);
        }
        
        if (enderecoLabel != null) {
            enderecoLabel.setText("Não informado");
        }
        
        // Informações da Turma (não disponíveis no DTO atual)
        if (turmaLabel != null) {
            turmaLabel.setText("Não informado");
        }
        
        if (professoraResponsavelLabel != null) {
            professoraResponsavelLabel.setText("Não informado");
        }
        
        if (grauEscolaridadeTurmaLabel != null) {
            grauEscolaridadeTurmaLabel.setText("Não informado");
        }
        
        if (turnoLabel != null) {
            turnoLabel.setText("Não informado");
        }
        
        if (horarioAtendimentoLabel != null) {
            horarioAtendimentoLabel.setText("Não informado");
        }
    }
    
    /**
     * Formata o gênero para exibição mais amigável
     */
    private String formatarGenero(String genero) {
        if (genero == null) {
            return "Não informado";
        }
        
        return switch (genero.toUpperCase()) {
            case "MASCULINO" -> "Masculino";
            case "FEMININO" -> "Feminino";
            case "OUTRO" -> "Outro";
            case "PREFIRO_NAO_INFORMAR" -> "Prefiro não informar";
            default -> genero;
        };
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
            case "ESTIMULACAO_PRECOCE" -> "Estimulação Precoce";
            case "FUNDAMENTAL_I" -> "Fundamental I";
            case "FUNDAMENTAL_II" -> "Fundamental II";
            case "MEDIO" -> "Ensino Médio";
            case "OUTRO" -> "Outro";
            case "PREFIRO_NAO_INFORMAR" -> "Prefiro não informar";
            default -> escolaridade;
        };
    }

    private String formatarTelefone(String contato) {
        if (contato == null || contato.trim().isEmpty()) {
            return "Não informado";
        }
        String digits = contato.replaceAll("\\D", "");
        if (digits.isEmpty()) {
            return "Não informado";
        }
        if (digits.length() > 11) digits = digits.substring(0, 11);
        StringBuilder sb = new StringBuilder();
        int len = digits.length();
        if (len > 0) sb.append('(');
        for (int i = 0; i < len; i++) {
            char d = digits.charAt(i);
            if (i == 2) sb.append(") ");
            if (len > 10) { // celular
                if (i == 7) sb.append('-');
            } else { // fixo
                if (i == 6) sb.append('-');
            }
            sb.append(d);
        }
        return sb.toString();
    }
    
    /**
     * Handler para o botão de fechar
     */
    @FXML
    private void handleCloseAction() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
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

