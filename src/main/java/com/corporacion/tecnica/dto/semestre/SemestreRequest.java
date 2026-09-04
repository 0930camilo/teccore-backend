package com.corporacion.tecnica.dto.semestre;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SemestreRequest {

    @NotNull
    private Integer numero;

    private String nombre;

    @NotNull
    private Long programaId;

    @NotNull
    private Long institucionId;
}


