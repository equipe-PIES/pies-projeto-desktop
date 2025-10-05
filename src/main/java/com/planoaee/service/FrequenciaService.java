package com.planoaee.service;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.planoaee.model.Aluno;
import com.planoaee.model.Frequencia;
import com.planoaee.repository.FrequenciaRepository;

/**
 * Serviço para regras de negócio relacionadas à frequência
 */
public class FrequenciaService {
    
    private static final Logger logger = Logger.getLogger(FrequenciaService.class.getName());
    private final FrequenciaRepository frequenciaRepository;
    private final AlunoService alunoService;
    
    public FrequenciaService() {
        this.frequenciaRepository = new FrequenciaRepository();
        this.alunoService = new AlunoService();
    }
    
    /**
     * Registra frequência de um aluno
     * @param aluno aluno
     * @param data data da frequência
     * @param presente se estava presente
     * @param observacoes observações
     * @return frequência registrada
     * @throws IllegalArgumentException se dados inválidos
     * @throws RuntimeException se houver erro na operação
     */
    public Frequencia registrarFrequencia(Aluno aluno, LocalDate data, boolean presente, String observacoes) {
        try {
            if (aluno == null) {
                throw new IllegalArgumentException("Aluno não pode ser nulo");
            }
            
            if (data == null) {
                throw new IllegalArgumentException("Data não pode ser nula");
            }
            
            // Verifica se já existe frequência para esta data
            if (frequenciaRepository.existeFrequencia(aluno.getId(), data)) {
                throw new IllegalArgumentException("Já existe frequência registrada para este aluno nesta data");
            }
            
            // Cria e salva a frequência
            Frequencia frequencia = new Frequencia(aluno, data, presente);
            frequencia.setObservacoes(observacoes);
            frequencia.setDataRegistro(LocalDateTime.now());
            
            Frequencia frequenciaSalva = frequenciaRepository.salvar(frequencia);
            
            logger.info("Frequência registrada: " + aluno.getNome() + " - " + data + " - " + (presente ? "Presente" : "Faltou"));
            return frequenciaSalva;
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao registrar frequência", e);
            throw new RuntimeException("Erro interno do sistema", e);
        }
    }
    
    /**
     * Atualiza frequência existente
     * @param frequencia frequência a ser atualizada
     * @return true se atualizada com sucesso, false caso contrário
     */
    public boolean atualizarFrequencia(Frequencia frequencia) {
        try {
            boolean sucesso = frequenciaRepository.atualizar(frequencia);
            
            if (sucesso) {
                logger.info("Frequência atualizada: ID " + frequencia.getId());
            }
            
            return sucesso;
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao atualizar frequência", e);
            throw new RuntimeException("Erro interno do sistema", e);
        }
    }
    
    /**
     * Registra frequência em lote para uma data
     * @param data data da frequência
     * @param frequenciasMap mapa com aluno_id -> presente
     * @param observacoes observações gerais
     * @return lista de frequências registradas
     */
    public List<Frequencia> registrarFrequenciaLote(LocalDate data, Map<Integer, Boolean> frequenciasMap, String observacoes) {
        List<Frequencia> frequenciasRegistradas = new ArrayList<>();
        
        try {
            for (Map.Entry<Integer, Boolean> entry : frequenciasMap.entrySet()) {
                Integer alunoId = entry.getKey();
                Boolean presente = entry.getValue();
                
                // Busca o aluno
                Aluno aluno = alunoService.buscarPorId(alunoId);
                if (aluno != null) {
                    // Verifica se já existe frequência
                    if (!frequenciaRepository.existeFrequencia(alunoId, data)) {
                        Frequencia frequencia = new Frequencia(aluno, data, presente);
                        frequencia.setObservacoes(observacoes);
                        frequencia.setDataRegistro(LocalDateTime.now());
                        
                        Frequencia frequenciaSalva = frequenciaRepository.salvar(frequencia);
                        frequenciasRegistradas.add(frequenciaSalva);
                    }
                }
            }
            
            logger.info("Frequência em lote registrada: " + frequenciasRegistradas.size() + " alunos");
            return frequenciasRegistradas;
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao registrar frequência em lote", e);
            throw new RuntimeException("Erro interno do sistema", e);
        }
    }
    
    /**
     * Lista frequências de um aluno
     * @param alunoId ID do aluno
     * @return lista de frequências
     */
    public List<Frequencia> listarFrequenciasPorAluno(Integer alunoId) {
        try {
            return frequenciaRepository.buscarPorAluno(alunoId);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao listar frequências por aluno", e);
            throw new RuntimeException("Erro interno do sistema", e);
        }
    }
    
    /**
     * Lista frequências de uma data específica
     * @param data data a ser consultada
     * @return lista de frequências
     */
    public List<Frequencia> listarFrequenciasPorData(LocalDate data) {
        try {
            return frequenciaRepository.buscarPorData(data);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao listar frequências por data", e);
            throw new RuntimeException("Erro interno do sistema", e);
        }
    }
    
    /**
     * Lista frequências em um período
     * @param dataInicio data de início
     * @param dataFim data de fim
     * @return lista de frequências
     */
    public List<Frequencia> listarFrequenciasPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        try {
            return frequenciaRepository.buscarPorPeriodo(dataInicio, dataFim);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao listar frequências por período", e);
            throw new RuntimeException("Erro interno do sistema", e);
        }
    }
    
    /**
     * Lista frequências de um aluno em um período
     * @param alunoId ID do aluno
     * @param dataInicio data de início
     * @param dataFim data de fim
     * @return lista de frequências
     */
    public List<Frequencia> listarFrequenciasPorAlunoEPeriodo(Integer alunoId, LocalDate dataInicio, LocalDate dataFim) {
        try {
            return frequenciaRepository.buscarPorAlunoEPeriodo(alunoId, dataInicio, dataFim);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao listar frequências por aluno e período", e);
            throw new RuntimeException("Erro interno do sistema", e);
        }
    }
    
    /**
     * Calcula estatísticas de frequência de um aluno
     * @param alunoId ID do aluno
     * @param dataInicio data de início
     * @param dataFim data de fim
     * @return mapa com estatísticas
     */
    public Map<String, Object> calcularEstatisticas(Integer alunoId, LocalDate dataInicio, LocalDate dataFim) {
        try {
            double[] stats = frequenciaRepository.calcularEstatisticas(alunoId, dataInicio, dataFim);
            
            Map<String, Object> estatisticas = new HashMap<>();
            estatisticas.put("totalDias", (int) stats[0]);
            estatisticas.put("diasPresentes", (int) stats[1]);
            estatisticas.put("diasFaltas", (int) stats[2]);
            estatisticas.put("percentualPresenca", stats[3]);
            
            return estatisticas;
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao calcular estatísticas", e);
            throw new RuntimeException("Erro interno do sistema", e);
        }
    }
    
    /**
     * Gera relatório mensal de frequência
     * @param mes mês (1-12)
     * @param ano ano
     * @return relatório formatado
     */
    public String gerarRelatorioMensal(int mes, int ano) {
        try {
            LocalDate dataInicio = LocalDate.of(ano, mes, 1);
            LocalDate dataFim = dataInicio.withDayOfMonth(dataInicio.lengthOfMonth());
            
            List<Frequencia> frequencias = listarFrequenciasPorPeriodo(dataInicio, dataFim);
            List<Aluno> alunos = alunoService.listarTodos();
            
            StringBuilder relatorio = new StringBuilder();
            relatorio.append("RELATÓRIO DE FREQUÊNCIA MENSAL - APAPEQ\n");
            relatorio.append("==========================================\n\n");
            relatorio.append("Período: ").append(dataInicio).append(" a ").append(dataFim).append("\n\n");
            
            // Estatísticas gerais
            int totalRegistros = frequencias.size();
            long totalPresentes = frequencias.stream().mapToLong(f -> f.isPresente() ? 1 : 0).sum();
            long totalFaltas = totalRegistros - totalPresentes;
            double percentualGeral = totalRegistros > 0 ? (totalPresentes * 100.0 / totalRegistros) : 0;
            
            relatorio.append("ESTATÍSTICAS GERAIS:\n");
            relatorio.append("Total de registros: ").append(totalRegistros).append("\n");
            relatorio.append("Total de presenças: ").append(totalPresentes).append("\n");
            relatorio.append("Total de faltas: ").append(totalFaltas).append("\n");
            relatorio.append("Percentual geral de presença: ").append(String.format("%.1f", percentualGeral)).append("%\n\n");
            
            // Estatísticas por aluno
            relatorio.append("ESTATÍSTICAS POR ALUNO:\n");
            relatorio.append("========================\n\n");
            
            for (Aluno aluno : alunos) {
                Map<String, Object> stats = calcularEstatisticas(aluno.getId(), dataInicio, dataFim);
                int totalDias = (int) stats.get("totalDias");
                
                if (totalDias > 0) {
                    int diasPresentes = (int) stats.get("diasPresentes");
                    int diasFaltas = (int) stats.get("diasFaltas");
                    double percentual = (double) stats.get("percentualPresenca");
                    
                    relatorio.append("• ").append(aluno.getNome()).append("\n");
                    relatorio.append("  Total de dias: ").append(totalDias).append("\n");
                    relatorio.append("  Dias presentes: ").append(diasPresentes).append("\n");
                    relatorio.append("  Dias faltas: ").append(diasFaltas).append("\n");
                    relatorio.append("  Percentual: ").append(String.format("%.1f", percentual)).append("%\n\n");
                }
            }
            
            return relatorio.toString();
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao gerar relatório mensal", e);
            return "Erro ao gerar relatório mensal";
        }
    }
    
    /**
     * Lista alunos com frequência baixa
     * @param percentualMinimo percentual mínimo de presença
     * @param dataInicio data de início
     * @param dataFim data de fim
     * @return lista de alunos com frequência baixa
     */
    public List<Object[]> listarAlunosComFrequenciaBaixa(double percentualMinimo, LocalDate dataInicio, LocalDate dataFim) {
        try {
            return frequenciaRepository.listarAlunosComFrequenciaBaixa(percentualMinimo, dataInicio, dataFim);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao listar alunos com frequência baixa", e);
            throw new RuntimeException("Erro interno do sistema", e);
        }
    }
    
    /**
     * Verifica se já existe frequência para um aluno em uma data
     * @param alunoId ID do aluno
     * @param data data a ser verificada
     * @return true se já existe, false caso contrário
     */
    public boolean existeFrequencia(Integer alunoId, LocalDate data) {
        try {
            return frequenciaRepository.existeFrequencia(alunoId, data);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao verificar existência de frequência", e);
            return false;
        }
    }
    
    /**
     * Gera relatório de frequência semestral
     * @param semestre semestre (1 ou 2)
     * @param ano ano
     * @return relatório formatado
     */
    public String gerarRelatorioSemestral(int semestre, int ano) {
        try {
            LocalDate dataInicio, dataFim;
            
            if (semestre == 1) {
                dataInicio = LocalDate.of(ano, 2, 1); // Fevereiro
                dataFim = LocalDate.of(ano, 7, 31);   // Julho
            } else {
                dataInicio = LocalDate.of(ano, 8, 1); // Agosto
                dataFim = LocalDate.of(ano, 12, 31);  // Dezembro
            }
            
            List<Frequencia> frequencias = listarFrequenciasPorPeriodo(dataInicio, dataFim);
            List<Aluno> alunos = alunoService.listarTodos();
            
            StringBuilder relatorio = new StringBuilder();
            relatorio.append("RELATÓRIO DE FREQUÊNCIA SEMESTRAL - APAPEQ\n");
            relatorio.append("=============================================\n\n");
            relatorio.append("Semestre: ").append(semestre).append("º/").append(ano).append("\n");
            relatorio.append("Período: ").append(dataInicio).append(" a ").append(dataFim).append("\n\n");
            
            // Estatísticas gerais
            int totalRegistros = frequencias.size();
            long totalPresentes = frequencias.stream().mapToLong(f -> f.isPresente() ? 1 : 0).sum();
            long totalFaltas = totalRegistros - totalPresentes;
            double percentualGeral = totalRegistros > 0 ? (totalPresentes * 100.0 / totalRegistros) : 0;
            
            relatorio.append("ESTATÍSTICAS GERAIS:\n");
            relatorio.append("Total de registros: ").append(totalRegistros).append("\n");
            relatorio.append("Total de presenças: ").append(totalPresentes).append("\n");
            relatorio.append("Total de faltas: ").append(totalFaltas).append("\n");
            relatorio.append("Percentual geral de presença: ").append(String.format("%.1f", percentualGeral)).append("%\n\n");
            
            // Análise por aluno
            relatorio.append("ANÁLISE POR ALUNO:\n");
            relatorio.append("==================\n\n");
            
            for (Aluno aluno : alunos) {
                Map<String, Object> stats = calcularEstatisticas(aluno.getId(), dataInicio, dataFim);
                int totalDias = (int) stats.get("totalDias");
                
                if (totalDias > 0) {
                    int diasPresentes = (int) stats.get("diasPresentes");
                    double percentual = (double) stats.get("percentualPresenca");
                    
                    relatorio.append("• ").append(aluno.getNome()).append("\n");
                    relatorio.append("  Frequência: ").append(String.format("%.1f", percentual)).append("%\n");
                    
                    if (percentual >= 80) {
                        relatorio.append("  Status: Excelente frequência\n");
                    } else if (percentual >= 60) {
                        relatorio.append("  Status: Boa frequência\n");
                    } else if (percentual >= 40) {
                        relatorio.append("  Status: Frequência regular\n");
                    } else {
                        relatorio.append("  Status: Frequência baixa - ATENÇÃO\n");
                    }
                    
                    relatorio.append("\n");
                }
            }
            
            return relatorio.toString();
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao gerar relatório semestral", e);
            return "Erro ao gerar relatório semestral";
        }
    }
}
