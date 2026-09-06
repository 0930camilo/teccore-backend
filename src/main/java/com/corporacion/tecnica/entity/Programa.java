package com.corporacion.tecnica.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "programas")
public class Programa extends BaseInstitutionEntity {

    @Column(nullable = false)
    private String nombre;

    private Integer duracionSemestres;

    private String nivel;

    @Column(precision = 12, scale = 2)
    private BigDecimal costoSemestral;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sede_id")
    private Sede sede;
}

