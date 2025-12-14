package com.pies.api.projeto.integrado.pies_backend.controller;

import com.pies.api.projeto.integrado.pies_backend.controller.dto.CreatePAEEDTO;
import com.pies.api.projeto.integrado.pies_backend.controller.dto.PAEEDTO;
import com.pies.api.projeto.integrado.pies_backend.service.PAEEService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/paee")
@CrossOrigin(origins = "*") 
public class PAEEController {

    private final PAEEService service;

    public PAEEController(PAEEService service) {
        this.service = service;
    }

    // Endpoint para criar um novo PAEE
    @PostMapping
    public ResponseEntity<PAEEDTO> create(@RequestBody CreatePAEEDTO dto) {
        // LOG: Mostra usuário autenticado e suas roles
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            System.out.println("=== PAEEController DEBUG ===");
            System.out.println("Usuário autenticado: " + auth.getName());
            System.out.println("Authorities: " + auth.getAuthorities());
        } else {
            System.out.println("=== PAEEController DEBUG: Authentication NULL ===");
        }
        PAEEDTO created = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // Endpoint para ver os detalhes de um PAEE específico
    @GetMapping("/{id}")
    public ResponseEntity<PAEEDTO> findById(@PathVariable String id) {
        return ResponseEntity.ok(service.findById(id));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<PAEEDTO> update(@PathVariable String id, @RequestBody CreatePAEEDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Endpoint para listar todos os PAEEs de um aluno
    @GetMapping("/aluno/{alunoId}")
    public ResponseEntity<List<PAEEDTO>> listByAluno(@PathVariable String alunoId) {
        return ResponseEntity.ok(service.findByAluno(alunoId));
    }
}
