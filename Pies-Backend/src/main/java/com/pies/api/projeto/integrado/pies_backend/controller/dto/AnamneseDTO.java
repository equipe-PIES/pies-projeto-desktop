package com.pies.api.projeto.integrado.pies_backend.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AnamneseDTO {

    private String id;

    @NotBlank(message = "ID do professor é obrigatório")
    private String professorId;

    private String professorNome;

    private String temConvulsao;

    @Size(max = 200)
    private String convenioMedico;

    private String vacinacaoEmDia;

    @Size(max = 500)
    private String doencaContagiosa;

    @Size(max = 500)
    private String usoMedicacoes;

    @Size(max = 500)
    private String servicosSaudeOuEducacao;

    @Size(max = 100)
    private String inicioEscolarizacao;

    @Size(max = 500)
    private String dificuldadesEscolares;

    @Size(max = 500)
    private String apoioPedagogicoEmCasa;

    @Size(max = 100)
    private String duracaoGestacao;

    private String fezPreNatal;

    @Size(max = 500)
    private String prematuridade;

    @Size(max = 200)
    private String cidadeNascimento;

    @Size(max = 200)
    private String maternidadeNascimento;

    @Size(max = 200)
    private String tipoParto;

    private String chorouAoNascer;

    private String ficouRoxo;

    private String usoIncubadora;

    private String foiAmamentado;

    @Size(max = 100)
    private String sustentouCabecaMeses;

    @Size(max = 100)
    private String engatinhouMeses;

    @Size(max = 100)
    private String sentouMeses;

    @Size(max = 100)
    private String andouMeses;

    @Size(max = 500)
    private String precisouTerapiaMotivo;

    @Size(max = 100)
    private String falouMeses;

    @Size(max = 100)
    private String primeiroBalbucioMeses;

    @Size(max = 100)
    private String primeiraPalavraQuando;

    @Size(max = 100)
    private String primeiraFraseQuando;

    @Size(max = 100)
    private String falaNaturalOuInibido;

    @Size(max = 500)
    private String disturbioFala;

    private String dormeSozinho;

    private String temQuartoProprio;

    @Size(max = 100)
    private String sonoCalmoOuAgitado;

    private String respeitaRegras;

    private String desmotivado;

    private String agressivo;

    private String apresentaInquietacao;
}

