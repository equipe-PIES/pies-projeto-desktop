package com.pies.api.projeto.integrado.pies_backend.controller;

import java.util.List;
import java.util.function.Supplier;

import org.springframework.http.HttpStatus;
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

import com.pies.api.projeto.integrado.pies_backend.controller.dto.EducandoDTO;
import com.pies.api.projeto.integrado.pies_backend.controller.dto.ResponsavelDTO;
import com.pies.api.projeto.integrado.pies_backend.exception.CpfJaCadastradoException;
import com.pies.api.projeto.integrado.pies_backend.exception.EducandoNotFoundException;
import com.pies.api.projeto.integrado.pies_backend.service.EducandoService;
import com.pies.api.projeto.integrado.pies_backend.service.AnamneseService;
import com.pies.api.projeto.integrado.pies_backend.service.DiagnosticoInicialService;
import com.pies.api.projeto.integrado.pies_backend.service.PDIService;
import com.pies.api.projeto.integrado.pies_backend.service.PAEEService;
import com.pies.api.projeto.integrado.pies_backend.service.RelatorioIndividualService;
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
    private final AnamneseService anamneseService;
    private final DiagnosticoInicialService diagnosticoInicialService;
    private final PDIService pdiService;
    private final PAEEService paeeService;
    private final RelatorioIndividualService relatorioIndividualService;

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
    @PreAuthorize("hasAnyRole('COORDENADOR','ADMIN')")
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
    @PreAuthorize("hasAnyRole('COORDENADOR','ADMIN')")
    public ResponseEntity<EducandoDTO> atualizar(@PathVariable String id, @RequestBody @Valid EducandoDTO dto) {
        return handleRequest(() -> ResponseEntity.ok(educandoService.atualizar(id, dto)));
    }

    /**
     * Remove um educando.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('COORDENADOR','ADMIN')")
    public ResponseEntity<Void> deletar(@PathVariable String id) {
        return handleRequest(() -> {
            educandoService.deletar(id);
            return ResponseEntity.noContent().<Void>build();
        });
    }

    @GetMapping("/{id}/progresso")
    public ResponseEntity<java.util.Map<String, Object>> progresso(@PathVariable String id) {
        java.util.Map<String, Object> m = new java.util.HashMap<>();
        try {
            var a = anamneseService.buscarPorEducando(id);
            m.put("anamnese", a != null);
        } catch (RuntimeException e) {
            m.put("anamnese", false);
        }
        try {
            var di = diagnosticoInicialService.buscarPorEducando(id);
            m.put("diagnosticoInicial", di != null);
        } catch (RuntimeException e) {
            m.put("diagnosticoInicial", false);
        }
        var pdis = (List<?>) pdiService.buscarPorEducandoId(id);
        m.put("pdiCount", pdis != null ? pdis.size() : 0);
        var paees = (List<?>) paeeService.findByAluno(id);
        m.put("paeeCount", paees != null ? paees.size() : 0);
        var ris = (List<?>) relatorioIndividualService.buscarPorEducando(id);
        m.put("relatorioCount", ris != null ? ris.size() : 0);
        return ResponseEntity.ok(m);
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
