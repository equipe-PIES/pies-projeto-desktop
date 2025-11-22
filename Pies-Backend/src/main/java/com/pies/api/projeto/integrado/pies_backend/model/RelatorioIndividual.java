package com.pies.api.projeto.integrado.pies_backend.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidade que representa um Relatório Individual de um educando no sistema PIES.
 * 
 * Um relatório individual contém informações detalhadas sobre o desenvolvimento,
 * participação e necessidades de um educando, sendo preenchido por professores
 * ou coordenadores.
 * 
 * A classe utiliza JPA para mapeamento ORM e relacionamentos com Educando e Professor.
 */
@Entity
@Table(name = "relatorios_individuais")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RelatorioIndividual {
    
    /**
     * Identificador único do relatório.
     * Gerado automaticamente como UUID pela estratégia de geração da JPA.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * Educando ao qual este relatório se refere.
     * Relacionamento ManyToOne: muitos relatórios podem pertencer a um educando.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "educando_id", nullable = false)
    private Educando educando;

    /**
     * Professor que criou/preencheu este relatório.
     * Relacionamento ManyToOne: muitos relatórios podem ser criados por um professor.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professor_id", nullable = false)
    private Professor professor;

    /**
     * Data e hora de criação do relatório.
     * Preenchida automaticamente quando o relatório é criado.
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    /**
     * Dados Funcionais: Descreve o nível de suporte que o aluno requer em seu dia a dia escolar,
     * se há estímulos sensoriais que o incomodam, sua interação social, participação nas atividades,
     * e os tempos de duração.
     */
    @Column(columnDefinition = "TEXT")
    private String dadosFuncionais;

    /**
     * Funcionalidade Cognitiva: Refere-se às habilidades cognitivas relacionadas à aprendizagem
     * dos conceitos curriculares, escritas, leituras, jogos e raciocínio lógico.
     */
    @Column(columnDefinition = "TEXT")
    private String funcionalidadeCognitiva;

    /**
     * Alfabetização e Letramento: Descreve detalhadamente como o aluno faz uso da leitura e da escrita
     * e como se expressa em relação a essas atividades. Inclui também qual é o apoio oferecido
     * para auxiliar o aluno nessas práticas.
     */
    @Column(columnDefinition = "TEXT")
    private String alfabetizacaoLetramento;

    /**
     * Adaptações Curriculares: Detalha os ajustes realizados para atender às necessidades do aluno.
     * Cita a metodologia utilizada para facilitar a aprendizagem e os resultados alcançados
     * com essas adaptações.
     */
    @Column(columnDefinition = "TEXT")
    private String adaptacoesCurriculares;

    /**
     * Participação nas Atividades Propostas: Descreve como o aluno participa das atividades.
     * Indica os momentos que demonstra interesse ou desinteresse durante as aulas.
     */
    @Column(columnDefinition = "TEXT")
    private String participacaoAtividades;

    /**
     * Autonomia: Relata como e o que é trabalhado para promover a autonomia do aluno em diferentes
     * contextos escolares. Isso inclui: Fazer escolhas, tomar iniciativas, cumprir planejamentos,
     * atender aos próprios interesses, realizar tarefas, resolver conflitos, defender-se,
     * expressar-se e solicitar ajuda.
     */
    @Column(columnDefinition = "TEXT")
    private String autonomia;

    /**
     * Interação com a Professora: Descreve como o aluno se relaciona e qual a intervenção
     * para que a interação ocorra de forma efetiva.
     */
    @Column(columnDefinition = "TEXT")
    private String interacaoProfessora;

    /**
     * Atividades de Vida Diária (AVDs): Informa se o aluno possui habilidades de autocuidado
     * como: higiene pessoal, alimentação, vestuário, autonomia para utilizar o banheiro.
     * Se sim, de todas, qual dificuldade e/ou possui baixa afetiva?, necessidade de auxílio
     * na locomoção.
     */
    @Column(columnDefinition = "TEXT")
    private String atividadesVidaDiaria;

    /**
     * Construtor para criação de um relatório com data de criação automática.
     * 
     * @param educando Educando ao qual o relatório se refere
     * @param professor Professor que está criando o relatório
     */
    public RelatorioIndividual(Educando educando, Professor professor) {
        this.educando = educando;
        this.professor = professor;
        this.dataCriacao = LocalDateTime.now();
    }
}

