package com.pies.api.projeto.integrado.pies_backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pies.api.projeto.integrado.pies_backend.controller.dto.CreateDiagnosticoInicialDTO;
import com.pies.api.projeto.integrado.pies_backend.controller.dto.DiagnosticoInicialDTO;
import com.pies.api.projeto.integrado.pies_backend.model.User;
import com.pies.api.projeto.integrado.pies_backend.service.DiagnosticoInicialService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/diagnosticos-iniciais")
@RequiredArgsConstructor
public class DiagnosticoInicialController {

    private final DiagnosticoInicialService service;

    @PostMapping("/educando/{educandoId}")
    public ResponseEntity<?> criar(@PathVariable String educandoId, @Valid @RequestBody CreateDiagnosticoInicialDTO dto) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Object principal = authentication.getPrincipal();
            String userId;
            if (principal instanceof User user) {
                userId = user.getId();
            } else {
                userId = principal.toString();
            }
            DiagnosticoInicialDTO salvo = service.salvar(dto, userId, educandoId);
            return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    private record ErrorResponse(String message) {}

    @GetMapping
    public ResponseEntity<List<DiagnosticoInicialDTO>> listarTodos() {
        List<DiagnosticoInicialDTO> lista = service.listarTodos();
        if (lista.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DiagnosticoInicialDTO> buscarPorId(@PathVariable String id) {
        try {
            return ResponseEntity.ok(service.buscarPorId(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/educando/{educandoId}")
    public ResponseEntity<DiagnosticoInicialDTO> atualizar(@PathVariable String id,
                                                           @PathVariable String educandoId,
                                                           @Valid @RequestBody CreateDiagnosticoInicialDTO dto) {
        try {
            return ResponseEntity.ok(service.atualizar(id, educandoId, dto));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable String id) {
        try {
            service.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/educando/{educandoId}")
    public ResponseEntity<DiagnosticoInicialDTO> buscarPorEducando(@PathVariable String educandoId) {
        try {
            return ResponseEntity.ok(service.buscarPorEducando(educandoId));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    
}
