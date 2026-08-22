package com.corporacion.tecnica.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "programas")
public class Programa extends BaseInstitutionEntity {

    @Column(nullable = false)
    private String nombre;

    private Integer duracionMeses;

    private String nivel;
}

