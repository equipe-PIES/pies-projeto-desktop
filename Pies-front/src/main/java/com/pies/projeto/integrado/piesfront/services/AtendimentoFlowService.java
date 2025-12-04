package com.pies.projeto.integrado.piesfront.services;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AtendimentoFlowService {
    public enum Etapa { ANAMNESE, DI, PDI, PAEE, COMPLETO }

    private static AtendimentoFlowService instance;
    private final Map<String, Etapa> estado = new ConcurrentHashMap<>();

    private AtendimentoFlowService() {}

    public static synchronized AtendimentoFlowService getInstance() {
        if (instance == null) {
            instance = new AtendimentoFlowService();
        }
        return instance;
    }

    public Etapa getEtapaAtual(String educandoId) {
        if (educandoId == null || educandoId.isEmpty()) {
            return Etapa.ANAMNESE;
        }
        return estado.getOrDefault(educandoId, Etapa.ANAMNESE);
    }

    public void concluirAnamnese(String educandoId) {
        if (educandoId == null || educandoId.isEmpty()) {
            return;
        }
        estado.put(educandoId, Etapa.DI);
    }

    public void concluirDI(String educandoId) {
        if (educandoId == null || educandoId.isEmpty()) {
            return;
        }
        estado.put(educandoId, Etapa.PDI);
    }

    public void concluirPDI(String educandoId) {
        if (educandoId == null || educandoId.isEmpty()) {
            return;
        }
        estado.put(educandoId, Etapa.PAEE);
    }

    public void concluirPAEE(String educandoId) {
        if (educandoId == null || educandoId.isEmpty()) {
            return;
        }
        estado.put(educandoId, Etapa.COMPLETO);
    }

    public void concluirRelatorioIndividual(String educandoId) {
        if (educandoId == null || educandoId.isEmpty()) {
            return;
        }
        estado.put(educandoId, Etapa.COMPLETO);
    }
}
