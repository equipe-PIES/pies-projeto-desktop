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
 * 
 * Esta classe implementa as operações CRUD (Create, Read, Update, Delete)
 * e realiza a conversão entre entidades (Educando) e DTOs (EducandoDTO).
 * 
 * Utiliza injeção de dependência via constructor (@RequiredArgsConstructor)
 * e transações gerenciadas pelo Spring (@Transactional).
 */
@Service
@RequiredArgsConstructor
public class EducandoService {

    /**
     * Repositório para acesso aos dados dos educandos no banco de dados.
     * Injetado automaticamente pelo Spring através do construtor gerado pelo Lombok.
     */
    private final EducandoRepository educandoRepository;

    /**
     * Lista todos os educandos cadastrados no sistema.
     * 
     * @Transactional(readOnly = true): Mantém a transação aberta durante toda a execução,
     * permitindo que o lazy loading dos responsáveis funcione corretamente. O readOnly = true
     * otimiza a consulta indicando que não haverá alterações no banco.
     * 
     * @return Lista de EducandoDTO contendo todos os educandos convertidos da entidade para DTO
     */
    @Transactional(readOnly = true)
    public List<EducandoDTO> listarTodos() {
        // Busca todos os educandos do banco de dados
        // findAll() retorna List<Educando> (entidades JPA)
        return educandoRepository.findAll().stream()
            // Converte cada entidade Educando para EducandoDTO
            // map() aplica a função toDTO() em cada elemento da lista
            .map(this::toDTO)
            // Coleta os resultados em uma nova List<EducandoDTO>
            .collect(Collectors.toList());
    }

    /**
     * Busca um educando específico pelo seu ID.
     * 
     * @Transactional(readOnly = true): Mantém a transação aberta durante toda a execução,
     * permitindo que o lazy loading dos responsáveis funcione corretamente quando acessado
     * no método toDTO(). O readOnly = true otimiza a consulta.
     * 
     * @param id Identificador único (UUID) do educando
     * @return EducandoDTO do educando encontrado
     * @throws EducandoNotFoundException se o educando não for encontrado com o ID fornecido
     */
    @Transactional(readOnly = true)
    public EducandoDTO buscarPorId(String id) {
        // Busca o educando no banco de dados
        // findById() retorna Optional<Educando> (pode estar vazio)
        return educandoRepository.findById(id)
            // Se o educando existir, converte para DTO
            // map() só executa se o Optional não estiver vazio
            .map(this::toDTO)
            // Se o educando não existir, lança exceção customizada
            // orElseThrow() lança exceção se o Optional estiver vazio
            .orElseThrow(() -> new EducandoNotFoundException(id));
    }

    /**
     * Salva um novo educando no banco de dados.
     * 
     * @Transactional: Garante que a operação seja executada completamente ou revertida
     * em caso de erro (ACID - Atomicidade). Todas as operações dentro deste método
     * fazem parte de uma única transação.
     * 
     * @param dto DTO contendo os dados do educando a ser criado
     * @return EducandoDTO do educando salvo (com ID gerado pelo JPA)
     * @throws CpfJaCadastradoException se o CPF já estiver cadastrado no sistema
     */
    @Transactional
    public EducandoDTO salvar(EducandoDTO dto) {
        // Validação de CPF único: verifica se já existe um educando com este CPF
        // Isso previne duplicação de CPF no banco de dados
        if (educandoRepository.existsByCpf(dto.getCpf())) {
            // Lança exceção customizada se o CPF já existir
            throw new CpfJaCadastradoException(dto.getCpf());
        }
        
        // Converte o DTO (camada de apresentação) para entidade (camada de persistência)
        // O ID não é setado aqui, será gerado automaticamente pelo JPA
        Educando entity = toEntity(dto);
        
        // Salva a entidade no banco de dados
        // save() persiste a entidade e retorna a entidade com o ID gerado
        Educando salvo = educandoRepository.save(entity);
        
        // Converte a entidade salva de volta para DTO para retornar ao controller
        // Isso garante que o ID gerado seja incluído na resposta
        return toDTO(salvo);
    }

    /**
     * Atualiza os dados de um educando existente.
     * 
     * @Transactional: Garante consistência dos dados durante a atualização.
     * Se qualquer erro ocorrer, todas as alterações são revertidas.
     * 
     * IMPORTANTE: Este método atualiza apenas os campos básicos do educando.
     * A lista de responsáveis não é atualizada aqui - isso deve ser feito
     * através de um serviço específico de responsáveis.
     * 
     * @param id Identificador único (UUID) do educando a ser atualizado
     * @param dto DTO contendo os novos dados do educando
     * @return EducandoDTO do educando atualizado
     * @throws EducandoNotFoundException se o educando não for encontrado com o ID fornecido
     * @throws CpfJaCadastradoException se o novo CPF já estiver cadastrado (e for diferente do atual)
     */
    @Transactional
    public EducandoDTO atualizar(String id, EducandoDTO dto) {
        // Busca o educando existente no banco de dados
        // Se não encontrar, lança exceção customizada
        Educando entity = educandoRepository.findById(id)
            .orElseThrow(() -> new EducandoNotFoundException(id));
        
        // Validação de CPF único: só valida se o CPF foi alterado
        // Compara o CPF atual da entidade com o CPF do DTO
        // Se forem diferentes E o novo CPF já existir, lança exceção
        if (!entity.getCpf().equals(dto.getCpf()) && educandoRepository.existsByCpf(dto.getCpf())) {
            throw new CpfJaCadastradoException(dto.getCpf());
        }
        
        // Usa BeanUtils para copiar propriedades do DTO para a entidade
        // BeanUtils.copyProperties() copia automaticamente campos com o mesmo nome
        // O terceiro parâmetro são os campos que devem ser IGNORADOS na cópia:
        // - "id": não copia o ID (mantém o ID original da entidade)
        // - "responsaveis": não copia a lista de responsáveis (gerenciada separadamente)
        BeanUtils.copyProperties(dto, entity, "id", "responsaveis");
        
        // Salva a entidade atualizada e converte para DTO antes de retornar
        return toDTO(educandoRepository.save(entity));
    }

    /**
     * Remove um educando do banco de dados.
     * 
     * @Transactional: Garante que a remoção seja executada completamente.
     * Se qualquer erro ocorrer, a operação é revertida.
     * 
     * IMPORTANTE: Com orphanRemoval = true na entidade Educando,
     * todos os responsáveis vinculados serão automaticamente removidos
     * quando o educando for deletado (cascata).
     * 
     * @param id Identificador único (UUID) do educando a ser removido
     * @throws EducandoNotFoundException se o educando não for encontrado com o ID fornecido
     */
    @Transactional
    public void deletar(String id) {
        // Busca o educando no banco de dados
        // Se não encontrar, lança exceção customizada
        Educando educando = educandoRepository.findById(id)
            .orElseThrow(() -> new EducandoNotFoundException(id));
        
        // Remove o educando do banco de dados
        // delete() remove a entidade e, devido ao cascade, remove os responsáveis também
        educandoRepository.delete(educando);
    }

    /**
     * Converte uma entidade Educando para um DTO EducandoDTO.
     * Inclui a conversão da lista de responsáveis se presente.
     * 
     * Este método realiza o mapeamento de dados da camada de persistência
     * para a camada de apresentação, expondo apenas os dados necessários
     * para a API REST.
     * 
     * @param e Entidade Educando a ser convertida
     * @return EducandoDTO com os dados convertidos
     */
    private EducandoDTO toDTO(Educando e) {
        // Cria uma nova instância do DTO
        EducandoDTO dto = new EducandoDTO();
        
        // Usa BeanUtils para copiar propriedades básicas da entidade para o DTO
        // BeanUtils.copyProperties() copia automaticamente campos com o mesmo nome
        // O terceiro parâmetro "responsaveis" indica que este campo deve ser IGNORADO
        // (será convertido manualmente abaixo)
        BeanUtils.copyProperties(e, dto, "responsaveis");
        
        // Converte a lista de responsáveis se existir e não estiver vazia
        // O relacionamento @OneToMany é lazy, mas como estamos dentro de uma transação
        // (@Transactional), o Hibernate carrega os dados automaticamente quando acessamos
        if (e.getResponsaveis() != null && !e.getResponsaveis().isEmpty()) {
            // Usa Stream API para converter cada Responsavel para ResponsavelDTO
            List<ResponsavelDTO> responsaveisDTO = e.getResponsaveis().stream()
                // map() aplica a função toResponsavelDTO() em cada responsável
                .map(this::toResponsavelDTO)
                // collect() agrupa os resultados em uma List<ResponsavelDTO>
                .collect(Collectors.toList());
            // Define a lista convertida no DTO
            dto.setResponsaveis(responsaveisDTO);
        }
        
        return dto;
    }

    /**
     * Converte um DTO EducandoDTO para uma entidade Educando.
     * 
     * Este método realiza o mapeamento de dados da camada de apresentação
     * para a camada de persistência, preparando os dados para serem salvos
     * no banco de dados.
     * 
     * NOTA: O ID não é copiado aqui, pois é gerado automaticamente pelo JPA
     * ao persistir uma nova entidade. Para atualizações, o ID deve ser
     * definido antes de chamar este método (ou usar o método atualizar()).
     * 
     * NOTA: A lista de responsáveis não está sendo convertida aqui. Se necessário,
     * a conversão deve ser feita através de um ResponsavelService ou método auxiliar.
     * 
     * @param dto EducandoDTO a ser convertido
     * @return Entidade Educando com os dados convertidos
     */
    private Educando toEntity(EducandoDTO dto) {
        Educando e = new Educando();
        BeanUtils.copyProperties(dto, e, "id", "responsaveis");

        // Verifica se o DTO tem responsáveis
        if (dto.getResponsaveis() != null && !dto.getResponsaveis().isEmpty()) {
            List<Responsavel> responsaveis = dto.getResponsaveis().stream().map(rdto -> {
                Responsavel r = new Responsavel();
                BeanUtils.copyProperties(rdto, r, "id", "educando", "endereco");

                // Converte o endereço, se existir
                if (rdto.getEndereco() != null) {
                    Endereco endereco = new Endereco();
                    BeanUtils.copyProperties(rdto.getEndereco(), endereco, "id");
                    r.setEndereco(endereco);
                }

                // Define a relação entre o responsável e o educando
                r.setEducando(e);
                return r;
            }).collect(Collectors.toList());

            e.setResponsaveis(responsaveis);
        }

        return e;
        }

    /**
     * Converte uma entidade Responsavel para um DTO ResponsavelDTO.
     * Inclui a conversão do endereço se presente.
     * 
     * Este método é usado internamente pelo método toDTO() para converter
     * os responsáveis vinculados a um educando.
     * 
     * @param r Entidade Responsavel a ser convertida
     * @return ResponsavelDTO com os dados convertidos
     */
    private ResponsavelDTO toResponsavelDTO(Responsavel r) {
        // Cria uma nova instância do DTO
        ResponsavelDTO dto = new ResponsavelDTO();
        
        // Usa BeanUtils para copiar propriedades básicas da entidade para o DTO
        // O terceiro parâmetro são os campos que devem ser IGNORADOS na cópia:
        // - "educando": não copia o relacionamento com Educando (evita referência circular)
        // - "endereco": não copia o endereço (será convertido manualmente abaixo)
        BeanUtils.copyProperties(r, dto, "educando", "endereco");
        
        // Converte o endereço se existir
        // O relacionamento @OneToOne pode ser null, então verificamos antes
        if (r.getEndereco() != null) {
            // Chama o método auxiliar para converter o endereço
            dto.setEndereco(toEnderecoDTO(r.getEndereco()));
        }
        
        return dto;
    }

    /**
     * Converte uma entidade Endereco para um DTO EnderecoDTO.
     * 
     * Este método é usado internamente pelo método toResponsavelDTO() para converter
     * o endereço vinculado a um responsável.
     * 
     * @param e Entidade Endereco a ser convertida
     * @return EnderecoDTO com os dados convertidos
     */
    private EnderecoDTO toEnderecoDTO(Endereco e) {
        // Cria uma nova instância do DTO
        EnderecoDTO dto = new EnderecoDTO();
        
        // Usa BeanUtils para copiar todas as propriedades
        // Como Endereco e EnderecoDTO têm os mesmos campos, não precisamos excluir nada
        BeanUtils.copyProperties(e, dto);
        
        return dto;
    }
    
    @Transactional
    public EducandoDTO adicionarResponsavel(String educandoId, ResponsavelDTO dto) {
        Educando educando = educandoRepository.findById(educandoId)
                .orElseThrow(() -> new EducandoNotFoundException(educandoId));

        Responsavel responsavel = new Responsavel();
        BeanUtils.copyProperties(dto, responsavel, "id", "educando", "endereco");
        responsavel.setEducando(educando);

        if (dto.getEndereco() != null) {
            Endereco endereco = new Endereco();
            BeanUtils.copyProperties(dto.getEndereco(), endereco, "id");
            responsavel.setEndereco(endereco);
        }

        // Adiciona o responsável à lista
        educando.getResponsaveis().add(responsavel);

        // Salva o educando novamente (CascadeType.ALL cuida do resto)
        Educando salvo = educandoRepository.save(educando);

        return toDTO(salvo);
    }


}
