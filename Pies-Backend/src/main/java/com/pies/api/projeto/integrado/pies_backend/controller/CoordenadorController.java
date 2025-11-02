package com.pies.api.projeto.integrado.pies_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller responsável por gerenciar todas as funcionalidades específicas de coordenadores.
 * Todas as rotas deste controller são protegidas e só podem ser acessadas por usuários
 * com a role COORDENADOR.
 * 
 * O isolamento de acesso é garantido através de:
 * 1. Configuração de segurança em SecurityConfigurations (/coordenador/** -> hasRole("COORDENADOR"))
 * 2. Anotações @PreAuthorize em cada endpoint
 * 
 * Professores e outros usuários NÃO conseguem acessar estas rotas.
 * O sistema permite apenas um coordenador por vez (não há restrição técnica, mas é a regra de negócio).
 */
@RestController
@RequestMapping("/coordenador")
public class CoordenadorController {

    /**
     * Endpoint para acessar o dashboard principal do coordenador.
     * Retorna informações gerais de gestão acadêmica e resumo das atividades.
     * 
     * @return ResponseEntity com mensagem de confirmação de acesso
     */
    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('COORDENADOR')")
    public ResponseEntity<String> getCoordenadorDashboard() {
        return ResponseEntity.ok("Dashboard do Coordenador - Acesso autorizado");
    }

    /**
     * Endpoint para listar todos os professores gerenciados pelo coordenador.
     * Permite ao coordenador visualizar e gerenciar os professores sob sua supervisão.
     * 
     * @return ResponseEntity com lista de professores
     */
    @GetMapping("/professores")
    @PreAuthorize("hasRole('COORDENADOR')")
    public ResponseEntity<String> getProfessores() {
        return ResponseEntity.ok("Lista de professores gerenciados pelo coordenador");
    }

    /**
     * Endpoint para acessar relatórios de gestão acadêmica.
     * Permite ao coordenador visualizar relatórios e métricas do sistema.
     * 
     * @return ResponseEntity com relatórios de gestão
     */
    @GetMapping("/relatorios")
    @PreAuthorize("hasRole('COORDENADOR')")
    public ResponseEntity<String> getRelatorios() {
        return ResponseEntity.ok("Relatórios de gestão acadêmica");
    }

    /**
     * Endpoint para acessar o calendário acadêmico com visão de coordenador.
     * Permite visualizar eventos, prazos e atividades acadêmicas.
     * 
     * @return ResponseEntity com calendário acadêmico
     */
    @GetMapping("/calendario")
    @PreAuthorize("hasRole('COORDENADOR')")
    public ResponseEntity<String> getCalendario() {
        return ResponseEntity.ok("Calendário acadêmico - visão coordenador");
    }

    /**
     * Endpoint para acessar o perfil pessoal do coordenador.
     * Permite visualizar e editar informações pessoais.
     * 
     * @return ResponseEntity com dados do perfil do coordenador
     */
    @GetMapping("/perfil")
    @PreAuthorize("hasRole('COORDENADOR')")
    public ResponseEntity<String> getPerfil() {
        return ResponseEntity.ok("Perfil do coordenador");
    }
}
