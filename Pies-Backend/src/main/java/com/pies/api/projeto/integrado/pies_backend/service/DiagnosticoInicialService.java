package com.pies.api.projeto.integrado.pies_backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pies.api.projeto.integrado.pies_backend.controller.dto.CreateDiagnosticoInicialDTO;
import com.pies.api.projeto.integrado.pies_backend.controller.dto.DiagnosticoInicialDTO;
import com.pies.api.projeto.integrado.pies_backend.model.DiagnosticoInicial;
import com.pies.api.projeto.integrado.pies_backend.model.Educando;
import com.pies.api.projeto.integrado.pies_backend.model.Professor;
import com.pies.api.projeto.integrado.pies_backend.repository.DiagnosticoInicialRepository;
import com.pies.api.projeto.integrado.pies_backend.repository.EducandoRepository;
import com.pies.api.projeto.integrado.pies_backend.repository.ProfessorRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DiagnosticoInicialService {

    private final DiagnosticoInicialRepository repository;
    private final EducandoRepository educandoRepository;
    private final ProfessorRepository professorRepository;

    @Transactional(readOnly = true)
    public List<DiagnosticoInicialDTO> listarTodos() {
        return repository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DiagnosticoInicialDTO buscarPorId(String id) {
        return repository.findByIdWithRelations(id).map(this::toDTO).orElseThrow();
    }

    @Transactional
    public DiagnosticoInicialDTO salvar(CreateDiagnosticoInicialDTO dto, String userId, String educandoId) {
        Educando educando = educandoRepository.findById(educandoId).orElseThrow();
        Professor professor = professorRepository.findByUserId(userId);
        if (professor == null) throw new RuntimeException("Professor não encontrado");

        if (repository.existsByEducandoId(educandoId)) {
            throw new RuntimeException("Diagnóstico Inicial já existe para o educando");
        }
        DiagnosticoInicial d = new DiagnosticoInicial(educando, professor);
        fillEntity(d, dto);
        d = repository.save(d);
        return toDTO(d);
    }

    @Transactional
    public DiagnosticoInicialDTO atualizar(String id, String educandoId, CreateDiagnosticoInicialDTO dto) {
        DiagnosticoInicial d = repository.findByIdWithRelations(id)
                .orElseThrow(() -> new RuntimeException("Diagnóstico não encontrado"));

        if (d.getEducando() == null || !d.getEducando().getId().equals(educandoId)) {
            throw new RuntimeException("Este diagnóstico não pertence ao aluno informado!");
        }

        fillEntity(d, dto);
        d = repository.save(d);
        return toDTO(d);
    }

    @Transactional
    public void deletar(String id) {
        DiagnosticoInicial d = repository.findById(id).orElseThrow();
        repository.delete(d);
    }

    @Transactional(readOnly = true)
    public DiagnosticoInicialDTO buscarPorEducando(String educandoId) {
        return repository.findByEducandoId(educandoId).map(this::toDTO).orElseThrow();
    }

    

    @Transactional(readOnly = true)
    public List<DiagnosticoInicialDTO> buscarPorProfessor(String professorId) {
        return repository.findByProfessorId(professorId).stream().map(this::toDTO).collect(Collectors.toList());
    }

    private void fillEntity(DiagnosticoInicial d, CreateDiagnosticoInicialDTO dto) {
        d.setFalaSeuNome(dto.falaSeuNome());
        d.setDizDataNascimento(dto.dizDataNascimento());
        d.setLePalavras(dto.lePalavras());
        d.setInformaNumeroTelefone(dto.informaNumeroTelefone());
        d.setEmiteRespostas(dto.emiteRespostas());
        d.setTransmiteRecado(dto.transmiteRecado());
        d.setInformaEndereco(dto.informaEndereco());
        d.setInformaNomePais(dto.informaNomePais());
        d.setCompreendeOrdens(dto.compreendeOrdens());
        d.setExpoeIdeias(dto.expoeIdeias());
        d.setRecontaHistorias(dto.recontaHistorias());
        d.setUsaSistemaCA(dto.usaSistemaCA());
        d.setRelataFatosComCoerencia(dto.relataFatosComCoerencia());
        d.setPronunciaLetrasAlfabeto(dto.pronunciaLetrasAlfabeto());
        d.setVerbalizaMusicas(dto.verbalizaMusicas());
        d.setInterpretaHistorias(dto.interpretaHistorias());
        d.setFormulaPerguntas(dto.formulaPerguntas());
        d.setUtilizaGestosParaSeComunicar(dto.utilizaGestosParaSeComunicar());

        d.setDemonstraCooperacao(dto.demonstraCooperacao());
        d.setTimidoInseguro(dto.timidoInseguro());
        d.setFazBirra(dto.fazBirra());
        d.setSolicitaOfereceAjuda(dto.solicitaOfereceAjuda());
        d.setRiComFrequencia(dto.riComFrequencia());
        d.setCompartilhaOQueESeu(dto.compartilhaOQueESeu());
        d.setDemonstraAmorGentilezaAtencao(dto.demonstraAmorGentilezaAtencao());
        d.setChoraComFrequencia(dto.choraComFrequencia());
        d.setInterageComColegas(dto.interageComColegas());

        d.setCaptaDetalhesGravura(dto.captaDetalhesGravura());
        d.setReconheceVozes(dto.reconheceVozes());
        d.setReconheceCancoes(dto.reconheceCancoes());
        d.setPercebeTexturas(dto.percebeTexturas());
        d.setPercepcaoCores(dto.percepcaoCores());
        d.setDiscriminaSons(dto.discriminaSons());
        d.setDiscriminaOdores(dto.discriminaOdores());
        d.setAceitaDiferentesTexturas(dto.aceitaDiferentesTexturas());
        d.setPercepcaoFormas(dto.percepcaoFormas());
        d.setIdentificaDirecaoSom(dto.identificaDirecaoSom());
        d.setPercebeDiscriminaSabores(dto.percebeDiscriminaSabores());
        d.setAcompanhaFocoLuminoso(dto.acompanhaFocoLuminoso());

        d.setMovimentoPincaComTesoura(dto.movimentoPincaComTesoura());
        d.setAmassaPapel(dto.amassaPapel());
        d.setCaiComFacilidade(dto.caiComFacilidade());
        d.setEncaixaPecas(dto.encaixaPecas());
        d.setRecorta(dto.recorta());
        d.setUnePontos(dto.unePontos());
        d.setConsegueCorrer(dto.consegueCorrer());
        d.setEmpilha(dto.empilha());
        d.setAgitacaoMotora(dto.agitacaoMotora());
        d.setAndaLinhaReta(dto.andaLinhaReta());
        d.setSobeDesceEscadas(dto.sobeDesceEscadas());
        d.setArremessaBola(dto.arremessaBola());

        d.setUsaSanitarioSemAjuda(dto.usaSanitarioSemAjuda());
        d.setPenteiaSeSo(dto.penteiaSeSo());
        d.setConsegueVestirDespirSe(dto.consegueVestirDespirSe());
        d.setLavaSecaAsMaos(dto.lavaSecaAsMaos());
        d.setBanhoComModeracao(dto.banhoComModeracao());
        d.setCalcaSeSo(dto.calcaSeSo());
        d.setReconheceRoupas(dto.reconheceRoupas());
        d.setAbreFechaTorneira(dto.abreFechaTorneira());
        d.setEscovaDentesSemAjuda(dto.escovaDentesSemAjuda());
        d.setConsegueDarNosLacos(dto.consegueDarNosLacos());
        d.setAbotoaDesabotoaRoupas(dto.abotoaDesabotoaRoupas());
        d.setIdentificaPartesDoCorpo(dto.identificaPartesDoCorpo());

        d.setGaratujas(dto.garatujas());
        d.setPreSilabico(dto.preSilabico());
        d.setSilabico(dto.silabico());
        d.setSilabicoAlfabetico(dto.silabicoAlfabetico());
        d.setAlfabetico(dto.alfabetico());

        d.setObservacoes(dto.observacoes());
    }

    private DiagnosticoInicialDTO toDTO(DiagnosticoInicial d) {
        String educandoId = d.getEducando() != null ? d.getEducando().getId() : null;
        String educandoNome = d.getEducando() != null ? d.getEducando().getNome() : null;
        String professorId = d.getProfessor() != null ? d.getProfessor().getId() : null;
        String professorNome = d.getProfessor() != null ? d.getProfessor().getNome() : null;

        return new DiagnosticoInicialDTO(
                d.getId(),
                educandoId,
                educandoNome,
                professorId,
                professorNome,
                d.getDataCriacao(),

                d.getFalaSeuNome(),
                d.getDizDataNascimento(),
                d.getLePalavras(),
                d.getInformaNumeroTelefone(),
                d.getEmiteRespostas(),
                d.getTransmiteRecado(),
                d.getInformaEndereco(),
                d.getInformaNomePais(),
                d.getCompreendeOrdens(),
                d.getExpoeIdeias(),
                d.getRecontaHistorias(),
                d.getUsaSistemaCA(),
                d.getRelataFatosComCoerencia(),
                d.getPronunciaLetrasAlfabeto(),
                d.getVerbalizaMusicas(),
                d.getInterpretaHistorias(),
                d.getFormulaPerguntas(),
                d.getUtilizaGestosParaSeComunicar(),

                d.getDemonstraCooperacao(),
                d.getTimidoInseguro(),
                d.getFazBirra(),
                d.getSolicitaOfereceAjuda(),
                d.getRiComFrequencia(),
                d.getCompartilhaOQueESeu(),
                d.getDemonstraAmorGentilezaAtencao(),
                d.getChoraComFrequencia(),
                d.getInterageComColegas(),

                d.getCaptaDetalhesGravura(),
                d.getReconheceVozes(),
                d.getReconheceCancoes(),
                d.getPercebeTexturas(),
                d.getPercepcaoCores(),
                d.getDiscriminaSons(),
                d.getDiscriminaOdores(),
                d.getAceitaDiferentesTexturas(),
                d.getPercepcaoFormas(),
                d.getIdentificaDirecaoSom(),
                d.getPercebeDiscriminaSabores(),
                d.getAcompanhaFocoLuminoso(),

                d.getMovimentoPincaComTesoura(),
                d.getAmassaPapel(),
                d.getCaiComFacilidade(),
                d.getEncaixaPecas(),
                d.getRecorta(),
                d.getUnePontos(),
                d.getConsegueCorrer(),
                d.getEmpilha(),
                d.getAgitacaoMotora(),
                d.getAndaLinhaReta(),
                d.getSobeDesceEscadas(),
                d.getArremessaBola(),

                d.getUsaSanitarioSemAjuda(),
                d.getPenteiaSeSo(),
                d.getConsegueVestirDespirSe(),
                d.getLavaSecaAsMaos(),
                d.getBanhoComModeracao(),
                d.getCalcaSeSo(),
                d.getReconheceRoupas(),
                d.getAbreFechaTorneira(),
                d.getEscovaDentesSemAjuda(),
                d.getConsegueDarNosLacos(),
                d.getAbotoaDesabotoaRoupas(),
                d.getIdentificaPartesDoCorpo(),

                d.getGaratujas(),
                d.getPreSilabico(),
                d.getSilabico(),
                d.getSilabicoAlfabetico(),
                d.getAlfabetico(),

                d.getObservacoes()
        );
    }
}
