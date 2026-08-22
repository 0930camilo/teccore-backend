package com.corporacion.tecnica.dto.materia;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MateriaResponse {
    private Long id;
    private String nombre;
    private Integer intensidadHoraria;
    private Long cursoId;
    private Long institucionId;
}

