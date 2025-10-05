package com.planoaee.view.controllers;

import com.planoaee.model.Usuario;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/**
 * Controller para a tela de dashboard principal
 */
public class DashboardController implements Initializable {
    
    private static final Logger logger = Logger.getLogger(DashboardController.class.getName());
    
    @FXML
    private VBox mainContainer;
    
    @FXML
    private MenuBar menuBar;
    
    @FXML
    private ToolBar toolBar;
    
    @FXML
    private Label lblWelcome;
    
    @FXML
    private Label lblUserInfo;
    
    private Usuario usuarioLogado;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logger.info("DashboardController inicializado");
        // Configurações iniciais serão feitas no método setUsuarioLogado
    }
    
    /**
     * Define o usuário logado e atualiza a interface
     * @param usuario usuário logado
     */
    public void setUsuarioLogado(Usuario usuario) {
        this.usuarioLogado = usuario;
        
        if (usuario != null) {
            lblWelcome.setText("Bem-vindo, " + usuario.getNome() + "!");
            lblUserInfo.setText("Logado como: " + usuario.getTipo().getDescricao());
            
            logger.info("Dashboard configurado para usuário: " + usuario.getNome());
        }
    }
    
    /**
     * Manipula o evento de logout
     */
    @FXML
    private void handleLogout() {
        // TODO: Implementar logout
        logger.info("Logout solicitado");
    }
    
    /**
     * Manipula o evento de sair do sistema
     */
    @FXML
    private void handleExit() {
        // TODO: Implementar saída do sistema
        logger.info("Saída do sistema solicitada");
    }
}

