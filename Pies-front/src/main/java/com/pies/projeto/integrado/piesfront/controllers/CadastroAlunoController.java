package com.pies.projeto.integrado.piesfront.controllers;

import com.pies.projeto.integrado.piesfront.dto.EducandoRequest;
import com.pies.projeto.integrado.piesfront.dto.EnderecoRequest;
import com.pies.projeto.integrado.piesfront.dto.ResponsavelRequest;
import com.pies.projeto.integrado.piesfront.dto.UserInfoDTO;
import com.pies.projeto.integrado.piesfront.services.AuthService;
import com.utils.Janelas;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javafx.animation.PauseTransition;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class CadastroAlunoController implements Initializable {
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

    //Campos do formulário (ALUNO)
    @FXML
    private TextField nomeAluno;
    @FXML
    private TextField cpfAluno;
    @FXML
    private DatePicker dtNascAluno;
    @FXML
    private ChoiceBox generoAluno;
    @FXML
    private ChoiceBox grauEscAluno;
    @FXML
    private TextField escolaAluno;
    @FXML
    private TextField cidAluno;
    @FXML
    private TextField nisAluno;
    @FXML
    private TextArea obsAluno;

    //Campos do formulário(RESPONSÁVEL)
    @FXML
    private TextField nomeRespon;
    @FXML
    private TextField cpfRespon;
    @FXML
    private ChoiceBox<String> parentescoRespon; // Agora ChoiceBox em vez de TextField
    @FXML
    private TextField contatoRespon;

    //Campos do formulário (ENDEREÇO)
    @FXML
    private ChoiceBox endUF;
    @FXML
    private TextField endCidade;
    @FXML
    private TextField endCEP;
    @FXML
    private TextField endRua;
    @FXML
    private TextField endNum;
    @FXML
    private TextField endBairro;
    @FXML
    private TextField endComplemento;
    @FXML
    private Label ErrorForm;

    // --------- BOTÕES DE FORMULÁRIO ---------
    @FXML
    private Button cadastroAlunoBt;
    @FXML
    private Button cancelCadastroBt;

    private final AuthService authService;

    public CadastroAlunoController() {
        this.authService = AuthService.getInstance(); // Usa a instância singleton
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        atualizarIndicadorDeTela(url);
        javafx.application.Platform.runLater(this::atualizarNomeUsuarioAsync);
        inicializarGeneros();
        inicializarGrauEscolarAluno();
        inicializarEstadoEnd();
        inicializarParentesco();
        conectarAcoesFormulario();
        aplicarCpfMask(cpfAluno);
        aplicarCpfMask(cpfRespon);
        aplicarTelefoneMask(contatoRespon);
        aplicarCepMask(endCEP);
    }

    // ----------------------------------------------------
    // MÉTODOS DE AÇÃO
    // ----------------------------------------------------

    /**
     * Atualiza o texto do indicador de tela baseado no arquivo FXML carregado.
     *
     * @param url URL do arquivo FXML que foi carregado
     */
    private void atualizarIndicadorDeTela(URL url) {
        if (indicadorDeTela == null || url == null) {
            return;
        }

        // Extrai o nome do arquivo do URL
        String arquivoFXML = url.getPath();

        // Determina o texto baseado no nome do arquivo
        //FALTA ADICIONAR MAIS INDICES
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
    private void atualizarNomeUsuarioAsync() {
        if (nameUser == null) {
            return;
        }
        Thread t = new Thread(() -> {
            UserInfoDTO userInfo = authService.getUserInfo();
            javafx.application.Platform.runLater(() -> {
                if (userInfo != null && userInfo.name() != null && !userInfo.name().isEmpty()) {
                    nameUser.setText(userInfo.name());
                } else {
                    nameUser.setText("Usuário");
                    System.err.println("Não foi possível carregar o nome do usuário.");
                }
            });
        });
        t.setDaemon(true);
        t.start();
    }

    /**
     * Método público para atualizar o indicador de tela manualmente.
     * Pode ser usado quando há navegação entre telas.
     *
     * @param texto Texto a ser exibido no indicador
     */
    public void setIndicadorDeTela(String texto) {
        if (indicadorDeTela != null) {
            indicadorDeTela.setText(texto);
        }
    }
    //Adiciona opções de genero ao choicebox de genero
    private void inicializarGeneros() {
        if (generoAluno != null && generoAluno.getItems().isEmpty()) {
            generoAluno.getItems().addAll("Masculino", "Feminino", "Outro", "Prefiro não informar");
        }
    }
    //Adiciona opções de genero ao choicebox de genero
    private void inicializarGrauEscolarAluno() {
        if (grauEscAluno != null && grauEscAluno.getItems().isEmpty()) {
            grauEscAluno.getItems().addAll("Educação Infantil", "Estimulação Precoce", "Fundamental I", "Fundamental II", "Ensino Médio", "Outro");
        }
    }
    private void inicializarEstadoEnd() {
        if (endUF != null && endUF.getItems().isEmpty()) {
            endUF.getItems().addAll(
                    "Acre", "Alagoas", "Amapá", "Amazonas", "Bahia", "Ceará", "Distrito Federal", // Incluindo o Distrito Federal
                    "Espírito Santo", "Goiás", "Maranhão", "Mato Grosso", "Mato Grosso do Sul", "Minas Gerais",
                    "Pará", "Paraíba", "Paraná", "Pernambuco", "Piauí", "Rio de Janeiro", "Rio Grande do Norte",
                    "Rio Grande do Sul", "Rondônia", "Roraima", "Santa Catarina", "São Paulo",
                    "Sergipe","Tocantins"
            );
        }
    }

    // Inicialização do Parentesco
    private void inicializarParentesco() {
        if (parentescoRespon != null && parentescoRespon.getItems().isEmpty()) {
            parentescoRespon.getItems().addAll("PAI", "MÃE", "AVÔ", "AVÓ", "OUTRO");
        }
    }

    /**
     * Handler para o botão de início.
     * Navega para a tela inicial do coordenador.
     */
    @FXML
    private void handleInicioButtonAction(javafx.event.ActionEvent event) {
        Janelas.carregarTela(event, "/com/pies/projeto/integrado/piesfront/screens/tela-inicio-coord.fxml", "Início - Coordenador(a)");
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

    @FXML
    private void handleProfessoresButtonAction(javafx.event.ActionEvent event) {
        Janelas.carregarTela(event, 
                "/com/pies/projeto/integrado/piesfront/screens/view-profs-coord.fxml", 
                "Professores");
    }

    @FXML
    private void handleTurmasButtonAction(javafx.event.ActionEvent event) {
        Janelas.carregarTela(event,
                "/com/pies/projeto/integrado/piesfront/screens/view-turmas-coord.fxml",
                "Turmas");
    }

    @FXML
    private void handleAlunosButtonAction(javafx.event.ActionEvent event) {
        Janelas.carregarTela(event,
                "/com/pies/projeto/integrado/piesfront/screens/view-alunos-coord.fxml",
                "Alunos");
    }
    /// VALIDAÇÃO DE FORMULÁRIO
    private boolean validarFormulario(){
        if(ErrorForm != null) {
            ErrorForm.setText("");
        }

        if (nomeAluno == null || nomeAluno.getText() == null || nomeAluno.getText().trim().isEmpty()) {
            mostrarErro("Informe o nome do(a) Aluno(a).");
            return false;
        }

        if (cpfAluno == null || cpfAluno.getText() == null || cpfAluno.getText().trim().isEmpty()) {
            mostrarErro("Informe o CPF do Aluno(a).");
            return false;
        }

        String cpf = cpfAluno.getText().trim();
        Pattern cpfPattern = Pattern.compile("\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}");
        if (!cpfPattern.matcher(cpf).matches()) {
            mostrarErro("CPF deve estar no formato 000.000.000-00.");
            return false;
        }

        if (dtNascAluno == null || dtNascAluno.getValue() == null) {
            mostrarErro("Informe a data de nascimento.");
            return false;
        }
        LocalDate data = dtNascAluno.getValue();
        if (!data.isBefore(LocalDate.now())) {
            mostrarErro("Data de nascimento deve ser no passado.");
            return false;
        }

        if (generoAluno == null || generoAluno.getValue() == null || ((String) generoAluno.getValue()).trim().isEmpty()) {
            mostrarErro("Selecione o gênero.");
            return false;
        }




        return true;
    }
    /// MOSTRAR ERRO
    private void mostrarErro(String mensagem){
        if(ErrorForm != null) {
            ErrorForm.setTextFill(javafx.scene.paint.Color.web("#f24d4d"));
            ErrorForm.setText(mensagem);
        } else {
            System.err.println("Formulário inválido: " + mensagem);
        }
    }
    /// LIMPAR ERRO
    private void limparErro() {
        if (ErrorForm != null) {
            ErrorForm.setText("");
        }
    }
    
    /// MOSTRAR SUCESSO
    private void mostrarSucesso(String mensagem) {
        if (ErrorForm != null) {
            ErrorForm.setTextFill(javafx.scene.paint.Color.GREEN);
            ErrorForm.setText(mensagem);
        }
    }
    
    private void showPopup(String mensagem, boolean sucesso) {
        javafx.scene.Scene scene = (inicioButton != null ? inicioButton.getScene() : cadastroAlunoBt.getScene());
        NotificacaoController.exibirCadastro(scene, sucesso);
    }
    
    /// LIMPAR FORMULÁRIO
    private void limparFormulario() {
        if (nomeAluno != null) nomeAluno.clear();
        if (cpfAluno != null) cpfAluno.clear();
        if (dtNascAluno != null) dtNascAluno.setValue(null);
        if (generoAluno != null) generoAluno.setValue(null);
        if (grauEscAluno != null) grauEscAluno.setValue(null);
        if (escolaAluno != null) escolaAluno.clear();
        if (cidAluno != null) cidAluno.clear();
        if (nisAluno != null) nisAluno.clear();
        if (obsAluno != null) obsAluno.clear();
        if (nomeRespon != null) nomeRespon.clear();
        if (cpfRespon != null) cpfRespon.clear();
        if (parentescoRespon != null) parentescoRespon.setValue(null);
        if (contatoRespon != null) contatoRespon.clear();
        if (endUF != null) endUF.setValue(null);
        if (endCidade != null) endCidade.clear();
        if (endCEP != null) endCEP.clear();
        if (endRua != null) endRua.clear();
        if (endNum != null) endNum.clear();
        if (endBairro != null) endBairro.clear();
        if (endComplemento != null) endComplemento.clear();
    }
    
    /// Conecção de ações dos botões do formulário
    private void conectarAcoesFormulario() {
        if (cadastroAlunoBt != null) {
            cadastroAlunoBt.setOnAction(e -> enviarCadastroAluno());
        }
        if (cancelCadastroBt != null) {
            cancelCadastroBt.setOnAction(e -> handleInicioButtonAction(e));
        }
    }

    // Implementação do envio de cadastro do aluno
    private final java.net.http.HttpClient httpClient = java.net.http.HttpClient.newBuilder().connectTimeout(java.time.Duration.ofSeconds(10)).build();
    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
    private void enviarCadastroAluno() {
        if (!validarFormularioCompleto()) {
            return;
        }
        limparErro();
        // Montar DTOs usando os records corretamente
        EnderecoRequest endereco = new EnderecoRequest(
            endUF.getValue() != null ? extrairSiglaUF(endUF.getValue().toString()) : "",
            endCidade.getText().trim(),
            endCEP.getText().trim(),
            endRua.getText().trim(),
            endNum.getText().trim(),
            endBairro.getText().trim(),
            endComplemento.getText().trim()
        );
        ResponsavelRequest responsavel = new ResponsavelRequest(
            nomeRespon.getText().trim(),
            cpfRespon.getText().trim(),
            contatoRespon.getText().trim(),
            parentescoRespon.getValue(),
            "OUTRO".equals(parentescoRespon.getValue()) ? "Outro" : null,
            endereco
        );
        EducandoRequest educando = new EducandoRequest(
            nomeAluno.getText().trim(),
            cpfAluno.getText().trim(),
            dtNascAluno.getValue().toString(),
            mapGeneroToBackend(generoAluno.getValue().toString()),
            cidAluno.getText().trim(),
            nisAluno.getText().trim(),
            escolaAluno.getText().trim(),
            mapEscolaridadeToBackend(grauEscAluno.getValue().toString()),
            (obsAluno != null && obsAluno.getText() != null) ? obsAluno.getText().trim() : null,
            java.util.List.of(responsavel)
        );
        // Montar JSON e fazer a requisição
        try {
            String json = objectMapper.writeValueAsString(educando);
            System.out.println("JSON enviado: " + json);
            java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                    .uri(java.net.URI.create("http://localhost:8080/api/educandos"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + authService.getCurrentToken())
                    .POST(java.net.http.HttpRequest.BodyPublishers.ofString(json))
                    .timeout(java.time.Duration.ofSeconds(10))
                    .build();
            java.net.http.HttpResponse<String> response = httpClient.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());
            System.out.println("Status Code: " + response.statusCode());
            System.out.println("Response Body: " + response.body());
            if (response.statusCode() == 201) {
                limparFormulario();
                mostrarSucesso("Aluno cadastrado com sucesso!");
                NotificacaoController.agendarCadastro(true);
                Janelas.carregarTela(new javafx.event.ActionEvent(inicioButton, null), 
                        "/com/pies/projeto/integrado/piesfront/screens/tela-inicio-coord.fxml", 
                        "Início - Coordenador(a)");
            } else if (response.statusCode() == 409) {
                mostrarErro("CPF já cadastrado. Verifique os dados.");
                showPopup("CPF já cadastrado. Verifique os dados.", false);
            } else if (response.statusCode() == 400) {
                String mensagemErro = extrairMensagemErro(response.body());
                if (mensagemErro.contains("CPF inválido") || mensagemErro.contains("CPF")) {
                    mostrarErro("CPF inválido. Use CPFs válidos para teste.");
                    showPopup("CPF inválido. Use CPFs válidos para teste.", false);
                } else {
                    mostrarErro("Dados inválidos: " + mensagemErro);
                    showPopup("Dados inválidos: " + mensagemErro, false);
                }
            } else if (response.statusCode() == 403) {
                mostrarErro("Acesso negado. Verifique suas permissões.");
                showPopup("Acesso negado. Verifique suas permissões.", false);
            } else {
                mostrarErro("Erro inesperado. Código: " + response.statusCode());
                showPopup("Erro inesperado. Código: " + response.statusCode(), false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mostrarErro("Erro ao enviar os dados: " + e.getMessage());
            showPopup("Erro ao enviar os dados: " + e.getMessage(), false);
        }
    }

    private String extrairSiglaUF(String estadoCompleto) {
        // Exemplo: "Ceará" -> "CE" (implemente conforme necessário, aqui retorno direto para compatibilidade)
        switch (estadoCompleto.toUpperCase()) {
            case "ACRE": return "AC";
            case "ALAGOAS": return "AL";
            case "AMAPÁ": case "AMAPA": return "AP";
            case "AMAZONAS": return "AM";
            case "BAHIA": return "BA";
            case "CEARÁ": case "CEARA": return "CE";
            case "DISTRITO FEDERAL": return "DF";
            case "ESPÍRITO SANTO": case "ESPIRITO SANTO": return "ES";
            case "GOIÁS": case "GOIAS": return "GO";
            case "MARANHÃO": case "MARANHAO": return "MA";
            case "MATO GROSSO": return "MT";
            case "MATO GROSSO DO SUL": return "MS";
            case "MINAS GERAIS": return "MG";
            case "PARÁ": case "PARA": return "PA";
            case "PARAÍBA": case "PARAIBA": return "PB";
            case "PARANÁ": case "PARANA": return "PR";
            case "PERNAMBUCO": return "PE";
            case "PIAUÍ": case "PIAUI": return "PI";
            case "RIO DE JANEIRO": return "RJ";
            case "RIO GRANDE DO NORTE": return "RN";
            case "RIO GRANDE DO SUL": return "RS";
            case "RONDÔNIA": case "RONDONIA": return "RO";
            case "RORAIMA": return "RR";
            case "SANTA CATARINA": return "SC";
            case "SÃO PAULO": case "SAO PAULO": return "SP";
            case "SERGIPE": return "SE";
            case "TOCANTINS": return "TO";
            default: return "";
        }
    }
    
    private String mapGeneroToBackend(String valor) {
        String v = valor.trim();
        if (v.equalsIgnoreCase("Masculino")) return "MASCULINO";
        if (v.equalsIgnoreCase("Feminino")) return "FEMININO";
        if (v.equalsIgnoreCase("Outro")) return "OUTRO";
        return "PREFIRO_NAO_INFORMAR";
    }

    private String mapEscolaridadeToBackend(String valor) {
        String v = valor.trim();
        if (v.equalsIgnoreCase("Educação Infantil") || v.equalsIgnoreCase("Educacao Infantil")) return "EDUCACAO_INFANTIL";
        if (v.equalsIgnoreCase("Estimulação Precoce") || v.equalsIgnoreCase("Estimulacao Precoce")) return "ESTIMULACAO_PRECOCE";
        if (v.equalsIgnoreCase("Fundamental I")) return "FUNDAMENTAL_I";
        if (v.equalsIgnoreCase("Fundamental II")) return "FUNDAMENTAL_II";
        if (v.equalsIgnoreCase("Ensino Médio") || v.equalsIgnoreCase("Ensino Medio")) return "MEDIO";
        if (v.equalsIgnoreCase("Outro")) return "OUTRO";
        return "PREFIRO_NAO_INFORMAR";
    }
    // VALIDAÇÃO DE FORMULÁRIO COMPLETO
    private boolean validarFormularioCompleto() {
        
        if (nomeAluno == null || nomeAluno.getText() == null || nomeAluno.getText().trim().isEmpty()) {
            mostrarErro("Informe o nome do(a) aluno(a).");
            return false;
        }
        if (cpfAluno == null || cpfAluno.getText() == null || cpfAluno.getText().trim().isEmpty()) {
            mostrarErro("Informe o CPF do(a) aluno(a).");
            return false;
        }
        String cpf = cpfAluno.getText().trim();
        java.util.regex.Pattern cpfPattern = java.util.regex.Pattern.compile("\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}");
        if (!cpfPattern.matcher(cpf).matches()) {
            mostrarErro("CPF do aluno deve estar no formato 000.000.000-00.");
            return false;
        }
        if (dtNascAluno == null || dtNascAluno.getValue() == null) {
            mostrarErro("Informe a data de nascimento do(a) aluno(a).");
            return false;
        }
        if (!dtNascAluno.getValue().isBefore(java.time.LocalDate.now())) {
            mostrarErro("Data de nascimento do(a) aluno(a) deve ser no passado.");
            return false;
        }
        if (generoAluno == null || generoAluno.getValue() == null || ((String) generoAluno.getValue()).trim().isEmpty()) {
            mostrarErro("Selecione o gênero do(a) aluno(a).");
            return false;
        }
        if (cidAluno == null || cidAluno.getText() == null || cidAluno.getText().trim().isEmpty()) {
            mostrarErro("Informe o CID.");
            return false;
        }
        if (nisAluno == null || nisAluno.getText() == null || nisAluno.getText().trim().isEmpty()) {
            mostrarErro("Informe o NIS.");
            return false;
        }
        if (escolaAluno == null || escolaAluno.getText() == null || escolaAluno.getText().trim().isEmpty()) {
            mostrarErro("Informe a escola do(a) aluno(a).");
            return false;
        }
        if (grauEscAluno == null || grauEscAluno.getValue() == null || ((String) grauEscAluno.getValue()).trim().isEmpty()) {
            mostrarErro("Selecione o grau escolar.");
            return false;
        }
        // Responsável
        if (nomeRespon == null || nomeRespon.getText() == null || nomeRespon.getText().trim().isEmpty()) {
            mostrarErro("Informe o nome do responsável.");
            return false;
        }
        if (cpfRespon == null || cpfRespon.getText() == null || cpfRespon.getText().trim().isEmpty()) {
            mostrarErro("Informe o CPF do responsável.");
            return false;
        }
        String cpfR = cpfRespon.getText().trim();
        if (!cpfPattern.matcher(cpfR).matches()) {
            mostrarErro("CPF do responsável deve estar no formato 000.000.000-00.");
            return false;
        }
        if (contatoRespon == null || contatoRespon.getText() == null || contatoRespon.getText().trim().isEmpty()) {
            mostrarErro("Informe o telefone do responsável.");
            return false;
        }
        if (parentescoRespon == null || parentescoRespon.getValue() == null || ((String) parentescoRespon.getValue()).trim().isEmpty()) {
            mostrarErro("Informe o parentesco do responsável.");
            return false;
        }
        // Endereço do responsável
        if (endCEP == null || endCEP.getText() == null || endCEP.getText().trim().isEmpty()) {
            mostrarErro("Informe o CEP do endereço.");
            return false;
        }
        if (endUF == null || endUF.getValue() == null || ((String) endUF.getValue()).trim().isEmpty()) {
            mostrarErro("Selecione o estado (UF).");
            return false;
        }
        if (endCidade == null || endCidade.getText() == null || endCidade.getText().trim().isEmpty()) {
            mostrarErro("Informe a cidade do endereço.");
            return false;
        }
        if (endBairro == null || endBairro.getText() == null || endBairro.getText().trim().isEmpty()) {
            mostrarErro("Informe o bairro do endereço.");
            return false;
        }
        if (endRua == null || endRua.getText() == null || endRua.getText().trim().isEmpty()) {
            mostrarErro("Informe a rua do endereço.");
            return false;
        }
        if (endNum == null || endNum.getText() == null || endNum.getText().trim().isEmpty()) {
            mostrarErro("Informe o número do endereço.");
            return false;
        }
        // Complemento pode ser opcional
        return true;
    }

    private String extrairMensagemErro(String responseBody) {
        try {
            if (responseBody == null || responseBody.trim().isEmpty()) {
                return "Requisição inválida.";
            }
            return responseBody;
        } catch (Exception e) {
            return "Requisição inválida.";
        }
    }
    
    
    private void aplicarCpfMask(TextField campo) {
        if (campo == null) return;
        campo.textProperty().addListener((obs, old, neu) -> {
            String digits = neu.replaceAll("\\D", "");
            if (digits.length() > 11) digits = digits.substring(0, 11);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < digits.length(); i++) {
                if (i == 3 || i == 6) sb.append('.');
                if (i == 9) sb.append('-');
                sb.append(digits.charAt(i));
            }
            String formatted = sb.toString();
            if (!formatted.equals(neu)) {
                campo.setText(formatted);
                campo.positionCaret(formatted.length());
            }
        });
    }

    private void aplicarTelefoneMask(TextField campo) {
        if (campo == null) return;
        campo.textProperty().addListener((obs, old, neu) -> {
            String digits = neu.replaceAll("\\D", "");
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
            String formatted = sb.toString();
            if (len > 0 && formatted.charAt(0) != '(') formatted = "(" + formatted;
            if (len > 0 && len < 2 && !formatted.endsWith("(")) formatted = "(" + digits;
            if (!formatted.equals(neu)) {
                campo.setText(formatted);
                campo.positionCaret(formatted.length());
            }
        });
    }

    private void aplicarCepMask(TextField campo) {
        if (campo == null) return;
        campo.textProperty().addListener((obs, old, neu) -> {
            String digits = neu.replaceAll("\\D", "");
            if (digits.length() > 8) digits = digits.substring(0, 8);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < digits.length(); i++) {
                if (i == 5) sb.append('-');
                sb.append(digits.charAt(i));
            }
            String formatted = sb.toString();
            if (!formatted.equals(neu)) {
                campo.setText(formatted);
                campo.positionCaret(formatted.length());
            }
        });
    }
}
