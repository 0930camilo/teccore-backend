package com.corporacion.tecnica.dto.programa;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProgramaRequest {

    @NotBlank
    private String nombre;

    private Integer duracionSemestres;

    private String nivel;

    @NotNull
    private Long institucionId;
}

