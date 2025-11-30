package com.pies.projeto.integrado.piesfront.controllers;

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
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import javafx.event.ActionEvent;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CadastroProfController implements Initializable {
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
    private Button cadastroProfBt;
    @FXML
    private Button cancelCadastroBt;
    @FXML
    private Label erroEscolhaSenha;

    // Campos do formulário
    @FXML
    private TextField nomeProf;
    @FXML
    private TextField cpfProf;
    @FXML
    private DatePicker dtNascProf;
    @FXML
    private ChoiceBox<String> generoProf;
    @FXML
    private TextArea obsProf;
    @FXML
    private TextField emailProf;
    @FXML
    private PasswordField passwordProf;
    @FXML
    private PasswordField confirmPasswordProf;

    private final AuthService authService;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public CadastroProfController() {
        this.authService = AuthService.getInstance();
        this.httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        atualizarIndicadorDeTela(url);
        atualizarNomeUsuario();

        inicializarGeneros();
        conectarAcoesFormulario();
        aplicarCpfMask(cpfProf);
    }

    /**
     * Atualiza o texto do indicador de tela baseado no arquivo FXML carregado.
     */
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
            // Texto padrão caso não reconheça a tela
            textoIndicador = "Indicador de Tela";
        }

        indicadorDeTela.setText(textoIndicador);
    }

    /**
     * Busca as informações do usuário logado e atualiza o nome exibido.
     */
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

    /**
     * Permite atualizar o indicador de tela manualmente em navegações.
     */
    public void setIndicadorDeTela(String texto) {
        if (indicadorDeTela != null) {
            indicadorDeTela.setText(texto);
        }
    }

    private void inicializarGeneros() {
        if (generoProf != null && generoProf.getItems().isEmpty()) {
            generoProf.getItems().addAll("Masculino", "Feminino", "Outro", "Prefiro não informar");
        }
    }

    private void conectarAcoesFormulario() {
        if (cadastroProfBt != null) {
            cadastroProfBt.setOnAction(e -> enviarCadastroProfessor());
        }
        if (cancelCadastroBt != null) {
            cancelCadastroBt.setOnAction(e -> handleInicioButtonAction(e));
        }
    }

    private boolean validarFormulario() {
        // Limpa mensagens anteriores
        if (erroEscolhaSenha != null) {
            erroEscolhaSenha.setText("");
        }

        if (nomeProf == null || nomeProf.getText() == null || nomeProf.getText().trim().isEmpty()) {
            mostrarErro("Informe o nome do(a) professor(a).");
            return false;
        }

        if (cpfProf == null || cpfProf.getText() == null || cpfProf.getText().trim().isEmpty()) {
            mostrarErro("Informe o CPF.");
            return false;
        }

        String cpf = cpfProf.getText().trim();
        Pattern cpfPattern = Pattern.compile("\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}");
        if (!cpfPattern.matcher(cpf).matches()) {
            mostrarErro("CPF deve estar no formato 000.000.000-00.");
            return false;
        }

        if (dtNascProf == null || dtNascProf.getValue() == null) {
            mostrarErro("Informe a data de nascimento.");
            return false;
        }

        LocalDate data = dtNascProf.getValue();
        if (!data.isBefore(LocalDate.now())) {
            mostrarErro("Data de nascimento deve ser no passado.");
            return false;
        }

        if (generoProf == null || generoProf.getValue() == null || generoProf.getValue().trim().isEmpty()) {
            mostrarErro("Selecione o gênero.");
            return false;
        }

        // Validações mínimas para cadastro de usuário (fluxo 2/2)
        if (emailProf == null || emailProf.getText() == null || emailProf.getText().trim().isEmpty()) {
            mostrarErro("Informe o e-mail do usuário (professor).");
            return false;
        }
        if (passwordProf == null || passwordProf.getText() == null || passwordProf.getText().trim().isEmpty()) {
            mostrarErro("Informe a senha do usuário (professor).");
            return false;
        }
        if (confirmPasswordProf == null || confirmPasswordProf.getText() == null || confirmPasswordProf.getText().trim().isEmpty()) {
            mostrarErro("Confirme a senha do usuário (professor).");
            return false;
        }
        if (!passwordProf.getText().equals(confirmPasswordProf.getText())) {
            mostrarErro("As senhas não coincidem.");
            return false;
        }

        return true;
    }

    private void aplicarCpfMask(TextField campo) {
        if (campo == null) return;
        campo.textProperty().addListener((obs, old, neu) -> {
            String digits = neu.replaceAll("\\D", "");
            if (digits.length() > 11) digits = digits.substring(0, 11);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < digits.length(); i++) {
                char c = digits.charAt(i);
                if (i == 3 || i == 6) sb.append('.');
                if (i == 9) sb.append('-');
                sb.append(c);
            }
            String formatted = sb.toString();
            if (!formatted.equals(neu)) {
                campo.setText(formatted);
                campo.positionCaret(formatted.length());
            }
        });
    }

    private void mostrarErro(String mensagem) {
        if (erroEscolhaSenha != null) {
            erroEscolhaSenha.setText(mensagem);
        } else {
            System.err.println("Formulário inválido: " + mensagem);
        }
    }

    private void limparErro() {
        if (erroEscolhaSenha != null) {
            erroEscolhaSenha.setText("");
        }
    }

    private void enviarCadastroProfessor() {
        if (!validarFormulario()) {
            return;
        }

        limparErro();

        String token = authService.getCurrentToken();
        if (token == null || token.isEmpty()) {
            mostrarErro("Sessão expirada. Faça login novamente.");
            return;
        }

        // Monta o corpo da requisição conforme CreateProfessorDTO no backend
        CreateProfessorRequest requestBody = new CreateProfessorRequest();
        requestBody.nome = nomeProf.getText().trim();
        requestBody.cpf = cpfProf.getText().trim();
        requestBody.dataNascimento = dtNascProf.getValue().format(DateTimeFormatter.ISO_LOCAL_DATE);
        requestBody.genero = mapGeneroToBackend(generoProf.getValue());
        // Formacao é obrigatória no backend. Como não há campo na UI ainda, usamos um default.
        requestBody.formacao = "Graduação";
        requestBody.observacoes = (obsProf != null && obsProf.getText() != null) ? obsProf.getText().trim() : null;

        try {
            String json = objectMapper.writeValueAsString(requestBody);
            
            // DEBUG: Imprimir o JSON que está sendo enviado
            System.out.println("=== JSON SENDO ENVIADO PARA /professores ===");
            System.out.println(json);
            System.out.println("============================================");

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/professores"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .timeout(Duration.ofSeconds(10))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 201) {
                // Após criar o professor, registra o usuário (email/senha) com role professor
                boolean registrado = registrarUsuarioProfessor(token);
                if (registrado) {
                    Janelas.carregarTela(new javafx.event.ActionEvent(inicioButton, null), "/com/pies/projeto/integrado/piesfront/screens/tela-inicio-coord.fxml", "Início - Coordenador(a)");
                }
            } else if (response.statusCode() == 400) {
                // Mostra mensagem de validação retornada pelo backend
                mostrarErro(extrairMensagemErro(response.body()));
            } else {
                mostrarErro("Falha ao cadastrar professor. Código: " + response.statusCode());
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

    private boolean registrarUsuarioProfessor(String token) {
        try {
            RegisterRequest body = new RegisterRequest();
            body.login = emailProf.getText().trim();
            body.password = passwordProf.getText().trim();
            body.role = "professor";

            String json = objectMapper.writeValueAsString(body);
            
            // DEBUG: Imprimir o JSON que está sendo enviado
            System.out.println("=== JSON SENDO ENVIADO PARA /auth/register ===");
            System.out.println(json);
            System.out.println("===============================================");

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/auth/register"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .timeout(Duration.ofSeconds(10))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return true;
            }

            if (response.statusCode() == 400) {
                mostrarErro("E-mail já cadastrado. Escolha outro e-mail.");
                return false;
            }

            mostrarErro("Falha ao registrar usuário. Código: " + response.statusCode());
            return false;

        } catch (Exception e) {
            e.printStackTrace();
            mostrarErro("Erro ao registrar usuário: " + e.getMessage());
            return false;
        }
    }

    // DTOs simples para serialização do corpo da requisição
    public static class CreateProfessorRequest {
        @JsonProperty("nome")
        public String nome;
        
        @JsonProperty("cpf")
        public String cpf;
        
        @JsonProperty("dataNascimento")
        public String dataNascimento; // ISO-8601 yyyy-MM-dd
        
        @JsonProperty("genero")
        public String genero;
        
        @JsonProperty("formacao")
        public String formacao;
        
        @JsonProperty("observacoes")
        public String observacoes;
    }

    public static class RegisterRequest {
        @JsonProperty("login")
        public String login;
        
        @JsonProperty("password")
        public String password;
        
        @JsonProperty("role")
        public String role;
    }

    private String mapGeneroToBackend(String valor) {
        String v = valor.trim();
        if (v.equalsIgnoreCase("Masculino")) return "MASCULINO";
        if (v.equalsIgnoreCase("Feminino")) return "FEMININO";
        if (v.equalsIgnoreCase("Outro")) return "OUTRO";
        return "PREFIRO_NAO_INFORMAR";
    }

    private String extrairMensagemErro(String responseBody) {
        try {
            // Pode ser uma string simples ou um JSON; tentamos mapear como texto simples primeiro
            if (responseBody == null || responseBody.trim().isEmpty()) {
                return "Requisição inválida.";
            }
            return responseBody;
        } catch (Exception e) {
            return "Requisição inválida.";
        }
    }

    /**
     * Navega para a tela inicial do coordenador.
     */
    @FXML
    private void handleInicioButtonAction(javafx.event.ActionEvent event) {
        Janelas.carregarTela(event, "/com/pies/projeto/integrado/piesfront/screens/tela-inicio-coord.fxml", "Início - Coordenador(a)");
    }

    /**
     * Faz logout e volta para a tela de login.
     */
    @FXML
    private void handleSairButtonAction(javafx.event.ActionEvent event) {
        authService.logout();
        Janelas.carregarTela(event, "/com/pies/projeto/integrado/piesfront/screens/tela-de-login.fxml", "Amparo Edu - Login");
    }

}
