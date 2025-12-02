package com.pies.api.projeto.integrado.pies_backend.service;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.pies.api.projeto.integrado.pies_backend.controller.dto.AnamneseDTO;
import com.pies.api.projeto.integrado.pies_backend.exception.AnamneseAlreadyExistsException;
import com.pies.api.projeto.integrado.pies_backend.exception.AnamneseNotFoundException;
import com.pies.api.projeto.integrado.pies_backend.exception.EducandoNotFoundException;
import com.pies.api.projeto.integrado.pies_backend.model.Anamnese;
import com.pies.api.projeto.integrado.pies_backend.model.Educando;
import com.pies.api.projeto.integrado.pies_backend.repository.AnamneseRepository;
import com.pies.api.projeto.integrado.pies_backend.repository.EducandoRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AnamneseService {

    private final AnamneseRepository anamneseRepository;
    private final EducandoRepository educandoRepository;

    @Transactional(readOnly = true)
    public AnamneseDTO buscarPorId(String id) {
        return anamneseRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new AnamneseNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public AnamneseDTO buscarPorEducando(String educandoId) {
        return anamneseRepository.findByEducandoId(educandoId)
                .map(this::toDTO)
                .orElseThrow(() -> new AnamneseNotFoundException(educandoId));
    }

    @Transactional
    public AnamneseDTO criar(String educandoId, AnamneseDTO dto) {
        Educando educando = educandoRepository.findById(educandoId)
                .orElseThrow(() -> new EducandoNotFoundException(educandoId));

        if (educando.getAnamnese() != null || anamneseRepository.existsByEducandoId(educandoId)) {
            throw new AnamneseAlreadyExistsException(educandoId);
        }

        Anamnese anamnese = toEntity(dto);
        anamnese.setEducando(educando);
        educando.setAnamnese(anamnese);

        return toDTO(anamneseRepository.save(anamnese));
    }

    @Transactional
    public AnamneseDTO atualizar(String educandoId, AnamneseDTO dto) {
        Anamnese anamnese = anamneseRepository.findByEducandoId(educandoId)
                .orElseThrow(() -> new AnamneseNotFoundException(educandoId));

        updateEntityFromDTO(anamnese, dto);

        return toDTO(anamneseRepository.save(anamnese));
    }

    private AnamneseDTO toDTO(Anamnese anamnese) {
        AnamneseDTO dto = new AnamneseDTO();
        dto.setId(anamnese.getId());
        dto.setTemConvulsao(toString(anamnese.getTemConvulsao()));
        dto.setConvenioMedico(anamnese.getConvenioMedico());
        dto.setVacinacaoEmDia(toString(anamnese.getVacinacaoEmDia()));
        dto.setDoencaContagiosa(anamnese.getDoencaContagiosa());
        dto.setUsoMedicacoes(anamnese.getUsoMedicacoes());
        dto.setServicosSaudeOuEducacao(anamnese.getServicosSaudeOuEducacao());
        dto.setInicioEscolarizacao(anamnese.getInicioEscolarizacao());
        dto.setDificuldadesEscolares(anamnese.getDificuldadesEscolares());
        dto.setApoioPedagogicoEmCasa(anamnese.getApoioPedagogicoEmCasa());
        dto.setDuracaoGestacao(anamnese.getDuracaoGestacao());
        dto.setFezPreNatal(toString(anamnese.getFezPreNatal()));
        dto.setPrematuridade(anamnese.getPrematuridade());
        dto.setCidadeNascimento(anamnese.getCidadeNascimento());
        dto.setMaternidadeNascimento(anamnese.getMaternidadeNascimento());
        dto.setTipoParto(anamnese.getTipoParto());
        dto.setChorouAoNascer(toString(anamnese.getChorouAoNascer()));
        dto.setFicouRoxo(toString(anamnese.getFicouRoxo()));
        dto.setUsoIncubadora(toString(anamnese.getUsoIncubadora()));
        dto.setFoiAmamentado(toString(anamnese.getFoiAmamentado()));
        dto.setSustentouCabecaMeses(anamnese.getSustentouCabecaMeses());
        dto.setEngatinhouMeses(anamnese.getEngatinhouMeses());
        dto.setSentouMeses(anamnese.getSentouMeses());
        dto.setAndouMeses(anamnese.getAndouMeses());
        dto.setPrecisouTerapiaMotivo(anamnese.getPrecisouTerapiaMotivo());
        dto.setFalouMeses(anamnese.getFalouMeses());
        dto.setPrimeiroBalbucioMeses(anamnese.getPrimeiroBalbucioMeses());
        dto.setPrimeiraPalavraQuando(anamnese.getPrimeiraPalavraQuando());
        dto.setPrimeiraFraseQuando(anamnese.getPrimeiraFraseQuando());
        dto.setFalaNaturalOuInibido(anamnese.getFalaNaturalOuInibido());
        dto.setDisturbioFala(anamnese.getDisturbioFala());
        dto.setDormeSozinho(toString(anamnese.getDormeSozinho()));
        dto.setTemQuartoProprio(toString(anamnese.getTemQuartoProprio()));
        dto.setSonoCalmoOuAgitado(anamnese.getSonoCalmoOuAgitado());
        dto.setRespeitaRegras(toString(anamnese.getRespeitaRegras()));
        dto.setDesmotivado(toString(anamnese.getDesmotivado()));
        dto.setAgressivo(toString(anamnese.getAgressivo()));
        dto.setApresentaInquietacao(toString(anamnese.getApresentaInquietacao()));
        return dto;
    }

    private Anamnese toEntity(AnamneseDTO dto) {
        Anamnese anamnese = new Anamnese();
        updateEntityFromDTO(anamnese, dto);
        return anamnese;
    }

    private void updateEntityFromDTO(Anamnese anamnese, AnamneseDTO dto) {
        anamnese.setTemConvulsao(toSimNao(dto.getTemConvulsao()));
        anamnese.setConvenioMedico(dto.getConvenioMedico());
        anamnese.setVacinacaoEmDia(toSimNao(dto.getVacinacaoEmDia()));
        anamnese.setDoencaContagiosa(dto.getDoencaContagiosa());
        anamnese.setUsoMedicacoes(dto.getUsoMedicacoes());
        anamnese.setServicosSaudeOuEducacao(dto.getServicosSaudeOuEducacao());
        anamnese.setInicioEscolarizacao(dto.getInicioEscolarizacao());
        anamnese.setDificuldadesEscolares(dto.getDificuldadesEscolares());
        anamnese.setApoioPedagogicoEmCasa(dto.getApoioPedagogicoEmCasa());
        anamnese.setDuracaoGestacao(dto.getDuracaoGestacao());
        anamnese.setFezPreNatal(toSimNao(dto.getFezPreNatal()));
        anamnese.setPrematuridade(dto.getPrematuridade());
        anamnese.setCidadeNascimento(dto.getCidadeNascimento());
        anamnese.setMaternidadeNascimento(dto.getMaternidadeNascimento());
        anamnese.setTipoParto(dto.getTipoParto());
        anamnese.setChorouAoNascer(toSimNao(dto.getChorouAoNascer()));
        anamnese.setFicouRoxo(toSimNao(dto.getFicouRoxo()));
        anamnese.setUsoIncubadora(toSimNao(dto.getUsoIncubadora()));
        anamnese.setFoiAmamentado(toSimNao(dto.getFoiAmamentado()));
        anamnese.setSustentouCabecaMeses(dto.getSustentouCabecaMeses());
        anamnese.setEngatinhouMeses(dto.getEngatinhouMeses());
        anamnese.setSentouMeses(dto.getSentouMeses());
        anamnese.setAndouMeses(dto.getAndouMeses());
        anamnese.setPrecisouTerapiaMotivo(dto.getPrecisouTerapiaMotivo());
        anamnese.setFalouMeses(dto.getFalouMeses());
        anamnese.setPrimeiroBalbucioMeses(dto.getPrimeiroBalbucioMeses());
        anamnese.setPrimeiraPalavraQuando(dto.getPrimeiraPalavraQuando());
        anamnese.setPrimeiraFraseQuando(dto.getPrimeiraFraseQuando());
        anamnese.setFalaNaturalOuInibido(dto.getFalaNaturalOuInibido());
        anamnese.setDisturbioFala(dto.getDisturbioFala());
        anamnese.setDormeSozinho(toSimNao(dto.getDormeSozinho()));
        anamnese.setTemQuartoProprio(toSimNao(dto.getTemQuartoProprio()));
        anamnese.setSonoCalmoOuAgitado(dto.getSonoCalmoOuAgitado());
        anamnese.setRespeitaRegras(toSimNao(dto.getRespeitaRegras()));
        anamnese.setDesmotivado(toSimNao(dto.getDesmotivado()));
        anamnese.setAgressivo(toSimNao(dto.getAgressivo()));
        anamnese.setApresentaInquietacao(toSimNao(dto.getApresentaInquietacao()));
    }

    private com.pies.api.projeto.integrado.pies_backend.model.Enums.SimNao toSimNao(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        String normalized = value.trim().toUpperCase();
        return switch (normalized) {
            case "SIM" -> com.pies.api.projeto.integrado.pies_backend.model.Enums.SimNao.SIM;
            case "NAO", "NÃO" -> com.pies.api.projeto.integrado.pies_backend.model.Enums.SimNao.NAO;
            case "AS_VEZES", "ÀS VEZES", "AS VEZES" -> com.pies.api.projeto.integrado.pies_backend.model.Enums.SimNao.AS_VEZES;
            default -> com.pies.api.projeto.integrado.pies_backend.model.Enums.SimNao.valueOf(normalized);
        };
    }

    private String toString(com.pies.api.projeto.integrado.pies_backend.model.Enums.SimNao value) {
        return value != null ? value.name() : null;
    }
}

