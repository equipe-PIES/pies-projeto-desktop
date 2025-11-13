
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
import com.pies.api.projeto.integrado.pies_backend.model.Professor;
import com.pies.api.projeto.integrado.pies_backend.model.Turma;
import com.pies.api.projeto.integrado.pies_backend.repository.ProfessorRepository;
import com.pies.api.projeto.integrado.pies_backend.repository.TurmaRepository;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/turmas")
public class TurmaController {

    private final TurmaRepository turmaRepository;
    private final ProfessorRepository professorRepository;

    public TurmaController(TurmaRepository turmaRepository, ProfessorRepository professorRepository) {
        this.turmaRepository = turmaRepository;
        this.professorRepository = professorRepository;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('COORDENADOR','ADMIN')")
    @Transactional
    public ResponseEntity<TurmaDTO> criar(@RequestBody @Valid CreateTurmaDTO payload) {
        System.out.println("=== CRIANDO TURMA ===");
        System.out.println("Professor ID recebido: " + payload.professorId());
        
        Optional<Professor> professorOpt = professorRepository.findById(payload.professorId());
        
        if (professorOpt.isEmpty()) {
            System.err.println("ERRO: Professor não encontrado na tabela professores! ID: " + payload.professorId());
            return ResponseEntity.badRequest().build();
        }
        
        Professor professor = professorOpt.get();
        System.out.println("Professor encontrado: " + professor.getNome() + " (ID: " + professor.getId() + ")");

        Turma turma = new Turma();
        turma.setNome(payload.nome());
        turma.setGrauEscolar(payload.grauEscolar());
        turma.setFaixaEtaria(payload.faixaEtaria());
        turma.setTurno(payload.turno());
        turma.setProfessor(professor);

        Turma salva = turmaRepository.save(turma);
        System.out.println("✓ Turma criada: " + salva.getNome());
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

        Optional<Professor> professorOpt = professorRepository.findById(payload.professorId());
        if (professorOpt.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        Professor professor = professorOpt.get();

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
        String professorNome = turma.getProfessor() != null ? turma.getProfessor().getNome() : null;
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
