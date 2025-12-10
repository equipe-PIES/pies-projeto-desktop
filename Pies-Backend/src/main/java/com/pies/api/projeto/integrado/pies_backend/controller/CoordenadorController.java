package com.pies.api.projeto.integrado.pies_backend.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pies.api.projeto.integrado.pies_backend.controller.dto.EducandoDTO;
import com.pies.api.projeto.integrado.pies_backend.controller.dto.ProfessorDTO;
import com.pies.api.projeto.integrado.pies_backend.controller.dto.TurmaDTO;
import com.pies.api.projeto.integrado.pies_backend.service.EducandoService;
import com.pies.api.projeto.integrado.pies_backend.service.TurmaService;
import com.pies.api.projeto.integrado.pies_backend.repository.ProfessorRepository;
import com.pies.api.projeto.integrado.pies_backend.repository.TurmaRepository;
import com.pies.api.projeto.integrado.pies_backend.model.Professor;
import com.pies.api.projeto.integrado.pies_backend.model.Turma;
import com.pies.api.projeto.integrado.pies_backend.model.Enums.GrauEscolar;

import lombok.RequiredArgsConstructor;
import java.util.stream.Collectors;

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
@RequiredArgsConstructor
public class CoordenadorController {

    private final EducandoService educandoService;
    private final TurmaService turmaService;
    private final ProfessorRepository professorRepository;
    private final TurmaRepository turmaRepository;

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
    public ResponseEntity<List<ProfessorDTO>> getProfessores() {
        List<Professor> professores = professorRepository.findAllByOrderByNomeAsc();
        List<ProfessorDTO> professoresDTO = professores.stream()
                .map(professor -> {
                    ProfessorDTO dto = new ProfessorDTO(professor);
                    List<Turma> turmas = turmaRepository.findByProfessor(professor);
                    List<String> turmasIds = turmas.stream()
                            .map(Turma::getId)
                            .collect(Collectors.toList());
                    dto.setTurmasIds(turmasIds);
                    return dto;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(professoresDTO);
    }

    /**
     * Endpoint para buscar professores por termo (nome).
     * Permite ao coordenador buscar professores pelo nome.
     * 
     * @param termo Termo de busca (nome ou parte do nome)
     * @return ResponseEntity com lista de professores encontrados
     */
    @GetMapping("/professores/buscar")
    @PreAuthorize("hasRole('COORDENADOR')")
    public ResponseEntity<List<ProfessorDTO>> buscarProfessores(@RequestParam String termo) {
        List<Professor> professores = professorRepository.findByNomeContainingIgnoreCase(termo);
        List<ProfessorDTO> professoresDTO = professores.stream()
                .map(professor -> {
                    ProfessorDTO dto = new ProfessorDTO(professor);
                    List<Turma> turmas = turmaRepository.findByProfessor(professor);
                    List<String> turmasIds = turmas.stream()
                            .map(Turma::getId)
                            .collect(Collectors.toList());
                    dto.setTurmasIds(turmasIds);
                    return dto;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(professoresDTO);
    }

    /**
     * Endpoint para listar todos os alunos (educandos) do sistema.
     * Permite ao coordenador visualizar todos os alunos cadastrados.
     * 
     * @return ResponseEntity com lista de alunos
     */
    @GetMapping("/alunos")
    @PreAuthorize("hasRole('COORDENADOR')")
    public ResponseEntity<List<EducandoDTO>> getAlunos() {
        List<EducandoDTO> alunos = educandoService.listarTodos();
        return ResponseEntity.ok(alunos);
    }

    /**
     * Endpoint para buscar alunos por termo (nome).
     * Permite ao coordenador buscar alunos pelo nome.
     * 
     * @param termo Termo de busca (nome ou parte do nome)
     * @return ResponseEntity com lista de alunos encontrados
     */
    @GetMapping("/alunos/buscar")
    @PreAuthorize("hasRole('COORDENADOR')")
    public ResponseEntity<List<EducandoDTO>> buscarAlunos(@RequestParam String termo) {
        List<EducandoDTO> alunos = educandoService.buscarPorTermo(termo);
        return ResponseEntity.ok(alunos);
    }

    /**
     * Endpoint para filtrar alunos por nome e grau de escolaridade.
     * Permite ao coordenador filtrar alunos usando múltiplos critérios.
     * 
     * @param nome Nome ou parte do nome (opcional)
     * @param escolaridade Grau de escolaridade (opcional)
     * @return ResponseEntity com lista de alunos encontrados
     */
    @GetMapping("/alunos/filtrar")
    @PreAuthorize("hasRole('COORDENADOR')")
    public ResponseEntity<List<EducandoDTO>> filtrarAlunos(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) GrauEscolar escolaridade) {
        List<EducandoDTO> alunos = educandoService.filtrarPorNomeEEscolaridade(nome, escolaridade);
        return ResponseEntity.ok(alunos);
    }

    /**
     * Endpoint para listar todas as turmas do sistema.
     * Permite ao coordenador visualizar todas as turmas cadastradas.
     * 
     * @return ResponseEntity com lista de turmas
     */
    @GetMapping("/turmas")
    @PreAuthorize("hasRole('COORDENADOR')")
    public ResponseEntity<List<TurmaDTO>> getTurmas() {
        List<TurmaDTO> turmas = turmaService.listarTodas();
        return ResponseEntity.ok(turmas);
    }

    /**
     * Endpoint para buscar turmas por termo (nome).
     * Permite ao coordenador buscar turmas pelo nome.
     * 
     * @param termo Termo de busca (nome ou parte do nome da turma)
     * @return ResponseEntity com lista de turmas encontradas
     */
    @GetMapping("/turmas/buscar")
    @PreAuthorize("hasRole('COORDENADOR')")
    public ResponseEntity<List<TurmaDTO>> buscarTurmas(@RequestParam String termo) {
        List<TurmaDTO> turmas = turmaService.buscarPorTermo(termo);
        return ResponseEntity.ok(turmas);
    }

    /**
     * Endpoint para filtrar turmas por nome, professor e grau de escolaridade.
     * Permite ao coordenador filtrar turmas usando múltiplos critérios.
     * 
     * @param nome Nome ou parte do nome da turma (opcional)
     * @param professorId ID do professor (opcional)
     * @param grauEscolar Grau de escolaridade (opcional)
     * @return ResponseEntity com lista de turmas encontradas
     */
    @GetMapping("/turmas/filtrar")
    @PreAuthorize("hasRole('COORDENADOR')")
    public ResponseEntity<List<TurmaDTO>> filtrarTurmas(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String professorId,
            @RequestParam(required = false) GrauEscolar grauEscolar) {
        List<TurmaDTO> turmas = turmaService.filtrarPorNomeProfessorEGrauEscolar(nome, professorId, grauEscolar);
        return ResponseEntity.ok(turmas);
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
