package com.pies.api.projeto.integrado.pies_backend.model;

import com.pies.api.projeto.integrado.pies_backend.model.Enums.SimNao;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "anamneses")
@Data
@NoArgsConstructor
public class Anamnese {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "educando_id", nullable = false, unique = true)
    private Educando educando;

    @Enumerated(EnumType.STRING)
    private SimNao temConvulsao;

    @Column(length = 200)
    private String convenioMedico;

    @Enumerated(EnumType.STRING)
    private SimNao vacinacaoEmDia;

    @Column(length = 500)
    private String doencaContagiosa;

    @Column(length = 500)
    private String usoMedicacoes;

    @Column(length = 500)
    private String servicosSaudeOuEducacao;

    @Column(length = 100)
    private String inicioEscolarizacao;

    @Column(length = 500)
    private String dificuldadesEscolares;

    @Column(length = 500)
    private String apoioPedagogicoEmCasa;

    @Column(length = 100)
    private String duracaoGestacao;

    @Enumerated(EnumType.STRING)
    private SimNao fezPreNatal;

    @Column(length = 500)
    private String prematuridade;

    @Column(length = 200)
    private String cidadeNascimento;

    @Column(length = 200)
    private String maternidadeNascimento;

    @Column(length = 200)
    private String tipoParto;

    @Enumerated(EnumType.STRING)
    private SimNao chorouAoNascer;

    @Enumerated(EnumType.STRING)
    private SimNao ficouRoxo;

    @Enumerated(EnumType.STRING)
    private SimNao usoIncubadora;

    @Enumerated(EnumType.STRING)
    private SimNao foiAmamentado;

    @Column(length = 100)
    private String sustentouCabecaMeses;

    @Column(length = 100)
    private String engatinhouMeses;

    @Column(length = 100)
    private String sentouMeses;

    @Column(length = 100)
    private String andouMeses;

    @Column(length = 500)
    private String precisouTerapiaMotivo;

    @Column(length = 100)
    private String falouMeses;

    @Column(length = 100)
    private String primeiroBalbucioMeses;

    @Column(length = 100)
    private String primeiraPalavraQuando;

    @Column(length = 100)
    private String primeiraFraseQuando;

    @Column(length = 100)
    private String falaNaturalOuInibido;

    @Column(length = 500)
    private String disturbioFala;

    @Enumerated(EnumType.STRING)
    private SimNao dormeSozinho;

    @Enumerated(EnumType.STRING)
    private SimNao temQuartoProprio;

    @Column(length = 100)
    private String sonoCalmoOuAgitado;

    @Enumerated(EnumType.STRING)
    private SimNao respeitaRegras;

    @Enumerated(EnumType.STRING)
    private SimNao desmotivado;

    @Enumerated(EnumType.STRING)
    private SimNao agressivo;

    @Enumerated(EnumType.STRING)
    private SimNao apresentaInquietacao;
}
