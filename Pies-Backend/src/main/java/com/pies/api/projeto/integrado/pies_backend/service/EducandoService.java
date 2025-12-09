package com.pies.api.projeto.integrado.pies_backend.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.pies.api.projeto.integrado.pies_backend.controller.dto.*;
import com.pies.api.projeto.integrado.pies_backend.exception.CpfJaCadastradoException;
import com.pies.api.projeto.integrado.pies_backend.exception.EducandoNotFoundException;
import com.pies.api.projeto.integrado.pies_backend.model.*;
import com.pies.api.projeto.integrado.pies_backend.repository.EducandoRepository;
import lombok.RequiredArgsConstructor;

/**
 * Serviço responsável pela lógica de negócio relacionada aos Educandos.
 * * Implementa operações CRUD e conversão de DTOs.
 * Foca em performance utilizando consultas otimizadas no repositório.
 */
@Service
@RequiredArgsConstructor
public class EducandoService {

    /**
     * Injeção do repositório via construtor (Lombok).
     */
    private final EducandoRepository educandoRepository;

    /**
     * Lista todos os educandos cadastrados.
     * * <p><b>OTIMIZAÇÃO:</b> Utiliza o método {@code findAllCompleto()} que executa um 
     * {@code JOIN FETCH} no banco. Isso traz o Aluno, suas Turmas e o Responsável 
     * em uma única consulta SQL, evitando o problema de "N+1 selects" e lentidão de rede.</p>
     * * @return Lista de EducandoDTO com todos os dados preenchidos.
     */
    @Transactional(readOnly = true)
    public List<EducandoDTO> listarTodos() {
        return educandoRepository.findAllCompleto().stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    /**
     * Busca um educando por ID com todos os detalhes.
     * * <p>Utiliza {@code findByIdCompleto} para garantir que turmas e responsável 
     * venham carregados, mesmo sendo LAZY na entidade.</p>
     * * @param id Identificador do educando.
     * @return DTO do educando.
     * @throws EducandoNotFoundException se não encontrar.
     */
    @Transactional(readOnly = true)
    public EducandoDTO buscarPorId(String id) {
        return educandoRepository.findByIdCompleto(id)
            .map(this::toDTO)
            .orElseThrow(() -> new EducandoNotFoundException(id));
    }

    /**
     * Salva um novo educando.
     * * @param dto Dados de entrada.
     * @return DTO com o ID gerado.
     * @throws CpfJaCadastradoException se o CPF já existir.
     */
    @Transactional
    public EducandoDTO salvar(EducandoDTO dto) {
        // Validação de Regra de Negócio: CPF Único
        if (educandoRepository.existsByCpf(dto.getCpf())) {
            throw new CpfJaCadastradoException(dto.getCpf());
        }
        
        Educando entity = toEntity(dto);
        Educando salvo = educandoRepository.save(entity);
        
        return toDTO(salvo);
    }

    /**
     * Atualiza dados cadastrais de um educando.
     * * <p>Nota: Este método atualiza dados simples. Para alterar o responsável,
     * o ideal é usar o método específico {@code definirResponsavel} ou garantir 
     * que o DTO venha completo.</p>
     * * @param id ID do educando a atualizar.
     * @param dto Novos dados.
     * @return DTO atualizado.
     */
    @Transactional
    public EducandoDTO atualizar(String id, EducandoDTO dto) {
        Educando entity = educandoRepository.findById(id)
            .orElseThrow(() -> new EducandoNotFoundException(id));
        
        // Verifica se o CPF mudou e se o novo já existe
        if (!entity.getCpf().equals(dto.getCpf()) && educandoRepository.existsByCpf(dto.getCpf())) {
            throw new CpfJaCadastradoException(dto.getCpf());
        }
        
        // Atualiza campos simples, ignorando ID e relacionamentos complexos que
        // devem ser tratados com cuidado ou em métodos específicos se necessário.
        BeanUtils.copyProperties(dto, entity, "id", "responsavel", "anamnese");
        
        // Se o DTO trouxer um responsável novo, poderíamos atualizar aqui também,
        // mas mantive a lógica simplificada focada nos dados do aluno.
        
        return toDTO(educandoRepository.save(entity));
    }

    /**
     * Remove um educando e seus dados em cascata (Responsável, Anamnese).
     */
    @Transactional
    public void deletar(String id) {
        Educando educando = educandoRepository.findById(id)
            .orElseThrow(() -> new EducandoNotFoundException(id));
        educandoRepository.delete(educando);
    }

    /**
     * Lista educandos de uma turma específica de forma otimizada.
     * * @param turmaId ID da turma.
     * @return Lista de alunos daquela turma.
     */
    @Transactional(readOnly = true)
    public List<EducandoDTO> listarPorTurma(String turmaId) {
        // Busca otimizada para não travar a tela de Turmas
        List<Educando> lista = educandoRepository.findAllByTurmaIdCompleto(turmaId);
        return lista.stream().map(this::toDTO).collect(Collectors.toList());
    }

    // ========================================================================
    // CONVERSORES (DTO <-> ENTITY)
    // ========================================================================

    /**
     * Converte Entidade -> DTO.
     * Trata manualmente os objetos aninhados (Responsável, Anamnese).
     */
    private EducandoDTO toDTO(Educando e) {
        EducandoDTO dto = new EducandoDTO();
        // Ignora campos complexos na cópia automática
        BeanUtils.copyProperties(e, dto, "responsavel", "anamnese");
        
        // Configura o ID da Turma, se houver
        if (e.getTurmas() != null && !e.getTurmas().isEmpty()) {
        dto.setTurmaId(e.getTurmas().get(0).getId());
        }

        // Conversão Manual: Responsável (Objeto Único)
        if (e.getResponsavel() != null) {
            dto.setResponsavel(toResponsavelDTO(e.getResponsavel()));
        }
        
        // Conversão Manual: Anamnese
        if (e.getAnamnese() != null) {
            dto.setAnamnese(toAnamneseDTO(e.getAnamnese()));
        }

        return dto;
    }

    /**
     * Busca educandos por nome ou termo (busca parcial, case insensitive).
     * 
     * @param termo Termo de busca (nome ou parte do nome)
     * @return Lista de EducandoDTO contendo os educandos encontrados
     */
    @Transactional(readOnly = true)
    public List<EducandoDTO> buscarPorTermo(String termo) {
        return educandoRepository.findByNomeContainingIgnoreCase(termo).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Filtra educandos por nome e grau de escolaridade (filtro combinado).
     * 
     * @param nome Nome ou parte do nome (pode ser null ou vazio para ignorar)
     * @param escolaridade Grau de escolaridade (pode ser null para ignorar)
     * @return Lista de EducandoDTO contendo os educandos encontrados
     */
    @Transactional(readOnly = true)
    public List<EducandoDTO> filtrarPorNomeEEscolaridade(String nome, com.pies.api.projeto.integrado.pies_backend.model.Enums.GrauEscolar escolaridade) {
        // Normaliza nome vazio para null
        String nomeFiltro = (nome != null && !nome.trim().isEmpty()) ? nome.trim() : null;
        
        return educandoRepository.findByNomeAndEscolaridade(nomeFiltro, escolaridade).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Converte DTO -> Entidade.
     * Constrói o grafo de objetos e configura os relacionamentos bidirecionais.
     */
    private Educando toEntity(EducandoDTO dto) {
        Educando e = new Educando();
        
        BeanUtils.copyProperties(dto, e, "id", "responsavel", "anamnese");

        if (dto.getResponsavel() != null) {
            Responsavel r = new Responsavel();
            ResponsavelDTO rDto = dto.getResponsavel();
            
            BeanUtils.copyProperties(rDto, r, "id", "educando", "endereco");

            if (rDto.getEndereco() != null) {
                Endereco endereco = new Endereco();
                BeanUtils.copyProperties(rDto.getEndereco(), endereco, "id");
                r.setEndereco(endereco);
            }

            r.setEducando(e); 
            
            e.setResponsavel(r);
        }

        if (dto.getAnamnese() != null) {
            Anamnese anamnese = toAnamneseEntity(dto.getAnamnese(), e);
            e.setAnamnese(anamnese);
        }

        return e;
    }

    // ========================================================================
    // MÉTODOS AUXILIARES DE CONVERSÃO
    // ========================================================================

    private ResponsavelDTO toResponsavelDTO(Responsavel r) {
        ResponsavelDTO dto = new ResponsavelDTO();
        BeanUtils.copyProperties(r, dto, "educando", "endereco");
        if (r.getEndereco() != null) {
            dto.setEndereco(toEnderecoDTO(r.getEndereco()));
        }
        return dto;
    }

    private EnderecoDTO toEnderecoDTO(Endereco e) {
        EnderecoDTO dto = new EnderecoDTO();
        BeanUtils.copyProperties(e, dto);
        return dto;
    }

    private AnamneseDTO toAnamneseDTO(Anamnese anamnese) {
        AnamneseDTO dto = new AnamneseDTO();
        BeanUtils.copyProperties(anamnese, dto, "educando");
        return dto;
    }

    private Anamnese toAnamneseEntity(AnamneseDTO dto, Educando educando) {
        Anamnese anamnese = new Anamnese();
        BeanUtils.copyProperties(dto, anamnese, "id", "educando");
        anamnese.setEducando(educando);
        return anamnese;
    }
    
    /**
     * Define ou substitui o responsável de um aluno.
     * Útil se a tela de edição de responsável for separada.
     */
    @Transactional
    public EducandoDTO definirResponsavel(String educandoId, ResponsavelDTO dto) {
        Educando educando = educandoRepository.findById(educandoId)
                .orElseThrow(() -> new EducandoNotFoundException(educandoId));

        Responsavel responsavel = new Responsavel();
        BeanUtils.copyProperties(dto, responsavel, "id", "educando", "endereco");
        
        // Vínculo com o Aluno
        responsavel.setEducando(educando);

        if (dto.getEndereco() != null) {
            Endereco endereco = new Endereco();
            BeanUtils.copyProperties(dto.getEndereco(), endereco, "id");
            responsavel.setEndereco(endereco);
        }

        // Atualiza a referência no objeto Pai
        educando.setResponsavel(responsavel);

        // O save() com CascadeType.ALL vai persistir o responsável novo
        Educando salvo = educandoRepository.save(educando);
        return toDTO(salvo);
    }
}