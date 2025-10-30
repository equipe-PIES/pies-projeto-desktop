module com.pies.projeto.integrado.piesfront {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics; // Stage, Scene, Color, etc.
    requires javafx.base;     // ObservableList, etc.
    requires javafx.web;      // WebView/WebEngine
    requires javafx.media;    // Se for usar mídia

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    opens com.pies.projeto.integrado.piesfront to javafx.fxml, javafx.graphics;
    exports com.pies.projeto.integrado.piesfront;
}