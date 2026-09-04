package com.corporacion.tecnica.dto.materia;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MateriaRequest {

    @NotBlank
    private String nombre;

    private Integer intensidadHoraria;

    @NotNull
    private Long semestreId;

    @NotNull
    private Long institucionId;
}

