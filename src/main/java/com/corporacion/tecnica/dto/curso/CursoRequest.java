package com.corporacion.tecnica.dto.curso;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CursoRequest {

    @NotBlank
    private String nombre;

    private String jornada;

    private String periodoAcademico;

    private Long programaId;

    @NotNull
    private Long institucionId;
}

