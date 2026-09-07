package com.corporacion.tecnica.dto.semestre;

import com.corporacion.tecnica.entity.EstadoRegistro;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SemestreRequest {

    @NotNull
    private Integer numero;

    private String nombre;

    private Integer anio;

    private EstadoRegistro estado;

    @NotNull
    private Long programaId;

    private Long institucionId;

    private Long sedeId;
}


