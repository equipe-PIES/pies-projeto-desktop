package com.pies.projeto.integrado.piesfront.controllers;

import com.pies.projeto.integrado.piesfront.dto.UserInfoDTO;
import com.pies.projeto.integrado.piesfront.services.AuthService;
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

    private final AuthService authService;

    public CadastroTurmaController() {
        this.authService = AuthService.getInstance(); // Usa a instância singleton
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Atualiza o texto do indicador baseado no arquivo FXML carregado
        atualizarIndicadorDeTela(url);

        // Busca e atualiza o nome do usuário
        atualizarNomeUsuario();
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
    private void atualizarNomeUsuario() {
        if (nameUser == null) {
            return;
        }

        // Busca as informações do usuário do backend
        UserInfoDTO userInfo = authService.getUserInfo();

        if (userInfo != null && userInfo.name() != null && !userInfo.name().isEmpty()) {
            nameUser.setText(userInfo.name());
        } else {
            // Se não conseguir buscar, mantém o texto padrão ou mostra uma mensagem
            nameUser.setText("Usuário");
            System.err.println("Não foi possível carregar o nome do usuário.");
        }
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
    private void handleInicioButtonAction() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/pies/projeto/integrado/piesfront/screens/tela-inicio-coord.fxml"));

            Stage currentStage = (Stage) inicioButton.getScene().getWindow();
            currentStage.setScene(new Scene(root));
            currentStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erro ao carregar a tela de início: " + e.getMessage());
        }**
    }

    /**
     * Handler para o botão de sair.
     * Faz logout do usuário e retorna para a tela de login.
     */
    @FXML
    private void handleSairButtonAction() {
        // Faz logout - limpa o token de autenticação
        authService.logout();

        // Carrega a tela de login
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
