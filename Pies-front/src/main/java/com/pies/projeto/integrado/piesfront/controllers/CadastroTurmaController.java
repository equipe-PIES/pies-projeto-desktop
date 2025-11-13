package com.pies.projeto.integrado.piesfront.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pies.projeto.integrado.piesfront.dto.AlunoSimplificadoDTO;
import com.pies.projeto.integrado.piesfront.dto.CreateTurmaDTO;
import com.pies.projeto.integrado.piesfront.dto.ProfessorDTO;
import com.pies.projeto.integrado.piesfront.dto.UserInfoDTO;
import com.pies.projeto.integrado.piesfront.services.AuthService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class CadastroTurmaController implements Initializable {
    // ----------------------------------------------------
    // DECLARAÇÃO DOS fx:id
    // ----------------------------------------------------
    @FXML
    private Label indicadorDeTela;
    @FXML
    private Label nameUser;
    @FXML
    private Button sairButton;
    @FXML
    private Button inicioButton;
    @FXML
    private Button cadastroTurmaButton;
    @FXML
    private Button cancelCadastroBt;

    // Campos do formulário de turma
    @FXML
    private Label NomeIdTurma;
    @FXML
    private TextField nomeTurmaField; // Para o nome da turma
    @FXML
    private ChoiceBox<String> grauTurma;
    @FXML
    private ChoiceBox<String> idadeAlunos;
    @FXML
    private ChoiceBox<String> profRespon;
    @FXML
    private ChoiceBox<String> turnoTurma;
    @FXML
    private Label erroMensagem;
    
    // Campos para adicionar alunos
    @FXML
    private TextField addCPF;
    @FXML
    private Button addCPFButton;
    @FXML
    private ListView<String> ListAlunosTurma;

    private final AuthService authService;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private List<ProfessorDTO> professoresDisponiveis;
    private List<AlunoSimplificadoDTO> alunosAdicionados;
    private List<AlunoSimplificadoDTO> todosAlunosCache; // Cache para evitar múltiplas requisições

    public CadastroTurmaController() {
        this.authService = AuthService.getInstance();
        this.httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
        this.objectMapper = new ObjectMapper();
        this.alunosAdicionados = new ArrayList<>();
        this.todosAlunosCache = null; // Inicialmente sem cache
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        atualizarIndicadorDeTela(url);
        atualizarNomeUsuario();
        
        inicializarChoiceBoxes();
        carregarProfessores();
        conectarAcoesFormulario();
        configurarListViewAlunos();
    }

    // ----------------------------------------------------
    // MÉTODOS DE INICIALIZAÇÃO
    // ----------------------------------------------------

    private void inicializarChoiceBoxes() {
        // Inicializa os graus escolares (conforme enum GrauEscolar do backend)
        if (grauTurma != null && grauTurma.getItems().isEmpty()) {
            grauTurma.getItems().addAll(
                "EDUCACAO_INFANTIL",
                "FUNDAMENTAL_I",
                "FUNDAMENTAL_II"
            );
        }

        // Inicializa faixas etárias
        if (idadeAlunos != null && idadeAlunos.getItems().isEmpty()) {
            idadeAlunos.getItems().addAll(
                "4-5 anos",
                "6-8 anos",
                "9-11 anos",
                "12-14 anos"
            );
        }

        // Inicializa turnos (conforme enum Turno do backend)
        if (turnoTurma != null && turnoTurma.getItems().isEmpty()) {
            turnoTurma.getItems().addAll(
                "MATUTINO",
                "VESPERTINO"
            );
        }
    }

    private void carregarProfessores() {
        String token = authService.getCurrentToken();
        if (token == null || token.isEmpty()) {
            mostrarErro("Sessão expirada. Faça login novamente.");
            return;
        }

        try {
            System.out.println("=== CARREGANDO PROFESSORES ===");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/professores"))
                    .header("Authorization", "Bearer " + token)
                    .GET()
                    .timeout(Duration.ofSeconds(10))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            System.out.println("Status: " + response.statusCode());
            System.out.println("Body: " + response.body().substring(0, Math.min(200, response.body().length())) + "...");

            if (response.statusCode() == 200) {
                professoresDisponiveis = objectMapper.readValue(
                    response.body(),
                    new TypeReference<List<ProfessorDTO>>() {}
                );

                System.out.println("✓ " + professoresDisponiveis.size() + " professores carregados");

                if (profRespon != null) {
                    profRespon.getItems().clear();
                    for (ProfessorDTO prof : professoresDisponiveis) {
                        profRespon.getItems().add(prof.getNome() + " - " + prof.getCpf());
                    }
                }
            } else if (response.statusCode() == 403) {
                mostrarErro("Acesso negado. Verifique suas permissões.");
                System.err.println("403 - Token pode estar expirado ou sem permissão");
            } else {
                mostrarErro("Erro ao carregar professores. Código: " + response.statusCode());
                System.err.println("Erro HTTP: " + response.statusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
            mostrarErro("Erro ao carregar professores: " + e.getMessage());
        }
    }

    private void conectarAcoesFormulario() {
        if (cadastroTurmaButton != null) {
            cadastroTurmaButton.setOnAction(e -> enviarCadastroTurma());
        }
        if (cancelCadastroBt != null) {
            cancelCadastroBt.setOnAction(e -> handleInicioButtonAction());
        }
        if (addCPFButton != null) {
            addCPFButton.setOnAction(e -> adicionarAlunoPorCPF());
        }
    }

    private void configurarListViewAlunos() {
        if (ListAlunosTurma != null) {
            ListAlunosTurma.setItems(FXCollections.observableArrayList());
            
            // Configura a célula customizada para mostrar nome e botão de remover
            ListAlunosTurma.setCellFactory(param -> new ListCell<String>() {
                private final Button btnRemover = new Button("✖");
                
                {
                    btnRemover.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-cursor: hand;");
                    btnRemover.setOnAction(event -> {
                        String item = getItem();
                        if (item != null) {
                            removerAluno(item);
                        }
                    });
                }
                
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        setText(item);
                        setGraphic(btnRemover);
                    }
                }
            });
        }
    }

    private void adicionarAlunoPorCPF() {
        if (addCPF == null || addCPF.getText() == null || addCPF.getText().trim().isEmpty()) {
            mostrarErro("Digite o CPF do aluno.");
            return;
        }

        String cpf = addCPF.getText().trim();
        limparErro();

        // Verifica se o aluno já foi adicionado
        boolean jaAdicionado = alunosAdicionados.stream()
                .anyMatch(aluno -> aluno.getCpf().equals(cpf));
        
        if (jaAdicionado) {
            mostrarErro("Este aluno já foi adicionado à turma.");
            return;
        }

        // Busca o aluno no backend
        String token = authService.getCurrentToken();
        if (token == null || token.isEmpty()) {
            mostrarErro("Sessão expirada. Faça login novamente.");
            return;
        }

        try {
            System.out.println("=== BUSCANDO ALUNO POR CPF ===");
            System.out.println("CPF: " + cpf);

            // Usa o cache se já tiver carregado, senão busca do backend
            List<AlunoSimplificadoDTO> todosAlunos;
            
            if (todosAlunosCache == null) {
                System.out.println("Cache vazio - buscando alunos do backend...");
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/educandos"))
                        .header("Authorization", "Bearer " + token)
                        .header("Content-Type", "application/json")
                        .GET()
                        .timeout(Duration.ofSeconds(10))
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    todosAlunosCache = objectMapper.readValue(
                        response.body(),
                        new TypeReference<List<AlunoSimplificadoDTO>>() {}
                    );
                    todosAlunos = todosAlunosCache;
                    System.out.println("Cache carregado com " + todosAlunos.size() + " alunos");
                } else if (response.statusCode() == 403) {
                    mostrarErro("Acesso negado ao buscar alunos.");
                    return;
                } else {
                    mostrarErro("Erro ao buscar aluno. Código: " + response.statusCode());
                    return;
                }
            } else {
                System.out.println("Usando cache com " + todosAlunosCache.size() + " alunos");
                todosAlunos = todosAlunosCache;
            }

            // Busca o aluno com o CPF informado (remove pontos e traços para comparação)
            String cpfLimpo = cpf.replaceAll("[.\\-]", "");
            AlunoSimplificadoDTO alunoEncontrado = todosAlunos.stream()
                    .filter(aluno -> {
                        String cpfBancoLimpo = aluno.getCpf().replaceAll("[.\\-]", "");
                        return cpfBancoLimpo.equals(cpfLimpo);
                    })
                    .findFirst()
                    .orElse(null);

            if (alunoEncontrado != null) {
                alunosAdicionados.add(alunoEncontrado);
                atualizarListaAlunos();
                addCPF.clear();
                System.out.println("✓ Aluno vinculado: " + alunoEncontrado.getNome());
            } else {
                mostrarErro("CPF não encontrado. Cadastre o aluno primeiro.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erro ao buscar aluno: " + e.getMessage());
            mostrarErro("Erro ao buscar aluno: " + e.getMessage());
        }
    }

    private void removerAluno(String nomeAluno) {
        // Remove o aluno da lista pelo nome
        alunosAdicionados.removeIf(aluno -> aluno.getNome().equals(nomeAluno));
        atualizarListaAlunos();
        System.out.println("Aluno removido: " + nomeAluno);
    }

    private void atualizarListaAlunos() {
        if (ListAlunosTurma != null) {
            ObservableList<String> nomesAlunos = FXCollections.observableArrayList();
            for (AlunoSimplificadoDTO aluno : alunosAdicionados) {
                nomesAlunos.add(aluno.getNome());
            }
            ListAlunosTurma.setItems(nomesAlunos);
        }
    }

    // ----------------------------------------------------
    // MÉTODOS DE VALIDAÇÃO E ENVIO
    // ----------------------------------------------------

    private boolean validarFormulario() {
        limparErro();

        // Verifica se há um campo de texto para o nome da turma
        String nomeTurma = null;
        if (NomeIdTurma != null && NomeIdTurma.getText() != null && !NomeIdTurma.getText().equals("[Nome da Turma]")) {
            nomeTurma = NomeIdTurma.getText().trim();
        }
        if (nomeTurmaField != null && nomeTurmaField.getText() != null && !nomeTurmaField.getText().trim().isEmpty()) {
            nomeTurma = nomeTurmaField.getText().trim();
        }

        if (nomeTurma == null || nomeTurma.isEmpty() || nomeTurma.equals("[Nome da Turma]")) {
            mostrarErro("Informe o nome da turma.");
            return false;
        }

        if (grauTurma == null || grauTurma.getValue() == null || grauTurma.getValue().trim().isEmpty()) {
            mostrarErro("Selecione o grau escolar.");
            return false;
        }

        if (idadeAlunos == null || idadeAlunos.getValue() == null || idadeAlunos.getValue().trim().isEmpty()) {
            mostrarErro("Selecione a faixa etária dos alunos.");
            return false;
        }

        if (profRespon == null || profRespon.getValue() == null || profRespon.getValue().trim().isEmpty()) {
            mostrarErro("Selecione o(a) professor(a) responsável.");
            return false;
        }

        if (turnoTurma == null || turnoTurma.getValue() == null || turnoTurma.getValue().trim().isEmpty()) {
            mostrarErro("Selecione o turno.");
            return false;
        }

        return true;
    }

    private void enviarCadastroTurma() {
        if (!validarFormulario()) {
            return;
        }

        String token = authService.getCurrentToken();
        if (token == null || token.isEmpty()) {
            mostrarErro("Sessão expirada. Faça login novamente.");
            return;
        }

        // Obtém o nome da turma
        String nomeTurma = null;
        if (NomeIdTurma != null && NomeIdTurma.getText() != null && !NomeIdTurma.getText().equals("[Nome da Turma]")) {
            nomeTurma = NomeIdTurma.getText().trim();
        }
        if (nomeTurmaField != null && nomeTurmaField.getText() != null && !nomeTurmaField.getText().trim().isEmpty()) {
            nomeTurma = nomeTurmaField.getText().trim();
        }

        // Obtém o ID do professor selecionado
        String professorId = obterProfessorIdSelecionado();
        if (professorId == null) {
            mostrarErro("Professor selecionado não encontrado.");
            return;
        }

        // Monta o DTO
        CreateTurmaDTO turmaDTO = new CreateTurmaDTO();
        turmaDTO.setNome(nomeTurma);
        turmaDTO.setGrauEscolar(grauTurma.getValue());
        turmaDTO.setFaixaEtaria(idadeAlunos.getValue());
        turmaDTO.setTurno(turnoTurma.getValue());
        turmaDTO.setProfessorId(professorId);

        try {
            String json = objectMapper.writeValueAsString(turmaDTO);
            
            System.out.println("=== JSON SENDO ENVIADO PARA /turmas ===");
            System.out.println(json);
            System.out.println("========================================");

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/turmas"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .timeout(Duration.ofSeconds(10))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("=== RESPOSTA DO BACKEND ===");
            System.out.println("Status: " + response.statusCode());
            System.out.println("Body: " + response.body());
            System.out.println("===========================");

            if (response.statusCode() == 200 || response.statusCode() == 201) {
                System.out.println("Turma cadastrada com sucesso!");
                handleInicioButtonAction();
            } else if (response.statusCode() == 400) {
                mostrarErro("Dados inválidos. Verifique os campos.\n" + response.body());
            } else {
                mostrarErro("Falha ao cadastrar turma. Código: " + response.statusCode());
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("=== ERRO DETALHADO ===");
            System.err.println("Mensagem: " + e.getMessage());
            System.err.println("Tipo: " + e.getClass().getName());
            if (e.getCause() != null) {
                System.err.println("Causa: " + e.getCause().getMessage());
            }
            System.err.println("======================");
            mostrarErro("Erro ao comunicar com o servidor: " + e.getMessage());
        }
    }

    private String obterProfessorIdSelecionado() {
        if (profRespon == null || profRespon.getValue() == null) {
            System.err.println("ERRO: Nenhum professor selecionado");
            return null;
        }

        String selecao = profRespon.getValue();
        System.out.println("=== BUSCANDO ID DO PROFESSOR ===");
        System.out.println("Seleção: " + selecao);
        System.out.println("Professores disponíveis: " + (professoresDisponiveis != null ? professoresDisponiveis.size() : 0));
        
        // A seleção está no formato "Nome - CPF"
        // Precisamos encontrar o professor correspondente na lista

        for (ProfessorDTO prof : professoresDisponiveis) {
            String itemText = prof.getNome() + " - " + prof.getCpf();
            System.out.println("  Comparando: [" + itemText + "] == [" + selecao + "] ? " + itemText.equals(selecao));
            if (itemText.equals(selecao)) {
                System.out.println("✓ Professor encontrado! ID: " + prof.getId());
                return prof.getId();
            }
        }

        System.err.println("ERRO: Professor não encontrado na lista!");
        return null;
    }

    private void mostrarErro(String mensagem) {
        if (erroMensagem != null) {
            erroMensagem.setText(mensagem);
            erroMensagem.setStyle("-fx-text-fill: red;");
        } else {
            System.err.println("Erro no formulário: " + mensagem);
        }
    }

    private void limparErro() {
        if (erroMensagem != null) {
            erroMensagem.setText("");
        }
    }

    // ----------------------------------------------------
    // MÉTODOS DE NAVEGAÇÃO E AUXILIARES
    // ----------------------------------------------------

    private void atualizarIndicadorDeTela(URL url) {
        if (indicadorDeTela == null || url == null) {
            return;
        }

        String arquivoFXML = url.getPath();
        String textoIndicador;

        if (arquivoFXML.contains("tela-inicio-coord.fxml")) {
            textoIndicador = "Início";
        } else if (arquivoFXML.contains("cadastro-de-aluno.fxml")) {
            textoIndicador = "Cadastro de Aluno(a)";
        } else if (arquivoFXML.contains("cadastro-de-prof.fxml")) {
            textoIndicador = "Cadastro de Professor(a)";
        } else if (arquivoFXML.contains("cadastro-de-turma.fxml")) {
            textoIndicador = "Cadastro de Turma";
        } else {
            textoIndicador = "Indicador de Tela";
        }

        indicadorDeTela.setText(textoIndicador);
    }

    private void atualizarNomeUsuario() {
        if (nameUser == null) {
            return;
        }

        UserInfoDTO userInfo = authService.getUserInfo();

        if (userInfo != null && userInfo.name() != null && !userInfo.name().isEmpty()) {
            nameUser.setText(userInfo.name());
        } else {
            nameUser.setText("Usuário");
            System.err.println("Não foi possível carregar o nome do usuário.");
        }
    }

    public void setIndicadorDeTela(String texto) {
        if (indicadorDeTela != null) {
            indicadorDeTela.setText(texto);
        }
    }

    @FXML
    private void handleInicioButtonAction() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/pies/projeto/integrado/piesfront/screens/tela-inicio-coord.fxml"));

            Stage currentStage = (Stage) inicioButton.getScene().getWindow();
            currentStage.setScene(new Scene(root));
            currentStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erro ao carregar a tela de início: " + e.getMessage());
        }
    }

    @FXML
    private void handleSairButtonAction() {
        authService.logout();

        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/pies/projeto/integrado/piesfront/screens/tela-de-login.fxml"));

            Stage currentStage = (Stage) sairButton.getScene().getWindow();
            currentStage.setScene(new Scene(root));
            currentStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erro ao carregar a tela de login: " + e.getMessage());
        }
    }
}
