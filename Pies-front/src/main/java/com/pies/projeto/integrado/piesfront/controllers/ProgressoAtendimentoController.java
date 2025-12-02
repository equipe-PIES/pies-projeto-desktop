package com.pies.projeto.integrado.piesfront.controllers;

import com.pies.projeto.integrado.piesfront.dto.EducandoDTO;
import com.pies.projeto.integrado.piesfront.dto.CreateRelatorioIndividualDTO;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import com.pies.projeto.integrado.piesfront.services.AtendimentoFlowService;
import com.pies.projeto.integrado.piesfront.services.AuthService;

/**
 * Controller para a tela de progresso de atendimento
 * Exibe o progresso do atendimento do educando em formato de popup
 */
public class ProgressoAtendimentoController implements Initializable {
    
    @FXML
    private Button iniciarAtendimento;
    
    @FXML
    private Button viewRelatorios;
    
    @FXML
    private Button statusAnamnese;
    
    @FXML
    private Button statusDI;
    
    @FXML
    private Button statusPDI;
    
    @FXML
    private Button statusPAEE;
    @FXML
    private Button editarAnamnese;
    @FXML
    private Button verAnamnese;
    @FXML
    private Button excluirAnamnese;
    @FXML
    private Button editarDiagnosticoInicial;
    @FXML
    private Button verDiagnosticoInicial;
    @FXML
    private Button excluirDiagnosticoInicial;
    @FXML
    private Button editarPDI;
    @FXML
    private Button verPDI;
    @FXML
    private Button excluirPDI;
    @FXML
    private Button editarPAEE;
    @FXML
    private Button verPAEE;
    @FXML
    private Button excluirPAEE;
    
    @FXML
    private Button closeProgressoAtd;
    
    private EducandoDTO educando;
    private final AuthService authService = AuthService.getInstance();
    
    /**
     * Define os dados do educando
     * @param educando DTO com as informações do educando
     */
    public void setEducando(EducandoDTO educando) {
        this.educando = educando;
        atualizarDados();
    }
    
    /**
     * Atualiza os dados da tela com base no educando
     */
    private void atualizarDados() {
        if (educando == null) {
            return;
        }
        // Todos os botões sempre mostram "Iniciar" para permitir múltiplos cadastros
        if (statusAnamnese != null) {
            statusAnamnese.setText("Iniciar");
            statusAnamnese.setStyle("");
        }
        
        if (statusDI != null) {
            statusDI.setText("Iniciar");
            statusDI.setStyle("");
        }
        
        if (statusPDI != null) {
            statusPDI.setText("Iniciar");
            statusPDI.setStyle("");
        }
        
        if (statusPAEE != null) {
            statusPAEE.setText("Iniciar");
            statusPAEE.setStyle("");
        }
        
        atualizarVisibilidadePorExistencia();
    }
    
    /**
     * Handler para o botão de iniciar atendimento
     */
    @FXML
    private void handleIniciarAtendimentoAction() {
        if (educando == null) {
            return;
        }
        AtendimentoFlowService.Etapa etapa = AtendimentoFlowService.getInstance().getEtapaAtual(educando.id());
        if (etapa == AtendimentoFlowService.Etapa.ANAMNESE) {
            navegarNoStagePai("/com/pies/projeto/integrado/piesfront/screens/anamnese-1.fxml", "Anamnese");
        } else if (etapa == AtendimentoFlowService.Etapa.DI) {
            navegarNoStagePai("/com/pies/projeto/integrado/piesfront/screens/diagnostico-1.fxml", "Diagnóstico Inicial");
        } else if (etapa == AtendimentoFlowService.Etapa.PDI) {
            navegarNoStagePai("/com/pies/projeto/integrado/piesfront/screens/pdi-1.fxml", "PDI");
        } else if (etapa == AtendimentoFlowService.Etapa.PAEE) {
            navegarNoStagePai("/com/pies/projeto/integrado/piesfront/screens/paee-1.fxml", "PAEE");
        }
    }
    
    /**
     * Handler para o botão de visualizar relatórios
     */
    @FXML
    private void handleViewRelatoriosAction() {
        // TODO: Implementar lógica para visualizar relatórios
        System.out.println("Visualizar relatórios: " + (educando != null ? educando.id() : "null"));
    }
    
    /**
     * Handler para o botão de status da anamnese
     */
    @FXML
    private void handleStatusAnamneseAction() {
        System.out.println("=== handleStatusAnamneseAction chamado ===");
        if (educando == null) {
            System.err.println("Educando é null!");
            return;
        }
        System.out.println("Educando: " + educando.nome() + " (ID: " + educando.id() + ")");
        // Verifica se já existe anamnese - se não existir, abre em modo novo
        var anamnese = authService.getAnamnesePorEducando(educando.id());
        boolean carregar = anamnese != null;
        navegarNoStagePaiComModo("/com/pies/projeto/integrado/piesfront/screens/anamnese-1.fxml", "Anamnese", carregar);
    }
    
    /**
     * Handler para o botão de status do diagnóstico inicial
     */
    private String getIdFromMap(java.util.Map<String, Object> m) {
        if (m == null) return null;
        Object id = m.get("id");
        return id instanceof String s ? s : null;
    }

    @FXML
    private void handleEditAnamneseAction() {
        if (educando == null) {
            return;
        }
        // Botão Editar sempre carrega dados existentes
        navegarNoStagePaiComModo("/com/pies/projeto/integrado/piesfront/screens/anamnese-1.fxml", "Anamnese", true);
    }

    @FXML
    private void handleStatusDIAction() {
        if (educando == null) {
            return;
        }
        navegarNoStagePai("/com/pies/projeto/integrado/piesfront/screens/diagnostico-1.fxml", "Diagnóstico Inicial");
    }

    @FXML
    private void handleEditPDIAction() {
        if (educando == null) {
            return;
        }
        navegarNoStagePai("/com/pies/projeto/integrado/piesfront/screens/pdi-1.fxml", "PDI");
    }

    @FXML
    private void handleEditPAEEAction() {
        if (educando == null) {
            return;
        }
        // Botão Editar sempre carrega dados existentes
        navegarNoStagePaiComModo("/com/pies/projeto/integrado/piesfront/screens/paee-1.fxml", "PAEE", true);
    }
    
    /**
     * Handler para o botão de status do PDI
     */
    @FXML
    private void handleStatusPDIAction() {
        if (educando == null) {
            return;
        }
        navegarNoStagePai("/com/pies/projeto/integrado/piesfront/screens/pdi-1.fxml", "PDI");
    }
    
    /**
     * Handler para o botão de status do PAEE
     */
    @FXML
    private void handleStatusPAEEAction() {
        System.out.println("=== handleStatusPAEEAction chamado ===");
        if (educando == null) {
            System.err.println("Educando é null!");
            return;
        }
        System.out.println("Educando: " + educando.nome() + " (ID: " + educando.id() + ")");
        // Verifica se já existe PAEE - se não existir, abre em modo novo
        var paees = authService.getPaeesPorEducandoRaw(educando.id());
        boolean carregar = paees != null && !paees.isEmpty();
        navegarNoStagePaiComModo("/com/pies/projeto/integrado/piesfront/screens/paee-1.fxml", "PAEE", carregar);
    }
    
    /**
     * Handler para o botão de fechar
     */
    @FXML
    private void handleCloseAction() {
        Stage stage = (Stage) closeProgressoAtd.getScene().getWindow();
        stage.close();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Inicialização
        if (educando != null) {
            atualizarDados();
        }
        if (editarAnamnese != null) editarAnamnese.setOnAction(e -> handleEditAnamneseAction());
        if (verAnamnese != null) verAnamnese.setOnAction(e -> handleEditAnamneseAction());
        if (excluirAnamnese != null) {
            excluirAnamnese.setOnAction(e -> {});
            excluirAnamnese.setVisible(false);
            excluirAnamnese.setManaged(false);
        }
        if (editarDiagnosticoInicial != null) editarDiagnosticoInicial.setOnAction(e -> handleStatusDIAction());
        if (verDiagnosticoInicial != null) verDiagnosticoInicial.setOnAction(e -> handleStatusDIAction());
        if (excluirDiagnosticoInicial != null) excluirDiagnosticoInicial.setOnAction(e -> {
            // TODO: Implementar quando o DiagnosticoInicial estiver completo
            System.out.println("Funcionalidade de DiagnosticoInicial ainda não implementada");
        });
        if (editarPDI != null) editarPDI.setOnAction(e -> handleEditPDIAction());
        if (verPDI != null) verPDI.setOnAction(e -> handleEditPDIAction());
        if (excluirPDI != null) excluirPDI.setOnAction(e -> {
            // TODO: Implementar quando o método deletarPDI estiver completo
            System.out.println("Funcionalidade de excluir PDI ainda não implementada");
        });
        if (editarPAEE != null) editarPAEE.setOnAction(e -> handleEditPAEEAction());
        if (verPAEE != null) verPAEE.setOnAction(e -> handleEditPAEEAction());
        if (excluirPAEE != null) excluirPAEE.setOnAction(e -> {
            // TODO: Implementar quando o método deletarPAEE estiver completo
            System.out.println("Funcionalidade de excluir PAEE ainda não implementada");
        });
    }

    private void navegarNoStagePai(String resource, String titulo) {
        try {
            System.out.println("=== navegarNoStagePai ===");
            System.out.println("Resource: " + resource);
            System.out.println("Título: " + titulo);
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource(resource));
            Parent root = loader.load();
            System.out.println("FXML carregado com sucesso");
            
            Object controller = loader.getController();
            if (controller instanceof AnamneseController c) {
                System.out.println("Controller é AnamneseController, setando educando...");
                c.setEducando(educando);
            } else if (controller instanceof PDIController c) {
                System.out.println("Controller é PDIController, setando educando...");
                c.setEducando(educando);
            } else if (controller instanceof PAEEController c) {
                System.out.println("Controller é PAEEController, setando educando...");
                c.setEducando(educando);
            } else if (controller instanceof RelatorioIndividualController c) {
                c.setEducando(educando);
            }
            // TODO: Descomentar quando DIController estiver implementado
            // else if (controller instanceof DIController c) {
            //     c.setEducando(educando);
            // }
            
            Stage popupStage = (Stage) closeProgressoAtd.getScene().getWindow();
            System.out.println("Popup stage: " + popupStage);
            
            Stage parentStage = (Stage) popupStage.getOwner();
            System.out.println("Parent stage: " + parentStage);
            
            if (parentStage == null) {
                System.out.println("Parent stage é null, usando popupStage como fallback");
                parentStage = popupStage; // fallback
            }
            
            parentStage.setTitle(titulo);
            parentStage.setScene(new Scene(root));
            
            // Força a maximização
            parentStage.setMaximized(false);
            parentStage.setMaximized(true);
            
            parentStage.show();
            System.out.println("Parent stage exibido");
            
            popupStage.close();
            System.out.println("Popup fechado");
            System.out.println("=== Navegação concluída ===");
        } catch (Exception e) {
            System.err.println("=== ERRO ao navegar ===");
            System.err.println("Mensagem: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void navegarNoStagePaiComModo(String resource, String titulo, boolean carregarDadosExistentes) {
        try {
            System.out.println("=== navegarNoStagePaiComModo ===");
            System.out.println("Resource: " + resource);
            System.out.println("Título: " + titulo);
            System.out.println("Carregar dados existentes: " + carregarDadosExistentes);
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource(resource));
            Parent root = loader.load();
            System.out.println("FXML carregado com sucesso");
            
            Object controller = loader.getController();
            if (controller instanceof AnamneseController c) {
                System.out.println("Controller é AnamneseController, setando educando...");
                c.setEducando(educando);
                if (!carregarDadosExistentes) {
                    c.setModoNovo(); // Define que é um novo cadastro
                }
            } else if (controller instanceof PDIController c) {
                System.out.println("Controller é PDIController, setando educando...");
                c.setEducando(educando);
                if (!carregarDadosExistentes) {
                    c.setModoNovo(); // Define que é um novo cadastro
                }
            } else if (controller instanceof PAEEController c) {
                System.out.println("Controller é PAEEController, setando educando...");
                c.setEducando(educando);
                if (!carregarDadosExistentes) {
                    c.setModoNovo(); // Define que é um novo cadastro
                }
            } else if (controller instanceof RelatorioIndividualController c) {
                c.setEducando(educando);
            }
            
            Stage popupStage = (Stage) closeProgressoAtd.getScene().getWindow();
            System.out.println("Popup stage: " + popupStage);
            
            Stage parentStage = (Stage) popupStage.getOwner();
            System.out.println("Parent stage: " + parentStage);
            
            if (parentStage == null) {
                System.out.println("Parent stage é null, usando popupStage como fallback");
                parentStage = popupStage; // fallback
            }
            
            parentStage.setTitle(titulo);
            parentStage.setScene(new Scene(root));
            
            // Força a maximização
            parentStage.setMaximized(false);
            parentStage.setMaximized(true);
            
            parentStage.show();
            System.out.println("Parent stage exibido");
            
            popupStage.close();
            System.out.println("Popup fechado");
            System.out.println("=== Navegação concluída ===");
        } catch (Exception e) {
            System.err.println("=== ERRO ao navegar ===");
            System.err.println("Mensagem: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void atualizarVisibilidadePorExistencia() {
        boolean hasAnamnese = false;
        boolean hasDI = false;
        boolean hasPDI = false;
        boolean hasPAEE = false;
        if (educando != null && educando.id() != null) {
            var a = authService.getAnamnesePorEducando(educando.id());
            hasAnamnese = a != null;
            // TODO: Implementar quando o DiagnosticoInicial estiver completo
            // var di = authService.getDiagnosticoInicialPorEducandoRaw(educando.id());
            hasDI = false; // di != null;
            var pdis = authService.getPdisPorEducandoRaw(educando.id());
            hasPDI = pdis != null && !pdis.isEmpty();
            var paees = authService.getPaeesPorEducandoRaw(educando.id());
            hasPAEE = paees != null && !paees.isEmpty();
        }
        // Todos os botões sempre visíveis
        if (editarAnamnese != null) { editarAnamnese.setVisible(true); editarAnamnese.setManaged(true); }
        if (statusAnamnese != null) { statusAnamnese.setVisible(true); statusAnamnese.setManaged(true); }
        if (excluirAnamnese != null) { excluirAnamnese.setVisible(false); excluirAnamnese.setManaged(false); }

        if (editarDiagnosticoInicial != null) { editarDiagnosticoInicial.setVisible(true); editarDiagnosticoInicial.setManaged(true); }
        if (statusDI != null) { statusDI.setVisible(true); statusDI.setManaged(true); }
        if (excluirDiagnosticoInicial != null) { excluirDiagnosticoInicial.setVisible(false); excluirDiagnosticoInicial.setManaged(false); }

        if (editarPDI != null) { editarPDI.setVisible(true); editarPDI.setManaged(true); }
        if (statusPDI != null) { statusPDI.setVisible(true); statusPDI.setManaged(true); }
        if (excluirPDI != null) { excluirPDI.setVisible(false); excluirPDI.setManaged(false); }

        if (editarPAEE != null) { editarPAEE.setVisible(true); editarPAEE.setManaged(true); }
        if (statusPAEE != null) { statusPAEE.setVisible(true); statusPAEE.setManaged(true); }
        if (excluirPAEE != null) { excluirPAEE.setVisible(false); excluirPAEE.setManaged(false); }
    }

    @FXML
    private void handleIniciarRelatorioIndividualAction() {
        if (educando == null) {
            return;
        }
        var dto = new CreateRelatorioIndividualDTO(
                educando.id(),
                null, null, null, null, null, null, null, null
        );
        authService.criarRelatorioIndividual(dto);
        navegarNoStagePai("/com/pies/projeto/integrado/piesfront/screens/relatorio-individual-1.fxml", "Relatório Individual");
    }
}

