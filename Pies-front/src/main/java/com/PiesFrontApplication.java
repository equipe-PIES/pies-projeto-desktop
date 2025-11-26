package com;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.text.Font;

import java.io.IOException;

public class PiesFrontApplication extends Application {

    Stage janela;

    @Override
    public void start(Stage stage) throws IOException {
        stage.setMinWidth(1000);
        stage.setMinHeight(600);
        FXMLLoader fxmlLoader = new FXMLLoader(PiesFrontApplication.class.getResource("/com/pies/projeto/integrado/piesfront/screens/cadastro-de-aluno.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 800);
        stage.setTitle("Amparo Edu");
        stage.setScene(scene);
        stage.show();
    }
}