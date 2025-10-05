package com.planoaee.view.controllers;

import com.planoaee.model.Aluno;
import com.planoaee.model.Frequencia;
import com.planoaee.service.AlunoService;
import com.planoaee.service.FrequenciaService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/**
 * Controller para controle de frequência
 */
public class FrequenciaController implements Initializable {
    
    private static final Logger logger = Logger.getLogger(FrequenciaController.class.getName());
    
    @FXML
    private DatePicker datePickerData;
    
    @FXML
    private TableView<Aluno> tableViewAlunos;
    
    @FXML
    private TableColumn<Aluno, String> colNome;
    
    @FXML
    private TableColumn<Aluno, Integer> colIdade;
    
    @FXML
    private TableColumn<Aluno, String> colStatus;
    
    @FXML
    private TableView<Frequencia> tableViewFrequencias;
    
    @FXML
    private TableColumn<Frequencia, String> colData;
    
    @FXML
    private TableColumn<Frequencia, String> colAluno;
    
    @FXML
    private TableColumn<Frequencia, String> colPresenca;
    
    @FXML
    private TableColumn<Frequencia, String> colObservacoes;
    
    @FXML
    private TextArea txtObservacoes;
    
    @FXML
    private Button btnRegistrarFrequencia;
    
    @FXML
    private Button btnAtualizarFrequencia;
    
    @FXML
    private Button btnRelatorioMensal;
    
    @FXML
    private Button btnRelatorioSemestral;
    
    @FXML
    private Label lblEstatisticas;
    
    private AlunoService alunoService;
    private FrequenciaService frequenciaService;
    private ObservableList<Aluno> alunosList;
    private ObservableList<Frequencia> frequenciasList;
    private Map<Integer, Boolean> frequenciasMap;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.alunoService = new AlunoService();
        this.frequenciaService = new FrequenciaService();
        this.alunosList = FXCollections.observableArrayList();
        this.frequenciasList = FXCollections.observableArrayList();
        this.frequenciasMap = new HashMap<>();
        
        configurarTabelas();
        configurarDataPicker();
        carregarAlunos();
        
        logger.info("FrequenciaController inicializado");
    }
    
    /**
     * Configura as tabelas
     */
    private void configurarTabelas() {
        // Tabela de alunos
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colIdade.setCellValueFactory(new PropertyValueFactory<>("idade"));
        colStatus.setCellValueFactory(cellData -> {
            Aluno aluno = cellData.getValue();
            if (frequenciasMap.containsKey(aluno.getId())) {
                return new javafx.beans.property.SimpleStringProperty(
                    frequenciasMap.get(aluno.getId()) ? "Presente" : "Faltou"
                );
            }
            return new javafx.beans.property.SimpleStringProperty("Não registrado");
        });
        
        tableViewAlunos.setItems(alunosList);
        
        // Tabela de frequências
        colData.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getData().toString())
        );
        colAluno.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getAluno().getNome())
        );
        colPresenca.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStatusFrequencia())
        );
        colObservacoes.setCellValueFactory(new PropertyValueFactory<>("observacoes"));
        
        tableViewFrequencias.setItems(frequenciasList);
        
        // Seleção na tabela de alunos
        tableViewAlunos.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                if (newValue != null) {
                    // Permite editar frequência do aluno selecionado
                    btnRegistrarFrequencia.setDisable(false);
                }
            }
        );
    }
    
    /**
     * Configura o date picker
     */
    private void configurarDataPicker() {
        datePickerData.setValue(LocalDate.now());
        
        // Atualiza frequências quando a data muda
        datePickerData.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                carregarFrequenciasPorData(newValue);
            }
        });
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
                atualizarEstatisticas();
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
     * Carrega frequências de uma data específica
     */
    private void carregarFrequenciasPorData(LocalDate data) {
        Task<List<Frequencia>> task = new Task<List<Frequencia>>() {
            @Override
            protected List<Frequencia> call() throws Exception {
                return frequenciaService.listarFrequenciasPorData(data);
            }
            
            @Override
            protected void succeeded() {
                frequenciasList.clear();
                frequenciasList.addAll(getValue());
                
                // Atualiza mapa de frequências para a tabela de alunos
                frequenciasMap.clear();
                for (Frequencia freq : getValue()) {
                    frequenciasMap.put(freq.getAluno().getId(), freq.getPresente());
                }
                
                // Atualiza a tabela de alunos
                tableViewAlunos.refresh();
                atualizarEstatisticas();
                logger.info("Frequências carregadas para " + data + ": " + getValue().size());
            }
            
            @Override
            protected void failed() {
                logger.log(java.util.logging.Level.SEVERE, "Erro ao carregar frequências", getException());
            }
        };
        
        new Thread(task).start();
    }
    
    /**
     * Manipula evento de registrar frequência
     */
    @FXML
    private void handleRegistrarFrequencia() {
        Aluno alunoSelecionado = tableViewAlunos.getSelectionModel().getSelectedItem();
        LocalDate data = datePickerData.getValue();
        
        if (alunoSelecionado == null) {
            mostrarErro("Erro", "Selecione um aluno");
            return;
        }
        
        if (data == null) {
            mostrarErro("Erro", "Selecione uma data");
            return;
        }
        
        // Verifica se já existe frequência para esta data
        if (frequenciaService.existeFrequencia(alunoSelecionado.getId(), data)) {
            mostrarErro("Erro", "Já existe frequência registrada para este aluno nesta data");
            return;
        }
        
        // Dialog para confirmar presença
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Registrar Frequência");
        alert.setHeaderText("Registrar frequência para: " + alunoSelecionado.getNome());
        alert.setContentText("Data: " + data + "\n\nO aluno estava presente?");
        
        ButtonType btnPresente = new ButtonType("Presente");
        ButtonType btnFaltou = new ButtonType("Faltou");
        ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        
        alert.getButtonTypes().setAll(btnPresente, btnFaltou, btnCancelar);
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            boolean presente = result.get() == btnPresente;
            String observacoes = txtObservacoes.getText().trim();
            
            Task<Frequencia> task = new Task<Frequencia>() {
                @Override
                protected Frequencia call() throws Exception {
                    return frequenciaService.registrarFrequencia(alunoSelecionado, data, presente, observacoes);
                }
                
                @Override
                protected void succeeded() {
                    mostrarSucesso("Sucesso", "Frequência registrada com sucesso!");
                    carregarFrequenciasPorData(data);
                    txtObservacoes.clear();
                    logger.info("Frequência registrada: " + alunoSelecionado.getNome() + " - " + data);
                }
                
                @Override
                protected void failed() {
                    mostrarErro("Erro", "Não foi possível registrar a frequência");
                    logger.log(java.util.logging.Level.SEVERE, "Erro ao registrar frequência", getException());
                }
            };
            
            new Thread(task).start();
        }
    }
    
    /**
     * Manipula evento de registrar frequência em lote
     */
    @FXML
    private void handleRegistrarFrequenciaLote() {
        LocalDate data = datePickerData.getValue();
        
        if (data == null) {
            mostrarErro("Erro", "Selecione uma data");
            return;
        }
        
        // Verifica se já existem frequências para esta data
        boolean temFrequencias = false;
        for (Aluno aluno : alunosList) {
            if (frequenciaService.existeFrequencia(aluno.getId(), data)) {
                temFrequencias = true;
                break;
            }
        }
        
        if (temFrequencias) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmar Operação");
            alert.setHeaderText("Já existem frequências registradas para esta data");
            alert.setContentText("Deseja continuar? Algumas frequências podem ser ignoradas.");
            
            Optional<ButtonType> result = alert.showAndWait();
            if (!result.isPresent() || result.get() != ButtonType.OK) {
                return;
            }
        }
        
        // Cria mapa de frequências (todos presentes por padrão)
        Map<Integer, Boolean> frequenciasLote = new HashMap<>();
        for (Aluno aluno : alunosList) {
            frequenciasLote.put(aluno.getId(), true); // Padrão: presente
        }
        
        Task<List<Frequencia>> task = new Task<List<Frequencia>>() {
            @Override
            protected List<Frequencia> call() throws Exception {
                String observacoes = txtObservacoes.getText().trim();
                return frequenciaService.registrarFrequenciaLote(data, frequenciasLote, observacoes);
            }
            
            @Override
            protected void succeeded() {
                List<Frequencia> frequenciasRegistradas = getValue();
                mostrarSucesso("Sucesso", frequenciasRegistradas.size() + " frequências registradas com sucesso!");
                carregarFrequenciasPorData(data);
                txtObservacoes.clear();
                logger.info("Frequência em lote registrada: " + frequenciasRegistradas.size() + " alunos");
            }
            
            @Override
            protected void failed() {
                mostrarErro("Erro", "Não foi possível registrar as frequências");
                logger.log(java.util.logging.Level.SEVERE, "Erro ao registrar frequências em lote", getException());
            }
        };
        
        new Thread(task).start();
    }
    
    /**
     * Manipula evento de relatório mensal
     */
    @FXML
    private void handleRelatorioMensal() {
        LocalDate data = datePickerData.getValue();
        if (data == null) {
            data = LocalDate.now();
        }
        
        Task<String> task = new Task<String>() {
            @Override
            protected String call() throws Exception {
                return frequenciaService.gerarRelatorioMensal(data.getMonthValue(), data.getYear());
            }
            
            @Override
            protected void succeeded() {
                String relatorio = getValue();
                mostrarRelatorio("Relatório Mensal de Frequência", relatorio);
                logger.info("Relatório mensal gerado para " + data.getMonthValue() + "/" + data.getYear());
            }
            
            @Override
            protected void failed() {
                mostrarErro("Erro", "Não foi possível gerar o relatório");
                logger.log(java.util.logging.Level.SEVERE, "Erro ao gerar relatório mensal", getException());
            }
        };
        
        new Thread(task).start();
    }
    
    /**
     * Manipula evento de relatório semestral
     */
    @FXML
    private void handleRelatorioSemestral() {
        LocalDate data = datePickerData.getValue();
        if (data == null) {
            data = LocalDate.now();
        }
        
        // Determina o semestre
        int semestre = data.getMonthValue() <= 7 ? 1 : 2;
        
        Task<String> task = new Task<String>() {
            @Override
            protected String call() throws Exception {
                return frequenciaService.gerarRelatorioSemestral(semestre, data.getYear());
            }
            
            @Override
            protected void succeeded() {
                String relatorio = getValue();
                mostrarRelatorio("Relatório Semestral de Frequência", relatorio);
                logger.info("Relatório semestral gerado para semestre " + semestre + "/" + data.getYear());
            }
            
            @Override
            protected void failed() {
                mostrarErro("Erro", "Não foi possível gerar o relatório");
                logger.log(java.util.logging.Level.SEVERE, "Erro ao gerar relatório semestral", getException());
            }
        };
        
        new Thread(task).start();
    }
    
    /**
     * Atualiza estatísticas na interface
     */
    private void atualizarEstatisticas() {
        if (datePickerData.getValue() == null) {
            lblEstatisticas.setText("Selecione uma data para ver as estatísticas");
            return;
        }
        
        LocalDate data = datePickerData.getValue();
        List<Frequencia> frequencias = frequenciasList;
        
        int totalAlunos = alunosList.size();
        int totalFrequencias = frequencias.size();
        long totalPresentes = frequencias.stream().mapToLong(f -> f.isPresente() ? 1 : 0).sum();
        long totalFaltas = totalFrequencias - totalPresentes;
        double percentual = totalFrequencias > 0 ? (totalPresentes * 100.0 / totalFrequencias) : 0;
        
        String stats = String.format(
            "Data: %s | Total: %d | Presentes: %d | Faltas: %d | Percentual: %.1f%%",
            data, totalFrequencias, totalPresentes, totalFaltas, percentual
        );
        
        lblEstatisticas.setText(stats);
    }
    
    /**
     * Mostra dialog de relatório
     */
    private void mostrarRelatorio(String titulo, String conteudo) {
        TextArea textArea = new TextArea(conteudo);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefRowCount(20);
        textArea.setPrefColumnCount(80);
        
        ScrollPane scrollPane = new ScrollPane(textArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.getDialogPane().setContent(scrollPane);
        alert.getDialogPane().setPrefSize(600, 400);
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
}
