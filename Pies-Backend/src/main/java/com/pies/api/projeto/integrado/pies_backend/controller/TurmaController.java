
package com.pies.api.projeto.integrado.pies_backend.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pies.api.projeto.integrado.pies_backend.controller.dto.CreateTurmaDTO;
import com.pies.api.projeto.integrado.pies_backend.controller.dto.TurmaDTO;
import com.pies.api.projeto.integrado.pies_backend.model.Turma;
import com.pies.api.projeto.integrado.pies_backend.model.User;
import com.pies.api.projeto.integrado.pies_backend.repository.TurmaRepository;
import com.pies.api.projeto.integrado.pies_backend.repository.UserRepository;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/turmas")
public class TurmaController {

    private final TurmaRepository turmaRepository;
    private final UserRepository userRepository;

    public TurmaController(TurmaRepository turmaRepository, UserRepository userRepository) {
        this.turmaRepository = turmaRepository;
        this.userRepository = userRepository;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('COORDENADOR','ADMIN')")
    @Transactional
    public ResponseEntity<TurmaDTO> criar(@RequestBody @Valid CreateTurmaDTO payload) {
        User professor = Optional.ofNullable(userRepository.findByEmail(payload.professorId()))
                .orElseGet(() -> userRepository.findById(payload.professorId()).orElse(null));
        if (professor == null) {
            return ResponseEntity.badRequest().build();
        }

        Turma turma = new Turma();
        turma.setNome(payload.nome());
        turma.setGrauEscolar(payload.grauEscolar());
        turma.setFaixaEtaria(payload.faixaEtaria());
        turma.setTurno(payload.turno());
        turma.setProfessor(professor);

        Turma salva = turmaRepository.save(turma);
        return ResponseEntity.ok(mapToDTO(salva));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('COORDENADOR','ADMIN','PROFESSOR')")
    public ResponseEntity<List<TurmaDTO>> listar() {
        List<TurmaDTO> lista = turmaRepository.findAll().stream().map(this::mapToDTO).toList();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('COORDENADOR','ADMIN','PROFESSOR')")
    public ResponseEntity<TurmaDTO> obter(@PathVariable String id) {
        return turmaRepository.findById(id)
                .map(t -> ResponseEntity.ok(mapToDTO(t)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('COORDENADOR','ADMIN')")
    @Transactional
    public ResponseEntity<TurmaDTO> atualizar(@PathVariable String id, @RequestBody @Valid CreateTurmaDTO payload) {
        Optional<Turma> opt = turmaRepository.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Turma turma = opt.get();

        User professor = Optional.ofNullable(userRepository.findByEmail(payload.professorId()))
                .orElseGet(() -> userRepository.findById(payload.professorId()).orElse(null));
        if (professor == null) {
            return ResponseEntity.badRequest().build();
        }

        turma.setNome(payload.nome());
        turma.setGrauEscolar(payload.grauEscolar());
        turma.setFaixaEtaria(payload.faixaEtaria());
        turma.setTurno(payload.turno());
        turma.setProfessor(professor);

        Turma salva = turmaRepository.save(turma);
        return ResponseEntity.ok(mapToDTO(salva));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('COORDENADOR','ADMIN')")
    @Transactional
    public ResponseEntity<Void> excluir(@PathVariable String id) {
        if (!turmaRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        turmaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private TurmaDTO mapToDTO(Turma turma) {
        String professorId = turma.getProfessor() != null ? turma.getProfessor().getId() : null;
        String professorNome = turma.getProfessor() != null ? turma.getProfessor().getName() : null;
        return new TurmaDTO(
            turma.getId(),
            turma.getNome(),
            turma.getGrauEscolar(),
            turma.getFaixaEtaria(),
            turma.getTurno(),
            professorId,
            professorNome
        );
    }
}
