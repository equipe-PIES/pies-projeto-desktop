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
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HomeCoordController implements Initializable {
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
    private Button addAluno;
    @FXML
    private Button addProf;
    @FXML
    private Button addTurma;
    private final AuthService authService;

    public HomeCoordController() {
        this.authService = AuthService.getInstance(); // Usa a instância singleton
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        atualizarIndicadorDeTela(url);
        javafx.application.Platform.runLater(this::atualizarNomeUsuarioAsync);
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
    
    /**
     * Handler para o botão de início.
     * Navega para a tela inicial do coordenador.
     */
    @FXML
    private void handleInicioButtonAction(javafx.event.ActionEvent event) {
        Janelas.carregarTela(event, "/com/pies/projeto/integrado/piesfront/screens/tela-inicio-coord.fxml", "Início - Coordenador(a)");
    }
    
    /**
     * Handler para o botão de adicionar aluno.
     * Navega para a tela de cadastro de aluno.
     */
    @FXML
    private void handleAddAlunoAction(javafx.event.ActionEvent event) {
        Janelas.carregarTela(event, "/com/pies/projeto/integrado/piesfront/screens/cadastro-de-aluno.fxml", "Cadastro de Aluno(a)");
    }
    /**
     * Handler para o botão de adicionar professor.
     * Navega para a tela de cadastro de professor.
     */
    @FXML
    private void handleAddProfAction(javafx.event.ActionEvent event) {
        Janelas.carregarTela(event, "/com/pies/projeto/integrado/piesfront/screens/cadastro-de-prof.fxml", "Cadastro de Professor(a)");
    }
    /**
     * Handler para o botão de adicionar turma.
     * Navega para a tela de cadastro de turma.
     */
    @FXML
    private void handleAddTurmaAction(javafx.event.ActionEvent event) {
        Janelas.carregarTela(event, "/com/pies/projeto/integrado/piesfront/screens/cadastro-de-turma.fxml", "Cadastro de Turma");
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
