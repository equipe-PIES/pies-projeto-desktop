package com.pies.projeto.integrado.piesfront.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pies.projeto.integrado.piesfront.dto.AlunoSimplificadoDTO;
import com.pies.projeto.integrado.piesfront.dto.CreateTurmaDTO;
import com.pies.projeto.integrado.piesfront.dto.ProfessorDTO;
import com.pies.projeto.integrado.piesfront.dto.UserInfoDTO;
import com.pies.projeto.integrado.piesfront.services.AuthService;
import com.utils.Janelas;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.animation.PauseTransition;
import java.util.stream.Collectors;
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
    
    // Busca e adição de alunos por nome
    @FXML
    private TextField buscarAlunoNome;
    @FXML
    private Button adicionarAlunoButton;
    @FXML
    private ListView<String> SugestoesAlunosList;
    @FXML
    private ListView<String> ListAlunosTurma;

    private final AuthService authService;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private List<ProfessorDTO> professoresDisponiveis;
    private List<AlunoSimplificadoDTO> alunosAdicionados;
    private List<AlunoSimplificadoDTO> todosAlunosCache; // Cache para evitar múltiplas requisições
    private PauseTransition searchDebounce;
    private String currentQuery = "";

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
        configurarBuscaAlunosPorNome();
        
        // Carrega o cache de alunos de forma proativa em background
        carregarCacheAlunosEmBackground();
    }

    // ----------------------------------------------------
    // MÉTODOS DE INICIALIZAÇÃO
    // ----------------------------------------------------

    private void inicializarChoiceBoxes() {
        // Inicializa os graus escolares (conforme enum GrauEscolar do backend)
        if (grauTurma != null && grauTurma.getItems().isEmpty()) {
            grauTurma.getItems().addAll(
                "Educação Infantil",
                "Estimulação Precoce",
                "Fundamental I",
                "Fundamental II",
                "Ensino Médio",
                "Outro"
            );
        }

        // Inicializa faixas etárias
        if (idadeAlunos != null && idadeAlunos.getItems().isEmpty()) {
            idadeAlunos.getItems().addAll(
                "4-5 anos",
                "6-8 anos",
                "9-11 anos",
                "12-14 anos",
                "15-18 anos",
                "acima de 18 anos"
            );
        }

        // Inicializa turnos (conforme enum Turno do backend)
        if (turnoTurma != null && turnoTurma.getItems().isEmpty()) {
            turnoTurma.getItems().addAll(
                "Matutino",
                "Vespertino"
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
            cancelCadastroBt.setOnAction(e -> handleInicioButtonAction(e));
        }
        if (adicionarAlunoButton != null) {
            adicionarAlunoButton.setOnAction(e -> adicionarAlunoSelecionado());
        }
    }

    /**
     * Carrega o cache de alunos de forma proativa em background
     * para que a primeira busca por CPF seja instantânea
     */
    private void carregarCacheAlunosEmBackground() {
        String token = authService.getCurrentToken();
        if (token == null || token.isEmpty()) {
            return; // Silenciosamente ignora se não há token
        }

        // Executa em thread separada para não bloquear a UI
        Thread thread = new Thread(() -> {
            try {
                System.out.println("=== CARREGANDO CACHE DE ALUNOS EM BACKGROUND ===");
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
                    System.out.println("✓ Cache carregado com " + todosAlunosCache.size() + " alunos");
                } else {
                    System.err.println("Erro ao carregar cache de alunos. Código: " + response.statusCode());
                }
            } catch (Exception e) {
                System.err.println("Erro ao carregar cache de alunos em background: " + e.getMessage());
            }
        });
        thread.setDaemon(true); // Thread daemon para não bloquear o encerramento da aplicação
        thread.start();
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

    private void configurarBuscaAlunosPorNome() {
        if (SugestoesAlunosList != null) {
            SugestoesAlunosList.setItems(FXCollections.observableArrayList());
            SugestoesAlunosList.setCellFactory(list -> new ListCell<String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        String q = currentQuery == null ? "" : currentQuery.trim().toLowerCase();
                        if (q.isEmpty()) {
                            setText(item);
                            setGraphic(null);
                        } else {
                            String lower = item.toLowerCase();
                            int idx = lower.indexOf(q);
                            if (idx >= 0) {
                                Text pre = new Text(item.substring(0, idx));
                                Text match = new Text(item.substring(idx, idx + q.length()));
                                match.setStyle("-fx-font-weight: bold; -fx-fill: #2c3e50;");
                                Text post = new Text(item.substring(idx + q.length()));
                                TextFlow flow = new TextFlow(pre, match, post);
                                setText(null);
                                setGraphic(flow);
                            } else {
                                setText(item);
                                setGraphic(null);
                            }
                        }
                    }
                }
            });
            SugestoesAlunosList.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) {
                    adicionarAlunoSelecionado();
                }
            });
        }
        if (buscarAlunoNome != null) {
            searchDebounce = new PauseTransition(javafx.util.Duration.millis(300));
            searchDebounce.setOnFinished(e -> atualizarSugestoesPorNome(currentQuery));
            buscarAlunoNome.textProperty().addListener((obs, oldVal, newVal) -> {
                currentQuery = newVal;
                searchDebounce.stop();
                searchDebounce.playFromStart();
            });
        }
    }

    private void atualizarSugestoesPorNome(String query) {
        if (SugestoesAlunosList == null) return;
        String q = query == null ? "" : query.trim().toLowerCase();
        if (q.isEmpty()) {
            SugestoesAlunosList.getItems().clear();
            return;
        }
        List<AlunoSimplificadoDTO> todosAlunos = obterTodosAlunos();
        if (todosAlunos == null) return;
        List<String> sugestoes = todosAlunos.stream()
                .filter(a -> a.getNome() != null && a.getNome().toLowerCase().contains(q))
                .limit(20)
                .map(a -> a.getNome() + " - " + a.getCpf())
                .collect(Collectors.toList());
        SugestoesAlunosList.setItems(FXCollections.observableArrayList(sugestoes));
    }

    private List<AlunoSimplificadoDTO> obterTodosAlunos() {
        if (todosAlunosCache != null) return todosAlunosCache;
        String token = authService.getCurrentToken();
        if (token == null || token.isEmpty()) return null;
        try {
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
                return todosAlunosCache;
            }
        } catch (Exception e) {
            System.err.println("Erro ao obter alunos: " + e.getMessage());
        }
        return null;
    }

    private void adicionarAlunoSelecionado() {
        if (SugestoesAlunosList == null || SugestoesAlunosList.getSelectionModel().getSelectedItem() == null) {
            mostrarErro("Selecione um aluno na lista de sugestões.");
            return;
        }
        String selecionado = SugestoesAlunosList.getSelectionModel().getSelectedItem();
        int idx = selecionado.lastIndexOf(" - ");
        final String cpf = (idx >= 0) ? selecionado.substring(idx + 3).trim() : null;
        if (cpf == null || cpf.isEmpty()) {
            mostrarErro("Não foi possível identificar o CPF do aluno selecionado.");
            return;
        }
        boolean jaAdicionado = alunosAdicionados.stream()
                .anyMatch(aluno -> aluno.getCpf().equals(cpf));
        if (jaAdicionado) {
            mostrarErro("Este aluno já foi adicionado à turma.");
            return;
        }
        List<AlunoSimplificadoDTO> todos = obterTodosAlunos();
        if (todos == null) {
            mostrarErro("Não foi possível carregar a lista de alunos.");
            return;
        }
        AlunoSimplificadoDTO alunoEncontrado = todos.stream()
                .filter(a -> a.getCpf().equals(cpf))
                .findFirst()
                .orElse(null);
        if (alunoEncontrado != null) {
            alunosAdicionados.add(alunoEncontrado);
            atualizarListaAlunos();
            System.out.println("✓ Aluno vinculado: " + alunoEncontrado.getNome());
        } else {
            mostrarErro("Aluno não encontrado.");
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
        turmaDTO.setGrauEscolar(mapGrauEscolarToBackend(grauTurma.getValue()));
        turmaDTO.setFaixaEtaria(idadeAlunos.getValue());
        turmaDTO.setTurno(mapTurnoToBackend(turnoTurma.getValue()));
        turmaDTO.setProfessorId(professorId);
        if (alunosAdicionados != null && !alunosAdicionados.isEmpty()) {
            List<String> cpfs = alunosAdicionados.stream().map(AlunoSimplificadoDTO::getCpf).collect(Collectors.toList());
            turmaDTO.setCpfsAlunos(cpfs);
        }

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
                Janelas.carregarTela(new javafx.event.ActionEvent(inicioButton, null), "/com/pies/projeto/integrado/piesfront/screens/tela-inicio-coord.fxml", "Início - Coordenador(a)");
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

    private String mapGrauEscolarToBackend(String valor) {
        String v = valor.trim();
        if (v.equalsIgnoreCase("Educação Infantil") || v.equalsIgnoreCase("Educacao Infantil")) return "EDUCACAO_INFANTIL";
        if (v.equalsIgnoreCase("Estimulação Precoce") || v.equalsIgnoreCase("Estimulacao Precoce")) return "ESTIMULACAO_PRECOCE";
        if (v.equalsIgnoreCase("Fundamental I")) return "FUNDAMENTAL_I";
        if (v.equalsIgnoreCase("Fundamental II")) return "FUNDAMENTAL_II";
        if (v.equalsIgnoreCase("Ensino Médio") || v.equalsIgnoreCase("Ensino Medio")) return "MEDIO";
        if (v.equalsIgnoreCase("Outro")) return "OUTRO";
        return "PREFIRO_NAO_INFORMAR";
    }

    private String mapTurnoToBackend(String valor) {
        String v = valor.trim();
        if (v.equalsIgnoreCase("Matutino")) return "MATUTINO";
        return "VESPERTINO";
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
    private void handleInicioButtonAction(javafx.event.ActionEvent event) {
        Janelas.carregarTela(event, "/com/pies/projeto/integrado/piesfront/screens/tela-inicio-coord.fxml", "Início - Coordenador(a)");
    }

    @FXML
    private void handleSairButtonAction(javafx.event.ActionEvent event) {
        authService.logout();
        Janelas.carregarTela(event, "/com/pies/projeto/integrado/piesfront/screens/tela-de-login.fxml", "Amparo Edu - Login");
    }
}
