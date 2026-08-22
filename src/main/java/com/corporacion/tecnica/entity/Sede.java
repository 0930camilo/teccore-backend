package com.corporacion.tecnica.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "sedes")
public class Sede extends BaseInstitutionEntity {

    @Column(nullable = false)
    private String nombre;

    private String ciudad;

    private String direccion;
}

