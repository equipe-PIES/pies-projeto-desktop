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
            FXMLLoader loader = new FXMLLoader(Janelas.class.getResource(caminhoFXML));
            Parent newRoot = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene currentScene = stage.getScene();
            currentScene.setRoot(newRoot);
            if (tituloJanela != null && !tituloJanela.isEmpty()) {
                stage.setTitle(tituloJanela);
            }
            stage.setMaximized(true);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erro ao navegar para: " + caminhoFXML);
        }
    }

    public static void carregarTela(Event event, String caminhoFXML, String tituloJanela,
                                    java.util.function.Consumer<Object> controllerConfigurer) {
        try {
            FXMLLoader loader = new FXMLLoader(Janelas.class.getResource(caminhoFXML));
            Parent newRoot = loader.load();
            Object controller = loader.getController();
            if (controllerConfigurer != null) {
                controllerConfigurer.accept(controller);
            }
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene currentScene = stage.getScene();
            currentScene.setRoot(newRoot);
            if (tituloJanela != null && !tituloJanela.isEmpty()) {
                stage.setTitle(tituloJanela);
            }
            stage.setMaximized(true);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erro ao navegar para: " + caminhoFXML);
        }
    }
}
