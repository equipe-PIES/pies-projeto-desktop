package com.utils;

import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Janelas {

    public static void carregarTela(Event event, String caminhoFXML, String tituloJanela) {
        try {
            // 1. Carrega o novo visual (FXML)
            FXMLLoader loader = new FXMLLoader(Janelas.class.getResource(caminhoFXML));
            Parent newRoot = loader.load();

            // 2. Pega o Stage atual
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // 3. Troca o conteúdo da cena atual
            Scene currentScene = stage.getScene();
            currentScene.setRoot(newRoot);
            
            // 4. Define o Título
            if (tituloJanela != null && !tituloJanela.isEmpty()) {
                stage.setTitle(tituloJanela);
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erro ao navegar para: " + caminhoFXML);
        }
    }
}