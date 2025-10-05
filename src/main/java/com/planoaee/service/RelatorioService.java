package com.planoaee.service;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.planoaee.model.Aluno;
import com.planoaee.model.Relatorio;
import com.planoaee.model.Usuario;
import com.planoaee.repository.RelatorioRepository;

/**
 * Serviço para regras de negócio relacionadas aos relatórios
 */
public class RelatorioService {
    
    private static final Logger logger = Logger.getLogger(RelatorioService.class.getName());
    private final RelatorioRepository relatorioRepository;
    
    public RelatorioService() {
        this.relatorioRepository = new RelatorioRepository();
    }
    
    /**
     * Cria um novo relatório
     * @param relatorio relatório a ser criado
     * @return relatório criado com ID gerado
     * @throws IllegalArgumentException se dados inválidos
     * @throws RuntimeException se houver erro na operação
     */
    public Relatorio criarRelatorio(Relatorio relatorio) {
        try {
            // Validações
            validarDadosRelatorio(relatorio);
            
            // Define data de criação se não definida
            if (relatorio.getDataCriacao() == null) {
                relatorio.setDataCriacao(LocalDateTime.now());
            }
            
            // Salva no banco
            Relatorio relatorioSalvo = relatorioRepository.salvar(relatorio);
            
            logger.info("Relatório criado com sucesso: " + relatorioSalvo.getId() + " - " + relatorioSalvo.getTipo());
            return relatorioSalvo;
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao criar relatório", e);
            throw new RuntimeException("Erro interno do sistema", e);
        }
    }
    
    /**
     * Atualiza um relatório existente
     * @param relatorio relatório a ser atualizado
     * @return true se atualizado com sucesso, false caso contrário
     * @throws IllegalArgumentException se dados inválidos
     * @throws RuntimeException se houver erro na operação
     */
    public boolean atualizarRelatorio(Relatorio relatorio) {
        try {
            // Validações
            validarDadosRelatorio(relatorio);
            
            boolean sucesso = relatorioRepository.atualizar(relatorio);
            
            if (sucesso) {
                logger.info("Relatório atualizado com sucesso: " + relatorio.getId());
            }
            
            return sucesso;
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao atualizar relatório", e);
            throw new RuntimeException("Erro interno do sistema", e);
        }
    }
    
    /**
     * Lista relatórios de um aluno
     * @param alunoId ID do aluno
     * @return lista de relatórios
     */
    public List<Relatorio> listarRelatoriosPorAluno(Integer alunoId) {
        try {
            return relatorioRepository.buscarPorAluno(alunoId);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao listar relatórios por aluno", e);
            throw new RuntimeException("Erro interno do sistema", e);
        }
    }
    
    /**
     * Lista relatórios de um profissional
     * @param profissionalId ID do profissional
     * @return lista de relatórios
     */
    public List<Relatorio> listarRelatoriosPorProfissional(Integer profissionalId) {
        try {
            return relatorioRepository.buscarPorProfissional(profissionalId);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao listar relatórios por profissional", e);
            throw new RuntimeException("Erro interno do sistema", e);
        }
    }
    
    /**
     * Lista relatórios em um período
     * @param dataInicio data de início
     * @param dataFim data de fim
     * @return lista de relatórios
     */
    public List<Relatorio> listarRelatoriosPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        try {
            return relatorioRepository.buscarPorPeriodo(dataInicio, dataFim);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao listar relatórios por período", e);
            throw new RuntimeException("Erro interno do sistema", e);
        }
    }
    
    /**
     * Lista relatórios por tipo
     * @param tipo tipo do relatório
     * @return lista de relatórios
     */
    public List<Relatorio> listarRelatoriosPorTipo(Relatorio.TipoRelatorio tipo) {
        try {
            return relatorioRepository.buscarPorTipo(tipo);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao listar relatórios por tipo", e);
            throw new RuntimeException("Erro interno do sistema", e);
        }
    }
    
    /**
     * Lista todos os relatórios
     * @return lista de relatórios
     */
    public List<Relatorio> listarTodos() {
        try {
            return relatorioRepository.listarTodos();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao listar todos os relatórios", e);
            throw new RuntimeException("Erro interno do sistema", e);
        }
    }
    
    /**
     * Busca relatório por ID
     * @param id ID do relatório
     * @return relatório encontrado ou null
     */
    public Relatorio buscarPorId(Integer id) {
        try {
            return relatorioRepository.buscarPorId(id);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao buscar relatório por ID", e);
            throw new RuntimeException("Erro interno do sistema", e);
        }
    }
    
    /**
     * Gera relatório semestral consolidado
     * @param semestre semestre (1 ou 2)
     * @param ano ano
     * @return relatório consolidado
     */
    public String gerarRelatorioSemestral(int semestre, int ano) {
        try {
            LocalDate dataInicio, dataFim;
            String periodoStr;
            
            if (semestre == 1) {
                dataInicio = LocalDate.of(ano, 2, 1); // Fevereiro
                dataFim = LocalDate.of(ano, 7, 31);   // Julho
                periodoStr = "1º Semestre " + ano;
            } else {
                dataInicio = LocalDate.of(ano, 8, 1); // Agosto
                dataFim = LocalDate.of(ano, 12, 31);  // Dezembro
                periodoStr = "2º Semestre " + ano;
            }
            
            List<Relatorio> relatorios = listarRelatoriosPorPeriodo(dataInicio, dataFim);
            Map<Relatorio.TipoRelatorio, Integer> contagemPorTipo = relatorioRepository.contarPorTipo();
            
            StringBuilder relatorio = new StringBuilder();
            relatorio.append("RELATÓRIO SEMESTRAL CONSOLIDADO - APAPEQ\n");
            relatorio.append("===========================================\n\n");
            relatorio.append("Período: ").append(periodoStr).append("\n");
            relatorio.append("Data de geração: ").append(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append("\n\n");
            
            // Estatísticas gerais
            relatorio.append("ESTATÍSTICAS GERAIS:\n");
            relatorio.append("====================\n");
            relatorio.append("Total de relatórios: ").append(relatorios.size()).append("\n\n");
            
            // Contagem por tipo
            relatorio.append("RELATÓRIOS POR TIPO:\n");
            relatorio.append("--------------------\n");
            for (Relatorio.TipoRelatorio tipo : Relatorio.TipoRelatorio.values()) {
                int count = contagemPorTipo.getOrDefault(tipo, 0);
                relatorio.append("• ").append(tipo.getDescricao()).append(": ").append(count).append("\n");
            }
            relatorio.append("\n");
            
            // Relatórios por profissional
            relatorio.append("RELATÓRIOS POR PROFISSIONAL:\n");
            relatorio.append("=============================\n");
            Map<String, Integer> relatoriosPorProfissional = new java.util.HashMap<>();
            for (Relatorio rel : relatorios) {
                String nome = rel.getProfissional().getNome();
                relatoriosPorProfissional.put(nome, relatoriosPorProfissional.getOrDefault(nome, 0) + 1);
            }
            
            for (Map.Entry<String, Integer> entry : relatoriosPorProfissional.entrySet()) {
                relatorio.append("• ").append(entry.getKey()).append(": ").append(entry.getValue()).append(" relatórios\n");
            }
            relatorio.append("\n");
            
            // Resumo dos relatórios
            relatorio.append("RESUMO DOS RELATÓRIOS:\n");
            relatorio.append("======================\n");
            for (Relatorio rel : relatorios) {
                relatorio.append("• ").append(rel.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                        .append(" - ").append(rel.getAluno().getNome())
                        .append(" - ").append(rel.getTipo().getDescricao())
                        .append(" - ").append(rel.getProfissional().getNome()).append("\n");
            }
            
            return relatorio.toString();
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao gerar relatório semestral", e);
            return "Erro ao gerar relatório semestral";
        }
    }
    
    /**
     * Aplica template padrão para um tipo de relatório
     * @param tipo tipo do relatório
     * @param aluno aluno
     * @param profissional profissional
     * @return conteúdo do template
     */
    public String aplicarTemplate(Relatorio.TipoRelatorio tipo, Aluno aluno, Usuario profissional) {
        StringBuilder template = new StringBuilder();
        
        template.append("RELATÓRIO ").append(tipo.getDescricao().toUpperCase()).append("\n");
        template.append("=====================================\n\n");
        
        template.append("DADOS DO ALUNO:\n");
        template.append("Nome: ").append(aluno.getNome()).append("\n");
        template.append("Idade: ").append(aluno.getIdade()).append(" anos\n");
        if (aluno.getResponsavel() != null && !aluno.getResponsavel().trim().isEmpty()) {
            template.append("Responsável: ").append(aluno.getResponsavel()).append("\n");
        }
        template.append("\n");
        
        template.append("DADOS DO PROFISSIONAL:\n");
        template.append("Nome: ").append(profissional.getNome()).append("\n");
        template.append("Tipo: ").append(profissional.getTipo().getDescricao()).append("\n");
        template.append("Data: ").append(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append("\n\n");
        
        // Template específico por tipo
        switch (tipo) {
            case EDUCACIONAL:
                template.append("OBJETIVOS EDUCACIONAIS:\n");
                template.append("• [Descrever objetivos específicos para o período]\n");
                template.append("• [Mencionar metas de aprendizagem]\n\n");
                
                template.append("ATIVIDADES DESENVOLVIDAS:\n");
                template.append("• [Listar atividades realizadas]\n");
                template.append("• [Descrever metodologias utilizadas]\n\n");
                
                template.append("AVALIAÇÃO DO DESEMPENHO:\n");
                template.append("• [Avaliar progresso do aluno]\n");
                template.append("• [Identificar dificuldades e avanços]\n\n");
                
                template.append("RECOMENDAÇÕES:\n");
                template.append("• [Sugerir estratégias para próximos períodos]\n");
                template.append("• [Propor adaptações necessárias]\n");
                break;
                
            case PSICOLOGICO:
                template.append("AVALIAÇÃO PSICOLÓGICA:\n");
                template.append("• [Descrever aspectos cognitivos observados]\n");
                template.append("• [Avaliar desenvolvimento emocional]\n\n");
                
                template.append("INTERVENÇÕES REALIZADAS:\n");
                template.append("• [Listar técnicas utilizadas]\n");
                template.append("• [Descrever abordagens terapêuticas]\n\n");
                
                template.append("COMPORTAMENTO E INTERAÇÃO:\n");
                template.append("• [Observar comportamento em diferentes situações]\n");
                template.append("• [Avaliar interação social]\n\n");
                
                template.append("ENCAMINHAMENTOS:\n");
                template.append("• [Sugerir encaminhamentos se necessário]\n");
                template.append("• [Recomendar acompanhamento especializado]\n");
                break;
                
            case FISIOTERAPICO:
                template.append("AVALIAÇÃO MOTORA:\n");
                template.append("• [Descrever habilidades motoras desenvolvidas]\n");
                template.append("• [Avaliar coordenação e equilíbrio]\n\n");
                
                template.append("EXERCÍCIOS E TERAPIAS:\n");
                template.append("• [Listar exercícios realizados]\n");
                template.append("• [Descrever terapias aplicadas]\n\n");
                
                template.append("EVOLUÇÃO MOTORA:\n");
                template.append("• [Avaliar progresso motor]\n");
                template.append("• [Identificar ganhos de força e flexibilidade]\n\n");
                
                template.append("ORIENTAÇÕES:\n");
                template.append("• [Fornecer orientações para casa]\n");
                template.append("• [Sugerir atividades complementares]\n");
                break;
        }
        
        return template.toString();
    }
    
    /**
     * Valida conteúdo do relatório
     * @param conteudo conteúdo a ser validado
     * @return true se válido, false caso contrário
     */
    public boolean validarConteudo(String conteudo) {
        if (conteudo == null || conteudo.trim().isEmpty()) {
            return false;
        }
        
        // Verifica se o conteúdo tem pelo menos 100 caracteres
        return conteudo.trim().length() >= 100;
    }
    
    /**
     * Valida dados básicos de um relatório
     * @param relatorio relatório a ser validado
     * @throws IllegalArgumentException se dados inválidos
     */
    private void validarDadosRelatorio(Relatorio relatorio) {
        if (relatorio == null) {
            throw new IllegalArgumentException("Relatório não pode ser nulo");
        }
        
        if (relatorio.getProfissional() == null) {
            throw new IllegalArgumentException("Profissional é obrigatório");
        }
        
        if (relatorio.getAluno() == null) {
            throw new IllegalArgumentException("Aluno é obrigatório");
        }
        
        if (relatorio.getData() == null) {
            throw new IllegalArgumentException("Data é obrigatória");
        }
        
        if (relatorio.getTipo() == null) {
            throw new IllegalArgumentException("Tipo de relatório é obrigatório");
        }
        
        if (!validarConteudo(relatorio.getConteudo())) {
            throw new IllegalArgumentException("Conteúdo deve ter pelo menos 100 caracteres");
        }
    }
    
    /**
     * Conta o total de relatórios
     * @return número total de relatórios
     */
    public int contarRelatorios() {
        try {
            return relatorioRepository.contarRelatorios();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao contar relatórios", e);
            throw new RuntimeException("Erro interno do sistema", e);
        }
    }
    
    /**
     * Conta relatórios por tipo
     * @return mapa com contagem por tipo
     */
    public Map<Relatorio.TipoRelatorio, Integer> contarPorTipo() {
        try {
            return relatorioRepository.contarPorTipo();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao contar relatórios por tipo", e);
            throw new RuntimeException("Erro interno do sistema", e);
        }
    }
}
