module com.pies.projeto.integrado.piesfront {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    opens com.pies.projeto.integrado.piesfront to javafx.fxml;
    exports com.pies.projeto.integrado.piesfront;
}