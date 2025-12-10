package com.pies.projeto.integrado.piesfront.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class ExcluirConfirmController {
    @FXML private Label fraseAviso;
    @FXML private Button cancelarButton;
    @FXML private Button excluirButton;

    private Runnable onCancel;
    private Runnable onConfirm;

    public void setOnCancel(Runnable r) {
        this.onCancel = r;
        if (cancelarButton != null) cancelarButton.setOnAction(e -> { if (onCancel != null) onCancel.run(); });
    }

    public void setOnConfirm(Runnable r) {
        this.onConfirm = r;
        if (excluirButton != null) excluirButton.setOnAction(e -> { if (onConfirm != null) onConfirm.run(); });
    }

    @FXML
    private void initialize() {
        if (cancelarButton != null) cancelarButton.setOnAction(e -> { if (onCancel != null) onCancel.run(); });
        if (excluirButton != null) excluirButton.setOnAction(e -> { if (onConfirm != null) onConfirm.run(); });
    }
}
