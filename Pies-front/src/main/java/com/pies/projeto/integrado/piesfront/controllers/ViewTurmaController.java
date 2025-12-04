package com.pies.projeto.integrado.piesfront.controllers;

import com.pies.projeto.integrado.piesfront.dto.EducandoDTO;
import com.pies.projeto.integrado.piesfront.dto.TurmaDTO;
import com.pies.projeto.integrado.piesfront.dto.UserInfoDTO;
import com.pies.projeto.integrado.piesfront.services.AuthService;
import com.utils.Janelas;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ViewTurmaController implements Initializable {
    // ----------------------------------------------------
    // DECLARAÇÃO DOS fx:id - Componentes Padrões
    // ----------------------------------------------------
    @FXML
    private Label indicadorDeTela;
    @FXML
    private Label nameUser;
    @FXML
    private Label cargoUser;
    @FXML
    private ImageView perfilUser;
    @FXML
    private Button sairButton;
    @FXML
    private Button turmasButton;
    @FXML
    private Button alunosButton;
    @FXML
    private Button relatoriosButton;
    
    // ----------------------------------------------------
    // DECLARAÇÃO DOS fx:id - Informações da Turma
    // ----------------------------------------------------
    @FXML
    private Label nomeTurmaLabel;
    @FXML
    private Label totalAlunosTurma;
    @FXML
    private Label grauTurma;
    @FXML
    private Label turnoTurma;
    @FXML
    private Label fxEtariaTurma;
    
    // ----------------------------------------------------
    // DECLARAÇÃO DOS fx:id - Busca e Lista de Alunos
    // ----------------------------------------------------
    @FXML
    private TextField buscarAluno;
    @FXML
    private Button buscarAlunoButton;
    @FXML
    private FlowPane containerAlunos;
    @FXML
    private ScrollPane alunosScrollPane;
    
    private final AuthService authService;
    private String turmaId; // ID da turma sendo visualizada
    private TurmaDTO turmaAtual;
    private List<EducandoDTO> todosEducandos;
    private List<EducandoDTO> educandosFiltrados;

    public ViewTurmaController() {
        this.authService = AuthService.getInstance();
    }
    
    /**
     * Define o ID da turma a ser visualizada
     * Deve ser chamado após carregar a tela
     */
    public void setTurmaId(String turmaId) {
        this.turmaId = turmaId;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        atualizarIndicadorDeTela(url);
        if (alunosScrollPane != null && containerAlunos != null) {
            alunosScrollPane.setFitToWidth(true);
            alunosScrollPane.viewportBoundsProperty().addListener((obs, oldBounds, newBounds) -> {
                double cardWidth = 520.0;
                double hgap = containerAlunos.getHgap();
                double threeColsWidth = cardWidth * 3 + hgap * 2;
                double wrap = Math.min(newBounds.getWidth(), threeColsWidth);
                containerAlunos.setPrefWrapLength(wrap);
            });
        }
        javafx.application.Platform.runLater(() -> {
            atualizarNomeUsuarioAsync();
            if (turmaId != null && !turmaId.isEmpty()) {
                carregarDadosTurmaEAlunosAsync();
            }
        });
    }

    // ----------------------------------------------------
    // MÉTODOS DE AÇÃO - Componentes Padrões
    // ----------------------------------------------------

    /**
     * Atualiza o texto do indicador de tela baseado no arquivo FXML carregado.
     */
    private void atualizarIndicadorDeTela(URL url) {
        if (indicadorDeTela == null || url == null) {
            return;
        }

        String arquivoFXML = url.getPath();
        String textoIndicador;

        if (arquivoFXML.contains("view-turma.fxml")) {
            textoIndicador = "Visualizar Turma";
        } else {
            textoIndicador = "Indicador de Tela";
        }

        indicadorDeTela.setText(textoIndicador);
    }

    /**
     * Busca as informações do usuário logado e atualiza o nome exibido.
     */
    private void atualizarNomeUsuarioAsync() {
        Thread t = new Thread(() -> {
            UserInfoDTO userInfo = authService.getUserInfo();
            javafx.application.Platform.runLater(() -> {
                if (userInfo != null) {
                    if (nameUser != null && userInfo.name() != null && !userInfo.name().isEmpty()) {
                        nameUser.setText(userInfo.name());
                    }
                    if (cargoUser != null && userInfo.role() != null) {
                        String cargo = switch (userInfo.role().toUpperCase()) {
                            case "PROFESSOR" -> "Professor(a)";
                            case "COORDENADOR" -> "Coordenador(a)";
                            case "ADMIN" -> "Administrador(a)";
                            default -> "Usuário";
                        };
                        cargoUser.setText(cargo);
                    }
                } else {
                    if (nameUser != null) {
                        nameUser.setText("Usuário");
                    }
                    System.err.println("Não foi possível carregar o nome do usuário.");
                }
            });
        });
        t.setDaemon(true);
        t.start();
    }

    private void carregarDadosTurmaEAlunosAsync() {
        java.util.concurrent.CompletableFuture<TurmaDTO> turmaFuture =
                java.util.concurrent.CompletableFuture.supplyAsync(() -> authService.getTurmaById(turmaId));
        java.util.concurrent.CompletableFuture<java.util.List<EducandoDTO>> educandosFuture =
                java.util.concurrent.CompletableFuture.supplyAsync(() -> authService.getEducandosPorTurma(turmaId));

        turmaFuture.thenAccept(turma -> javafx.application.Platform.runLater(() -> {
            turmaAtual = turma;
            if (turmaAtual != null) {
                if (nomeTurmaLabel != null) {
                    String nome = turmaAtual.nome() != null ? turmaAtual.nome() : "Sem nome";
                    nomeTurmaLabel.setText(nome);
                }
                if (grauTurma != null) {
                    String grau = turmaAtual.grauEscolar() != null ?
                            formatarGrauEscolar(turmaAtual.grauEscolar()) : "Não informado";
                    grauTurma.setText(grau);
                }
                if (turnoTurma != null) {
                    String turno = turmaAtual.turno() != null ?
                            formatarTurno(turmaAtual.turno()) : "Não informado";
                    turnoTurma.setText(turno);
                }
                if (fxEtariaTurma != null) {
                    String faixaEtaria = turmaAtual.faixaEtaria() != null ?
                            turmaAtual.faixaEtaria() : "Não informado";
                    fxEtariaTurma.setText(faixaEtaria);
                }
            }
        }));

        turmaFuture.thenCombine(educandosFuture, (turma, educandos) -> {
            if (turma == null || educandos == null) return java.util.List.<EducandoDTO>of();
            return educandos;
        }).thenAccept(alunos -> javafx.application.Platform.runLater(() -> {
            educandosFiltrados = alunos;
            if (totalAlunosTurma != null) {
                totalAlunosTurma.setText(String.valueOf(educandosFiltrados.size()));
            }
            exibirAlunos(educandosFiltrados);
        }));
    }
    
    /**
     * Carrega as informações da turma e preenche os campos
     */
    private void carregarInformacoesTurma() {
        if (turmaId == null || turmaId.isEmpty()) {
            System.err.println("ID da turma não foi definido!");
            return;
        }
        
        // Busca a turma do backend
        turmaAtual = authService.getTurmaById(turmaId);
        
        if (turmaAtual == null) {
            System.err.println("Turma não encontrada!");
            return;
        }
        
        // Preenche os campos com as informações da turma
        if (nomeTurmaLabel != null) {
            String nome = turmaAtual.nome() != null ? turmaAtual.nome() : "Sem nome";
            nomeTurmaLabel.setText(nome);
        }
        
        if (grauTurma != null) {
            String grau = turmaAtual.grauEscolar() != null ? 
                    formatarGrauEscolar(turmaAtual.grauEscolar()) : "Não informado";
            grauTurma.setText(grau);
        }
        
        if (turnoTurma != null) {
            String turno = turmaAtual.turno() != null ? 
                    formatarTurno(turmaAtual.turno()) : "Não informado";
            turnoTurma.setText(turno);
        }
        
        if (fxEtariaTurma != null) {
            String faixaEtaria = turmaAtual.faixaEtaria() != null ? 
                    turmaAtual.faixaEtaria() : "Não informado";
            fxEtariaTurma.setText(faixaEtaria);
        }
    }
    
    /**
     * Carrega os alunos relacionados à turma e exibe no FlowPane
     */
    private void carregarAlunos() {
        if (containerAlunos == null) {
            System.err.println("FlowPane containerAlunos não foi encontrado!");
            return;
        }
        
        // Limpa os cards existentes
        containerAlunos.getChildren().clear();
        
        // Busca todos os educandos do backend
        todosEducandos = authService.getEducandos();
        
        if (todosEducandos == null || todosEducandos.isEmpty()) {
            Label semAlunosLabel = new Label("Nenhum aluno cadastrado no sistema.");
            semAlunosLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #666;");
            containerAlunos.getChildren().add(semAlunosLabel);
            return;
        }
        
        // Filtra alunos vinculados a esta turma pelo turmaId
        if (turmaAtual != null && turmaAtual.id() != null) {
            educandosFiltrados = todosEducandos.stream()
                    .filter(educando -> turmaAtual.id().equals(educando.turmaId()))
                    .collect(Collectors.toList());
        } else {
            // Se não houver ID da turma, não mostra alunos
            educandosFiltrados = new ArrayList<>();
        }
        
        // Atualiza o total de alunos
        if (totalAlunosTurma != null) {
            totalAlunosTurma.setText(String.valueOf(educandosFiltrados.size()));
        }
        
        // Cria um card para cada aluno
        exibirAlunos(educandosFiltrados);
    }
    
    /**
     * Exibe os alunos no FlowPane
     */
    private void exibirAlunos(List<EducandoDTO> alunos) {
        if (containerAlunos == null) {
            return;
        }
        
        containerAlunos.getChildren().clear();
        
        if (alunos == null || alunos.isEmpty()) {
            Label semAlunosLabel = new Label("Nenhum aluno encontrado.");
            semAlunosLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #666;");
            containerAlunos.getChildren().add(semAlunosLabel);
            return;
        }
        
        for (EducandoDTO educando : alunos) {
            try {
                // Carrega o FXML do card
                FXMLLoader loader = new FXMLLoader(getClass().getResource(
                        "/com/pies/projeto/integrado/piesfront/screens/card-aluno.fxml"));
                VBox cardNode = loader.load();
                
                // Obtém o controller do card e define os dados
                CardAlunoController cardController = loader.getController();
                cardController.setEducando(educando);
                
                // Adiciona o card ao FlowPane
                containerAlunos.getChildren().add(cardNode);
                
            } catch (IOException e) {
                System.err.println("Erro ao carregar card de aluno: " + e.getMessage());
                e.printStackTrace();
            }
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
     * Handler para o botão de buscar aluno
     */
    @FXML
    private void handleBuscarAlunoAction() {
        if (buscarAluno == null || containerAlunos == null) {
            return;
        }
        
        String termoBusca = buscarAluno.getText().trim().toLowerCase();
        
        if (termoBusca.isEmpty()) {
            // Se o campo estiver vazio, mostra todos os alunos da turma
            exibirAlunos(educandosFiltrados);
            return;
        }
        
        // Filtra os alunos pelo termo de busca (nome ou CPF)
        List<EducandoDTO> alunosEncontrados = educandosFiltrados.stream()
                .filter(educando -> {
                    String nome = educando.nome() != null ? educando.nome().toLowerCase() : "";
                    String cpf = educando.cpf() != null ? educando.cpf().toLowerCase() : "";
                    return nome.contains(termoBusca) || cpf.contains(termoBusca);
                })
                .collect(Collectors.toList());
        
        // Exibe os alunos encontrados
        exibirAlunos(alunosEncontrados);
    }
    
    /**
     * Handler para o botão de turmas
     */
    @FXML
    private void handleTurmasButtonAction(javafx.event.ActionEvent event) {
        Janelas.carregarTela(event, "/com/pies/projeto/integrado/piesfront/screens/tela-inicio-professor.fxml", "Início - Professor(a)");
    }

    /**
     * Handler para o botão de sair.
     * Faz logout do usuário e retorna para a tela de login.
     */
    @FXML
    private void handleSairButtonAction(javafx.event.ActionEvent event) {
        // Faz logout - limpa o token de autenticação
        authService.logout();

        // Carrega a tela de login
        Janelas.carregarTela(event, "/com/pies/projeto/integrado/piesfront/screens/tela-de-login.fxml", "Amparo Edu - Login");
    }
}
