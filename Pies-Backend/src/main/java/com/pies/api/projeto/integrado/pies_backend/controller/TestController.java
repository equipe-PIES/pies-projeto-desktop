package com.pies.api.projeto.integrado.pies_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller de teste para validar o sistema de autenticação e autorização.
 * Contém endpoints para testar diferentes níveis de acesso e verificar
 * se o isolamento de roles está funcionando corretamente.
 * 
 * Este controller é usado apenas para testes e deve ser removido em produção.
 */
@RestController
@RequestMapping("/test")
public class TestController {

    /**
     * Endpoint público para teste - não requer autenticação.
     * Qualquer pessoa pode acessar este endpoint.
     * 
     * @return ResponseEntity com mensagem de confirmação
     */
    @GetMapping("/public")
    public ResponseEntity<String> publicEndpoint() {
        return ResponseEntity.ok("Endpoint público - qualquer um pode acessar");
    }

    /**
     * Endpoint que requer apenas autenticação - qualquer usuário logado pode acessar.
     * Testa se o sistema de autenticação básico está funcionando.
     * 
     * @return ResponseEntity com mensagem de confirmação
     */
    @GetMapping("/authenticated")
    public ResponseEntity<String> authenticatedEndpoint() {
        return ResponseEntity.ok("Endpoint autenticado - apenas usuários logados");
    }

    /**
     * Endpoint restrito apenas para usuários com role ADMIN.
     * Testa se o controle de acesso por role está funcionando.
     * 
     * @return ResponseEntity com mensagem de confirmação
     */
    @GetMapping("/admin-only")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> adminOnlyEndpoint() {
        return ResponseEntity.ok("Endpoint apenas para ADMIN");
    }

    /**
     * Endpoint restrito apenas para usuários com role PROFESSOR.
     * Testa se professores conseguem acessar e outros usuários são bloqueados.
     * 
     * @return ResponseEntity com mensagem de confirmação
     */
    @GetMapping("/professor-only")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<String> professorOnlyEndpoint() {
        return ResponseEntity.ok("Endpoint apenas para PROFESSOR");
    }

    /**
     * Endpoint restrito apenas para usuários com role COORDENADOR.
     * Testa se coordenadores conseguem acessar e outros usuários são bloqueados.
     * 
     * @return ResponseEntity com mensagem de confirmação
     */
    @GetMapping("/coordenador-only")
    @PreAuthorize("hasRole('COORDENADOR')")
    public ResponseEntity<String> coordenadorOnlyEndpoint() {
        return ResponseEntity.ok("Endpoint apenas para COORDENADOR");
    }
}
