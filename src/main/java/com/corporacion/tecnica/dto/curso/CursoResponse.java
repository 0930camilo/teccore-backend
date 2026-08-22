package com.corporacion.tecnica.dto.curso;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CursoResponse {
    private Long id;
    private String nombre;
    private String jornada;
    private String periodoAcademico;
    private Long programaId;
    private Long institucionId;
}

