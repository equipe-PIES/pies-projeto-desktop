package com.pies.api.projeto.integrado.pies_backend.controller.dto;

import com.pies.api.projeto.integrado.pies_backend.model.Enums.SimNao;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AnamneseDTO {

    private String id;

    private SimNao temConvulsao;

    @Size(max = 200)
    private String convenioMedico;

    private SimNao vacinacaoEmDia;

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

    private SimNao fezPreNatal;

    @Size(max = 500)
    private String prematuridade;

    @Size(max = 200)
    private String cidadeNascimento;

    @Size(max = 200)
    private String maternidadeNascimento;

    @Size(max = 200)
    private String tipoParto;

    private SimNao chorouAoNascer;

    private SimNao ficouRoxo;

    private SimNao usoIncubadora;

    private SimNao foiAmamentado;

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

    private SimNao dormeSozinho;

    private SimNao temQuartoProprio;

    @Size(max = 100)
    private String sonoCalmoOuAgitado;

    private SimNao respeitaRegras;

    private SimNao desmotivado;

    private SimNao agressivo;

    private SimNao apresentaInquietacao;
}

