package com.pies.api.projeto.integrado.pies_backend.controller;

import java.util.List;
import java.util.function.Supplier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.pies.api.projeto.integrado.pies_backend.controller.dto.EducandoDTO;
import com.pies.api.projeto.integrado.pies_backend.controller.dto.ResponsavelDTO;
import com.pies.api.projeto.integrado.pies_backend.exception.CpfJaCadastradoException;
import com.pies.api.projeto.integrado.pies_backend.exception.EducandoNotFoundException;
import com.pies.api.projeto.integrado.pies_backend.service.EducandoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controller REST responsável pelos endpoints relacionados aos Educandos.
 * 
 * Esta classe expõe os endpoints da API REST para operações CRUD (Create, Read, Update, Delete)
 * relacionadas aos educandos. Utiliza o padrão RESTful com mapeamento de métodos HTTP
 * para operações específicas.
 * 
 * O tratamento de exceções é centralizado no método auxiliar handleRequest(),
 * reduzindo código repetitivo e facilitando a manutenção.
 * 
 * Endpoints disponíveis:
 * - GET    /api/educandos          - Lista todos os educandos
 * - GET    /api/educandos/{id}     - Busca um educando por ID
 * - POST   /api/educandos          - Cria um novo educando
 * - PUT    /api/educandos/{id}     - Atualiza um educando existente
 * - DELETE /api/educandos/{id}     - Remove um educando
 */
@RestController
@RequestMapping("/api/educandos")
@RequiredArgsConstructor
public class EducandoController {

    /**
     * Serviço responsável pela lógica de negócio dos educandos.
     * Injetado automaticamente pelo Spring através do construtor gerado pelo Lombok
     * (@RequiredArgsConstructor cria um construtor que recebe todos os campos final).
     */
    private final EducandoService educandoService;

    /**
     * Lista todos os educandos cadastrados no sistema.
     * 
     * Endpoint: GET /api/educandos
     * 
     * Este método não precisa de tratamento de exceções pois sempre retorna
     * uma lista (pode estar vazia, mas nunca lança exceção).
     * 
     * @return ResponseEntity com lista de EducandoDTO e status HTTP 200 OK
     */
    @GetMapping
    public ResponseEntity<List<EducandoDTO>> listarTodos() {
        // Chama o serviço para buscar todos os educandos
        // ResponseEntity.ok() retorna status 200 com o corpo da resposta
        return ResponseEntity.ok(educandoService.listarTodos());
    }

    @GetMapping("/simplificados")
    public ResponseEntity<List<EducandoDTO>> listarSimplificados() {
        return ResponseEntity.ok(educandoService.listarSimplificados());
    }

    /**
     * Busca um educando específico pelo seu ID.
     * 
     * Endpoint: GET /api/educandos/{id}
     * 
     * Utiliza o método auxiliar handleRequest() para tratamento centralizado
     * de exceções. Se o educando não for encontrado, retorna 404 Not Found.
     * 
     * @param id Identificador único (UUID) do educando a ser buscado
     *            Extraído automaticamente da URL pelo Spring (@PathVariable)
     * @return ResponseEntity com EducandoDTO e status HTTP 200 OK,
     *         ou 404 Not Found se o educando não existir
     */
    @GetMapping("/{id}")
    public ResponseEntity<EducandoDTO> buscarPorId(@PathVariable String id) {
        // Usa handleRequest() para tratamento centralizado de exceções
        // Lambda () -> executa a operação e retorna o resultado
        // Se educandoService.buscarPorId() lançar exceção, handleRequest() trata
        return handleRequest(() -> ResponseEntity.ok(educandoService.buscarPorId(id)));
    }

    /**
     * Cria um novo educando no sistema.
     * 
     * Endpoint: POST /api/educandos
     * 
     * O DTO é validado automaticamente através da anotação @Valid.
     * Validações incluem: CPF válido, campos obrigatórios, data de nascimento no passado, etc.
     * 
     * Utiliza o método auxiliar handleRequest() para tratamento centralizado
     * de exceções. Se o CPF já estiver cadastrado, retorna 409 Conflict.
     * 
     * @param dto DTO contendo os dados do educando a ser criado
     *            Extraído automaticamente do corpo da requisição pelo Spring (@RequestBody)
     *            Validado automaticamente pela anotação @Valid
     * @return ResponseEntity com EducandoDTO do educando criado e status HTTP 201 Created,
     *         ou 409 Conflict se o CPF já estiver cadastrado
     */
    @PostMapping
    public ResponseEntity<EducandoDTO> criar(@RequestBody @Valid EducandoDTO dto) {
        // Usa handleRequest() para tratamento centralizado de exceções
        // Lambda com bloco {} permite executar múltiplas instruções
        return handleRequest(() -> {
            // Chama o serviço para salvar o educando
            EducandoDTO salvo = educandoService.salvar(dto);
            // Retorna status 201 Created (padrão REST para criação)
            // ResponseEntity.status() permite especificar o código HTTP
            return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
        });
    }

    @GetMapping("/turma/{turmaId}")
    public ResponseEntity<java.util.List<EducandoDTO>> listarPorTurma(@PathVariable String turmaId) {
        return ResponseEntity.ok(educandoService.listarPorTurma(turmaId));
    }

    /**
     * Atualiza os dados de um educando existente.
     * 
     * Endpoint: PUT /api/educandos/{id}
     * 
     * O DTO é validado automaticamente através da anotação @Valid.
     * Apenas os campos básicos do educando são atualizados.
     * A lista de responsáveis não é atualizada aqui (gerenciada separadamente).
     * 
     * Utiliza o método auxiliar handleRequest() para tratamento centralizado
     * de exceções. Retorna 404 se o educando não existir, ou 409 se o CPF já estiver cadastrado.
     * 
     * @param id Identificador único (UUID) do educando a ser atualizado
     *            Extraído automaticamente da URL pelo Spring (@PathVariable)
     * @param dto DTO contendo os novos dados do educando
     *            Extraído automaticamente do corpo da requisição pelo Spring (@RequestBody)
     *            Validado automaticamente pela anotação @Valid
     * @return ResponseEntity com EducandoDTO do educando atualizado e status HTTP 200 OK,
     *         ou 404 Not Found se o educando não existir,
     *         ou 409 Conflict se o novo CPF já estiver cadastrado
     */
    @PutMapping("/{id}")
    public ResponseEntity<EducandoDTO> atualizar(@PathVariable String id, @RequestBody @Valid EducandoDTO dto) {
        // Usa handleRequest() para tratamento centralizado de exceções
        // Lambda inline executa a operação e retorna o resultado
        return handleRequest(() -> ResponseEntity.ok(educandoService.atualizar(id, dto)));
    }

    /**
     * Remove um educando do sistema.
     * 
     * Endpoint: DELETE /api/educandos/{id}
     * 
     * IMPORTANTE: Com orphanRemoval = true na entidade Educando,
     * todos os responsáveis vinculados serão automaticamente removidos
     * quando o educando for deletado (cascata).
     * 
     * Utiliza o método auxiliar handleRequest() para tratamento centralizado
     * de exceções. Retorna 204 No Content em caso de sucesso (padrão REST para deleção).
     * 
     * @param id Identificador único (UUID) do educando a ser removido
     *            Extraído automaticamente da URL pelo Spring (@PathVariable)
     * @return ResponseEntity com status HTTP 204 No Content (sem corpo),
     *         ou 404 Not Found se o educando não existir
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable String id) {
        // Usa handleRequest() para tratamento centralizado de exceções
        // Lambda com bloco {} permite executar múltiplas instruções
        return handleRequest(() -> {
            // Chama o serviço para deletar o educando
            educandoService.deletar(id);
            // Retorna status 204 No Content (padrão REST para deleção bem-sucedida)
            // noContent() retorna ResponseEntity sem corpo
            // <Void> especifica explicitamente o tipo genérico para o compilador
            return ResponseEntity.noContent().<Void>build();
        });
    }

    /**
     * Método auxiliar para tratar exceções de forma centralizada.
     * 
     * Este método reduz código repetitivo nos métodos do controller, centralizando
     * o tratamento de exceções em um único lugar. Facilita a manutenção pois,
     * se a lógica de tratamento mudar, só precisa ser alterada aqui.
     * 
     * Utiliza Generics (<T>) para funcionar com qualquer tipo de retorno:
     * - ResponseEntity<EducandoDTO> para métodos que retornam educandos
     * - ResponseEntity<Void> para métodos que não retornam corpo (como DELETE)
     * 
     * Utiliza Supplier<ResponseEntity<T>> para receber uma função que executa
     * a operação e retorna o resultado. Isso permite que o código seja executado
     * dentro do try-catch deste método.
     * 
     * @param <T> Tipo genérico do corpo da resposta (EducandoDTO, Void, etc.)
     * @param supplier Função (lambda) que retorna ResponseEntity com o resultado da operação
     *                 Esta função é executada dentro do try-catch para capturar exceções
     * @return ResponseEntity com o resultado da operação ou erro apropriado:
     *         - 404 Not Found para EducandoNotFoundException
     *         - 409 Conflict para CpfJaCadastradoException
     *         - Resultado da operação se tudo ocorrer bem
     */
    private <T> ResponseEntity<T> handleRequest(Supplier<ResponseEntity<T>> supplier) {
        try {
            // Executa a função (lambda) passada como parâmetro
            // supplier.get() chama o método do controller que foi passado como lambda
            // Se a operação for bem-sucedida, retorna o resultado
            return supplier.get();
        } catch (EducandoNotFoundException e) {
            // Educando não encontrado: retorna 404 Not Found
            // notFound() é um método estático que cria ResponseEntity com status 404
            // build() finaliza a construção do ResponseEntity
            return ResponseEntity.notFound().build();
        } catch (CpfJaCadastradoException e) {
            // CPF já cadastrado: retorna 409 Conflict
            // status() permite especificar o código HTTP
            // build() cria ResponseEntity sem corpo (apenas status)
            // O tipo genérico <T> permite que o compilador aceite isso mesmo
            // quando T é EducandoDTO (pois não retornamos corpo no erro)
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PostMapping("/{id}/responsaveis")
    public ResponseEntity<EducandoDTO> adicionarResponsavel(
            @PathVariable String id,
            @RequestBody @Valid ResponsavelDTO responsavelDTO) {

            return handleRequest(() -> {
            EducandoDTO atualizado = educandoService.adicionarResponsavel(id, responsavelDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(atualizado);
        });
    }

}
