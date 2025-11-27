package com.pies.api.projeto.integrado.pies_backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pies.api.projeto.integrado.pies_backend.controller.dto.CreateTurmaDTO;
import com.pies.api.projeto.integrado.pies_backend.controller.dto.TurmaDTO;
import com.pies.api.projeto.integrado.pies_backend.model.Professor;
import com.pies.api.projeto.integrado.pies_backend.model.Turma;
import com.pies.api.projeto.integrado.pies_backend.repository.ProfessorRepository;
import com.pies.api.projeto.integrado.pies_backend.repository.TurmaRepository;

import lombok.RequiredArgsConstructor;

/**
 * Serviço responsável pela lógica de negócio relacionada às Turmas.
 * 
 * Esta classe implementa as operações CRUD (Create, Read, Update, Delete)
 * e realiza a conversão entre entidades (Turma) e DTOs (TurmaDTO).
 * 
 * Utiliza injeção de dependência via constructor (@RequiredArgsConstructor)
 * e transações gerenciadas pelo Spring (@Transactional).
 */
@Service
@RequiredArgsConstructor
public class TurmaService {

    private final TurmaRepository turmaRepository;
    private final ProfessorRepository professorRepository;

    /**
     * Lista todas as turmas cadastradas no sistema.
     * 
     * @Transactional(readOnly = true): Mantém a transação aberta durante toda a execução,
     * permitindo que o lazy loading do professor e educandos funcione corretamente.
     * 
     * @return Lista de TurmaDTO contendo todas as turmas convertidas da entidade para DTO
     */
    @Transactional(readOnly = true)
    public List<TurmaDTO> listarTodas() {
        return turmaRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca uma turma específica pelo seu ID.
     * 
     * @Transactional(readOnly = true): Mantém a transação aberta durante toda a execução,
     * permitindo que o lazy loading do professor e educandos funcione corretamente.
     * 
     * @param id Identificador único (UUID) da turma
     * @return TurmaDTO da turma encontrada
     * @throws RuntimeException se a turma não for encontrada com o ID fornecido
     */
    @Transactional(readOnly = true)
    public TurmaDTO buscarPorId(String id) {
        return turmaRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Turma não encontrada com ID: " + id));
    }

    /**
     * Salva uma nova turma no banco de dados.
     * 
     * @Transactional: Garante que a operação seja executada completamente ou revertida
     * em caso de erro (ACID - Atomicidade).
     * 
     * @param dto DTO contendo os dados da turma a ser criada
     * @return TurmaDTO da turma salva (com ID gerado pelo JPA)
     * @throws RuntimeException se o professor não for encontrado ou se algum educando não existir
     */
    @Transactional
    public TurmaDTO salvar(CreateTurmaDTO dto) {
        // Busca o professor responsável
        Professor professor = professorRepository.findById(dto.professorId())
                .orElseThrow(() -> new RuntimeException("Professor não encontrado com ID: " + dto.professorId()));

        // Cria a entidade Turma
        Turma turma = new Turma();
        turma.setNome(dto.nome());
        turma.setGrauEscolar(dto.grauEscolar());
        turma.setFaixaEtaria(dto.faixaEtaria());
        turma.setTurno(dto.turno());
        turma.setProfessor(professor);

        // Salva a turma no banco de dados
        Turma salva = turmaRepository.save(turma);
        return toDTO(salva);
    }

    /**
     * Atualiza os dados de uma turma existente.
     * 
     * @Transactional: Garante consistência dos dados durante a atualização.
     * Se qualquer erro ocorrer, todas as alterações são revertidas.
     * 
     * @param id Identificador único (UUID) da turma a ser atualizada
     * @param dto DTO contendo os novos dados da turma
     * @return TurmaDTO da turma atualizada
     * @throws RuntimeException se a turma ou o professor não forem encontrados
     */
    @Transactional
    public TurmaDTO atualizar(String id, CreateTurmaDTO dto) {
        // Busca a turma existente
        Turma turma = turmaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Turma não encontrada com ID: " + id));

        // Busca o professor responsável
        Professor professor = professorRepository.findById(dto.professorId())
                .orElseThrow(() -> new RuntimeException("Professor não encontrado com ID: " + dto.professorId()));

        // Atualiza os dados básicos da turma
        turma.setNome(dto.nome());
        turma.setGrauEscolar(dto.grauEscolar());
        turma.setFaixaEtaria(dto.faixaEtaria());
        turma.setTurno(dto.turno());
        turma.setProfessor(professor);

        // Salva a turma atualizada
        Turma salva = turmaRepository.save(turma);
        return toDTO(salva);
    }

    /**
     * Remove uma turma do banco de dados.
     * 
     * @Transactional: Garante que a remoção seja executada completamente.
     * 
     * IMPORTANTE: A remoção da turma não remove os educandos, apenas desvincula
     * o relacionamento (devido ao ManyToMany).
     * 
     * @param id Identificador único (UUID) da turma a ser removida
     * @throws RuntimeException se a turma não for encontrada com o ID fornecido
     */
    @Transactional
    public void deletar(String id) {
        Turma turma = turmaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Turma não encontrada com ID: " + id));
        turmaRepository.delete(turma);
    }

    /**
     * Lista todas as turmas de um professor específico.
     * 
     * @Transactional(readOnly = true): Mantém a transação aberta durante toda a execução.
     * 
     * @param professorId ID do professor
     * @return Lista de TurmaDTO das turmas do professor
     */
    @Transactional(readOnly = true)
    public List<TurmaDTO> listarPorProfessor(String professorId) {
        return turmaRepository.findAll().stream()
                .filter(t -> t.getProfessor() != null && t.getProfessor().getId().equals(professorId))
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Converte uma entidade Turma para um DTO TurmaDTO.
     * Inclui a conversão do professor.
     * 
     * @param t Entidade Turma a ser convertida
     * @return TurmaDTO com os dados convertidos
     */
    private TurmaDTO toDTO(Turma t) {
        String professorId = t.getProfessor() != null ? t.getProfessor().getId() : null;
        String professorNome = t.getProfessor() != null ? t.getProfessor().getNome() : null;
        String professorCpf = t.getProfessor() != null ? t.getProfessor().getCpf() : null;
        
        return new TurmaDTO(
                t.getId(),
                t.getNome(),
                t.getGrauEscolar(),
                t.getFaixaEtaria(),
                t.getTurno(),
                professorId,
                professorNome,
                professorCpf
        );
    }
}

