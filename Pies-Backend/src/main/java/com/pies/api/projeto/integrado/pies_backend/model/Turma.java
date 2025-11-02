package com.pies.api.projeto.integrado.pies_backend.model;

import com.pies.api.projeto.integrado.pies_backend.model.Enums.GrauEscolar;
import com.pies.api.projeto.integrado.pies_backend.model.Enums.Turno;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "turmas")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class Turma {
    @Id
    @GeneratedValue(strategy= GenerationType.UUID)
    private String id;

    private String nome;

    @Enumerated(EnumType.STRING) // Salvando o nome do enum como texto no banco
    private GrauEscolar grauEscolar;
    
    private String faixaEtaria;

    @Enumerated(EnumType.STRING)
    private Turno turno;
    
    @ManyToOne(fetch= FetchType.LAZY) // no ManyToOne Muitas turmas podem ter 1 professor
    
    /* no FetchType.LAZY Quando for buscar uma turma, o professor não é carregado imediatamente.
Ele só é buscado quando for realmente acessado.*/

    @JoinColumn(name = "professor_id") // Nome da coluna de ligação na tabela
    private User professor;
}
