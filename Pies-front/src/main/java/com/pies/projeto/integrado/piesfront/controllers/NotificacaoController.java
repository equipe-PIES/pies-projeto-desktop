package com.pies.projeto.integrado.piesfront.controllers;

import javafx.animation.PauseTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class NotificacaoController {
    private enum Tipo { DOCUMENTO, CADASTRO }
    private static String pendingMensagem;
    private static Boolean pendingSucesso;
    private static Tipo pendingTipo;

    private static javafx.scene.control.Label encontrarMensagemLabel(Parent conteudo) {
        javafx.scene.control.Label resultado = null;
        if (conteudo instanceof javafx.scene.control.TitledPane tp) {
            javafx.scene.Node content = tp.getContent();
            if (content instanceof javafx.scene.Parent p) {
                java.util.ArrayDeque<javafx.scene.Node> stack = new java.util.ArrayDeque<>();
                stack.push(p);
                while (!stack.isEmpty() && resultado == null) {
                    javafx.scene.Node n = stack.pop();
                    if (n instanceof javafx.scene.control.Label l && "mensagemLabel".equals(n.getId())) {
                        resultado = l;
                        break;
                    }
                    if (n instanceof javafx.scene.Parent pn) {
                        for (javafx.scene.Node c : pn.getChildrenUnmodifiable()) {
                            stack.push(c);
                        }
                    }
                }
            }
        }
        return resultado;
    }

    private static String mensagemPadrao(Tipo tipo, boolean sucesso) {
        if (tipo == Tipo.CADASTRO) {
            return sucesso ? "Cadastro efetuado com sucesso!" : "Erro ao realizar o cadastro!";
        }
        return sucesso ? "Documento registrado com sucesso!" : "Erro ao registrar documento!";
    }

    public static void exibirTexto(Pane container, String mensagem, boolean sucesso) {
        if (container == null || container.getScene() == null) return;
        try {
            Parent conteudo = FXMLLoader.load(NotificacaoController.class.getResource(
                    "/com/pies/projeto/integrado/piesfront/screens/notificacao-feedback.fxml"));
            Label msgLabel = encontrarMensagemLabel(conteudo);
            if (msgLabel != null) {
                msgLabel.setText(mensagem);
            }

            javafx.scene.layout.Pane overlay = new javafx.scene.layout.Pane(conteudo);
            overlay.setMouseTransparent(true);
            overlay.setManaged(false);
            overlay.prefWidthProperty().bind(container.widthProperty());
            overlay.prefHeightProperty().bind(container.heightProperty());
            overlay.toFront();
            overlay.toFront();

            if (container instanceof javafx.scene.layout.BorderPane bp) {
                bp.getChildren().add(overlay);
            } else {
                container.getChildren().add(overlay);
            }

            Runnable center = () -> {
                if (conteudo instanceof javafx.scene.layout.Region r) {
                    r.applyCss();
                    r.autosize();
                    double w = r.prefWidth(-1);
                    double h = r.prefHeight(-1);
                    double cw = container.getWidth();
                    double ch = container.getHeight();
                    r.setLayoutX((cw - w) / 2);
                    r.setLayoutY((ch - h) / 2);
                }
            };
            center.run();
            container.widthProperty().addListener((obs, o, n) -> center.run());
            container.heightProperty().addListener((obs, o, n) -> center.run());

            PauseTransition pt = new PauseTransition(Duration.seconds(5));
            pt.setOnFinished(e -> container.getChildren().remove(overlay));
            pt.play();
        } catch (Exception ignored) {
        }
    }

    public static void exibirTexto(Scene scene, String mensagem, boolean sucesso) {
        if (scene == null || scene.getRoot() == null) return;
        Parent root = scene.getRoot();
        if (root instanceof Pane p) {
            exibirTexto(p, mensagem, sucesso);
        }
    }

    private static void exibirComTipo(Pane container, boolean sucesso, Tipo tipo) {
        if (container == null || container.getScene() == null) return;
        try {
            Parent conteudo = FXMLLoader.load(NotificacaoController.class.getResource(
                    "/com/pies/projeto/integrado/piesfront/screens/notificacao-feedback.fxml"));
            Label msgLabel = encontrarMensagemLabel(conteudo);
            if (msgLabel != null) {
                msgLabel.setText(mensagemPadrao(tipo, sucesso));
            }

            javafx.scene.layout.Pane overlay = new javafx.scene.layout.Pane(conteudo);
            overlay.setMouseTransparent(true);
            overlay.setManaged(false);
            overlay.prefWidthProperty().bind(container.widthProperty());
            overlay.prefHeightProperty().bind(container.heightProperty());
            overlay.toFront();

            if (container instanceof javafx.scene.layout.BorderPane bp) {
                bp.getChildren().add(overlay);
            } else {
                container.getChildren().add(overlay);
            }

            Runnable center = () -> {
                if (conteudo instanceof javafx.scene.layout.Region r) {
                    r.applyCss();
                    r.autosize();
                    double w = r.prefWidth(-1);
                    double h = r.prefHeight(-1);
                    double cw = container.getWidth();
                    double ch = container.getHeight();
                    r.setLayoutX((cw - w) / 2);
                    r.setLayoutY((ch - h) / 2);
                }
            };
            center.run();
            container.widthProperty().addListener((obs, o, n) -> center.run());
            container.heightProperty().addListener((obs, o, n) -> center.run());

            PauseTransition pt = new PauseTransition(Duration.seconds(5));
            pt.setOnFinished(e -> container.getChildren().remove(overlay));
            pt.play();
        } catch (Exception ignored) {
        }
    }

    public static void exibir(Pane container, String mensagem, boolean sucesso) {
        exibirComTipo(container, sucesso, Tipo.DOCUMENTO);
    }

    public static void exibir(Scene scene, String mensagem, boolean sucesso) {
        if (scene == null || scene.getRoot() == null) return;
        Parent root = scene.getRoot();
        if (root instanceof Pane p) {
            exibirComTipo(p, sucesso, Tipo.DOCUMENTO);
        }
    }

    public static void exibirCadastro(Pane container, boolean sucesso) {
        exibirComTipo(container, sucesso, Tipo.CADASTRO);
    }

    public static void exibirCadastro(Scene scene, boolean sucesso) {
        if (scene == null || scene.getRoot() == null) return;
        Parent root = scene.getRoot();
        if (root instanceof Pane p) {
            exibirComTipo(p, sucesso, Tipo.CADASTRO);
        }
    }

    public static void agendar(String mensagem, boolean sucesso) {
        pendingMensagem = mensagem;
        pendingSucesso = sucesso;
        pendingTipo = Tipo.DOCUMENTO;
    }

    public static void agendarDocumento(boolean sucesso) {
        pendingMensagem = null;
        pendingSucesso = sucesso;
        pendingTipo = Tipo.DOCUMENTO;
    }

    public static void agendarCadastro(boolean sucesso) {
        pendingMensagem = null;
        pendingSucesso = sucesso;
        pendingTipo = Tipo.CADASTRO;
    }

    public static void exibirSePendente(Scene scene) {
        if (pendingSucesso != null) {
            if (scene != null && scene.getRoot() instanceof Pane p) {
                if (pendingMensagem != null) {
                    exibirTexto(p, pendingMensagem, pendingSucesso);
                } else {
                    exibirComTipo(p, pendingSucesso, pendingTipo != null ? pendingTipo : Tipo.DOCUMENTO);
                }
            }
            pendingMensagem = null;
            pendingSucesso = null;
            pendingTipo = null;
        }
    }
}
