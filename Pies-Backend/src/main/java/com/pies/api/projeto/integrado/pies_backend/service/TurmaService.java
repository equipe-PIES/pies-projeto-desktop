package com.pies.api.projeto.integrado.pies_backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pies.api.projeto.integrado.pies_backend.controller.dto.CreateTurmaDTO;
import com.pies.api.projeto.integrado.pies_backend.controller.dto.EducandoDTO;
import com.pies.api.projeto.integrado.pies_backend.controller.dto.ResponsavelDTO;
import com.pies.api.projeto.integrado.pies_backend.controller.dto.AnamneseDTO;
import com.pies.api.projeto.integrado.pies_backend.controller.dto.EnderecoDTO;
import com.pies.api.projeto.integrado.pies_backend.controller.dto.TurmaDTO;
import com.pies.api.projeto.integrado.pies_backend.model.Professor;
import com.pies.api.projeto.integrado.pies_backend.model.Turma;
import com.pies.api.projeto.integrado.pies_backend.repository.EducandoRepository;
import com.pies.api.projeto.integrado.pies_backend.repository.ProfessorRepository;
import com.pies.api.projeto.integrado.pies_backend.repository.TurmaRepository;

import lombok.RequiredArgsConstructor;

/**
 * Serviço responsável pela lógica de negócio relacionada às Turmas.
 * <p>
 * Principais responsabilidades:
 * <ul>
 * <li>CRUD de Turmas</li>
 * <li>Vínculo automático de Alunos via CPF na criação</li>
 * <li>Otimização de consultas (evitando N+1 Queries)</li>
 * <li>Conversão complexa de DTOs (Turma -> Alunos -> Responsável -> Endereço)</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class TurmaService {

    private final TurmaRepository turmaRepository;
    private final ProfessorRepository professorRepository;
    private final EducandoRepository educandoRepository;

    /**
     * Lista todas as turmas cadastradas.
     * <p><b>Performance:</b> Utiliza {@code findAllCompleto()} para trazer a Turma, 
     * o Professor e a Lista de Alunos em uma única consulta SQL (JOIN FETCH).</p>
     * * @return Lista de DTOs completos.
     */
    @Transactional(readOnly = true)
    public List<TurmaDTO> listarTodas() {
        return turmaRepository.findAllCompleto().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca uma turma específica por ID.
     * <p><b>Detalhes:</b> Traz a árvore completa de dados (Turma -> Alunos -> Responsável),
     * permitindo que a tela de visualização mostre todos os detalhes sem novas consultas.</p>
     * * @param id ID da turma.
     * @return DTO da turma.
     * @throws RuntimeException se a turma não for encontrada.
     */
    @Transactional(readOnly = true)
    public TurmaDTO buscarPorId(String id) {
        return turmaRepository.findByIdCompleto(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Turma não encontrada com ID: " + id));
    }

    /**
     * Cria uma nova turma e vincula alunos.
     * * @param dto Dados de criação.
     * @return DTO da turma salva.
     */
    @Transactional
    public TurmaDTO salvar(CreateTurmaDTO dto) {
        // 1. Valida e busca o professor
        Professor professor = professorRepository.findById(dto.professorId())
                .orElseThrow(() -> new RuntimeException("Professor não encontrado com ID: " + dto.professorId()));

        // 2. Cria a entidade Turma
        Turma turma = new Turma();
        turma.setNome(dto.nome());
        turma.setGrauEscolar(dto.grauEscolar());
        turma.setFaixaEtaria(dto.faixaEtaria());
        turma.setTurno(dto.turno());
        turma.setProfessor(professor);

        // 3. Salva a turma primeiro (necessário para gerar o ID antes de vincular alunos)
        Turma salva = turmaRepository.save(turma);

        // 4. Lógica de Vínculo de Alunos por CPF
        if (dto.cpfsAlunos() != null && !dto.cpfsAlunos().isEmpty()) {
            for (String cpf : dto.cpfsAlunos()) {
                educandoRepository.findByCpf(cpf).ifPresent(educando -> {
                    // Adiciona a turma à lista do educando (lado proprietário da relação ManyToMany)
                    educando.getTurmas().add(salva);
                    educandoRepository.save(educando);
                });
            }
        }
        
        // Retorna o DTO convertido (agora incluindo os alunos recém-vinculados)
        return toDTO(salva);
    }

    /**
     * Atualiza dados de uma turma existente.
     * * @param id ID da turma.
     * @param dto Novos dados.
     * @return DTO atualizado.
     */
    @Transactional
    public TurmaDTO atualizar(String id, CreateTurmaDTO dto) {
        Turma turma = turmaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Turma não encontrada com ID: " + id));

        Professor professor = professorRepository.findById(dto.professorId())
                .orElseThrow(() -> new RuntimeException("Professor não encontrado com ID: " + dto.professorId()));

        turma.setNome(dto.nome());
        turma.setGrauEscolar(dto.grauEscolar());
        turma.setFaixaEtaria(dto.faixaEtaria());
        turma.setTurno(dto.turno());
        turma.setProfessor(professor);

        Turma salva = turmaRepository.save(turma);
        return toDTO(salva);
    }

    /**
     * Remove uma turma.
     * <p>Nota: A exclusão remove apenas o vínculo com os alunos, 
     * não apaga os alunos do banco.</p>
     */
    @Transactional
    public void deletar(String id) {
        Turma turma = turmaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Turma não encontrada com ID: " + id));
        turmaRepository.delete(turma);
    }

    /**
     * Lista turmas de um professor específico.
     * * @param professorId ID do professor.
     * @return Lista de turmas.
     */
    @Transactional(readOnly = true)
    public List<TurmaDTO> listarPorProfessor(String professorId) {
        Professor professor = professorRepository.findById(professorId)
                .orElseThrow(() -> new RuntimeException("Professor não encontrado com ID: " + professorId));

        return turmaRepository.findByProfessorOrderByNomeAsc(professor).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca turmas por nome ou termo (busca parcial, case insensitive).
     * 
     * @param termo Termo de busca (nome ou parte do nome da turma)
     * @return Lista de TurmaDTO contendo as turmas encontradas
     */
    @Transactional(readOnly = true)
    public List<TurmaDTO> buscarPorTermo(String termo) {
        return turmaRepository.findByNomeContainingIgnoreCase(termo).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Filtra turmas por nome, professor e grau de escolaridade (filtro combinado).
     * 
     * @param nome Nome ou parte do nome da turma (pode ser null ou vazio para ignorar)
     * @param professorId ID do professor (pode ser null para ignorar)
     * @param grauEscolar Grau de escolaridade (pode ser null para ignorar)
     * @return Lista de TurmaDTO contendo as turmas encontradas
     */
    @Transactional(readOnly = true)
    public List<TurmaDTO> filtrarPorNomeProfessorEGrauEscolar(String nome, String professorId, com.pies.api.projeto.integrado.pies_backend.model.Enums.GrauEscolar grauEscolar) {
        // Normaliza nome vazio para null
        String nomeFiltro = (nome != null && !nome.trim().isEmpty()) ? nome.trim() : null;
        
        return turmaRepository.findByNomeAndProfessorAndGrauEscolar(nomeFiltro, professorId, grauEscolar).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // =================================================================================
    // MÉTODOS DE CONVERSÃO (DTO <-> ENTITY)
    // =================================================================================

    /**
     * Converte uma entidade Turma para TurmaDTO.
     * <p>Responsável por orquestrar a conversão profunda, incluindo a lista de alunos.</p>
     */
    private TurmaDTO toDTO(Turma t) {
        String professorId = t.getProfessor() != null ? t.getProfessor().getId() : null;
        String professorNome = t.getProfessor() != null ? t.getProfessor().getNome() : null;
        String professorCpf = t.getProfessor() != null ? t.getProfessor().getCpf() : null;
        
        List<EducandoDTO> educandosDTO = null;
        
        // Verifica se a lista de alunos foi carregada do banco
        if (t.getEducandos() != null) {
            // Passamos o ID desta turma para o conversor de educando
            // Isso garante que o campo 'turmaId' no DTO do aluno venha preenchido corretamente
            String idDestaTurma = t.getId();
            
            educandosDTO = t.getEducandos().stream()
                .map(e -> toEducandoDTO(e, idDestaTurma)) 
                .collect(Collectors.toList());
        }

        return new TurmaDTO(
                t.getId(),
                t.getNome(),
                t.getGrauEscolar(),
                t.getFaixaEtaria(),
                t.getTurno(),
                professorId,
                professorNome,
                professorCpf,
                educandosDTO
        );
    }

    /**
     * Converte uma entidade Educando para EducandoDTO.
     * <p><b>Importante:</b> Realiza o mapeamento manual do {@code Responsavel} e {@code Endereco},
     * pois o BeanUtils não lida bem com objetos aninhados profundos automaticamente.</p>
     * * @param e Entidade Educando.
     * @param turmaIdPadrao ID da turma atual (para garantir que o DTO saiba a qual turma pertence).
     * @return DTO completo.
     */
    private EducandoDTO toEducandoDTO(com.pies.api.projeto.integrado.pies_backend.model.Educando e, String turmaIdPadrao) {
        EducandoDTO dto = new EducandoDTO();
        
        // 1. Copia dados básicos (Nome, CPF, Nascimento, etc)
        org.springframework.beans.BeanUtils.copyProperties(e, dto, "responsavel", "anamnese");
        
        // 2. Lógica para preencher o ID da Turma
        // Se foi passado um ID padrão, usa ele. Se não, tenta pegar da lista do aluno.
        if (turmaIdPadrao != null) {
            dto.setTurmaId(turmaIdPadrao);
        } else if (e.getTurmas() != null && !e.getTurmas().isEmpty()) {
            dto.setTurmaId(e.getTurmas().get(0).getId());
        }

        // 3. Conversão Manual Completa do Responsável
        if (e.getResponsavel() != null) {
            ResponsavelDTO rDto = new ResponsavelDTO();
            // Copia dados do responsável (nome, cpf, contato)
            org.springframework.beans.BeanUtils.copyProperties(e.getResponsavel(), rDto, "educando", "endereco");
            
            // Copia Endereço do Responsável (objeto aninhado)
            if (e.getResponsavel().getEndereco() != null) {
                EnderecoDTO endDto = new EnderecoDTO();
                org.springframework.beans.BeanUtils.copyProperties(e.getResponsavel().getEndereco(), endDto);
                rDto.setEndereco(endDto);
            }
            
            dto.setResponsavel(rDto);
        }
        
        // 4. Conversão da Anamnese
        if (e.getAnamnese() != null) {
            AnamneseDTO anamneseDTO = new AnamneseDTO();
            org.springframework.beans.BeanUtils.copyProperties(e.getAnamnese(), anamneseDTO, "educando");
            dto.setAnamnese(anamneseDTO);
        }
        
        return dto;
    }
}