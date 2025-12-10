package com.pies.api.projeto.integrado.pies_backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pies.api.projeto.integrado.pies_backend.controller.dto.CreateRelatorioIndividualDTO;
import com.pies.api.projeto.integrado.pies_backend.controller.dto.RelatorioIndividualDTO;
import com.pies.api.projeto.integrado.pies_backend.model.Educando;
import com.pies.api.projeto.integrado.pies_backend.model.Professor;
import com.pies.api.projeto.integrado.pies_backend.model.RelatorioIndividual;
import com.pies.api.projeto.integrado.pies_backend.repository.EducandoRepository;
import com.pies.api.projeto.integrado.pies_backend.repository.ProfessorRepository;
import com.pies.api.projeto.integrado.pies_backend.repository.RelatorioIndividualRepository;

import lombok.RequiredArgsConstructor;

/**
 * Serviço responsável pela lógica de negócio relacionada aos Relatórios Individuais.
 * 
 * Esta classe implementa as operações CRUD (Create, Read, Update, Delete)
 * e realiza a conversão entre entidades (RelatorioIndividual) e DTOs (RelatorioIndividualDTO).
 * 
 * Utiliza injeção de dependência via constructor (@RequiredArgsConstructor)
 * e transações gerenciadas pelo Spring (@Transactional).
 */
@Service
@RequiredArgsConstructor
public class RelatorioIndividualService {

    private final RelatorioIndividualRepository relatorioRepository;
    private final EducandoRepository educandoRepository;
    private final ProfessorRepository professorRepository;

    /**
     * Lista todos os relatórios individuais cadastrados no sistema.
     * 
     * @Transactional(readOnly = true): Mantém a transação aberta durante toda a execução,
     * permitindo que o lazy loading do educando e professor funcione corretamente.
     * 
     * @return Lista de RelatorioIndividualDTO contendo todos os relatórios convertidos
     */
    @Transactional(readOnly = true)
    public List<RelatorioIndividualDTO> listarTodos() {
        return relatorioRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca um relatório específico pelo seu ID.
     * 
     * @Transactional(readOnly = true): Mantém a transação aberta durante toda a execução,
     * permitindo que o lazy loading do educando e professor funcione corretamente.
     * 
     * @param id Identificador único (UUID) do relatório
     * @return RelatorioIndividualDTO do relatório encontrado
     * @throws RuntimeException se o relatório não for encontrado com o ID fornecido
     */
    @Transactional(readOnly = true)
    public RelatorioIndividualDTO buscarPorId(String id) {
        return relatorioRepository.findByIdWithRelations(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Relatório não encontrado com ID: " + id));
    }

    /**
     * Salva um novo relatório individual no banco de dados.
     * 
     * @Transactional: Garante que a operação seja executada completamente ou revertida
     * em caso de erro (ACID - Atomicidade).
     * 
     * @param dto DTO contendo os dados do relatório a ser criado
     * @param userId ID do usuário (User) que está criando o relatório (obtido do contexto de segurança)
     * @return RelatorioIndividualDTO do relatório salvo (com ID gerado pelo JPA)
     * @throws RuntimeException se o educando não for encontrado ou se o professor não existir
     */
    @Transactional
    public RelatorioIndividualDTO salvar(CreateRelatorioIndividualDTO dto, String userId) {
        // Busca o educando
        Educando educando = educandoRepository.findById(dto.educandoId())
                .orElseThrow(() -> new RuntimeException("Educando não encontrado com ID: " + dto.educandoId()));

        // Busca o professor pelo userId
        Professor professor = professorRepository.findByUserId(userId);
        if (professor == null) {
            throw new RuntimeException("Professor não encontrado para o usuário com ID: " + userId);
        }

        // Cria a entidade RelatorioIndividual
        RelatorioIndividual relatorio = new RelatorioIndividual(educando, professor);
        relatorio.setDadosFuncionais(dto.dadosFuncionais());
        relatorio.setFuncionalidadeCognitiva(dto.funcionalidadeCognitiva());
        relatorio.setAlfabetizacaoLetramento(dto.alfabetizacaoLetramento());
        relatorio.setAdaptacoesCurriculares(dto.adaptacoesCurriculares());
        relatorio.setParticipacaoAtividades(dto.participacaoAtividades());
        relatorio.setAutonomia(dto.autonomia());
        relatorio.setInteracaoProfessora(dto.interacaoProfessora());
        relatorio.setAtividadesVidaDiaria(dto.atividadesVidaDiaria());

        // Salva o relatório no banco de dados
        RelatorioIndividual salvo = relatorioRepository.save(relatorio);
        return toDTO(salvo);
    }

    /**
     * Atualiza os dados de um relatório existente.
     * 
     * @Transactional: Garante consistência dos dados durante a atualização.
     * Se qualquer erro ocorrer, todas as alterações são revertidas.
     * 
     * @param id Identificador único (UUID) do relatório a ser atualizado
     * @param dto DTO contendo os novos dados do relatório
     * @return RelatorioIndividualDTO do relatório atualizado
     * @throws RuntimeException se o relatório não for encontrado
     */
    @Transactional
    public RelatorioIndividualDTO atualizar(String id, CreateRelatorioIndividualDTO dto) {
        // Busca o relatório existente
        RelatorioIndividual relatorio = relatorioRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new RuntimeException("Relatório não encontrado com ID: " + id));

        // Verifica se o educando foi alterado (se necessário)
        if (dto.educandoId() != null && !relatorio.getEducando().getId().equals(dto.educandoId())) {
            Educando educando = educandoRepository.findById(dto.educandoId())
                    .orElseThrow(() -> new RuntimeException("Educando não encontrado com ID: " + dto.educandoId()));
            relatorio.setEducando(educando);
        }

        // Atualiza os campos do relatório
        relatorio.setDadosFuncionais(dto.dadosFuncionais());
        relatorio.setFuncionalidadeCognitiva(dto.funcionalidadeCognitiva());
        relatorio.setAlfabetizacaoLetramento(dto.alfabetizacaoLetramento());
        relatorio.setAdaptacoesCurriculares(dto.adaptacoesCurriculares());
        relatorio.setParticipacaoAtividades(dto.participacaoAtividades());
        relatorio.setAutonomia(dto.autonomia());
        relatorio.setInteracaoProfessora(dto.interacaoProfessora());
        relatorio.setAtividadesVidaDiaria(dto.atividadesVidaDiaria());

        // Salva o relatório atualizado
        RelatorioIndividual salvo = relatorioRepository.save(relatorio);
        return toDTO(salvo);
    }

    /**
     * Remove um relatório do banco de dados.
     * 
     * @Transactional: Garante que a remoção seja executada completamente.
     * 
     * @param id Identificador único (UUID) do relatório a ser removido
     * @throws RuntimeException se o relatório não for encontrado com o ID fornecido
     */
    @Transactional
    public void deletar(String id) {
        RelatorioIndividual relatorio = relatorioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Relatório não encontrado com ID: " + id));
        relatorioRepository.delete(relatorio);
    }

    /**
     * Lista todos os relatórios de um educando específico.
     * 
     * @Transactional(readOnly = true): Mantém a transação aberta durante toda a execução.
     * 
     * @param educandoId ID do educando
     * @return Lista de RelatorioIndividualDTO dos relatórios do educando
     */
    @Transactional(readOnly = true)
    public List<RelatorioIndividualDTO> listarPorEducando(String educandoId) {
        return relatorioRepository.findByEducandoId(educandoId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Lista todos os relatórios criados por um professor específico.
     * 
     * @Transactional(readOnly = true): Mantém a transação aberta durante toda a execução.
     * 
     * @param professorId ID do professor
     * @return Lista de RelatorioIndividualDTO dos relatórios criados pelo professor
     */
    @Transactional(readOnly = true)
    public List<RelatorioIndividualDTO> listarPorProfessor(String professorId) {
        return relatorioRepository.findByProfessorId(professorId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca o relatório mais recente de um educando.
     * 
     * @Transactional(readOnly = true): Mantém a transação aberta durante toda a execução.
     * 
     * @param educandoId ID do educando
     * @return RelatorioIndividualDTO do relatório mais recente, ou null se não existir
     */
    @Transactional(readOnly = true)
    public RelatorioIndividualDTO buscarMaisRecentePorEducando(String educandoId) {
        return relatorioRepository.findMostRecentByEducandoId(educandoId)
                .map(this::toDTO)
                .orElse(null);
    }

    /**
     * Converte uma entidade RelatorioIndividual para um DTO RelatorioIndividualDTO.
     * Inclui a conversão do educando e professor se presentes.
     * 
     * @param r Entidade RelatorioIndividual a ser convertida
     * @return RelatorioIndividualDTO com os dados convertidos
     */
    private RelatorioIndividualDTO toDTO(RelatorioIndividual r) {
        System.out.println("=== toDTO RelatorioIndividual ===");
        System.out.println("ID do relatório: " + r.getId());
        System.out.println("DadosFuncionais: " + r.getDadosFuncionais());
        System.out.println("FuncionalidadeCognitiva: " + r.getFuncionalidadeCognitiva());
        System.out.println("AlfabetizacaoLetramento: " + r.getAlfabetizacaoLetramento());
        
        String educandoId = r.getEducando() != null ? r.getEducando().getId() : null;
        String educandoNome = r.getEducando() != null ? r.getEducando().getNome() : null;
        
        String professorId = r.getProfessor() != null ? r.getProfessor().getId() : null;
        String professorNome = r.getProfessor() != null ? r.getProfessor().getNome() : null;

        return new RelatorioIndividualDTO(
                r.getId(),
                educandoId,
                educandoNome,
                professorId,
                r.getEducando() != null ? r.getEducando().getDataNascimento() : null,
                r.getEducando() != null ? r.getEducando().getCid() : null,
                professorNome,
                r.getDataCriacao(),
                r.getDadosFuncionais(),
                r.getFuncionalidadeCognitiva(),
                r.getAlfabetizacaoLetramento(),
                r.getAdaptacoesCurriculares(),
                r.getParticipacaoAtividades(),
                r.getAutonomia(),
                r.getInteracaoProfessora(),
                r.getAtividadesVidaDiaria()
        );
    }

    /**
     * Busca todos os relatórios de um educando específico.
     * 
     * @Transactional(readOnly = true): Mantém a transação aberta durante toda a execução,
     * permitindo que o lazy loading do educando e professor funcione corretamente.
     * 
     * @param educandoId ID do educando
     * @return Lista de RelatorioIndividualDTO do educando
     */
    @Transactional(readOnly = true)
    public List<RelatorioIndividualDTO> buscarPorEducando(String educandoId) {
        return relatorioRepository.findByEducandoId(educandoId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}

