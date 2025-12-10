package com.pies.api.projeto.integrado.pies_backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pies.api.projeto.integrado.pies_backend.controller.dto.CreatePAEEDTO;
import com.pies.api.projeto.integrado.pies_backend.controller.dto.PAEEDTO;
import com.pies.api.projeto.integrado.pies_backend.exception.EducandoNotFoundException;
import com.pies.api.projeto.integrado.pies_backend.exception.PAEENotFoundException;
import com.pies.api.projeto.integrado.pies_backend.exception.ProfessorNotFoundException;
import com.pies.api.projeto.integrado.pies_backend.model.Educando;
import com.pies.api.projeto.integrado.pies_backend.model.Professor;
import com.pies.api.projeto.integrado.pies_backend.model.PAEE;
import com.pies.api.projeto.integrado.pies_backend.repository.EducandoRepository;
import com.pies.api.projeto.integrado.pies_backend.repository.PAEERepository;
import com.pies.api.projeto.integrado.pies_backend.repository.ProfessorRepository;

import lombok.RequiredArgsConstructor;

/**
 * Serviço responsável pela lógica de negócio relacionada aos PAEEs.
 * 
 * Esta classe implementa as operações CRUD (Create, Read, Update, Delete)
 * e realiza a conversão entre entidades (PAEE) e DTOs (PAEEDTO).
 * 
 * Utiliza injeção de dependência via constructor (@RequiredArgsConstructor)
 * e transações gerenciadas pelo Spring (@Transactional).
 */
@Service
@RequiredArgsConstructor
public class PAEEService {

    /**
     * Repositório para acesso aos dados dos PAEEs no banco de dados.
     * Injetado automaticamente pelo Spring através do construtor gerado pelo Lombok.
     */
    private final PAEERepository paeeRepository;

    /**
     * Repositório para acesso aos dados dos educandos no banco de dados.
     * Necessário para validar e associar o educando ao PAEE.
     */
    private final EducandoRepository educandoRepository;

    /**
     * Repositório para acesso aos dados dos professores no banco de dados.
     * Necessário para validar e associar o professor responsável ao PAEE.
     */
    private final ProfessorRepository professorRepository;

    /**
     * Lista todos os PAEEs cadastrados no sistema.
     * 
     * @Transactional(readOnly = true): Mantém a transação aberta durante toda a execução,
     * permitindo que o lazy loading do educando funcione corretamente. O readOnly = true
     * otimiza a consulta indicando que não haverá alterações no banco.
     * 
     * @return Lista de PAEEDTO contendo todos os PAEEs convertidos da entidade para DTO
     */
    @Transactional(readOnly = true)
    public List<PAEEDTO> listarTodos() {
        return paeeRepository.findAllWithEducando().stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    /**
     * Busca um PAEE específico pelo seu ID.
     * 
     * @Transactional(readOnly = true): Mantém a transação aberta durante toda a execução,
     * permitindo que o lazy loading do educando funcione corretamente quando acessado
     * no método toDTO(). O readOnly = true otimiza a consulta.
     * 
     * @param id Identificador único (UUID) do PAEE
     * @return PAEEDTO do PAEE encontrado
     * @throws PAEENotFoundException se o PAEE não for encontrado com o ID fornecido
     */
    @Transactional(readOnly = true)
    public PAEEDTO buscarPorId(String id) {
        return paeeRepository.findByIdWithEducando(id)
            .map(this::toDTO)
            .orElseThrow(() -> new PAEENotFoundException(id));
    }

    /**
     * Busca todos os PAEEs de um educando específico.
     * 
     * @Transactional(readOnly = true): Mantém a transação aberta durante toda a execução.
     * 
     * @param educandoId ID do educando
     * @return Lista de PAEEDTO do educando
     */
    @Transactional(readOnly = true)
    public List<PAEEDTO> buscarPorEducandoId(String educandoId) {
        return paeeRepository.findByEducandoId(educandoId).stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    /**
     * Salva um novo PAEE no banco de dados.
     * 
     * @Transactional: Garante que a operação seja executada completamente ou revertida
     * em caso de erro (ACID - Atomicidade). Todas as operações dentro deste método
     * fazem parte de uma única transação.
     * 
     * @param dto DTO contendo os dados do PAEE a ser criado
     * @return PAEEDTO do PAEE salvo (com ID gerado pelo JPA)
     * @throws EducandoNotFoundException se o educando não for encontrado
     */
    @Transactional
    public PAEEDTO salvar(CreatePAEEDTO dto) {
        // Busca o educando no banco de dados
        Educando educando = educandoRepository.findById(dto.educandoId())
            .orElseThrow(() -> new EducandoNotFoundException(dto.educandoId()));

        Professor professor = professorRepository.findById(dto.professorId())
            .orElseThrow(() -> new ProfessorNotFoundException(dto.professorId()));
        
        // Converte o DTO para entidade
        PAEE entity = toEntity(dto, educando, professor);
        
        // Salva a entidade no banco de dados
        PAEE salvo = paeeRepository.save(entity);
        
        // Converte a entidade salva de volta para DTO para retornar ao controller
        return toDTO(salvo);
    }

    /**
     * Atualiza os dados de um PAEE existente.
     * 
     * @Transactional: Garante consistência dos dados durante a atualização.
     * Se qualquer erro ocorrer, todas as alterações são revertidas.
     * 
     * @param id Identificador único (UUID) do PAEE a ser atualizado
     * @param dto DTO contendo os novos dados do PAEE
     * @return PAEEDTO do PAEE atualizado
     * @throws PAEENotFoundException se o PAEE não for encontrado com o ID fornecido
     * @throws EducandoNotFoundException se o educando não for encontrado
     */
    @Transactional
    public PAEEDTO atualizar(String id, CreatePAEEDTO dto) {
        // Busca o PAEE existente no banco de dados
        PAEE entity = paeeRepository.findById(id)
            .orElseThrow(() -> new PAEENotFoundException(id));
        
        // Busca o educando (pode ter sido alterado)
        Educando educando = educandoRepository.findById(dto.educandoId())
            .orElseThrow(() -> new EducandoNotFoundException(dto.educandoId()));

        Professor professor = professorRepository.findById(dto.professorId())
            .orElseThrow(() -> new ProfessorNotFoundException(dto.professorId()));
        
        // Atualiza os campos da entidade
        entity.setResumoCaso(dto.resumoCaso());
        entity.setDificuldadesMotoresPsicomotores(dto.dificuldadesMotoresPsicomotores());
        entity.setDificuldadesCognitivo(dto.dificuldadesCognitivo());
        entity.setDificuldadesSensorial(dto.dificuldadesSensorial());
        entity.setDificuldadesLinguagemComunicacao(dto.dificuldadesLinguagemComunicacao());
        entity.setDificuldadesFamiliar(dto.dificuldadesFamiliar());
        entity.setDificuldadesAfetivoInterpessoais(dto.dificuldadesAfetivoInterpessoais());
        entity.setDificuldadesRaciocinioLogicoMatematico(dto.dificuldadesRaciocinioLogicoMatematico());
        entity.setDificuldadesAVAs(dto.dificuldadesAVAs());
        entity.setDesenvolvimentoMotoresPsicomotoresDificuldades(dto.desenvolvimentoMotoresPsicomotoresDificuldades());
        entity.setDesenvolvimentoMotoresPsicomotoresIntervencoes(dto.desenvolvimentoMotoresPsicomotoresIntervencoes());
        entity.setComunicacaoLinguagemDificuldades(dto.comunicacaoLinguagemDificuldades());
        entity.setComunicacaoLinguagemIntervencoes(dto.comunicacaoLinguagemIntervencoes());
        entity.setDificuldadesRaciocinio(dto.dificuldadesRaciocinio());
        entity.setIntervencoesRaciocinio(dto.intervencoesRaciocinio());
        entity.setDificuldadesAtencao(dto.dificuldadesAtencao());
        entity.setIntervencoesAtencao(dto.intervencoesAtencao());
        entity.setDificuldadesMemoria(dto.dificuldadesMemoria());
        entity.setIntervencoesMemoria(dto.intervencoesMemoria());
        entity.setDificuldadesPercepcao(dto.dificuldadesPercepcao());
        entity.setIntervencoesPercepcao(dto.intervencoesPercepcao());
        entity.setDificuldadesSociabilidade(dto.dificuldadesSociabilidade());
        entity.setIntervencoesSociabilidade(dto.intervencoesSociabilidade());
        entity.setDificuldadesAVA(dto.dificuldadesAVA());
        entity.setIntervencoesAVA(dto.intervencoesAVA());
        entity.setObjetivosAEE(dto.objetivosAEE());
        entity.setEnvAEE(dto.envAEE());
        entity.setEnvPsicologo(dto.envPsicologo());
        entity.setEnvFisioterapeuta(dto.envFisioterapeuta());
        entity.setEnvPsicopedagogo(dto.envPsicopedagogo());
        entity.setEnvTO(dto.envTO());
        entity.setEnvEducacaoFisica(dto.envEducacaoFisica());
        entity.setEnvEstimulacaoPrecoce(dto.envEstimulacaoPrecoce());
        entity.setEducando(educando);
        entity.setProfessor(professor);
        
        // Salva a entidade atualizada e converte para DTO antes de retornar
        return toDTO(paeeRepository.save(entity));
    }

    /**
     * Remove um PAEE do banco de dados.
     * 
     * @Transactional: Garante que a remoção seja executada completamente.
     * Se qualquer erro ocorrer, a operação é revertida.
     * 
     * @param id Identificador único (UUID) do PAEE a ser removido
     * @throws PAEENotFoundException se o PAEE não for encontrado com o ID fornecido
     */
    @Transactional
    public void deletar(String id) {
        // Busca o PAEE no banco de dados
        PAEE paee = paeeRepository.findById(id)
            .orElseThrow(() -> new PAEENotFoundException(id));
        
        // Remove o PAEE do banco de dados
        paeeRepository.delete(paee);
    }

    /**
     * Converte uma entidade PAEE para um DTO PAEEDTO.
     * Inclui informações do educando relacionado.
     * 
     * Este método realiza o mapeamento de dados da camada de persistência
     * para a camada de apresentação, expondo apenas os dados necessários
     * para a API REST.
     * 
     * @param p Entidade PAEE a ser convertida
     * @return PAEEDTO com os dados convertidos
     */
    private PAEEDTO toDTO(PAEE p) {
        PAEEDTO dto = new PAEEDTO();
        
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
     * Converte um DTO CreatePAEEDTO para uma entidade PAEE.
     * 
     * Este método realiza o mapeamento de dados da camada de apresentação
     * para a camada de persistência, preparando os dados para serem salvos
     * no banco de dados.
     * 
     * @param dto CreatePAEEDTO a ser convertido
     * @param educando Educando relacionado ao PAEE
     * @return Entidade PAEE com os dados convertidos
     */
    private PAEE toEntity(CreatePAEEDTO dto, Educando educando, Professor professor) {
        PAEE p = new PAEE();
        
        p.setResumoCaso(dto.resumoCaso());
        p.setDificuldadesMotoresPsicomotores(dto.dificuldadesMotoresPsicomotores());
        p.setDificuldadesCognitivo(dto.dificuldadesCognitivo());
        p.setDificuldadesSensorial(dto.dificuldadesSensorial());
        p.setDificuldadesLinguagemComunicacao(dto.dificuldadesLinguagemComunicacao());
        p.setDificuldadesFamiliar(dto.dificuldadesFamiliar());
        p.setDificuldadesAfetivoInterpessoais(dto.dificuldadesAfetivoInterpessoais());
        p.setDificuldadesRaciocinioLogicoMatematico(dto.dificuldadesRaciocinioLogicoMatematico());
        p.setDificuldadesAVAs(dto.dificuldadesAVAs());
        p.setDesenvolvimentoMotoresPsicomotoresDificuldades(dto.desenvolvimentoMotoresPsicomotoresDificuldades());
        p.setDesenvolvimentoMotoresPsicomotoresIntervencoes(dto.desenvolvimentoMotoresPsicomotoresIntervencoes());
        p.setComunicacaoLinguagemDificuldades(dto.comunicacaoLinguagemDificuldades());
        p.setComunicacaoLinguagemIntervencoes(dto.comunicacaoLinguagemIntervencoes());
        p.setDificuldadesRaciocinio(dto.dificuldadesRaciocinio());
        p.setIntervencoesRaciocinio(dto.intervencoesRaciocinio());
        p.setDificuldadesAtencao(dto.dificuldadesAtencao());
        p.setIntervencoesAtencao(dto.intervencoesAtencao());
        p.setDificuldadesMemoria(dto.dificuldadesMemoria());
        p.setIntervencoesMemoria(dto.intervencoesMemoria());
        p.setDificuldadesPercepcao(dto.dificuldadesPercepcao());
        p.setIntervencoesPercepcao(dto.intervencoesPercepcao());
        p.setDificuldadesSociabilidade(dto.dificuldadesSociabilidade());
        p.setIntervencoesSociabilidade(dto.intervencoesSociabilidade());
        p.setDificuldadesAVA(dto.dificuldadesAVA());
        p.setIntervencoesAVA(dto.intervencoesAVA());
        p.setObjetivosAEE(dto.objetivosAEE());
        p.setEnvAEE(dto.envAEE());
        p.setEnvPsicologo(dto.envPsicologo());
        p.setEnvFisioterapeuta(dto.envFisioterapeuta());
        p.setEnvPsicopedagogo(dto.envPsicopedagogo());
        p.setEnvTO(dto.envTO());
        p.setEnvEducacaoFisica(dto.envEducacaoFisica());
        p.setEnvEstimulacaoPrecoce(dto.envEstimulacaoPrecoce());
        p.setEducando(educando);
        p.setProfessor(professor);
        
        return p;
    }
}

