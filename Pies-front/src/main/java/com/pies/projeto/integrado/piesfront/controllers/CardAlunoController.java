package com.pies.projeto.integrado.piesfront.controllers;

import com.pies.projeto.integrado.piesfront.dto.EducandoDTO;
import com.pies.projeto.integrado.piesfront.dto.CreateRelatorioIndividualDTO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.Period;
import java.util.ResourceBundle;

import com.pies.projeto.integrado.piesfront.services.AtendimentoFlowService;
import com.pies.projeto.integrado.piesfront.services.AuthService;
import com.pies.projeto.integrado.piesfront.dto.TurmaDTO;
import com.utils.Janelas;
import javafx.event.ActionEvent;
import com.pies.projeto.integrado.piesfront.controllers.CadastroAlunoController;

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
    private Label turmaLabel;
    
    @FXML
    private Button statusAtendimentoButton;
    
    @FXML
    private Button verProgressoButton;
    
    @FXML
    private Button infoButton;
    @FXML
    private Button excluirAluno;
    @FXML
    private Button editarAluno;
    
    private EducandoDTO educando;
    private final AuthService authService = AuthService.getInstance();
    private TurmaDTO turmaInfo;
    
    /**
     * Define os dados do educando a serem exibidos no card
     * @param educando DTO com as informações do educando
     */
    public void setEducando(EducandoDTO educando) {
        this.educando = educando;
        atualizarDados();
    }

    @FXML
    private void handleEditarAlunoAction(javafx.event.ActionEvent event) {
        if (educando == null || educando.id() == null || editarAluno == null) return;
        com.pies.projeto.integrado.piesfront.dto.EducandoDTO completo = authService.getEducandoById(educando.id());
        Janelas.carregarTela(event,
                "/com/pies/projeto/integrado/piesfront/screens/cadastro-de-aluno.fxml",
                "Cadastro de Aluno(a)",
                ctrl -> {
                    if (ctrl instanceof CadastroAlunoController c) {
                        c.setIndicadorDeTela("Cadastro de Aluno(a)");
                        c.setEducando(completo != null ? completo : educando);
                    }
                });
    }

    @FXML
    private void handleExcluirAlunoAction() {
        if (cardAluno == null || cardAluno.getScene() == null || educando == null || educando.id() == null) return;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/pies/projeto/integrado/piesfront/screens/excluir-confirm-card.fxml"));
            Parent conteudo = loader.load();
            ExcluirConfirmController ctrl = loader.getController();

            Pane root = cardAluno.getScene().getRoot() instanceof Pane p ? p : null;
            if (root == null) return;
            Pane overlay = new Pane(conteudo);
            overlay.setMouseTransparent(false);
            overlay.setManaged(false);
            overlay.prefWidthProperty().bind(root.widthProperty());
            overlay.prefHeightProperty().bind(root.heightProperty());
            root.getChildren().add(overlay);

            Runnable center = () -> {
                if (conteudo instanceof Region r) {
                    r.applyCss();
                    r.autosize();
                    double w = r.prefWidth(-1);
                    double h = r.prefHeight(-1);
                    double cw = root.getWidth();
                    double ch = root.getHeight();
                    r.setLayoutX((cw - w) / 2);
                    r.setLayoutY((ch - h) / 2);
                }
            };
            center.run();
            root.widthProperty().addListener((obs, o, n) -> center.run());
            root.heightProperty().addListener((obs, o, n) -> center.run());

            ctrl.setOnCancel(() -> root.getChildren().remove(overlay));
            ctrl.setOnConfirm(() -> {
                boolean ok = authService.deletarEducando(educando.id());
                root.getChildren().remove(overlay);
                String nome = educando.nome() != null ? educando.nome() : "";
                String msg = ok ? ("Aluno(a) " + nome + " foi excluído(a) com sucesso!") : "Falha ao excluir cadastro de aluno(a)!";
                NotificacaoController.exibirTexto(root, msg, ok);
                if (ok && cardAluno.getParent() instanceof Pane parent) {
                    parent.getChildren().remove(cardAluno);
                }
            });
        } catch (IOException e) {
        }
    }
    
    public void setTurmaInfo(TurmaDTO turma) {
        this.turmaInfo = turma;
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

        if (turmaLabel != null) {
            String turmaNome = "Não informado";
            if (turmaInfo != null && turmaInfo.nome() != null) {
                turmaNome = turmaInfo.nome();
            }
            turmaLabel.setText("Turma: " + turmaNome);
        }

        if (statusAtendimentoButton != null) {
            AtendimentoFlowService.Etapa etapa = AtendimentoFlowService.getInstance()
                    .getEtapaAtual(educando.id());
            String texto = switch (etapa) {
                case ANAMNESE -> "Anamnese";
                case DI -> "Diagnóstico Inicial";
                case PDI -> "PDI";
                case PAEE -> "PAEE";
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
        } else if (etapa == AtendimentoFlowService.Etapa.DI) {
            abrirTelaDiagnosticoInicial("/com/pies/projeto/integrado/piesfront/screens/diagnostico-1.fxml", "Diagnóstico Inicial");
        } else if (etapa == AtendimentoFlowService.Etapa.PDI) {
            abrirTelaPdi("/com/pies/projeto/integrado/piesfront/screens/pdi-1.fxml", "PDI");
        } else if (etapa == AtendimentoFlowService.Etapa.PAEE) {
            abrirTelaPaee("/com/pies/projeto/integrado/piesfront/screens/paee-1.fxml", "PAEE");
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
            com.pies.projeto.integrado.piesfront.dto.EducandoDTO completo = authService.getEducandoById(educando.id());
            controller.setEducando(completo != null ? completo : educando);
            
            // Cria a janela popup
            Stage popupStage = new Stage();
            popupStage.setTitle("Informações do Aluno");
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.initStyle(StageStyle.UTILITY);
            popupStage.setScene(new Scene(root));
            popupStage.setResizable(false);
            
            // Centraliza a janela
            Stage parentStage = (Stage) (cardAluno != null ? cardAluno.getScene().getWindow() : null);
            popupStage.initOwner(parentStage);
            popupStage.centerOnScreen();
            
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
            popupStage.centerOnScreen();
            
            popupStage.showAndWait();
            
        } catch (IOException e) {
            System.err.println("Erro ao carregar tela de progresso: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void abrirTelaAnamnese(String resource, String titulo) {
        Janelas.carregarTela(new ActionEvent(statusAtendimentoButton, null), resource, titulo, controller -> {
            if (controller instanceof AnamneseController c) {
                c.setEducando(educando);
            }
        });
    }

    private void abrirTelaPdi(String resource, String titulo) {
        Janelas.carregarTela(new ActionEvent(statusAtendimentoButton, null), resource, titulo, controller -> {
            if (controller instanceof PDIController c) {
                c.setEducando(educando);
            }
        });
    }

    private void abrirTelaPaee(String resource, String titulo) {
        Janelas.carregarTela(new ActionEvent(statusAtendimentoButton, null), resource, titulo, controller -> {
            if (controller instanceof PAEEController c) {
                c.setEducando(educando);
            }
        });
    }

    private void abrirTelaRelatorio(String resource, String titulo) {
        Janelas.carregarTela(new ActionEvent(statusAtendimentoButton, null), resource, titulo, controller -> {
            if (controller instanceof RelatorioIndividualController c) {
                c.setEducando(educando);
            }
        });
    }

    private void abrirTelaDiagnosticoInicial(String resource, String titulo) {
        Janelas.carregarTela(new javafx.event.ActionEvent(statusAtendimentoButton, null), resource, titulo, controller -> {
            if (controller instanceof DIController c) {
                c.setEducando(educando);
            }
        });
    }
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Inicialização do card
        if (educando != null) {
            atualizarDados();
        }
        if (excluirAluno != null) excluirAluno.setOnAction(e -> handleExcluirAlunoAction());
        if (editarAluno != null) editarAluno.setOnAction(this::handleEditarAlunoAction);
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
