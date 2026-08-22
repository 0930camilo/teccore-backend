package com.corporacion.tecnica.dto.actividad;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActividadRequest {

    @NotBlank
    private String titulo;

    private String tipo;

    private LocalDate fechaEntrega;

    private Long materiaId;

    @NotNull
    private Long institucionId;
}

