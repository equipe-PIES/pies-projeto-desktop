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
 * * Agora reflete a arquitetura de Responsável Único (1:1) e utiliza
 * os serviços otimizados para performance.
 */
@RestController
@RequestMapping("/api/educandos")
@RequiredArgsConstructor
public class EducandoController {

    private final EducandoService educandoService;

    /**
     * Lista todos os educandos.
     * O Service agora usa "findAllCompleto" para trazer tudo em 1 query.
     */
    @GetMapping
    public ResponseEntity<List<EducandoDTO>> listarTodos() {
        return ResponseEntity.ok(educandoService.listarTodos());
    }

    /**
     * Lista resumida (se houver implementação específica no service).
     */
    @GetMapping("/simplificados")
    public ResponseEntity<List<EducandoDTO>> listarSimplificados() {
        return ResponseEntity.ok(educandoService.listarTodos());
    }

    /**
     * Busca um educando por ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<EducandoDTO> buscarPorId(@PathVariable String id) {
        return handleRequest(() -> ResponseEntity.ok(educandoService.buscarPorId(id)));
    }

    /**
     * Cria um novo educando.
     */
    @PostMapping
    public ResponseEntity<EducandoDTO> criar(@RequestBody @Valid EducandoDTO dto) {
        return handleRequest(() -> {
            EducandoDTO salvo = educandoService.salvar(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
        });
    }

    /**
     * Lista alunos por turma.
     */
    @GetMapping("/turma/{turmaId}")
    public ResponseEntity<List<EducandoDTO>> listarPorTurma(@PathVariable String turmaId) {
        return ResponseEntity.ok(educandoService.listarPorTurma(turmaId));
    }

    /**
     * Atualiza dados básicos do educando.
     */
    @PutMapping("/{id}")
    public ResponseEntity<EducandoDTO> atualizar(@PathVariable String id, @RequestBody @Valid EducandoDTO dto) {
        return handleRequest(() -> ResponseEntity.ok(educandoService.atualizar(id, dto)));
    }

    /**
     * Remove um educando.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable String id) {
        return handleRequest(() -> {
            educandoService.deletar(id);
            return ResponseEntity.noContent().<Void>build();
        });
    }

    /**
     * Define ou substitui o Responsável do aluno.
     * * Endpoint: PUT /api/educandos/{id}/responsavel
     * * MUDANÇA:
     * - URL no singular ("responsavel").
     * - Verbo PUT (semântica de "definir/substituir").
     * - Chama o novo método "definirResponsavel" do service.
     */
    @PutMapping("/{id}/responsavel")
    public ResponseEntity<EducandoDTO> definirResponsavel(
            @PathVariable String id,
            @RequestBody @Valid ResponsavelDTO responsavelDTO) {

        return handleRequest(() -> {
            // Chama o método atualizado do service
            EducandoDTO atualizado = educandoService.definirResponsavel(id, responsavelDTO);
            return ResponseEntity.ok(atualizado);
        });
    }

    // ========================================================================
    // MÉTODO AUXILIAR DE TRATAMENTO DE ERROS
    // ========================================================================

    private <T> ResponseEntity<T> handleRequest(Supplier<ResponseEntity<T>> supplier) {
        try {
            return supplier.get();
        } catch (EducandoNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (CpfJaCadastradoException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }
}