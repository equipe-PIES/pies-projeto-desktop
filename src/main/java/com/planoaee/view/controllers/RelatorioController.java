package com.planoaee.view.controllers;

import com.planoaee.model.Aluno;
import com.planoaee.model.Relatorio;
import com.planoaee.model.Usuario;
import com.planoaee.service.AlunoService;
import com.planoaee.service.RelatorioService;
import com.planoaee.service.UsuarioService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/**
 * Controller para gerenciamento de relatórios
 */
public class RelatorioController implements Initializable {
    
    private static final Logger logger = Logger.getLogger(RelatorioController.class.getName());
    
    @FXML
    private TableView<Relatorio> tableViewRelatorios;
    
    @FXML
    private TableColumn<Relatorio, String> colData;
    
    @FXML
    private TableColumn<Relatorio, String> colAluno;
    
    @FXML
    private TableColumn<Relatorio, String> colTipo;
    
    @FXML
    private TableColumn<Relatorio, String> colProfissional;
    
    @FXML
    private TableColumn<Relatorio, String> colPeriodo;
    
    @FXML
    private ComboBox<Relatorio.TipoRelatorio> comboBoxTipo;
    
    @FXML
    private ComboBox<Aluno> comboBoxAluno;
    
    @FXML
    private ComboBox<Usuario> comboBoxProfissional;
    
    @FXML
    private DatePicker datePickerData;
    
    @FXML
    private TextField txtPeriodo;
    
    @FXML
    private TextArea txtConteudo;
    
    @FXML
    private Button btnNovo;
    
    @FXML
    private Button btnSalvar;
    
    @FXML
    private Button btnEditar;
    
    @FXML
    private Button btnExcluir;
    
    @FXML
    private Button btnBuscar;
    
    @FXML
    private Button btnLimpar;
    
    @FXML
    private Button btnAplicarTemplate;
    
    @FXML
    private Button btnRelatorioSemestral;
    
    private RelatorioService relatorioService;
    private AlunoService alunoService;
    private UsuarioService usuarioService;
    private ObservableList<Relatorio> relatoriosList;
    private ObservableList<Aluno> alunosList;
    private ObservableList<Usuario> profissionaisList;
    private Relatorio relatorioSelecionado;
    private Usuario usuarioLogado;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.relatorioService = new RelatorioService();
        this.alunoService = new AlunoService();
        this.usuarioService = new UsuarioService();
        this.relatoriosList = FXCollections.observableArrayList();
        this.alunosList = FXCollections.observableArrayList();
        this.profissionaisList = FXCollections.observableArrayList();
        
        configurarTabela();
        configurarComboBoxes();
        carregarDados();
        
        // Desabilita botões inicialmente
        btnSalvar.setDisable(true);
        btnEditar.setDisable(true);
        btnExcluir.setDisable(true);
        
        logger.info("RelatorioController inicializado");
    }
    
    /**
     * Define o usuário logado
     * @param usuario usuário logado
     */
    public void setUsuarioLogado(Usuario usuario) {
        this.usuarioLogado = usuario;
        if (usuario != null) {
            comboBoxProfissional.setValue(usuario);
            comboBoxProfissional.setDisable(true);
        }
    }
    
    /**
     * Configura a tabela de relatórios
     */
    private void configurarTabela() {
        colData.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getData().toString())
        );
        colAluno.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getAluno().getNome())
        );
        colTipo.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTipo().getDescricao())
        );
        colProfissional.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getProfissional().getNome())
        );
        colPeriodo.setCellValueFactory(new PropertyValueFactory<>("periodo"));
        
        tableViewRelatorios.setItems(relatoriosList);
        
        // Seleção na tabela
        tableViewRelatorios.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                relatorioSelecionado = newValue;
                if (newValue != null) {
                    preencherFormulario(newValue);
                    btnEditar.setDisable(false);
                    btnExcluir.setDisable(false);
                } else {
                    limparFormulario();
                    btnEditar.setDisable(true);
                    btnExcluir.setDisable(true);
                }
            }
        );
    }
    
    /**
     * Configura os combo boxes
     */
    private void configurarComboBoxes() {
        // Combo box de tipo
        ObservableList<Relatorio.TipoRelatorio> tipos = FXCollections.observableArrayList(
            Relatorio.TipoRelatorio.values()
        );
        comboBoxTipo.setItems(tipos);
        
        // Combo box de alunos
        comboBoxAluno.setItems(alunosList);
        
        // Combo box de profissionais
        comboBoxProfissional.setItems(profissionaisList);
    }
    
    /**
     * Carrega dados iniciais
     */
    private void carregarDados() {
        carregarAlunos();
        carregarProfissionais();
        carregarRelatorios();
        
        // Define data padrão
        datePickerData.setValue(LocalDate.now());
    }
    
    /**
     * Carrega lista de alunos
     */
    private void carregarAlunos() {
        Task<List<Aluno>> task = new Task<List<Aluno>>() {
            @Override
            protected List<Aluno> call() throws Exception {
                return alunoService.listarTodos();
            }
            
            @Override
            protected void succeeded() {
                alunosList.clear();
                alunosList.addAll(getValue());
                logger.info("Alunos carregados: " + alunosList.size());
            }
            
            @Override
            protected void failed() {
                logger.log(java.util.logging.Level.SEVERE, "Erro ao carregar alunos", getException());
            }
        };
        
        new Thread(task).start();
    }
    
    /**
     * Carrega lista de profissionais
     */
    private void carregarProfissionais() {
        Task<List<Usuario>> task = new Task<List<Usuario>>() {
            @Override
            protected List<Usuario> call() throws Exception {
                return usuarioService.listarTodos();
            }
            
            @Override
            protected void succeeded() {
                profissionaisList.clear();
                profissionaisList.addAll(getValue());
                logger.info("Profissionais carregados: " + profissionaisList.size());
            }
            
            @Override
            protected void failed() {
                logger.log(java.util.logging.Level.SEVERE, "Erro ao carregar profissionais", getException());
            }
        };
        
        new Thread(task).start();
    }
    
    /**
     * Carrega lista de relatórios
     */
    private void carregarRelatorios() {
        Task<List<Relatorio>> task = new Task<List<Relatorio>>() {
            @Override
            protected List<Relatorio> call() throws Exception {
                return relatorioService.listarTodos();
            }
            
            @Override
            protected void succeeded() {
                relatoriosList.clear();
                relatoriosList.addAll(getValue());
                logger.info("Relatórios carregados: " + relatoriosList.size());
            }
            
            @Override
            protected void failed() {
                logger.log(java.util.logging.Level.SEVERE, "Erro ao carregar relatórios", getException());
            }
        };
        
        new Thread(task).start();
    }
    
    /**
     * Manipula evento de novo relatório
     */
    @FXML
    private void handleNovo() {
        limparFormulario();
        btnSalvar.setDisable(false);
        btnEditar.setDisable(true);
        btnExcluir.setDisable(true);
        tableViewRelatorios.getSelectionModel().clearSelection();
        
        // Define valores padrão
        datePickerData.setValue(LocalDate.now());
        if (usuarioLogado != null) {
            comboBoxProfissional.setValue(usuarioLogado);
        }
    }
    
    /**
     * Manipula evento de salvar relatório
     */
    @FXML
    private void handleSalvar() {
        if (!validarFormulario()) {
            return;
        }
        
        Relatorio relatorio = criarRelatorioDoFormulario();
        
        Task<Relatorio> task = new Task<Relatorio>() {
            @Override
            protected Relatorio call() throws Exception {
                return relatorioService.criarRelatorio(relatorio);
            }
            
            @Override
            protected void succeeded() {
                Relatorio relatorioSalvo = getValue();
                relatoriosList.add(relatorioSalvo);
                mostrarSucesso("Sucesso", "Relatório criado com sucesso!");
                limparFormulario();
                btnSalvar.setDisable(true);
                logger.info("Relatório criado: " + relatorioSalvo.getId());
            }
            
            @Override
            protected void failed() {
                mostrarErro("Erro", "Não foi possível criar o relatório");
                logger.log(java.util.logging.Level.SEVERE, "Erro ao criar relatório", getException());
            }
        };
        
        new Thread(task).start();
    }
    
    /**
     * Manipula evento de editar relatório
     */
    @FXML
    private void handleEditar() {
        if (relatorioSelecionado == null) {
            return;
        }
        
        if (!validarFormulario()) {
            return;
        }
        
        // Atualiza dados do relatório selecionado
        relatorioSelecionado.setData(datePickerData.getValue());
        relatorioSelecionado.setTipo(comboBoxTipo.getValue());
        relatorioSelecionado.setAluno(comboBoxAluno.getValue());
        relatorioSelecionado.setProfissional(comboBoxProfissional.getValue());
        relatorioSelecionado.setPeriodo(txtPeriodo.getText().trim());
        relatorioSelecionado.setConteudo(txtConteudo.getText().trim());
        
        Task<Boolean> task = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                return relatorioService.atualizarRelatorio(relatorioSelecionado);
            }
            
            @Override
            protected void succeeded() {
                if (getValue()) {
                    // Atualiza a lista
                    int index = relatoriosList.indexOf(relatorioSelecionado);
                    if (index >= 0) {
                        relatoriosList.set(index, relatorioSelecionado);
                    }
                    mostrarSucesso("Sucesso", "Relatório atualizado com sucesso!");
                    logger.info("Relatório atualizado: " + relatorioSelecionado.getId());
                } else {
                    mostrarErro("Erro", "Não foi possível atualizar o relatório");
                }
            }
            
            @Override
            protected void failed() {
                mostrarErro("Erro", "Não foi possível atualizar o relatório");
                logger.log(java.util.logging.Level.SEVERE, "Erro ao atualizar relatório", getException());
            }
        };
        
        new Thread(task).start();
    }
    
    /**
     * Manipula evento de excluir relatório
     */
    @FXML
    private void handleExcluir() {
        if (relatorioSelecionado == null) {
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Exclusão");
        alert.setHeaderText("Deseja realmente excluir este relatório?");
        alert.setContentText("Relatório de: " + relatorioSelecionado.getAluno().getNome() + 
                           "\nTipo: " + relatorioSelecionado.getTipo().getDescricao() + 
                           "\nData: " + relatorioSelecionado.getData() + 
                           "\n\nEsta ação não pode ser desfeita.");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // TODO: Implementar exclusão de relatório
            mostrarInfo("Info", "Funcionalidade de exclusão será implementada em breve");
        }
    }
    
    /**
     * Manipula evento de buscar relatórios
     */
    @FXML
    private void handleBuscar() {
        // TODO: Implementar busca com filtros
        carregarRelatorios();
    }
    
    /**
     * Manipula evento de limpar formulário
     */
    @FXML
    private void handleLimpar() {
        limparFormulario();
        tableViewRelatorios.getSelectionModel().clearSelection();
        btnSalvar.setDisable(true);
        btnEditar.setDisable(true);
        btnExcluir.setDisable(true);
    }
    
    /**
     * Manipula evento de aplicar template
     */
    @FXML
    private void handleAplicarTemplate() {
        Relatorio.TipoRelatorio tipo = comboBoxTipo.getValue();
        Aluno aluno = comboBoxAluno.getValue();
        Usuario profissional = comboBoxProfissional.getValue();
        
        if (tipo == null || aluno == null || profissional == null) {
            mostrarErro("Erro", "Selecione tipo, aluno e profissional para aplicar o template");
            return;
        }
        
        Task<String> task = new Task<String>() {
            @Override
            protected String call() throws Exception {
                return relatorioService.aplicarTemplate(tipo, aluno, profissional);
            }
            
            @Override
            protected void succeeded() {
                String template = getValue();
                txtConteudo.setText(template);
                logger.info("Template aplicado: " + tipo.getDescricao());
            }
            
            @Override
            protected void failed() {
                mostrarErro("Erro", "Não foi possível aplicar o template");
                logger.log(java.util.logging.Level.SEVERE, "Erro ao aplicar template", getException());
            }
        };
        
        new Thread(task).start();
    }
    
    /**
     * Manipula evento de relatório semestral
     */
    @FXML
    private void handleRelatorioSemestral() {
        // Dialog para selecionar semestre e ano
        Dialog<Object[]> dialog = new Dialog<>();
        dialog.setTitle("Relatório Semestral");
        dialog.setHeaderText("Selecione o semestre e ano para gerar o relatório");
        
        ComboBox<Integer> comboBoxSemestre = new ComboBox<>();
        comboBoxSemestre.getItems().addAll(1, 2);
        comboBoxSemestre.setValue(1);
        
        ComboBox<Integer> comboBoxAno = new ComboBox<>();
        int anoAtual = LocalDate.now().getYear();
        comboBoxAno.getItems().addAll(anoAtual - 1, anoAtual, anoAtual + 1);
        comboBoxAno.setValue(anoAtual);
        
        dialog.getDialogPane().setContent(new javafx.scene.layout.VBox(10, 
            new Label("Semestre:"), comboBoxSemestre,
            new Label("Ano:"), comboBoxAno
        ));
        
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        Optional<Object[]> result = dialog.showAndWait();
        if (result.isPresent()) {
            int semestre = comboBoxSemestre.getValue();
            int ano = comboBoxAno.getValue();
            
            Task<String> task = new Task<String>() {
                @Override
                protected String call() throws Exception {
                    return relatorioService.gerarRelatorioSemestral(semestre, ano);
                }
                
                @Override
                protected void succeeded() {
                    String relatorio = getValue();
                    mostrarRelatorio("Relatório Semestral " + semestre + "/" + ano, relatorio);
                    logger.info("Relatório semestral gerado: " + semestre + "/" + ano);
                }
                
                @Override
                protected void failed() {
                    mostrarErro("Erro", "Não foi possível gerar o relatório");
                    logger.log(java.util.logging.Level.SEVERE, "Erro ao gerar relatório semestral", getException());
                }
            };
            
            new Thread(task).start();
        }
    }
    
    /**
     * Cria objeto Relatorio a partir dos dados do formulário
     */
    private Relatorio criarRelatorioDoFormulario() {
        Relatorio relatorio = new Relatorio();
        relatorio.setData(datePickerData.getValue());
        relatorio.setTipo(comboBoxTipo.getValue());
        relatorio.setAluno(comboBoxAluno.getValue());
        relatorio.setProfissional(comboBoxProfissional.getValue());
        relatorio.setPeriodo(txtPeriodo.getText().trim());
        relatorio.setConteudo(txtConteudo.getText().trim());
        return relatorio;
    }
    
    /**
     * Preenche formulário com dados do relatório selecionado
     */
    private void preencherFormulario(Relatorio relatorio) {
        datePickerData.setValue(relatorio.getData());
        comboBoxTipo.setValue(relatorio.getTipo());
        comboBoxAluno.setValue(relatorio.getAluno());
        comboBoxProfissional.setValue(relatorio.getProfissional());
        txtPeriodo.setText(relatorio.getPeriodo() != null ? relatorio.getPeriodo() : "");
        txtConteudo.setText(relatorio.getConteudo());
    }
    
    /**
     * Limpa o formulário
     */
    private void limparFormulario() {
        datePickerData.setValue(LocalDate.now());
        comboBoxTipo.setValue(null);
        comboBoxAluno.setValue(null);
        if (usuarioLogado != null) {
            comboBoxProfissional.setValue(usuarioLogado);
        } else {
            comboBoxProfissional.setValue(null);
        }
        txtPeriodo.clear();
        txtConteudo.clear();
    }
    
    /**
     * Valida dados do formulário
     */
    private boolean validarFormulario() {
        StringBuilder erros = new StringBuilder();
        
        if (datePickerData.getValue() == null) {
            erros.append("• Data é obrigatória\n");
        }
        
        if (comboBoxTipo.getValue() == null) {
            erros.append("• Tipo é obrigatório\n");
        }
        
        if (comboBoxAluno.getValue() == null) {
            erros.append("• Aluno é obrigatório\n");
        }
        
        if (comboBoxProfissional.getValue() == null) {
            erros.append("• Profissional é obrigatório\n");
        }
        
        if (txtConteudo.getText().trim().isEmpty()) {
            erros.append("• Conteúdo é obrigatório\n");
        } else if (txtConteudo.getText().trim().length() < 100) {
            erros.append("• Conteúdo deve ter pelo menos 100 caracteres\n");
        }
        
        if (erros.length() > 0) {
            mostrarErro("Dados inválidos", erros.toString());
            return false;
        }
        
        return true;
    }
    
    /**
     * Mostra dialog de relatório
     */
    private void mostrarRelatorio(String titulo, String conteudo) {
        TextArea textArea = new TextArea(conteudo);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefRowCount(25);
        textArea.setPrefColumnCount(80);
        
        ScrollPane scrollPane = new ScrollPane(textArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.getDialogPane().setContent(scrollPane);
        alert.getDialogPane().setPrefSize(700, 500);
        alert.showAndWait();
    }
    
    /**
     * Mostra dialog de erro
     */
    private void mostrarErro(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
    
    /**
     * Mostra dialog de sucesso
     */
    private void mostrarSucesso(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
    
    /**
     * Mostra dialog de informação
     */
    private void mostrarInfo(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
