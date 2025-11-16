package com.pies.projeto.integrado.piesfront.controllers;

import com.pies.projeto.integrado.piesfront.dto.TurmaDTO;
import com.pies.projeto.integrado.piesfront.dto.UserInfoDTO;
import com.pies.projeto.integrado.piesfront.services.AuthService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class HomeProfController implements Initializable {
    // ----------------------------------------------------
    // DECLARAÇÃO DOS fx:id
    // ----------------------------------------------------
    @FXML
    private Label indicadorDeTela;
    @FXML
    private Label nameUser;
    @FXML
    private Label cargoUser;
    @FXML
    private Button sairButton;
    @FXML
    private Button turmasButton;
    @FXML
    private FlowPane containerCards;

    private final AuthService authService;

    public HomeProfController() {
        this.authService = AuthService.getInstance(); // Usa a instância singleton
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Atualiza o texto do indicador baseado no arquivo FXML carregado
        atualizarIndicadorDeTela(url);

        // Busca e atualiza o nome do usuário
        atualizarNomeUsuario();
        
        // Carrega as turmas do professor
        carregarTurmas();
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

        if (arquivoFXML.contains("tela-inicio-professor.fxml")) {
            textoIndicador = "Turmas";
        } else if (arquivoFXML.contains("tela-inicio-prof.fxml")) {
            textoIndicador = "Turmas";
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
        // Busca as informações do usuário do backend
        UserInfoDTO userInfo = authService.getUserInfo();

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
            // Se não conseguir buscar, mantém o texto padrão ou mostra uma mensagem
            if (nameUser != null) {
                nameUser.setText("Usuário");
            }
            System.err.println("Não foi possível carregar o nome do usuário.");
        }
    }
    
    /**
     * Carrega as turmas do professor logado e exibe como cards
     */
    private void carregarTurmas() {
        if (containerCards == null) {
            System.err.println("FlowPane containerCards não foi encontrado!");
            return;
        }
        
        // Limpa os cards existentes
        containerCards.getChildren().clear();
        
        // Busca informações do usuário logado para filtrar as turmas
        UserInfoDTO userInfo = authService.getUserInfo();
        if (userInfo == null || userInfo.id() == null) {
            System.err.println("Não foi possível obter informações do usuário logado.");
            return;
        }
        
        // Busca todas as turmas do backend
        List<TurmaDTO> todasTurmas = authService.getTurmas();
        
        // Filtra apenas as turmas do professor logado
        List<TurmaDTO> turmasDoProfessor = todasTurmas.stream()
                .filter(turma -> userInfo.id().equals(turma.professorId()))
                .collect(Collectors.toList());
        
        // Cria um card para cada turma
        for (TurmaDTO turma : turmasDoProfessor) {
            try {
                // Carrega o FXML do card
                FXMLLoader loader = new FXMLLoader(getClass().getResource(
                        "/com/pies/projeto/integrado/piesfront/screens/card-turma.fxml"));
                VBox cardNode = loader.load();
                
                // Obtém o controller do card e define os dados
                CardTurmaController cardController = loader.getController();
                cardController.setTurma(turma);
                
                // Adiciona o card ao FlowPane
                containerCards.getChildren().add(cardNode);
                
            } catch (IOException e) {
                System.err.println("Erro ao carregar card de turma: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        // Se não houver turmas, exibe mensagem
        if (turmasDoProfessor.isEmpty()) {
            Label semTurmasLabel = new Label("Nenhuma turma designada a este professor.");
            semTurmasLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #666;");
            containerCards.getChildren().add(semTurmasLabel);
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
     * Handler para o botão de turmas.
     * Recarrega a tela de turmas (já é a tela atual, então apenas recarrega os cards)
     */
    @FXML
    private void handleTurmasButtonAction() {
        // Recarrega as turmas
        carregarTurmas();
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
