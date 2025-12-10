package com.pies.api.projeto.integrado.pies_backend.controller;

import java.io.InputStream;
import java.util.List;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
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
 * * Gerencia todas as operações relacionadas aos relatórios dos educandos,
 * incluindo criação, listagem, atualização, remoção e geração de arquivos PDF.
 * * Base URL: /api/relatorios-individuais
 */
@RestController
@RequestMapping("/api/relatorios-individuais")
@RequiredArgsConstructor
public class RelatorioIndividualController {

    private final RelatorioIndividualService relatorioService;
    private final RelatorioPDFService pdfService;

    /**
     * Cria um novo relatório individual.
     * * O professor logado é automaticamente associado ao relatório através
     * do contexto de segurança do Spring Security.
     * * Método: POST
     * URL: /api/relatorios-individuais
     * * @param dto Dados do relatório a ser criado (validado via @Valid)
     * @return 201 Created com o relatório criado ou 400 Bad Request em caso de erro
     */
    @PostMapping
    public ResponseEntity<?> criar(@Valid @RequestBody CreateRelatorioIndividualDTO dto) {
        try {
            // Obtém o usuário logado do contexto de segurança para vincular ao relatório
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User user = (User) authentication.getPrincipal();
            String userId = user.getId();
            
            RelatorioIndividualDTO relatorio = relatorioService.salvar(dto, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(relatorio);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Record auxiliar para padronizar o retorno de mensagens de erro JSON.
     * @param message A mensagem de erro explicativa.
     */
    private record ErrorResponse(String message) {}

    /**
     * Lista todos os relatórios individuais cadastrados no sistema.
     * * Método: GET
     * URL: /api/relatorios-individuais
     * * @return 200 OK com a lista de relatórios ou 204 No Content se a lista estiver vazia
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
     * Busca um relatório específico pelo seu identificador único.
     * * Método: GET
     * URL: /api/relatorios-individuais/{id}
     * * @param id ID do relatório a ser buscado
     * @return 200 OK com o relatório encontrado ou 404 Not Found se não existir
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
     * Atualiza os dados de um relatório existente.
     * * Método: PUT
     * URL: /api/relatorios-individuais/{id}
     * * @param id ID do relatório a ser atualizado
     * @param dto Novos dados para atualização
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
     * Remove um relatório do sistema permanentemente.
     * * Método: DELETE
     * URL: /api/relatorios-individuais/{id}
     * * @param id ID do relatório a ser removido
     * @return 204 No Content se removido com sucesso ou 404 Not Found se não existir
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
     * Busca todos os relatórios vinculados a um educando específico.
     * Útil para ver o histórico de avaliações de um aluno.
     * * Método: GET
     * URL: /api/relatorios-individuais/educando/{educandoId}
     * * @param educandoId ID do educando
     * @return 200 OK com a lista de relatórios ou 204 No Content se vazio
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
     * Gera e retorna o arquivo PDF do relatório individual.
     * * Este endpoint recupera os dados do relatório e tenta carregar o logo institucional
     * da pasta 'src/main/resources/img'. Caso o logo exista, ele é inserido no PDF.
     * O arquivo é retornado pronto para download no navegador.
     * * Método: GET
     * URL: /api/relatorios-individuais/{id}/pdf
     * * @param id ID do relatório individual para geração do documento
     * @return 200 OK com o recurso (PDF) ou erro adequado (404/500)
     */
    @GetMapping("/{id}/pdf")
    public ResponseEntity<Resource> gerarPDF(@PathVariable String id) {
        try {
            // 1. Busca os dados do relatório no banco
            RelatorioIndividualDTO relatorio = relatorioService.buscarPorId(id);
            
            // 2. Carrega a imagem do logo institucional
            // O arquivo deve estar em: src/main/resources/img/logo_apapeq.png
            byte[] imagemBytes = null;
            try {
                ClassPathResource imageResource = new ClassPathResource("img/logo_apapeq.png");
                
                // Verifica se o arquivo existe antes de tentar ler
                if (imageResource.exists()) {
                    try (InputStream inputStream = imageResource.getInputStream()) {
                        imagemBytes = inputStream.readAllBytes();
                    }
                } else {
                    // Log silencioso ou aviso no console do servidor
                    System.out.println("AVISO: Logo 'img/logo_apapeq.png' não encontrado em resources. O PDF será gerado sem logo.");
                }
            } catch (Exception e) {
                // Captura erros de IO, permissão, etc., para não impedir a geração do PDF
                System.err.println("ERRO ao carregar imagem do logo: " + e.getMessage());
            }

            // 3. Chama o serviço de PDF passando os dados E a imagem (que pode ser null)
            byte[] pdfBytes = pdfService.gerarPDF(relatorio, imagemBytes);
            
            // Valida se o PDF foi gerado corretamente (não está vazio)
            if (pdfBytes == null || pdfBytes.length == 0) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ByteArrayResource("Erro crítico: O arquivo PDF gerado está vazio.".getBytes()));
            }
            
            // Cria o recurso para resposta HTTP
            ByteArrayResource resource = new ByteArrayResource(pdfBytes);
            
            // 4. Formata o nome do arquivo para download (Sanitização básica de nome)
            String nomeEducando = relatorio.educandoNome() != null ? 
                    relatorio.educandoNome().replaceAll("\\s+", "_").replaceAll("[^a-zA-Z0-9_]", "") : 
                    "Educando";
            
            // Nome final: Relatorio_Final_NomeDoAluno_IDcurto.pdf
            String nomeArquivo = "Relatorio_Final_" + nomeEducando + "_" + 
                               relatorio.id().substring(0, Math.min(8, relatorio.id().length())) + ".pdf";
            
            // 5. Configura os headers de resposta para forçar o download ou visualização correta
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nomeArquivo + "\"; filename*=UTF-8''" + nomeArquivo)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
                    // Headers para evitar cache do navegador em downloads dinâmicos
                    .header(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate")
                    .header(HttpHeaders.PRAGMA, "no-cache")
                    .header(HttpHeaders.EXPIRES, "0")
                    .contentLength(pdfBytes.length)
                    .body(resource);
                    
        } catch (RuntimeException e) {
            // Relatório não encontrado no banco
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            // Erro genérico de servidor
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ByteArrayResource(("Erro interno ao gerar PDF: " + e.getMessage()).getBytes()));
        }
    }
}