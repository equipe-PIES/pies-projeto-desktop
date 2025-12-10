package com.pies.api.projeto.integrado.pies_backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pies.api.projeto.integrado.pies_backend.controller.dto.CreatePDIDTO;
import com.pies.api.projeto.integrado.pies_backend.controller.dto.PDIDTO;
import com.pies.api.projeto.integrado.pies_backend.exception.EducandoNotFoundException;
import com.pies.api.projeto.integrado.pies_backend.exception.PDINotFoundException;
import com.pies.api.projeto.integrado.pies_backend.exception.ProfessorNotFoundException;
import com.pies.api.projeto.integrado.pies_backend.model.Educando;
import com.pies.api.projeto.integrado.pies_backend.model.Professor;
import com.pies.api.projeto.integrado.pies_backend.model.PDI;
import com.pies.api.projeto.integrado.pies_backend.repository.EducandoRepository;
import com.pies.api.projeto.integrado.pies_backend.repository.PDIRepository;
import com.pies.api.projeto.integrado.pies_backend.repository.ProfessorRepository;

import lombok.RequiredArgsConstructor;

/**
 * Serviço responsável pela lógica de negócio relacionada aos PDIs.
 * 
 * Esta classe implementa as operações CRUD (Create, Read, Update, Delete)
 * e realiza a conversão entre entidades (PDI) e DTOs (PDIDTO).
 * 
 * Utiliza injeção de dependência via constructor (@RequiredArgsConstructor)
 * e transações gerenciadas pelo Spring (@Transactional).
 */
@Service
@RequiredArgsConstructor
public class PDIService {

    /**
     * Repositório para acesso aos dados dos PDIs no banco de dados.
     * Injetado automaticamente pelo Spring através do construtor gerado pelo Lombok.
     */
    private final PDIRepository pdiRepository;

    /**
     * Repositório para acesso aos dados dos educandos no banco de dados.
     * Necessário para validar e associar o educando ao PDI.
     */
    private final EducandoRepository educandoRepository;

    /**
     * Repositório para acesso aos dados dos professores no banco de dados.
     * Necessário para validar e associar o professor responsável ao PDI.
     */
    private final ProfessorRepository professorRepository;

    /**
     * Lista todos os PDIs cadastrados no sistema.
     * 
     * @Transactional(readOnly = true): Mantém a transação aberta durante toda a execução,
     * permitindo que o lazy loading do educando funcione corretamente. O readOnly = true
     * otimiza a consulta indicando que não haverá alterações no banco.
     * 
     * @return Lista de PDIDTO contendo todos os PDIs convertidos da entidade para DTO
     */
    @Transactional(readOnly = true)
    public List<PDIDTO> listarTodos() {
        return pdiRepository.findAllWithEducando().stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    /**
     * Busca um PDI específico pelo seu ID.
     * 
     * @Transactional(readOnly = true): Mantém a transação aberta durante toda a execução,
     * permitindo que o lazy loading do educando funcione corretamente quando acessado
     * no método toDTO(). O readOnly = true otimiza a consulta.
     * 
     * @param id Identificador único (UUID) do PDI
     * @return PDIDTO do PDI encontrado
     * @throws PDINotFoundException se o PDI não for encontrado com o ID fornecido
     */
    @Transactional(readOnly = true)
    public PDIDTO buscarPorId(String id) {
        return pdiRepository.findByIdWithEducando(id)
            .map(this::toDTO)
            .orElseThrow(() -> new PDINotFoundException(id));
    }

    /**
     * Busca todos os PDIs de um educando específico.
     * 
     * @Transactional(readOnly = true): Mantém a transação aberta durante toda a execução.
     * 
     * @param educandoId ID do educando
     * @return Lista de PDIDTO do educando
     */
    @Transactional(readOnly = true)
    public List<PDIDTO> buscarPorEducandoId(String educandoId) {
        return pdiRepository.findByEducandoId(educandoId).stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    /**
     * Salva um novo PDI no banco de dados.
     * 
     * @Transactional: Garante que a operação seja executada completamente ou revertida
     * em caso de erro (ACID - Atomicidade). Todas as operações dentro deste método
     * fazem parte de uma única transação.
     * 
     * @param dto DTO contendo os dados do PDI a ser criado
     * @return PDIDTO do PDI salvo (com ID gerado pelo JPA)
     * @throws EducandoNotFoundException se o educando não for encontrado
     */
    @Transactional
    public PDIDTO salvar(CreatePDIDTO dto) {
        // Busca o educando no banco de dados
        Educando educando = educandoRepository.findById(dto.educandoId())
            .orElseThrow(() -> new EducandoNotFoundException(dto.educandoId()));

        Professor professor = professorRepository.findById(dto.professorId())
            .orElseThrow(() -> new ProfessorNotFoundException(dto.professorId()));
        
        // Converte o DTO para entidade
        PDI entity = toEntity(dto, educando, professor);
        
        // Salva a entidade no banco de dados
        PDI salvo = pdiRepository.save(entity);
        
        // Converte a entidade salva de volta para DTO para retornar ao controller
        return toDTO(salvo);
    }

    /**
     * Atualiza os dados de um PDI existente.
     * 
     * @Transactional: Garante consistência dos dados durante a atualização.
     * Se qualquer erro ocorrer, todas as alterações são revertidas.
     * 
     * @param id Identificador único (UUID) do PDI a ser atualizado
     * @param dto DTO contendo os novos dados do PDI
     * @return PDIDTO do PDI atualizado
     * @throws PDINotFoundException se o PDI não for encontrado com o ID fornecido
     * @throws EducandoNotFoundException se o educando não for encontrado
     */
    @Transactional
    public PDIDTO atualizar(String id, CreatePDIDTO dto) {
        // Busca o PDI existente no banco de dados
        PDI entity = pdiRepository.findById(id)
            .orElseThrow(() -> new PDINotFoundException(id));
        
        // Busca o educando (pode ter sido alterado)
        Educando educando = educandoRepository.findById(dto.educandoId())
            .orElseThrow(() -> new EducandoNotFoundException(dto.educandoId()));

        Professor professor = professorRepository.findById(dto.professorId())
            .orElseThrow(() -> new ProfessorNotFoundException(dto.professorId()));
        
        // Atualiza os campos da entidade
        entity.setPeriodoPlanoAEE(dto.periodoPlanoAEE());
        entity.setHorarioTempoAtendimento(dto.horarioTempoAtendimento());
        entity.setFrequenciaAtendimento(dto.frequenciaAtendimento());
        entity.setDiasSemana(dto.diasSemana());
        entity.setComposicaoAtendimento(dto.composicaoAtendimento());
        entity.setObjetivosPlano(dto.objetivosPlano());
        entity.setPotencialidades(dto.potencialidades());
        entity.setNecessidadesEducacionaisEspeciais(dto.necessidadesEducacionaisEspeciais());
        entity.setHabilidades(dto.habilidades());
        entity.setAtividadesASeremDesenvolvidas(dto.atividadesASeremDesenvolvidas());
        entity.setRecursosMateriais(dto.recursosMateriais());
        entity.setRecursosQueNecessitamAdequacao(dto.recursosQueNecessitamAdequacao());
        entity.setRecursosMateriaisASeremProduzidos(dto.recursosMateriaisASeremProduzidos());
        entity.setParceriasNecessarias(dto.parceriasNecessarias());
        entity.setEducando(educando);
        entity.setProfessor(professor);
        
        // Salva a entidade atualizada e converte para DTO antes de retornar
        return toDTO(pdiRepository.save(entity));
    }

    /**
     * Remove um PDI do banco de dados.
     * 
     * @Transactional: Garante que a remoção seja executada completamente.
     * Se qualquer erro ocorrer, a operação é revertida.
     * 
     * @param id Identificador único (UUID) do PDI a ser removido
     * @throws PDINotFoundException se o PDI não for encontrado com o ID fornecido
     */
    @Transactional
    public void deletar(String id) {
        // Busca o PDI no banco de dados
        PDI pdi = pdiRepository.findById(id)
            .orElseThrow(() -> new PDINotFoundException(id));
        
        // Remove o PDI do banco de dados
        pdiRepository.delete(pdi);
    }

    /**
     * Converte uma entidade PDI para um DTO PDIDTO.
     * Inclui informações do educando relacionado.
     * 
     * Este método realiza o mapeamento de dados da camada de persistência
     * para a camada de apresentação, expondo apenas os dados necessários
     * para a API REST.
     * 
     * @param p Entidade PDI a ser convertida
     * @return PDIDTO com os dados convertidos
     */
    private PDIDTO toDTO(PDI p) {
        PDIDTO dto = new PDIDTO();
        
        // Copia propriedades básicas da entidade para o DTO
        BeanUtils.copyProperties(p, dto, "educando", "professor");
        
        // Adiciona informações do educando se existir
        if (p.getEducando() != null) {
            dto.setEducandoId(p.getEducando().getId());
            dto.setEducandoNome(p.getEducando().getNome());
        }

        if (p.getProfessor() != null) {
            dto.setProfessorId(p.getProfessor().getId());
            dto.setProfessorNome(p.getProfessor().getNome());
        }
        
        return dto;
    }

    /**
     * Converte um DTO CreatePDIDTO para uma entidade PDI.
     * 
     * Este método realiza o mapeamento de dados da camada de apresentação
     * para a camada de persistência, preparando os dados para serem salvos
     * no banco de dados.
     * 
     * @param dto CreatePDIDTO a ser convertido
     * @param educando Educando relacionado ao PDI
     * @return Entidade PDI com os dados convertidos
     */
    private PDI toEntity(CreatePDIDTO dto, Educando educando, Professor professor) {
        PDI p = new PDI();
        
        p.setPeriodoPlanoAEE(dto.periodoPlanoAEE());
        p.setHorarioTempoAtendimento(dto.horarioTempoAtendimento());
        p.setFrequenciaAtendimento(dto.frequenciaAtendimento());
        p.setDiasSemana(dto.diasSemana());
        p.setComposicaoAtendimento(dto.composicaoAtendimento());
        p.setObjetivosPlano(dto.objetivosPlano());
        p.setPotencialidades(dto.potencialidades());
        p.setNecessidadesEducacionaisEspeciais(dto.necessidadesEducacionaisEspeciais());
        p.setHabilidades(dto.habilidades());
        p.setAtividadesASeremDesenvolvidas(dto.atividadesASeremDesenvolvidas());
        p.setRecursosMateriais(dto.recursosMateriais());
        p.setRecursosQueNecessitamAdequacao(dto.recursosQueNecessitamAdequacao());
        p.setRecursosMateriaisASeremProduzidos(dto.recursosMateriaisASeremProduzidos());
        p.setParceriasNecessarias(dto.parceriasNecessarias());
        p.setEducando(educando);
        p.setProfessor(professor);
        
        return p;
    }
}

