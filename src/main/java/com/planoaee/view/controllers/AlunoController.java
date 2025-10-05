package com.planoaee.view.controllers;

import com.planoaee.model.Aluno;
import com.planoaee.service.AlunoService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/**
 * Controller para gerenciamento de alunos
 */
public class AlunoController implements Initializable {
    
    private static final Logger logger = Logger.getLogger(AlunoController.class.getName());
    
    @FXML
    private TableView<Aluno> tableViewAlunos;
    
    @FXML
    private TableColumn<Aluno, String> colNome;
    
    @FXML
    private TableColumn<Aluno, Integer> colIdade;
    
    @FXML
    private TableColumn<Aluno, String> colResponsavel;
    
    @FXML
    private TableColumn<Aluno, String> colContato;
    
    @FXML
    private TextField txtNome;
    
    @FXML
    private Spinner<Integer> spinnerIdade;
    
    @FXML
    private TextField txtResponsavel;
    
    @FXML
    private TextField txtContato;
    
    @FXML
    private TextArea txtObservacoes;
    
    @FXML
    private TextField txtBusca;
    
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
    
    private AlunoService alunoService;
    private ObservableList<Aluno> alunosList;
    private Aluno alunoSelecionado;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.alunoService = new AlunoService();
        this.alunosList = FXCollections.observableArrayList();
        
        configurarTabela();
        configurarSpinner();
        carregarAlunos();
        
        // Desabilita botões inicialmente
        btnSalvar.setDisable(true);
        btnEditar.setDisable(true);
        btnExcluir.setDisable(true);
        
        logger.info("AlunoController inicializado");
    }
    
    /**
     * Configura as colunas da tabela
     */
    private void configurarTabela() {
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colIdade.setCellValueFactory(new PropertyValueFactory<>("idade"));
        colResponsavel.setCellValueFactory(new PropertyValueFactory<>("responsavel"));
        colContato.setCellValueFactory(new PropertyValueFactory<>("contato"));
        
        tableViewAlunos.setItems(alunosList);
        
        // Seleção na tabela
        tableViewAlunos.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                alunoSelecionado = newValue;
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
     * Configura o spinner de idade
     */
    private void configurarSpinner() {
        SpinnerValueFactory.IntegerSpinnerValueFactory valueFactory = 
            new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 120, 5);
        spinnerIdade.setValueFactory(valueFactory);
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
                mostrarErro("Erro", "Não foi possível carregar os alunos");
                logger.log(java.util.logging.Level.SEVERE, "Erro ao carregar alunos", getException());
            }
        };
        
        new Thread(task).start();
    }
    
    /**
     * Manipula evento de novo aluno
     */
    @FXML
    private void handleNovo() {
        limparFormulario();
        btnSalvar.setDisable(false);
        btnEditar.setDisable(true);
        btnExcluir.setDisable(true);
        tableViewAlunos.getSelectionModel().clearSelection();
        txtNome.requestFocus();
    }
    
    /**
     * Manipula evento de salvar aluno
     */
    @FXML
    private void handleSalvar() {
        if (!validarFormulario()) {
            return;
        }
        
        Aluno aluno = criarAlunoDoFormulario();
        
        Task<Aluno> task = new Task<Aluno>() {
            @Override
            protected Aluno call() throws Exception {
                return alunoService.cadastrarAluno(aluno);
            }
            
            @Override
            protected void succeeded() {
                Aluno alunoSalvo = getValue();
                alunosList.add(alunoSalvo);
                mostrarSucesso("Sucesso", "Aluno cadastrado com sucesso!");
                limparFormulario();
                btnSalvar.setDisable(true);
                logger.info("Aluno cadastrado: " + alunoSalvo.getNome());
            }
            
            @Override
            protected void failed() {
                mostrarErro("Erro", "Não foi possível cadastrar o aluno");
                logger.log(java.util.logging.Level.SEVERE, "Erro ao cadastrar aluno", getException());
            }
        };
        
        new Thread(task).start();
    }
    
    /**
     * Manipula evento de editar aluno
     */
    @FXML
    private void handleEditar() {
        if (alunoSelecionado == null) {
            return;
        }
        
        if (!validarFormulario()) {
            return;
        }
        
        // Atualiza dados do aluno selecionado
        alunoSelecionado.setNome(txtNome.getText().trim());
        alunoSelecionado.setIdade(spinnerIdade.getValue());
        alunoSelecionado.setResponsavel(txtResponsavel.getText().trim());
        alunoSelecionado.setContato(txtContato.getText().trim());
        alunoSelecionado.setObservacoes(txtObservacoes.getText().trim());
        
        Task<Boolean> task = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                return alunoService.atualizarAluno(alunoSelecionado);
            }
            
            @Override
            protected void succeeded() {
                if (getValue()) {
                    // Atualiza a lista
                    int index = alunosList.indexOf(alunoSelecionado);
                    if (index >= 0) {
                        alunosList.set(index, alunoSelecionado);
                    }
                    mostrarSucesso("Sucesso", "Aluno atualizado com sucesso!");
                    logger.info("Aluno atualizado: " + alunoSelecionado.getNome());
                } else {
                    mostrarErro("Erro", "Não foi possível atualizar o aluno");
                }
            }
            
            @Override
            protected void failed() {
                mostrarErro("Erro", "Não foi possível atualizar o aluno");
                logger.log(java.util.logging.Level.SEVERE, "Erro ao atualizar aluno", getException());
            }
        };
        
        new Thread(task).start();
    }
    
    /**
     * Manipula evento de excluir aluno
     */
    @FXML
    private void handleExcluir() {
        if (alunoSelecionado == null) {
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Exclusão");
        alert.setHeaderText("Deseja realmente excluir este aluno?");
        alert.setContentText("Aluno: " + alunoSelecionado.getNome() + "\n\nEsta ação não pode ser desfeita.");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Task<Boolean> task = new Task<Boolean>() {
                @Override
                protected Boolean call() throws Exception {
                    return alunoService.desativarAluno(alunoSelecionado.getId());
                }
                
                @Override
                protected void succeeded() {
                    if (getValue()) {
                        alunosList.remove(alunoSelecionado);
                        mostrarSucesso("Sucesso", "Aluno excluído com sucesso!");
                        limparFormulario();
                        logger.info("Aluno excluído: " + alunoSelecionado.getNome());
                    } else {
                        mostrarErro("Erro", "Não foi possível excluir o aluno");
                    }
                }
                
                @Override
                protected void failed() {
                    mostrarErro("Erro", "Não foi possível excluir o aluno");
                    logger.log(java.util.logging.Level.SEVERE, "Erro ao excluir aluno", getException());
                }
            };
            
            new Thread(task).start();
        }
    }
    
    /**
     * Manipula evento de buscar alunos
     */
    @FXML
    private void handleBuscar() {
        String termo = txtBusca.getText().trim();
        
        Task<List<Aluno>> task = new Task<List<Aluno>>() {
            @Override
            protected List<Aluno> call() throws Exception {
                if (termo.isEmpty()) {
                    return alunoService.listarTodos();
                } else {
                    return alunoService.buscarPorNome(termo);
                }
            }
            
            @Override
            protected void succeeded() {
                alunosList.clear();
                alunosList.addAll(getValue());
                logger.info("Busca realizada: " + getValue().size() + " alunos encontrados");
            }
            
            @Override
            protected void failed() {
                mostrarErro("Erro", "Não foi possível realizar a busca");
                logger.log(java.util.logging.Level.SEVERE, "Erro na busca", getException());
            }
        };
        
        new Thread(task).start();
    }
    
    /**
     * Manipula evento de limpar formulário
     */
    @FXML
    private void handleLimpar() {
        limparFormulario();
        tableViewAlunos.getSelectionModel().clearSelection();
        btnSalvar.setDisable(true);
        btnEditar.setDisable(true);
        btnExcluir.setDisable(true);
    }
    
    /**
     * Cria objeto Aluno a partir dos dados do formulário
     */
    private Aluno criarAlunoDoFormulario() {
        Aluno aluno = new Aluno();
        aluno.setNome(txtNome.getText().trim());
        aluno.setIdade(spinnerIdade.getValue());
        aluno.setResponsavel(txtResponsavel.getText().trim());
        aluno.setContato(txtContato.getText().trim());
        aluno.setObservacoes(txtObservacoes.getText().trim());
        aluno.setDataCadastro(LocalDateTime.now());
        return aluno;
    }
    
    /**
     * Preenche formulário com dados do aluno selecionado
     */
    private void preencherFormulario(Aluno aluno) {
        txtNome.setText(aluno.getNome());
        spinnerIdade.getValueFactory().setValue(aluno.getIdade());
        txtResponsavel.setText(aluno.getResponsavel() != null ? aluno.getResponsavel() : "");
        txtContato.setText(aluno.getContato() != null ? aluno.getContato() : "");
        txtObservacoes.setText(aluno.getObservacoes() != null ? aluno.getObservacoes() : "");
    }
    
    /**
     * Limpa o formulário
     */
    private void limparFormulario() {
        txtNome.clear();
        spinnerIdade.getValueFactory().setValue(5);
        txtResponsavel.clear();
        txtContato.clear();
        txtObservacoes.clear();
    }
    
    /**
     * Valida dados do formulário
     */
    private boolean validarFormulario() {
        StringBuilder erros = new StringBuilder();
        
        if (txtNome.getText().trim().isEmpty()) {
            erros.append("• Nome é obrigatório\n");
        }
        
        if (spinnerIdade.getValue() == null || spinnerIdade.getValue() < 0) {
            erros.append("• Idade deve ser um número válido\n");
        }
        
        if (erros.length() > 0) {
            mostrarErro("Dados inválidos", erros.toString());
            return false;
        }
        
        return true;
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
}
