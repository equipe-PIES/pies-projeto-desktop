package com.pies.api.projeto.integrado.pies_backend.controller;

import java.util.List;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

import com.pies.api.projeto.integrado.pies_backend.controller.dto.CreateRelatorioIndividualDTO;
import com.pies.api.projeto.integrado.pies_backend.controller.dto.RelatorioIndividualDTO;
import com.pies.api.projeto.integrado.pies_backend.model.User;
import com.pies.api.projeto.integrado.pies_backend.service.RelatorioIndividualService;
import com.pies.api.projeto.integrado.pies_backend.service.RelatorioPDFService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controller REST responsável pelos endpoints de Relatório Individual.
 * 
 * Base URL: /api/relatorios-individuais
 * 
 * Endpoints disponíveis:
 * - POST   /api/relatorios-individuais           - Criar novo relatório
 * - GET    /api/relatorios-individuais           - Listar todos os relatórios
 * - GET    /api/relatorios-individuais/{id}      - Buscar relatório por ID
 * - PUT    /api/relatorios-individuais/{id}      - Atualizar relatório existente
 * - DELETE /api/relatorios-individuais/{id}      - Deletar relatório
 * - GET    /api/relatorios-individuais/educando/{educandoId} - Buscar relatórios por educando
 */
@RestController
@RequestMapping("/api/relatorios-individuais")
@RequiredArgsConstructor
public class RelatorioIndividualController {

    private final RelatorioIndividualService relatorioService;
    private final RelatorioPDFService pdfService;

    /**
     * Cria um novo relatório individual.
     * 
     * O professor logado é automaticamente associado ao relatório através
     * do contexto de segurança do Spring Security. O userId do User é usado
     * para buscar o Professor correspondente.
     * 
     * POST /api/relatorios-individuais
     * 
     * @param dto Dados do relatório a ser criado
     * @return 201 Created com o relatório criado ou 400 Bad Request em caso de erro
     */
    @PostMapping
    public ResponseEntity<?> criar(@Valid @RequestBody CreateRelatorioIndividualDTO dto) {
        try {
            // Obtém o ID do usuário logado do contexto de segurança
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User user = (User) authentication.getPrincipal();
            String userId = user.getId();
            
            RelatorioIndividualDTO relatorio = relatorioService.salvar(dto, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(relatorio);
        } catch (RuntimeException e) {
            // Retorna mensagem de erro detalhada
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Classe auxiliar para retornar mensagens de erro
     */
    private record ErrorResponse(String message) {}


    /**
     * Lista todos os relatórios individuais cadastrados.
     * 
     * GET /api/relatorios-individuais
     * 
     * @return 200 OK com a lista de relatórios ou 204 No Content se não houver relatórios
     */
    @GetMapping
    public ResponseEntity<List<RelatorioIndividualDTO>> listarTodos() {
        List<RelatorioIndividualDTO> relatorios = relatorioService.listarTodos();
        if (relatorios.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(relatorios);
    }

    /**
     * Busca um relatório específico pelo ID.
     * 
     * GET /api/relatorios-individuais/{id}
     * 
     * @param id ID do relatório
     * @return 200 OK com o relatório ou 404 Not Found se não encontrado
     */
    @GetMapping("/{id}")
    public ResponseEntity<RelatorioIndividualDTO> buscarPorId(@PathVariable String id) {
        try {
            RelatorioIndividualDTO relatorio = relatorioService.buscarPorId(id);
            return ResponseEntity.ok(relatorio);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Atualiza um relatório existente.
     * 
     * PUT /api/relatorios-individuais/{id}
     * 
     * @param id ID do relatório a ser atualizado
     * @param dto Novos dados do relatório
     * @return 200 OK com o relatório atualizado ou 404 Not Found se não encontrado
     */
    @PutMapping("/{id}")
    public ResponseEntity<RelatorioIndividualDTO> atualizar(
            @PathVariable String id,
            @Valid @RequestBody CreateRelatorioIndividualDTO dto) {
        try {
            RelatorioIndividualDTO relatorio = relatorioService.atualizar(id, dto);
            return ResponseEntity.ok(relatorio);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Deleta um relatório pelo ID.
     * 
     * DELETE /api/relatorios-individuais/{id}
     * 
     * @param id ID do relatório a ser deletado
     * @return 204 No Content em sucesso ou 404 Not Found se não encontrado
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable String id) {
        try {
            relatorioService.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Busca todos os relatórios de um educando específico.
     * 
     * GET /api/relatorios-individuais/educando/{educandoId}
     * 
     * @param educandoId ID do educando
     * @return 200 OK com a lista de relatórios ou 204 No Content se não houver relatórios
     */
    @GetMapping("/educando/{educandoId}")
    public ResponseEntity<List<RelatorioIndividualDTO>> buscarPorEducando(@PathVariable String educandoId) {
        List<RelatorioIndividualDTO> relatorios = relatorioService.buscarPorEducando(educandoId);
        if (relatorios.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(relatorios);
    }

    /**
     * Gera e retorna o PDF do relatório individual.
     * 
     * GET /api/relatorios-individuais/{id}/pdf
     * 
     * Este endpoint gera um PDF profissional e formatado com todas as informações
     * do relatório individual do educando. O PDF é retornado como download.
     * 
     * @param id ID do relatório individual
     * @return 200 OK com o PDF em formato application/pdf ou 404 Not Found se não encontrado
     */
    @GetMapping("/{id}/pdf")
    public ResponseEntity<Resource> gerarPDF(@PathVariable String id) {
        try {
            // Busca o relatório pelo ID
            RelatorioIndividualDTO relatorio = relatorioService.buscarPorId(id);
            
            // Gera o PDF usando o serviço
            byte[] pdfBytes = pdfService.gerarPDF(relatorio);
            
            // Valida se o PDF foi gerado corretamente
            if (pdfBytes == null || pdfBytes.length == 0) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ByteArrayResource("Erro ao gerar PDF".getBytes()));
            }
            
            // Cria o recurso a partir dos bytes do PDF
            ByteArrayResource resource = new ByteArrayResource(pdfBytes);
            
            // Gera nome do arquivo baseado no nome do educando e ID do relatório
            String nomeArquivo = "Relatorio_Final_" + 
                    (relatorio.educandoNome() != null ? 
                     relatorio.educandoNome().replaceAll("\\s+", "_").replaceAll("[^a-zA-Z0-9_]", "") : 
                     "Educando") + 
                    "_" + relatorio.id().substring(0, Math.min(8, relatorio.id().length())) + ".pdf";
            
            // Retorna o PDF com headers apropriados para download
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nomeArquivo + "\"; filename*=UTF-8''" + nomeArquivo)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
                    .header(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate")
                    .header(HttpHeaders.PRAGMA, "no-cache")
                    .header(HttpHeaders.EXPIRES, "0")
                    .contentLength(pdfBytes.length)
                    .body(resource);
                    
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ByteArrayResource(("Erro ao gerar PDF: " + e.getMessage()).getBytes()));
        }
    }
}
