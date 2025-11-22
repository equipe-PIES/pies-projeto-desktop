package com.pies.api.projeto.integrado.pies_backend.controller.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO para criação de um novo Relatório Individual.
 * 
 * Contém todos os campos que podem ser preenchidos ao criar um relatório.
 * O educandoId é obrigatório, enquanto os demais campos são opcionais
 * (podem ser preenchidos posteriormente).
 */
public record CreateRelatorioIndividualDTO(
    /**
     * ID do educando ao qual este relatório se refere.
     * Campo obrigatório.
     */
    @NotBlank(message = "ID do educando é obrigatório")
    String educandoId,
    
    /**
     * Dados Funcionais: Nível de suporte, estímulos sensoriais, interação social,
     * participação em atividades e tempos de duração.
     */
    String dadosFuncionais,
    
    /**
     * Funcionalidade Cognitiva: Habilidades cognitivas relacionadas à aprendizagem
     * dos conceitos curriculares, escritas, leituras, jogos e raciocínio lógico.
     */
    String funcionalidadeCognitiva,
    
    /**
     * Alfabetização e Letramento: Como o aluno faz uso da leitura e escrita,
     * como se expressa e qual apoio é oferecido.
     */
    String alfabetizacaoLetramento,
    
    /**
     * Adaptações Curriculares: Ajustes realizados, metodologia utilizada
     * e resultados alcançados.
     */
    String adaptacoesCurriculares,
    
    /**
     * Participação nas Atividades Propostas: Como o aluno participa das atividades,
     * momentos de interesse ou desinteresse.
     */
    String participacaoAtividades,
    
    /**
     * Autonomia: Como e o que é trabalhado para promover a autonomia do aluno
     * em diferentes contextos escolares.
     */
    String autonomia,
    
    /**
     * Interação com a Professora: Como o aluno se relaciona e qual intervenção
     * é necessária para interação efetiva.
     */
    String interacaoProfessora,
    
    /**
     * Atividades de Vida Diária (AVDs): Habilidades de autocuidado, dificuldades
     * e necessidade de auxílio na locomoção.
     */
    String atividadesVidaDiaria
) {}

