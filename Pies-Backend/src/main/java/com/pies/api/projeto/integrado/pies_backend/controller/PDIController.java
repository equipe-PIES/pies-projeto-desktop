package com.pies.api.projeto.integrado.pies_backend.controller;

import java.util.List;
import java.util.function.Supplier;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pies.api.projeto.integrado.pies_backend.controller.dto.CreatePDIDTO;
import com.pies.api.projeto.integrado.pies_backend.controller.dto.PDIDTO;
import com.pies.api.projeto.integrado.pies_backend.exception.EducandoNotFoundException;
import com.pies.api.projeto.integrado.pies_backend.exception.PDINotFoundException;
import com.pies.api.projeto.integrado.pies_backend.service.PDIService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controller REST responsável pelos endpoints relacionados aos PDIs.
 * 
 * Esta classe expõe os endpoints da API REST para operações CRUD (Create, Read, Update, Delete)
 * relacionadas aos Planos de Desenvolvimento Individual. Utiliza o padrão RESTful com mapeamento
 * de métodos HTTP para operações específicas.
 * 
 * O tratamento de exceções é centralizado no método auxiliar handleRequest(),
 * reduzindo código repetitivo e facilitando a manutenção.
 * 
 * Endpoints disponíveis:
 * - GET    /api/pdis                    - Lista todos os PDIs
 * - GET    /api/pdis/{id}               - Busca um PDI por ID
 * - GET    /api/pdis/educando/{educandoId} - Busca PDIs de um educando
 * - POST   /api/pdis                    - Cria um novo PDI
 * - PUT    /api/pdis/{id}               - Atualiza um PDI existente
 * - DELETE /api/pdis/{id}               - Remove um PDI
 */
@RestController
@RequestMapping("/api/pdis")
@RequiredArgsConstructor
public class PDIController {

    /**
     * Serviço responsável pela lógica de negócio dos PDIs.
     * Injetado automaticamente pelo Spring através do construtor gerado pelo Lombok
     * (@RequiredArgsConstructor cria um construtor que recebe todos os campos final).
     */
    private final PDIService pdiService;

    /**
     * Lista todos os PDIs cadastrados no sistema.
     * 
     * Endpoint: GET /api/pdis
     * 
     * Este método não precisa de tratamento de exceções pois sempre retorna
     * uma lista (pode estar vazia, mas nunca lança exceção).
     * 
     * @return ResponseEntity com lista de PDIDTO e status HTTP 200 OK
     */
    @GetMapping
    public ResponseEntity<List<PDIDTO>> listarTodos() {
        return ResponseEntity.ok(pdiService.listarTodos());
    }

    /**
     * Busca um PDI específico pelo seu ID.
     * 
     * Endpoint: GET /api/pdis/{id}
     * 
     * Utiliza o método auxiliar handleRequest() para tratamento centralizado
     * de exceções. Se o PDI não for encontrado, retorna 404 Not Found.
     * 
     * @param id Identificador único (UUID) do PDI a ser buscado
     *            Extraído automaticamente da URL pelo Spring (@PathVariable)
     * @return ResponseEntity com PDIDTO e status HTTP 200 OK,
     *         ou 404 Not Found se o PDI não existir
     */
    @GetMapping("/{id}")
    public ResponseEntity<PDIDTO> buscarPorId(@PathVariable String id) {
        return handleRequest(() -> ResponseEntity.ok(pdiService.buscarPorId(id)));
    }

    /**
     * Busca todos os PDIs de um educando específico.
     * 
     * Endpoint: GET /api/pdis/educando/{educandoId}
     * 
     * Utiliza o método auxiliar handleRequest() para tratamento centralizado
     * de exceções. Se o educando não for encontrado, retorna 404 Not Found.
     * 
     * @param educandoId Identificador único (UUID) do educando
     *                   Extraído automaticamente da URL pelo Spring (@PathVariable)
     * @return ResponseEntity com lista de PDIDTO e status HTTP 200 OK,
     *         ou 404 Not Found se o educando não existir
     */
    @GetMapping("/educando/{educandoId}")
    public ResponseEntity<List<PDIDTO>> buscarPorEducandoId(@PathVariable String educandoId) {
        return handleRequest(() -> ResponseEntity.ok(pdiService.buscarPorEducandoId(educandoId)));
    }

    /**
     * Cria um novo PDI no sistema.
     * 
     * Endpoint: POST /api/pdis
     * 
     * O DTO é validado automaticamente através da anotação @Valid.
     * Validações incluem: campos obrigatórios, tamanho máximo de texto, etc.
     * 
     * Utiliza o método auxiliar handleRequest() para tratamento centralizado
     * de exceções. Se o educando não for encontrado, retorna 404 Not Found.
     * 
     * @param dto DTO contendo os dados do PDI a ser criado
     *            Extraído automaticamente do corpo da requisição pelo Spring (@RequestBody)
     *            Validado automaticamente pela anotação @Valid
     * @return ResponseEntity com PDIDTO do PDI criado e status HTTP 201 Created,
     *         ou 404 Not Found se o educando não existir
     */
    @PostMapping
    public ResponseEntity<PDIDTO> criar(@RequestBody @Valid CreatePDIDTO dto) {
        return handleRequest(() -> {
            PDIDTO salvo = pdiService.salvar(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
        });
    }

    /**
     * Atualiza os dados de um PDI existente.
     * 
     * Endpoint: PUT /api/pdis/{id}
     * 
     * O DTO é validado automaticamente através da anotação @Valid.
     * 
     * Utiliza o método auxiliar handleRequest() para tratamento centralizado
     * de exceções. Retorna 404 se o PDI ou educando não existirem.
     * 
     * @param id Identificador único (UUID) do PDI a ser atualizado
     *            Extraído automaticamente da URL pelo Spring (@PathVariable)
     * @param dto DTO contendo os novos dados do PDI
     *            Extraído automaticamente do corpo da requisição pelo Spring (@RequestBody)
     *            Validado automaticamente pela anotação @Valid
     * @return ResponseEntity com PDIDTO do PDI atualizado e status HTTP 200 OK,
     *         ou 404 Not Found se o PDI ou educando não existirem
     */
    @PutMapping("/{id}")
    public ResponseEntity<PDIDTO> atualizar(@PathVariable String id, @RequestBody @Valid CreatePDIDTO dto) {
        return handleRequest(() -> ResponseEntity.ok(pdiService.atualizar(id, dto)));
    }

    /**
     * Remove um PDI do sistema.
     * 
     * Endpoint: DELETE /api/pdis/{id}
     * 
     * Utiliza o método auxiliar handleRequest() para tratamento centralizado
     * de exceções. Retorna 204 No Content em caso de sucesso (padrão REST para deleção).
     * 
     * @param id Identificador único (UUID) do PDI a ser removido
     *            Extraído automaticamente da URL pelo Spring (@PathVariable)
     * @return ResponseEntity com status HTTP 204 No Content (sem corpo),
     *         ou 404 Not Found se o PDI não existir
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable String id) {
        return handleRequest(() -> {
            pdiService.deletar(id);
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
     * - ResponseEntity<PDIDTO> para métodos que retornam PDIs
     * - ResponseEntity<List<PDIDTO>> para métodos que retornam listas
     * - ResponseEntity<Void> para métodos que não retornam corpo (como DELETE)
     * 
     * Utiliza Supplier<ResponseEntity<T>> para receber uma função que executa
     * a operação e retorna o resultado. Isso permite que o código seja executado
     * dentro do try-catch deste método.
     * 
     * @param <T> Tipo genérico do corpo da resposta (PDIDTO, List<PDIDTO>, Void, etc.)
     * @param supplier Função (lambda) que retorna ResponseEntity com o resultado da operação
     *                 Esta função é executada dentro do try-catch para capturar exceções
     * @return ResponseEntity com o resultado da operação ou erro apropriado:
     *         - 404 Not Found para PDINotFoundException ou EducandoNotFoundException
     *         - Resultado da operação se tudo ocorrer bem
     */
    private <T> ResponseEntity<T> handleRequest(Supplier<ResponseEntity<T>> supplier) {
        try {
            return supplier.get();
        } catch (PDINotFoundException | EducandoNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

