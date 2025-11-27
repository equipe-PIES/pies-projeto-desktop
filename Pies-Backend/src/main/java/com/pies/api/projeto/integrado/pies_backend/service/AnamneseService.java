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

        Anamnese anamnese = new Anamnese();
        BeanUtils.copyProperties(dto, anamnese, "id", "educando");
        anamnese.setEducando(educando);
        educando.setAnamnese(anamnese);

        return toDTO(anamneseRepository.save(anamnese));
    }

    @Transactional
    public AnamneseDTO atualizar(String educandoId, AnamneseDTO dto) {
        Anamnese anamnese = anamneseRepository.findByEducandoId(educandoId)
                .orElseThrow(() -> new AnamneseNotFoundException(educandoId));

        BeanUtils.copyProperties(dto, anamnese, "id", "educando");

        return toDTO(anamneseRepository.save(anamnese));
    }

    private AnamneseDTO toDTO(Anamnese anamnese) {
        AnamneseDTO dto = new AnamneseDTO();
        BeanUtils.copyProperties(anamnese, dto, "educando");
        return dto;
    }
}

