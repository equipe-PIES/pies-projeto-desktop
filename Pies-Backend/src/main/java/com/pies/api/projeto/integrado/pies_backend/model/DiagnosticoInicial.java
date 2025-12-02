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
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "diagnosticos_iniciais")
@Data
@NoArgsConstructor
public class DiagnosticoInicial {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @jakarta.persistence.OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "educando_id", nullable = false, unique = true)
    private Educando educando;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professor_id", nullable = false)
    private Professor professor;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    // Comunicação
    private Boolean falaSeuNome;
    private Boolean dizDataNascimento;
    private Boolean lePalavras;
    private Boolean informaNumeroTelefone;
    private Boolean emiteRespostas;
    private Boolean transmiteRecado;
    private Boolean informaEndereco;
    private Boolean informaNomePais;
    private Boolean compreendeOrdens;
    private Boolean expoeIdeias;
    private Boolean recontaHistorias;
    private Boolean usaSistemaCA;
    private Boolean relataFatosComCoerencia;
    private Boolean pronunciaLetrasAlfabeto;
    private Boolean verbalizaMusicas;
    private Boolean interpretaHistorias;
    private Boolean formulaPerguntas;
    private Boolean utilizaGestosParaSeComunicar;

    // Afetiva
    private Boolean demonstraCooperacao;
    private Boolean timidoInseguro;
    private Boolean fazBirra;
    private Boolean solicitaOfereceAjuda;
    private Boolean riComFrequencia;
    private Boolean compartilhaOQueESeu;
    private Boolean demonstraAmorGentilezaAtencao;
    private Boolean choraComFrequencia;
    private Boolean interageComColegas;

    // Sensorial
    private Boolean captaDetalhesGravura;
    private Boolean reconheceVozes;
    private Boolean reconheceCancoes;
    private Boolean percebeTexturas;
    private Boolean percepcaoCores;
    private Boolean discriminaSons;
    private Boolean discriminaOdores;
    private Boolean aceitaDiferentesTexturas;
    private Boolean percepcaoFormas;
    private Boolean identificaDirecaoSom;
    private Boolean percebeDiscriminaSabores;
    private Boolean acompanhaFocoLuminoso;

    // Motora
    private Boolean movimentoPincaComTesoura;
    private Boolean amassaPapel;
    private Boolean caiComFacilidade;
    private Boolean encaixaPecas;
    private Boolean recorta;
    private Boolean unePontos;
    private Boolean consegueCorrer;
    private Boolean empilha;
    private Boolean agitacaoMotora;
    private Boolean andaLinhaReta;
    private Boolean sobeDesceEscadas;
    private Boolean arremessaBola;

    // AVDs
    private Boolean usaSanitarioSemAjuda;
    private Boolean penteiaSeSo;
    private Boolean consegueVestirDespirSe;
    private Boolean lavaSecaAsMaos;
    private Boolean banhoComModeracao;
    private Boolean calcaSeSo;
    private Boolean reconheceRoupas;
    private Boolean abreFechaTorneira;
    private Boolean escovaDentesSemAjuda;
    private Boolean consegueDarNosLacos;
    private Boolean abotoaDesabotoaRoupas;
    private Boolean identificaPartesDoCorpo;

    // Níveis de Aprendizagem
    private Boolean garatujas;
    private Boolean preSilabico;
    private Boolean silabico;
    private Boolean silabicoAlfabetico;
    private Boolean alfabetico;

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    public DiagnosticoInicial(Educando educando, Professor professor) {
        this.educando = educando;
        this.professor = professor;
        this.dataCriacao = LocalDateTime.now();
    }
}
