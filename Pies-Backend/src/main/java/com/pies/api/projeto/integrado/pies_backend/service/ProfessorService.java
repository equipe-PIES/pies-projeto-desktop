package com.pies.api.projeto.integrado.pies_backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pies.api.projeto.integrado.pies_backend.controller.dto.CreateProfessorDTO;
import com.pies.api.projeto.integrado.pies_backend.controller.dto.ProfessorDTO;
import com.pies.api.projeto.integrado.pies_backend.controller.dto.UpdateProfessorDTO;
import com.pies.api.projeto.integrado.pies_backend.exception.ProfessorNotFoundException;
import com.pies.api.projeto.integrado.pies_backend.model.Enums.Genero;
import com.pies.api.projeto.integrado.pies_backend.model.Professor;
import com.pies.api.projeto.integrado.pies_backend.model.Turma;
import com.pies.api.projeto.integrado.pies_backend.repository.ProfessorRepository;
import com.pies.api.projeto.integrado.pies_backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

/**
 * Serviço responsável pela lógica de negócio relacionada aos Professores.
 * * <p>Esta classe gerencia o ciclo de vida da entidade {@link Professor}, incluindo:
 * <ul>
 * <li>Operações CRUD (Create, Read, Update, Delete)</li>
 * <li>Validações de Regra de Negócio (ex: Unicidade de CPF)</li>
 * <li>Conversão de Entidades para DTOs</li>
 * <li>Associação de usuários de sistema aos perfis de professor</li>
 * </ul>
 * * <p><b>OTIMIZAÇÃO DE PERFORMANCE:</b>
 * Diferente de uma implementação padrão, este serviço utiliza consultas customizadas
 * no repositório (JOIN FETCH) para carregar as turmas vinculadas ao professor
 * em uma única viagem ao banco de dados, evitando o problema "N+1 Queries".
 */
@Service
@RequiredArgsConstructor
public class ProfessorService {

    private final ProfessorRepository professorRepository;
    private final UserRepository userRepository;

    /**
     * Lista todos os professores cadastrados no sistema.
     * * <p><b>Performance:</b> Utiliza o método {@code findAllCompleto()} que executa
     * um {@code LEFT JOIN FETCH} com a tabela de Turmas. Isso garante que, ao listar
     * 50 professores, seja feita apenas <b>1 consulta SQL</b> ao invés de 51.</p>
     * * @return Lista de {@link ProfessorDTO} contendo os dados do professor e os IDs de suas turmas.
     */
    @Transactional(readOnly = true)
    public List<ProfessorDTO> listarTodos() {
        // Busca otimizada: Traz Professor + Lista de Turmas (em memória)
        List<Professor> professores = professorRepository.findAllCompleto();

        return professores.stream()
                .map(this::converterComTurmas)
                .collect(Collectors.toList());
    }

    /**
     * Busca um professor específico pelo seu ID (UUID).
     * * <p>Utiliza {@code findByIdCompleto} para garantir que a lista de turmas
     * venha preenchida, permitindo que a visualização de detalhes mostre
     * quais turmas este professor leciona sem consultas adicionais.</p>
     * * @param id Identificador único do professor.
     * @return DTO com os dados completos.
     * @throws ProfessorNotFoundException se o professor não for encontrado.
     */
    @Transactional(readOnly = true)
    public ProfessorDTO buscarPorId(String id) {
        Professor professor = professorRepository.findByIdCompleto(id)
                .orElseThrow(() -> new ProfessorNotFoundException(id));
        return converterComTurmas(professor);
    }

    /**
     * Busca os dados do professor associado ao usuário logado atualmente.
     * Útil para a funcionalidade "Meu Perfil" ou Dashboard do Professor.
     * * @param emailUsuario Email do usuário autenticado (extraído do Token JWT).
     * @return DTO com os dados do professor vinculado àquele usuário.
     * @throws ProfessorNotFoundException se o usuário não existir ou não for um professor.
     */
    @Transactional(readOnly = true)
    public ProfessorDTO buscarPerfilUsuario(String emailUsuario) {
        var user = userRepository.findByEmail(emailUsuario);
        if (user == null) {
            throw new ProfessorNotFoundException("Usuário de sistema não encontrado: " + emailUsuario);
        }

        // Busca otimizada pelo User ID para trazer as turmas junto
        Professor professor = professorRepository.findByUserIdCompleto(user.getId())
                .orElseThrow(() -> new ProfessorNotFoundException("Nenhum perfil de professor associado ao usuário: " + emailUsuario));

        return converterComTurmas(professor);
    }

    // ========================================================================
    // MÉTODOS DE ESCRITA (CREATE / UPDATE / DELETE)
    // ========================================================================

    /**
     * Cria um novo cadastro de professor.
     * * @param dto Dados de entrada para criação.
     * @return DTO do professor recém-criado.
     * @throws IllegalArgumentException se já existir um professor com o mesmo CPF.
     */
    @Transactional
    public ProfessorDTO criar(CreateProfessorDTO dto) {
        // Regra de Negócio: CPF Único
        if (professorRepository.existsByCpf(dto.getCpf())) {
            throw new IllegalArgumentException("Já existe um professor cadastrado com o CPF: " + dto.getCpf());
        }

        Professor professor = new Professor(
                dto.getNome(),
                dto.getCpf(),
                dto.getDataNascimento(),
                dto.getGenero(),
                dto.getFormacao(),
                dto.getObservacoes()
        );

        Professor salvo = professorRepository.save(professor);
        
        // Retorna DTO simples (sem turmas, pois um professor novo ainda não tem turmas)
        return new ProfessorDTO(salvo); 
    }

    /**
     * Atualiza os dados cadastrais de um professor existente.
     * * @param id ID do professor a ser atualizado.
     * @param dto Novos dados.
     * @return DTO com os dados atualizados.
     * @throws ProfessorNotFoundException se o ID não existir.
     * @throws IllegalArgumentException se tentar alterar o CPF para um que já existe em outro cadastro.
     */
    @Transactional
    public ProfessorDTO atualizar(String id, UpdateProfessorDTO dto) {
        Professor professor = professorRepository.findById(id)
                .orElseThrow(() -> new ProfessorNotFoundException(id));

        // Validação: Se o CPF mudou, verifica se o novo já está em uso por outra pessoa
        if (!professor.getCpf().equals(dto.getCpf()) &&
                professorRepository.existsByCpf(dto.getCpf())) {
            throw new IllegalArgumentException("Já existe um professor cadastrado com o CPF: " + dto.getCpf());
        }

        // Atualização dos campos
        professor.setNome(dto.getNome());
        professor.setCpf(dto.getCpf());
        professor.setDataNascimento(dto.getDataNascimento());
        professor.setGenero(dto.getGenero());
        professor.setFormacao(dto.getFormacao());
        professor.setObservacoes(dto.getObservacoes());

        Professor atualizado = professorRepository.save(professor);
        
        // Retorna DTO simples (updates de cadastro geralmente não precisam devolver a lista de turmas)
        return new ProfessorDTO(atualizado);
    }

    /**
     * Remove um professor do sistema.
     * * @param id ID do professor a ser removido.
     * @throws ProfessorNotFoundException se o ID não existir.
     */
    @Transactional
    public void deletar(String id) {
        if (!professorRepository.existsById(id)) {
            throw new ProfessorNotFoundException(id);
        }
        professorRepository.deleteById(id);
    }

    /**
     * Vincula uma conta de usuário (login) a um perfil de professor.
     * Necessário para permitir que o professor faça login no sistema.
     * * @param professorId ID do perfil do professor.
     * @param userId ID do usuário criado no sistema de autenticação.
     * @return DTO atualizado.
     */
    @Transactional
    public ProfessorDTO associarUsuario(String professorId, String userId) {
        Professor professor = professorRepository.findById(professorId)
                .orElseThrow(() -> new ProfessorNotFoundException(professorId));

        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("Usuário com ID " + userId + " não encontrado");
        }

        professor.setUserId(userId);
        Professor salvo = professorRepository.save(professor);
        return new ProfessorDTO(salvo);
    }

    // ========================================================================
    // MÉTODOS DE BUSCA SIMPLES (FILTROS)
    // ========================================================================

    @Transactional(readOnly = true)
    public List<ProfessorDTO> buscarPorNome(String nome) {
        return professorRepository.findByNomeContainingIgnoreCase(nome).stream()
                .map(ProfessorDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProfessorDTO> buscarPorGenero(Genero genero) {
        return professorRepository.findByGenero(genero).stream()
                .map(ProfessorDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProfessorDTO> buscarPorFormacao(String formacao) {
        return professorRepository.findByFormacao(formacao).stream()
                .map(ProfessorDTO::new)
                .collect(Collectors.toList());
    }

    // ========================================================================
    // CONVERSORES E UTILITÁRIOS
    // ========================================================================

    /**
     * Converte uma Entidade Professor para DTO, preenchendo a lista de IDs de Turmas.
     * * <p><b>ATENÇÃO:</b> Este método assume que a lista {@code professor.getTurmas()}
     * já foi carregada em memória (via JOIN FETCH no repositório).
     * Se for chamado em um objeto carregado sem fetch, pode gerar queries extras
     * ou LazyInitializationException se a sessão estiver fechada.</p>
     * * @param professor Entidade carregada do banco.
     * @return DTO pronto para envio ao front-end.
     */
    private ProfessorDTO converterComTurmas(Professor professor) {
        ProfessorDTO dto = new ProfessorDTO(professor);

        // Verifica se a lista de turmas está disponível e não é nula
        if (professor.getTurmas() != null) {
            List<String> turmasIds = professor.getTurmas().stream()
                    .map(Turma::getId)
                    .collect(Collectors.toList());
            dto.setTurmasIds(turmasIds);
        }

        return dto;
    }
}