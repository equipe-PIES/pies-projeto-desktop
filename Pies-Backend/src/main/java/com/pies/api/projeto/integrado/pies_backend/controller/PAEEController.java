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

import com.pies.api.projeto.integrado.pies_backend.controller.dto.CreatePAEEDTO;
import com.pies.api.projeto.integrado.pies_backend.controller.dto.PAEEDTO;
import com.pies.api.projeto.integrado.pies_backend.exception.EducandoNotFoundException;
import com.pies.api.projeto.integrado.pies_backend.exception.PAEENotFoundException;
import com.pies.api.projeto.integrado.pies_backend.exception.ProfessorNotFoundException;
import com.pies.api.projeto.integrado.pies_backend.service.PAEEService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controller REST responsável pelos endpoints relacionados aos PAEEs.
 * 
 * Esta classe expõe os endpoints da API REST para operações CRUD (Create, Read, Update, Delete)
 * relacionadas aos Planos de Atendimento Educacional Especializado. Utiliza o padrão RESTful com mapeamento
 * de métodos HTTP para operações específicas.
 * 
 * O tratamento de exceções é centralizado no método auxiliar handleRequest(),
 * reduzindo código repetitivo e facilitando a manutenção.
 * 
 * Endpoints disponíveis:
 * - GET    /api/paees                    - Lista todos os PAEEs
 * - GET    /api/paees/{id}               - Busca um PAEE por ID
 * - GET    /api/paees/educando/{educandoId} - Busca PAEEs de um educando
 * - POST   /api/paees                    - Cria um novo PAEE
 * - PUT    /api/paees/{id}               - Atualiza um PAEE existente
 * - DELETE /api/paees/{id}               - Remove um PAEE
 */
@RestController
@RequestMapping("/api/paees")
@RequiredArgsConstructor
public class PAEEController {

    /**
     * Serviço responsável pela lógica de negócio dos PAEEs.
     * Injetado automaticamente pelo Spring através do construtor gerado pelo Lombok
     * (@RequiredArgsConstructor cria um construtor que recebe todos os campos final).
     */
    private final PAEEService paeeService;

    /**
     * Lista todos os PAEEs cadastrados no sistema.
     * 
     * Endpoint: GET /api/paees
     * 
     * Este método não precisa de tratamento de exceções pois sempre retorna
     * uma lista (pode estar vazia, mas nunca lança exceção).
     * 
     * @return ResponseEntity com lista de PAEEDTO e status HTTP 200 OK
     */
    @GetMapping
    public ResponseEntity<List<PAEEDTO>> listarTodos() {
        return ResponseEntity.ok(paeeService.listarTodos());
    }

    /**
     * Busca um PAEE específico pelo seu ID.
     * 
     * Endpoint: GET /api/paees/{id}
     * 
     * Utiliza o método auxiliar handleRequest() para tratamento centralizado
     * de exceções. Se o PAEE não for encontrado, retorna 404 Not Found.
     * 
     * @param id Identificador único (UUID) do PAEE a ser buscado
     *            Extraído automaticamente da URL pelo Spring (@PathVariable)
     * @return ResponseEntity com PAEEDTO e status HTTP 200 OK,
     *         ou 404 Not Found se o PAEE não existir
     */
    @GetMapping("/{id}")
    public ResponseEntity<PAEEDTO> buscarPorId(@PathVariable String id) {
        return handleRequest(() -> ResponseEntity.ok(paeeService.buscarPorId(id)));
    }

    /**
     * Busca todos os PAEEs de um educando específico.
     * 
     * Endpoint: GET /api/paees/educando/{educandoId}
     * 
     * Utiliza o método auxiliar handleRequest() para tratamento centralizado
     * de exceções. Se o educando não for encontrado, retorna 404 Not Found.
     * 
     * @param educandoId Identificador único (UUID) do educando
     *                   Extraído automaticamente da URL pelo Spring (@PathVariable)
     * @return ResponseEntity com lista de PAEEDTO e status HTTP 200 OK,
     *         ou 404 Not Found se o educando não existir
     */
    @GetMapping("/educando/{educandoId}")
    public ResponseEntity<List<PAEEDTO>> buscarPorEducandoId(@PathVariable String educandoId) {
        return handleRequest(() -> ResponseEntity.ok(paeeService.buscarPorEducandoId(educandoId)));
    }

    /**
     * Cria um novo PAEE no sistema.
     * 
     * Endpoint: POST /api/paees
     * 
     * O DTO é validado automaticamente através da anotação @Valid.
     * Validações incluem: campos obrigatórios, tamanho máximo de texto, etc.
     * 
     * Utiliza o método auxiliar handleRequest() para tratamento centralizado
     * de exceções. Se o educando não for encontrado, retorna 404 Not Found.
     * 
     * @param dto DTO contendo os dados do PAEE a ser criado
     *            Extraído automaticamente do corpo da requisição pelo Spring (@RequestBody)
     *            Validado automaticamente pela anotação @Valid
     * @return ResponseEntity com PAEEDTO do PAEE criado e status HTTP 201 Created,
     *         ou 404 Not Found se o educando não existir
     */
    @PostMapping
    public ResponseEntity<PAEEDTO> criar(@RequestBody @Valid CreatePAEEDTO dto) {
        return handleRequest(() -> {
            PAEEDTO salvo = paeeService.salvar(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
        });
    }

    /**
     * Atualiza os dados de um PAEE existente.
     * 
     * Endpoint: PUT /api/paees/{id}
     * 
     * O DTO é validado automaticamente através da anotação @Valid.
     * 
     * Utiliza o método auxiliar handleRequest() para tratamento centralizado
     * de exceções. Retorna 404 se o PAEE ou educando não existirem.
     * 
     * @param id Identificador único (UUID) do PAEE a ser atualizado
     *            Extraído automaticamente da URL pelo Spring (@PathVariable)
     * @param dto DTO contendo os novos dados do PAEE
     *            Extraído automaticamente do corpo da requisição pelo Spring (@RequestBody)
     *            Validado automaticamente pela anotação @Valid
     * @return ResponseEntity com PAEEDTO do PAEE atualizado e status HTTP 200 OK,
     *         ou 404 Not Found se o PAEE ou educando não existirem
     */
    @PutMapping("/{id}")
    public ResponseEntity<PAEEDTO> atualizar(@PathVariable String id, @RequestBody @Valid CreatePAEEDTO dto) {
        return handleRequest(() -> ResponseEntity.ok(paeeService.atualizar(id, dto)));
    }

    /**
     * Remove um PAEE do sistema.
     * 
     * Endpoint: DELETE /api/paees/{id}
     * 
     * Utiliza o método auxiliar handleRequest() para tratamento centralizado
     * de exceções. Retorna 204 No Content em caso de sucesso (padrão REST para deleção).
     * 
     * @param id Identificador único (UUID) do PAEE a ser removido
     *            Extraído automaticamente da URL pelo Spring (@PathVariable)
     * @return ResponseEntity com status HTTP 204 No Content (sem corpo),
     *         ou 404 Not Found se o PAEE não existir
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable String id) {
        return handleRequest(() -> {
            paeeService.deletar(id);
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
     * - ResponseEntity<PAEEDTO> para métodos que retornam PAEEs
     * - ResponseEntity<List<PAEEDTO>> para métodos que retornam listas
     * - ResponseEntity<Void> para métodos que não retornam corpo (como DELETE)
     * 
     * Utiliza Supplier<ResponseEntity<T>> para receber uma função que executa
     * a operação e retorna o resultado. Isso permite que o código seja executado
     * dentro do try-catch deste método.
     * 
     * @param <T> Tipo genérico do corpo da resposta (PAEEDTO, List<PAEEDTO>, Void, etc.)
     * @param supplier Função (lambda) que retorna ResponseEntity com o resultado da operação
     *                 Esta função é executada dentro do try-catch para capturar exceções
     * @return ResponseEntity com o resultado da operação ou erro apropriado:
     *         - 404 Not Found para PAEENotFoundException ou EducandoNotFoundException
     *         - Resultado da operação se tudo ocorrer bem
     */
    private <T> ResponseEntity<T> handleRequest(Supplier<ResponseEntity<T>> supplier) {
        try {
            return supplier.get();
        } catch (PAEENotFoundException | EducandoNotFoundException | ProfessorNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

