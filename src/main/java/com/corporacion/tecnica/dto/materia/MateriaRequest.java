package com.corporacion.tecnica.dto.materia;

import com.corporacion.tecnica.entity.EstadoRegistro;
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

    private Long institucionId;

    private Long sedeId;

    private EstadoRegistro estado;
}

