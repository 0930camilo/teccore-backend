package com.corporacion.tecnica.dto.semestre;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SemestreResponse {
    private Long id;
    private Integer numero;
    private String nombre;
    private Long programaId;
    private Long institucionId;
}


