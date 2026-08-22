package com.corporacion.tecnica.dto.actividad;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActividadResponse {
    private Long id;
    private String titulo;
    private String tipo;
    private LocalDate fechaEntrega;
    private Long materiaId;
    private Long institucionId;
}

