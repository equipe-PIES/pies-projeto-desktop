package com.pies.api.projeto.integrado.pies_backend.controller;

import java.util.function.Supplier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.pies.api.projeto.integrado.pies_backend.controller.dto.AnamneseDTO;
import com.pies.api.projeto.integrado.pies_backend.exception.AnamneseAlreadyExistsException;
import com.pies.api.projeto.integrado.pies_backend.exception.AnamneseNotFoundException;
import com.pies.api.projeto.integrado.pies_backend.exception.EducandoNotFoundException;
import com.pies.api.projeto.integrado.pies_backend.exception.ProfessorNotFoundException;
import com.pies.api.projeto.integrado.pies_backend.service.AnamneseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/anamneses")
@RequiredArgsConstructor
public class AnamneseController {

    private final AnamneseService anamneseService;

    @GetMapping("/{id}")
    public ResponseEntity<AnamneseDTO> buscarPorId(@PathVariable String id) {
        return handleRequest(() -> ResponseEntity.ok(anamneseService.buscarPorId(id)));
    }

    @GetMapping("/educando/{educandoId}")
    public ResponseEntity<AnamneseDTO> buscarPorEducando(@PathVariable String educandoId) {
        return handleRequest(() -> ResponseEntity.ok(anamneseService.buscarPorEducando(educandoId)));
    }

    @PostMapping("/educando/{educandoId}")
    public ResponseEntity<AnamneseDTO> criar(@PathVariable String educandoId,
                                             @RequestBody @Valid AnamneseDTO dto) {
        return handleRequest(() -> {
            AnamneseDTO salvo = anamneseService.criar(educandoId, dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
        });
    }

    @PutMapping("/educando/{educandoId}")
    public ResponseEntity<AnamneseDTO> atualizar(@PathVariable String educandoId,
                                                 @RequestBody @Valid AnamneseDTO dto) {
        return handleRequest(() -> ResponseEntity.ok(anamneseService.atualizar(educandoId, dto)));
    }

    @DeleteMapping("/educando/{educandoId}")
    public ResponseEntity<Void> deletarPorEducando(@PathVariable String educandoId) {
        return handleRequest(() -> {
            anamneseService.deletarPorEducando(educandoId);
            return ResponseEntity.noContent().build();
        });
    }

    private <T> ResponseEntity<T> handleRequest(Supplier<ResponseEntity<T>> supplier) {
        try {
            return supplier.get();
        } catch (EducandoNotFoundException | AnamneseNotFoundException | ProfessorNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (AnamneseAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }
}

