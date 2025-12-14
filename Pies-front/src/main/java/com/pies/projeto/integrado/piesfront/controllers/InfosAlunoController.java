package com.pies.projeto.integrado.piesfront.controllers;

import com.pies.projeto.integrado.piesfront.dto.EducandoDTO;
import com.pies.projeto.integrado.piesfront.dto.ResponsavelDTO;
import com.pies.projeto.integrado.piesfront.dto.EnderecoDTO;
import com.pies.projeto.integrado.piesfront.dto.TurmaDTO;
import com.pies.projeto.integrado.piesfront.services.AuthService;
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
    
    // @FXML
    // private Label horarioAtendimentoLabel;
    
    @FXML
    private Button closeButton;
    
    private EducandoDTO educando;
    private final AuthService authService = AuthService.getInstance();
    
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
            observacoesLabel.setText(educando.observacao() != null && !educando.observacao().isEmpty() 
                    ? educando.observacao() : "Não informado");
        }
        
        // Informações do Responsável
        ResponsavelDTO responsavel = educando.responsavel();
        
        if (nomeResponsavelLabel != null) {
            nomeResponsavelLabel.setText(responsavel != null && responsavel.nome() != null 
                    ? responsavel.nome() : "Não informado");
        }
        
        if (parentescoLabel != null) {
            String parentesco = "Não informado";
            if (responsavel != null) {
                if (responsavel.parentesco() != null) {
                    parentesco = formatarParentesco(responsavel.parentesco());
                    if ("OUTRO".equals(responsavel.parentesco()) && responsavel.outroParentesco() != null) {
                        parentesco = responsavel.outroParentesco();
                    }
                }
            }
            parentescoLabel.setText(parentesco);
        }
        
        if (cpfResponsavelLabel != null) {
            cpfResponsavelLabel.setText(responsavel != null && responsavel.cpf() != null 
                    ? responsavel.cpf() : "Não informado");
        }
        
        if (contatoLabel != null) {
            String telefoneFormatado = "Não informado";
            if (responsavel != null && responsavel.contato() != null) {
                telefoneFormatado = formatarTelefone(responsavel.contato());
            }
            contatoLabel.setText(telefoneFormatado);
        }
        
        if (enderecoLabel != null) {
            String enderecoCompleto = "Não informado";
            if (responsavel != null && responsavel.endereco() != null) {
                enderecoCompleto = formatarEndereco(responsavel.endereco());
            }
            enderecoLabel.setText(enderecoCompleto);
        }
        
        // Informações da Turma
        if (educando.turmaId() != null) {
            try {
                TurmaDTO turma = authService.getTurmaById(educando.turmaId());
                
                if (turmaLabel != null) {
                    turmaLabel.setText(turma != null && turma.nome() != null 
                            ? turma.nome() : "Não informado");
                }
                
                if (professoraResponsavelLabel != null) {
                    professoraResponsavelLabel.setText(turma != null && turma.professorNome() != null 
                            ? turma.professorNome() : "Não informado");
                }
                
                if (grauEscolaridadeTurmaLabel != null) {
                    grauEscolaridadeTurmaLabel.setText(turma != null && turma.grauEscolar() != null 
                            ? turma.grauEscolar() : "Não informado");
                }
                
                if (turnoLabel != null) {
                    String turnoFormatado = "Não informado";
                    if (turma != null && turma.turno() != null) {
                        turnoFormatado = formatarTurno(turma.turno());
                    }
                    turnoLabel.setText(turnoFormatado);
                }
            } catch (Exception e) {
                System.err.println("Erro ao buscar informações da turma: " + e.getMessage());
                if (turmaLabel != null) turmaLabel.setText("Não informado");
                if (professoraResponsavelLabel != null) professoraResponsavelLabel.setText("Não informado");
                if (grauEscolaridadeTurmaLabel != null) grauEscolaridadeTurmaLabel.setText("Não informado");
                if (turnoLabel != null) turnoLabel.setText("Não informado");
            }
        } else {
            if (turmaLabel != null) turmaLabel.setText("Não informado");
            if (professoraResponsavelLabel != null) professoraResponsavelLabel.setText("Não informado");
            if (grauEscolaridadeTurmaLabel != null) grauEscolaridadeTurmaLabel.setText("Não informado");
            if (turnoLabel != null) turnoLabel.setText("Não informado");
        }
        
        // if (horarioAtendimentoLabel != null) {
        //     horarioAtendimentoLabel.setText("Não informado");
        // }
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

    /**
     * Formata o parentesco para exibição mais amigável
     */
    private String formatarParentesco(String parentesco) {
        if (parentesco == null) {
            return "Não informado";
        }
        
        return switch (parentesco) {
            case "PAI" -> "Pai";
            case "MAE" -> "Mãe";
            case "AVO" -> "Avô(ó)";
            case "TIO" -> "Tio(a)";
            case "IRMAO" -> "Irmão(ã)";
            case "OUTRO" -> "Outro";
            default -> parentesco;
        };
    }
    
    /**
     * Formata o endereço completo
     */
    private String formatarEndereco(EnderecoDTO endereco) {
        if (endereco == null) {
            return "Não informado";
        }
        
        StringBuilder sb = new StringBuilder();
        
        if (endereco.rua() != null && !endereco.rua().isEmpty()) {
            sb.append(endereco.rua());
        }
        
        if (endereco.numero() != null && !endereco.numero().isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(endereco.numero());
        }
        
        if (endereco.complemento() != null && !endereco.complemento().isEmpty()) {
            if (sb.length() > 0) sb.append(" - ");
            sb.append(endereco.complemento());
        }
        
        if (endereco.bairro() != null && !endereco.bairro().isEmpty()) {
            if (sb.length() > 0) sb.append(" - ");
            sb.append(endereco.bairro());
        }
        
        if (endereco.cidade() != null && !endereco.cidade().isEmpty()) {
            if (sb.length() > 0) sb.append(" - ");
            sb.append(endereco.cidade());
        }
        
        if (endereco.uf() != null && !endereco.uf().isEmpty()) {
            if (sb.length() > 0) sb.append("/");
            sb.append(endereco.uf());
        }
        
        if (endereco.cep() != null && !endereco.cep().isEmpty()) {
            if (sb.length() > 0) sb.append(" - CEP: ");
            sb.append(endereco.cep());
        }
        
        return sb.length() > 0 ? sb.toString() : "Não informado";
    }
    
    /**
     * Formata o turno para exibição mais amigável
     */
    private String formatarTurno(String turno) {
        if (turno == null) {
            return "Não informado";
        }
        
        return switch (turno) {
            case "MANHA" -> "Manhã";
            case "TARDE" -> "Tarde";
            case "NOITE" -> "Noite";
            case "INTEGRAL" -> "Integral";
            default -> turno;
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

