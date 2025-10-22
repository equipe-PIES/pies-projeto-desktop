package com.pies.api.projeto.integrado.pies_backend.controller;

// Imports para manipulação de coleções
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pies.api.projeto.integrado.pies_backend.controller.dto.CreateProfessorDTO;
import com.pies.api.projeto.integrado.pies_backend.controller.dto.ProfessorDTO;
import com.pies.api.projeto.integrado.pies_backend.controller.dto.UpdateProfessorDTO;
import com.pies.api.projeto.integrado.pies_backend.model.Professor;
import com.pies.api.projeto.integrado.pies_backend.repository.ProfessorRepository;

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
     * Repository para operações de banco de dados
     * Injetado automaticamente pelo Spring (Dependency Injection)
     */
    @Autowired // Spring injeta automaticamente a implementação do ProfessorRepository
    private ProfessorRepository professorRepository;

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
        // Busca todos os professores ordenados por nome
        List<Professor> professores = professorRepository.findAllByOrderByNomeAsc();
        
        // Converte lista de entidades Professor em lista de DTOs
        // Usa Stream API para transformação funcional
        List<ProfessorDTO> professoresDTO = professores.stream()
                .map(ProfessorDTO::new) // Converte cada Professor em ProfessorDTO
                .collect(Collectors.toList()); // Coleta em uma nova lista
        
        // Retorna resposta HTTP 200 (OK) com a lista de professores
        return ResponseEntity.ok(professoresDTO);
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
        // Busca professor pelo ID - retorna Optional para tratamento seguro de valores nulos
        Optional<Professor> professor = professorRepository.findById(id);
        
        // Verifica se professor foi encontrado
        if (professor.isPresent()) {
            // Professor encontrado - converte para DTO e retorna 200 (OK)
            return ResponseEntity.ok(new ProfessorDTO(professor.get()));
        }
        
        // Professor não encontrado - retorna 404 (Not Found)
        return ResponseEntity.notFound().build();
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
        List<Professor> professores = professorRepository.findByNomeContainingIgnoreCase(nome);
        List<ProfessorDTO> professoresDTO = professores.stream()
                .map(ProfessorDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(professoresDTO);
    }

    /**
     * Busca professores por gênero.
     * 
     * @param genero Gênero dos professores
     * @return ResponseEntity com lista de professores do gênero informado
     */
    @GetMapping("/genero/{genero}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COORDENADOR')")
    public ResponseEntity<List<ProfessorDTO>> buscarProfessoresPorGenero(@PathVariable String genero) {
        List<Professor> professores = professorRepository.findByGenero(genero);
        List<ProfessorDTO> professoresDTO = professores.stream()
                .map(ProfessorDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(professoresDTO);
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
        List<Professor> professores = professorRepository.findByFormacao(formacao);
        List<ProfessorDTO> professoresDTO = professores.stream()
                .map(ProfessorDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(professoresDTO);
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
        // VALIDAÇÃO DE NEGÓCIO: Verifica se CPF já existe
        if (professorRepository.existsByCpf(createProfessorDTO.getCpf())) {
            // CPF duplicado - retorna erro 400 (Bad Request)
            return ResponseEntity.badRequest()
                    .body("Já existe um professor cadastrado com o CPF: " + createProfessorDTO.getCpf());
        }

        // CRIAÇÃO DA ENTIDADE: Constrói objeto Professor com dados do DTO
        Professor professor = new Professor(
                createProfessorDTO.getNome(),           // Nome do professor
                createProfessorDTO.getCpf(),            // CPF validado
                createProfessorDTO.getDataNascimento(), // Data de nascimento
                createProfessorDTO.getGenero(),         // Gênero
                createProfessorDTO.getFormacao(),       // Formação
                createProfessorDTO.getObservacoes()     // Observações (pode ser null)
        );

        // PERSISTÊNCIA: Salva professor no banco de dados
        Professor professorSalvo = professorRepository.save(professor);
        
        // RESPOSTA: Retorna professor criado com status 201 (Created)
        return ResponseEntity.status(HttpStatus.CREATED).body(new ProfessorDTO(professorSalvo));
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
        Optional<Professor> professorExistente = professorRepository.findById(id);
        if (!professorExistente.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Professor professor = professorExistente.get();
        
        // Verifica se o CPF está sendo alterado e se já existe outro professor com esse CPF
        if (!professor.getCpf().equals(updateProfessorDTO.getCpf()) && 
            professorRepository.existsByCpf(updateProfessorDTO.getCpf())) {
            return ResponseEntity.badRequest()
                    .body("Já existe um professor cadastrado com o CPF: " + updateProfessorDTO.getCpf());
        }

        // Atualiza os dados do professor
        professor.setNome(updateProfessorDTO.getNome());
        professor.setCpf(updateProfessorDTO.getCpf());
        professor.setDataNascimento(updateProfessorDTO.getDataNascimento());
        professor.setGenero(updateProfessorDTO.getGenero());
        professor.setFormacao(updateProfessorDTO.getFormacao());
        professor.setObservacoes(updateProfessorDTO.getObservacoes());

        Professor professorAtualizado = professorRepository.save(professor);
        return ResponseEntity.ok(new ProfessorDTO(professorAtualizado));
    }

    /**
     * Remove um professor do sistema.
     * 
     * @param id ID do professor a ser removido
     * @return ResponseEntity confirmando a remoção ou 404 se não encontrado
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> removerProfessor(@PathVariable String id) {
        Optional<Professor> professor = professorRepository.findById(id);
        if (!professor.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        professorRepository.deleteById(id);
        return ResponseEntity.ok("Professor removido com sucesso");
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
}
