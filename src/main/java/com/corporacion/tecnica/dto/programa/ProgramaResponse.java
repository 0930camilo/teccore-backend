package com.corporacion.tecnica.dto.programa;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProgramaResponse {
    private Long id;
    private String nombre;
    private Integer duracionSemestres;
    private String nivel;
    private Long institucionId;
}

