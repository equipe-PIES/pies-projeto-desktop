package com.pies.api.projeto.integrado.pies_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller responsável por gerenciar todas as funcionalidades específicas de professores.
 * Todas as rotas deste controller são protegidas e só podem ser acessadas por usuários
 * com a role PROFESSOR.
 * 
 * O isolamento de acesso é garantido através de:
 * 1. Configuração de segurança em SecurityConfigurations (/professor/** -> hasRole("PROFESSOR"))
 * 2. Anotações @PreAuthorize em cada endpoint
 * 
 * Coordenadores e outros usuários NÃO conseguem acessar estas rotas.
 */
@RestController
@RequestMapping("/professor")
public class ProfessorController {

    /**
     * Endpoint para acessar o dashboard principal do professor.
     * Retorna informações gerais e resumo das atividades do professor.
     * 
     * @return ResponseEntity com mensagem de confirmação de acesso
     */
    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<String> getProfessorDashboard() {
        return ResponseEntity.ok("Dashboard do Professor - Acesso autorizado");
    }

    /**
     * Endpoint para listar todas as turmas associadas ao professor logado.
     * Permite ao professor visualizar suas turmas e alunos.
     * 
     * @return ResponseEntity com lista de turmas do professor
     */
    @GetMapping("/turmas")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<String> getTurmas() {
        return ResponseEntity.ok("Lista de turmas do professor");
    }

    /**
     * Endpoint para acessar o sistema de notas do professor.
     * Permite visualizar e gerenciar as notas dos alunos.
     * 
     * @return ResponseEntity com sistema de notas
     */
    @GetMapping("/notas")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<String> getNotas() {
        return ResponseEntity.ok("Sistema de notas do professor");
    }

    /**
     * Endpoint para acessar o perfil pessoal do professor.
     * Permite visualizar e editar informações pessoais.
     * 
     * @return ResponseEntity com dados do perfil do professor
     */
    @GetMapping("/perfil")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<String> getPerfil() {
        return ResponseEntity.ok("Perfil do professor");
    }
}
