package com.corporacion.tecnica.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "semestres", uniqueConstraints = {
        @UniqueConstraint(name = "uk_semestre_programa_numero", columnNames = {"programa_id", "numero"})
})
public class Semestre extends BaseInstitutionEntity {

    @Column(nullable = false)
    private Integer numero;

    @Column(nullable = false)
    private String nombre;

    @Column(name = "anio")
    private Integer anio;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "programa_id", nullable = false)
    private Programa programa;
}


