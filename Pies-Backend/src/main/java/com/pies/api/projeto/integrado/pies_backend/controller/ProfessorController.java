package com.pies.api.projeto.integrado.pies_backend.controller;

// Imports para manipulação de coleções
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pies.api.projeto.integrado.pies_backend.controller.dto.CreateProfessorDTO;
import com.pies.api.projeto.integrado.pies_backend.controller.dto.ProfessorDTO;
import com.pies.api.projeto.integrado.pies_backend.controller.dto.UpdateProfessorDTO;
import com.pies.api.projeto.integrado.pies_backend.model.Enums.Genero;
import com.pies.api.projeto.integrado.pies_backend.service.ProfessorService;

import jakarta.validation.Valid;

/**
 * CONTROLLER LAYER - CAMADA DE APRESENTAÇÃO
 * 
 * Controller REST responsável por gerenciar todas as funcionalidades de professores.
 * Implementa operações CRUD completas para gerenciamento de professores no sistema PIES.
 * 
 * Características:
 * - API REST com endpoints padronizados
 * - Controle de acesso baseado em roles (ADMIN, COORDENADOR, PROFESSOR)
 * - Validações automáticas com @Valid
 * - Tratamento de erros com códigos HTTP apropriados
 * - Conversão automática entre DTOs e entidades
 * 
 * Endpoints disponíveis:
 * - GET /professores - Lista todos os professores
 * - GET /professores/{id} - Busca professor por ID
 * - GET /professores/buscar - Busca professores por nome
 * - GET /professores/genero/{genero} - Busca professores por gênero
 * - GET /professores/formacao/{formacao} - Busca professores por formação
 * - POST /professores - Cria novo professor
 * - PUT /professores/{id} - Atualiza professor existente
 * - DELETE /professores/{id} - Remove professor
 * 
 * Controle de Acesso:
 * - ADMIN: Acesso total a todas as operações
 * - COORDENADOR: Pode criar, listar, buscar e atualizar professores
 * - PROFESSOR: Pode apenas visualizar seu próprio perfil
 */
@RestController // Marca como controller REST - retorna dados serializados (JSON)
@RequestMapping("/professores") // Define base URL para todos os endpoints deste controller
public class ProfessorController {

    // ========== INJEÇÃO DE DEPENDÊNCIA ==========
    
    /**
     * Serviço responsável pela lógica de negócio dos professores.
     * Injetado automaticamente pelo Spring (Dependency Injection)
     */
    @Autowired
    private ProfessorService professorService;

    // ========== ENDPOINTS DE CONSULTA (GET) ==========
    
    /**
     * Lista todos os professores cadastrados no sistema
     * 
     * Endpoint: GET /professores
     * Permissões: ADMIN, COORDENADOR
     * 
     * Funcionalidade:
     * - Busca todos os professores ordenados alfabeticamente por nome
     * - Converte entidades Professor em DTOs para resposta
     * - Retorna lista vazia se não houver professores cadastrados
     * 
     * @return ResponseEntity com lista de professores em formato JSON
     */
    @GetMapping // Mapeia requisições GET para /professores
    @PreAuthorize("hasRole('ADMIN') or hasRole('COORDENADOR')") // Apenas ADMIN e COORDENADOR podem listar
    public ResponseEntity<List<ProfessorDTO>> listarProfessores() {
        return ResponseEntity.ok(professorService.listarTodos());
    }

    /**
     * Busca um professor específico pelo ID
     * 
     * Endpoint: GET /professores/{id}
     * Permissões: ADMIN, COORDENADOR, PROFESSOR
     * 
     * Funcionalidade:
     * - Busca professor pelo ID único
     * - Retorna dados completos do professor se encontrado
     * - Retorna 404 (Not Found) se professor não existir
     * - PROFESSOR pode visualizar apenas seu próprio perfil (implementação futura)
     * 
     * @param id ID único do professor (UUID)
     * @return ResponseEntity com dados do professor ou 404 se não encontrado
     */
    @GetMapping("/{id}") // Mapeia GET /professores/{id} - {id} é um path variable
    @PreAuthorize("hasRole('ADMIN') or hasRole('COORDENADOR') or hasRole('PROFESSOR')") // Todas as roles podem visualizar
    public ResponseEntity<ProfessorDTO> buscarProfessorPorId(@PathVariable String id) {
        try {
            return ResponseEntity.ok(professorService.buscarPorId(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Busca professores pelo nome (busca parcial).
     * 
     * @param nome Nome ou parte do nome do professor
     * @return ResponseEntity com lista de professores encontrados
     */
    @GetMapping("/buscar")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COORDENADOR')")
    public ResponseEntity<List<ProfessorDTO>> buscarProfessoresPorNome(@RequestParam String nome) {
        return ResponseEntity.ok(professorService.buscarPorNome(nome));
    }

    /**
     * Busca professores por gênero.
     * 
     * @param genero Gênero dos professores
     * @return ResponseEntity com lista de professores do gênero informado
     */
    @GetMapping("/genero/{genero}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COORDENADOR')")
    public ResponseEntity<List<ProfessorDTO>> buscarProfessoresPorGenero(@PathVariable Genero genero) {
        return ResponseEntity.ok(professorService.buscarPorGenero(genero));
    }

    /**
     * Busca professores por formação.
     * 
     * @param formacao Formação dos professores
     * @return ResponseEntity com lista de professores com a formação informada
     */
    @GetMapping("/formacao/{formacao}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COORDENADOR')")
    public ResponseEntity<List<ProfessorDTO>> buscarProfessoresPorFormacao(@PathVariable String formacao) {
        return ResponseEntity.ok(professorService.buscarPorFormacao(formacao));
    }

    // ========== ENDPOINTS DE CRIAÇÃO (POST) ==========
    
    /**
     * Cria um novo professor no sistema
     * 
     * Endpoint: POST /professores
     * Permissões: ADMIN, COORDENADOR
     * 
     * Funcionalidade:
     * - Valida dados de entrada automaticamente com @Valid
     * - Verifica se CPF já existe (unicidade)
     * - Cria novo professor no banco de dados
     * - Retorna dados do professor criado com status 201 (Created)
     * - Retorna erro 400 (Bad Request) se CPF duplicado ou dados inválidos
     * 
     * @param createProfessorDTO Dados do professor a ser criado (validados automaticamente)
     * @return ResponseEntity com dados do professor criado ou mensagem de erro
     */
    @PostMapping // Mapeia requisições POST para /professores
    @PreAuthorize("hasRole('ADMIN') or hasRole('COORDENADOR')") // Apenas ADMIN e COORDENADOR podem criar
    public ResponseEntity<?> criarProfessor(@Valid @RequestBody CreateProfessorDTO createProfessorDTO) {
        try {
            ProfessorDTO professorDTO = professorService.criar(createProfessorDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(professorDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Atualiza os dados de um professor existente.
     * 
     * @param id ID do professor a ser atualizado
     * @param updateProfessorDTO Novos dados do professor
     * @return ResponseEntity com dados atualizados do professor ou 404 se não encontrado
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COORDENADOR')")
    public ResponseEntity<?> atualizarProfessor(@PathVariable String id, 
                                               @Valid @RequestBody UpdateProfessorDTO updateProfessorDTO) {
        try {
            ProfessorDTO professorDTO = professorService.atualizar(id, updateProfessorDTO);
            return ResponseEntity.ok(professorDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Remove um professor do sistema.
     * 
     * @param id ID do professor a ser removido
     * @return ResponseEntity confirmando a remoção ou 404 se não encontrado
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('COORDENADOR','ADMIN')")
    public ResponseEntity<?> removerProfessor(@PathVariable String id) {
        try {
            professorService.deletar(id);
            return ResponseEntity.ok("Professor removido com sucesso");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

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
    
    /**
     * Busca os dados do professor logado (me = "eu" em inglês)
     * Endpoint: GET /professores/me
     * Permissões: PROFESSOR
     * 
     * Retorna os dados completos do professor autenticado, incluindo lista de turmas
     * 
     * @param authentication Informações do usuário autenticado (injetado automaticamente)
     * @return ResponseEntity com dados do professor ou 404 se não encontrado
     */
    @GetMapping("/me")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<ProfessorDTO> getMeuPerfil(Authentication authentication) {
        try {
            String emailUsuario = authentication.getName();
            ProfessorDTO professorDTO = professorService.buscarPerfilUsuario(emailUsuario);
            return ResponseEntity.ok(professorDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Associa um usuário a um professor
     * 
     * Endpoint: PUT /professores/{id}/associar-usuario/{userId}
     * Permissões: ADMIN, COORDENADOR
     * 
     * Funcionalidade:
     * - Busca o professor pelo ID
     * - Associa o usuário (userId) ao professor
     * - Retorna o professor atualizado
     * 
     * @param id ID do professor
     * @param userId ID do usuário a associar
     * @return ResponseEntity com professor atualizado ou erro
     */
    @PutMapping("/{id}/associar-usuario/{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COORDENADOR')")
    public ResponseEntity<?> associarUsuario(@PathVariable String id, @PathVariable String userId) {
        try {
            ProfessorDTO professorDTO = professorService.associarUsuario(id, userId);
            return ResponseEntity.ok(professorDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
