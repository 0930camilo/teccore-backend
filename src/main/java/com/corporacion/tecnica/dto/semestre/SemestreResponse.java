package com.corporacion.tecnica.dto.semestre;

import com.corporacion.tecnica.entity.EstadoRegistro;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SemestreResponse {
    private Long id;
    private Integer numero;
    private String nombre;
    private Integer anio;
    private EstadoRegistro estado;
    private Long programaId;
    private String programaNombre;
    private Long institucionId;
    private String institucionNombre;
}


