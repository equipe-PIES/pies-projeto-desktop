module com.pies.projeto.integrado.piesfront {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires com.fasterxml.jackson.databind;

    opens com.pies.projeto.integrado.piesfront.controllers to javafx.fxml;
    opens com.pies.projeto.integrado.piesfront.dto;
    opens com to javafx.fxml;

    exports com;
    exports com.pies.projeto.integrado.piesfront.controllers;
    exports com.pies.projeto.integrado.piesfront.services;
    exports com.pies.projeto.integrado.piesfront.dto;
}