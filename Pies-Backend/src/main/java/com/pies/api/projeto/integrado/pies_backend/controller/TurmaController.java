package com.pies.api.projeto.integrado.pies_backend.controller;

import java.util.List;
import java.util.function.Supplier;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.pies.api.projeto.integrado.pies_backend.controller.dto.CreateTurmaDTO;
import com.pies.api.projeto.integrado.pies_backend.controller.dto.TurmaDTO;
import com.pies.api.projeto.integrado.pies_backend.service.TurmaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controller REST para gerenciamento de Turmas.
 */
@RestController
@RequestMapping("/turmas")
@RequiredArgsConstructor
public class TurmaController {

    /**
     * Injetamos APENAS o Service. 
     */
    private final TurmaService turmaService;

    /**
     * Cria uma nova turma.
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('COORDENADOR','ADMIN')")
    public ResponseEntity<TurmaDTO> criar(@RequestBody @Valid CreateTurmaDTO payload) {
        return handleRequest(() -> {
            TurmaDTO novaTurma = turmaService.salvar(payload);
            return ResponseEntity.status(HttpStatus.CREATED).body(novaTurma);
        });
    }

    /**
     * Lista todas as turmas.
     * Usa o método otimizado do Service (1 consulta SQL apenas).
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('COORDENADOR','ADMIN','PROFESSOR')")
    public ResponseEntity<List<TurmaDTO>> listar() {
        return ResponseEntity.ok(turmaService.listarTodas());
    }

    /**
     * Busca turma por ID.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('COORDENADOR','ADMIN','PROFESSOR')")
    public ResponseEntity<TurmaDTO> obter(@PathVariable String id) {
        return handleRequest(() -> ResponseEntity.ok(turmaService.buscarPorId(id)));
    }

    /**
     * Atualiza uma turma.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('COORDENADOR','ADMIN')")
    public ResponseEntity<TurmaDTO> atualizar(@PathVariable String id, @RequestBody @Valid CreateTurmaDTO payload) {
        return handleRequest(() -> ResponseEntity.ok(turmaService.atualizar(id, payload)));
    }

    /**
     * Exclui uma turma.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('COORDENADOR','ADMIN')")
    public ResponseEntity<Void> excluir(@PathVariable String id) {
        return handleRequest(() -> {
            turmaService.deletar(id);
            return ResponseEntity.noContent().<Void>build();
        });
    }

    /**
     * Tratamento centralizado de exceções.
     */
    private <T> ResponseEntity<T> handleRequest(Supplier<ResponseEntity<T>> supplier) {
        try {
            return supplier.get();
        } catch (RuntimeException e) {
            // Log simples para debug
            System.err.println("Erro na requisição: " + e.getMessage());
            
            if (e.getMessage().contains("não encontrada") || e.getMessage().contains("não encontrado")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().build();
        }
    }
}