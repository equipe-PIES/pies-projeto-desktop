package com.pies.api.projeto.integrado.pies_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "paee")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PAEE {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    // Relacionamentos: Mantive Long, assumindo que suas tabelas de Usuário/Aluno 
    // ainda usam IDs numéricos. Se elas também forem UUID, altere aqui para String.
    @Column(nullable = false)
    private String alunoId;

    @Column(nullable = false)
    private String professorId;

    @CreationTimestamp
    private LocalDateTime dataCriacao;

    // =================================================================================
    // TELA 1: Resumo e Seleção de Dificuldades
    // =================================================================================
    @Column(columnDefinition = "TEXT")
    private String resumoCaso;

    private Boolean apresentaDificuldadeMotora;
    private Boolean apresentaDificuldadeCognitiva;
    private Boolean apresentaDificuldadeSensorial;
    private Boolean apresentaDificuldadeLinguagem;
    private Boolean apresentaDificuldadeFamiliar;
    private Boolean apresentaDificuldadeAfetiva;
    private Boolean apresentaDificuldadeLogica;
    private Boolean apresentaDificuldadeAVA;

    // =================================================================================
    // TELAS 2, 3, 4 e 5: Detalhamento das Áreas
    // =================================================================================
    
    // Área: Memória
    @Column(columnDefinition = "TEXT")
    private String memoriaDificuldades;
    @Column(columnDefinition = "TEXT")
    private String memoriaIntervencoes;

    // Área: Percepção
    @Column(columnDefinition = "TEXT")
    private String percepcaoDificuldades;
    @Column(columnDefinition = "TEXT")
    private String percepcaoIntervencoes;

    // Área: Raciocínio Lógico
    @Column(columnDefinition = "TEXT")
    private String raciocinioLogicoDificuldades;
    @Column(columnDefinition = "TEXT")
    private String raciocinioLogicoIntervencoes;

    // Área: Atenção e Concentração
    @Column(columnDefinition = "TEXT")
    private String atencaoConcentracaoDificuldades;
    @Column(columnDefinition = "TEXT")
    private String atencaoConcentracaoIntervencoes;

    // Área: Sociabilidade e Afetividade
    @Column(columnDefinition = "TEXT")
    private String sociabilidadeDificuldades;
    @Column(columnDefinition = "TEXT")
    private String sociabilidadeIntervencoes;

    // Área: AVAs (Atividades de Vida Autônoma)
    @Column(columnDefinition = "TEXT")
    private String avasDificuldades;
    @Column(columnDefinition = "TEXT")
    private String avasIntervencoes;

    // Área: Desenvolvimento Motor/Psicomotor
    @Column(columnDefinition = "TEXT")
    private String desenvolvimentoMotorDificuldades;
    @Column(columnDefinition = "TEXT")
    private String desenvolvimentoMotorIntervencoes;

    // Área: Comunicação e Linguagem
    @Column(columnDefinition = "TEXT")
    private String comunicacaoLinguagemDificuldades;
    @Column(columnDefinition = "TEXT")
    private String comunicacaoLinguagemIntervencoes;

    // =================================================================================
    // TELA 6: Objetivos Finais e Profissionais Envolvidos
    // =================================================================================
    @Column(columnDefinition = "TEXT")
    private String objetivosGerais;

    private Boolean apoioAEE;
    private Boolean apoioPsicologo;
    private Boolean apoioFisioterapeuta;
    private Boolean apoioPsicopedagogo;
    private Boolean apoioTO;
    private Boolean apoioEdFisica;
    private Boolean apoioEstimulacaoPrecoce;
}
