package com.planoaee.service;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.planoaee.model.Aluno;
import com.planoaee.repository.AlunoRepository;

/**
 * Serviço para regras de negócio relacionadas aos alunos
 */
public class AlunoService {
    
    private static final Logger logger = Logger.getLogger(AlunoService.class.getName());
    private final AlunoRepository alunoRepository;
    
    public AlunoService() {
        this.alunoRepository = new AlunoRepository();
    }
    
    /**
     * Cadastra um novo aluno no sistema
     * @param aluno aluno a ser cadastrado
     * @return aluno cadastrado com ID gerado
     * @throws IllegalArgumentException se dados inválidos
     * @throws RuntimeException se houver erro na operação
     */
    public Aluno cadastrarAluno(Aluno aluno) {
        try {
            // Validações
            validarDadosAluno(aluno);
            
            // Define data de cadastro
            if (aluno.getDataCadastro() == null) {
                aluno.setDataCadastro(LocalDateTime.now());
            }
            
            // Salva no banco
            Aluno alunoSalvo = alunoRepository.salvar(aluno);
            
            logger.info("Aluno cadastrado com sucesso: " + alunoSalvo.getNome());
            return alunoSalvo;
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao cadastrar aluno", e);
            throw new RuntimeException("Erro interno do sistema", e);
        }
    }
    
    /**
     * Atualiza dados de um aluno
     * @param aluno aluno com dados atualizados
     * @return true se atualizado com sucesso, false caso contrário
     * @throws IllegalArgumentException se dados inválidos
     * @throws RuntimeException se houver erro na operação
     */
    public boolean atualizarAluno(Aluno aluno) {
        try {
            // Validações
            validarDadosAluno(aluno);
            
            boolean sucesso = alunoRepository.atualizar(aluno);
            
            if (sucesso) {
                logger.info("Aluno atualizado com sucesso: " + aluno.getNome());
            }
            
            return sucesso;
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao atualizar aluno", e);
            throw new RuntimeException("Erro interno do sistema", e);
        }
    }
    
    /**
     * Busca aluno por ID
     * @param id ID do aluno
     * @return aluno encontrado ou null
     */
    public Aluno buscarPorId(Integer id) {
        try {
            return alunoRepository.buscarPorId(id);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao buscar aluno por ID", e);
            throw new RuntimeException("Erro interno do sistema", e);
        }
    }
    
    /**
     * Lista todos os alunos ativos
     * @return lista de alunos
     */
    public List<Aluno> listarTodos() {
        try {
            return alunoRepository.listarTodos();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao listar alunos", e);
            throw new RuntimeException("Erro interno do sistema", e);
        }
    }
    
    /**
     * Busca alunos por nome
     * @param nome nome ou parte do nome
     * @return lista de alunos encontrados
     */
    public List<Aluno> buscarPorNome(String nome) {
        try {
            if (nome == null || nome.trim().isEmpty()) {
                return listarTodos();
            }
            return alunoRepository.buscarPorNome(nome.trim());
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao buscar alunos por nome", e);
            throw new RuntimeException("Erro interno do sistema", e);
        }
    }
    
    /**
     * Busca alunos por faixa etária
     * @param idadeMinima idade mínima
     * @param idadeMaxima idade máxima
     * @return lista de alunos na faixa etária
     */
    public List<Aluno> buscarPorIdade(int idadeMinima, int idadeMaxima) {
        try {
            return alunoRepository.buscarPorIdade(idadeMinima, idadeMaxima);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao buscar alunos por idade", e);
            throw new RuntimeException("Erro interno do sistema", e);
        }
    }
    
    /**
     * Desativa um aluno
     * @param id ID do aluno
     * @return true se desativado com sucesso, false caso contrário
     */
    public boolean desativarAluno(Integer id) {
        try {
            boolean sucesso = alunoRepository.desativar(id);
            
            if (sucesso) {
                logger.info("Aluno desativado com sucesso: ID " + id);
            }
            
            return sucesso;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao desativar aluno", e);
            throw new RuntimeException("Erro interno do sistema", e);
        }
    }
    
    /**
     * Conta o total de alunos ativos
     * @return número total de alunos
     */
    public int contarAlunos() {
        try {
            return alunoRepository.contarAlunos();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao contar alunos", e);
            throw new RuntimeException("Erro interno do sistema", e);
        }
    }
    
    /**
     * Lista alunos com frequência baixa
     * @param percentualMinimo percentual mínimo de presença
     * @param diasPeriodo período em dias para análise
     * @return lista de alunos com frequência baixa
     */
    public List<Aluno> listarAlunosComFrequenciaBaixa(double percentualMinimo, int diasPeriodo) {
        try {
            return alunoRepository.listarAlunosComFrequenciaBaixa(percentualMinimo, diasPeriodo);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao listar alunos com frequência baixa", e);
            throw new RuntimeException("Erro interno do sistema", e);
        }
    }
    
    /**
     * Valida dados básicos de um aluno
     * @param aluno aluno a ser validado
     * @throws IllegalArgumentException se dados inválidos
     */
    private void validarDadosAluno(Aluno aluno) {
        if (aluno == null) {
            throw new IllegalArgumentException("Aluno não pode ser nulo");
        }
        
        if (aluno.getNome() == null || aluno.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }
        
        if (aluno.getNome().trim().length() < 2) {
            throw new IllegalArgumentException("Nome deve ter pelo menos 2 caracteres");
        }
        
        if (aluno.getIdade() == null || aluno.getIdade() < 0) {
            throw new IllegalArgumentException("Idade deve ser um número positivo");
        }
        
        if (aluno.getIdade() > 120) {
            throw new IllegalArgumentException("Idade deve ser um valor válido");
        }
        
        // Validações opcionais
        if (aluno.getContato() != null && !aluno.getContato().trim().isEmpty()) {
            String contato = aluno.getContato().trim();
            // Verifica se contém pelo menos um número
            if (!contato.matches(".*\\d.*")) {
                throw new IllegalArgumentException("Contato deve conter pelo menos um número");
            }
        }
    }
    
    /**
     * Valida se o aluno pode ser excluído
     * @param id ID do aluno
     * @return true se pode ser excluído, false caso contrário
     */
    public boolean podeExcluirAluno(Integer id) {
        // TODO: Implementar verificação de dependências
        // Verificar se aluno tem frequência, relatórios ou planos associados
        return true;
    }
    
    /**
     * Gera relatório de alunos
     * @return dados para relatório
     */
    public String gerarRelatorioAlunos() {
        try {
            List<Aluno> alunos = listarTodos();
            StringBuilder relatorio = new StringBuilder();
            
            relatorio.append("RELATÓRIO DE ALUNOS - APAPEQ\n");
            relatorio.append("===============================\n\n");
            relatorio.append("Total de Alunos: ").append(alunos.size()).append("\n\n");
            
            for (Aluno aluno : alunos) {
                relatorio.append("• ").append(aluno.getNome())
                        .append(" (").append(aluno.getIdade()).append(" anos)\n");
                if (aluno.getResponsavel() != null && !aluno.getResponsavel().trim().isEmpty()) {
                    relatorio.append("  Responsável: ").append(aluno.getResponsavel()).append("\n");
                }
                if (aluno.getContato() != null && !aluno.getContato().trim().isEmpty()) {
                    relatorio.append("  Contato: ").append(aluno.getContato()).append("\n");
                }
                relatorio.append("\n");
            }
            
            return relatorio.toString();
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao gerar relatório de alunos", e);
            return "Erro ao gerar relatório de alunos";
        }
    }
}
