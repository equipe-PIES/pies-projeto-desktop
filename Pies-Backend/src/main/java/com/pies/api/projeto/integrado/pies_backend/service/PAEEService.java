package com.pies.api.projeto.integrado.pies_backend.service;

import com.pies.api.projeto.integrado.pies_backend.controller.dto.CreatePAEEDTO;
import com.pies.api.projeto.integrado.pies_backend.controller.dto.PAEEDTO;
import com.pies.api.projeto.integrado.pies_backend.model.PAEE;
import com.pies.api.projeto.integrado.pies_backend.repository.PAEERepository;
import com.pies.api.projeto.integrado.pies_backend.exception.PAEENotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PAEEService {

    private final PAEERepository repository;

    public PAEEService(PAEERepository repository) {
        this.repository = repository;
    }

    @Transactional
    public PAEEDTO create(CreatePAEEDTO dto) {
        PAEE entity = new PAEE();
        
        BeanUtils.copyProperties(dto, entity);

        // Salva no banco
        PAEE saved = repository.save(entity);
        
        // Converte de volta para DTO para responder a API
        return mapToDTO(saved);
    }
    
    @Transactional
    public PAEEDTO update(String id, CreatePAEEDTO dto) {
        PAEE entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("PAEE não encontrado com id: " + id));
        BeanUtils.copyProperties(dto, entity);
        PAEE saved = repository.save(entity);
        return mapToDTO(saved);
    }
    
    @Transactional
    public void delete(String id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
        } else {
            throw new PAEENotFoundException(id);
        }
    }

    public PAEEDTO findById(String id) {
        PAEE entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("PAEE não encontrado com id: " + id));
        return mapToDTO(entity);
    }

    public List<PAEEDTO> findByAluno(String alunoId) {
        return repository.findByAlunoId(alunoId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Método auxiliar para transformar Entidade do banco em DTO de resposta
    private PAEEDTO mapToDTO(PAEE entity) {
        PAEEDTO dto = new PAEEDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}
