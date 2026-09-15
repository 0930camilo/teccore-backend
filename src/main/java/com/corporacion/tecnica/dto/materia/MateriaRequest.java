package com.corporacion.tecnica.dto.materia;

import com.corporacion.tecnica.entity.DiaSemana;
import com.corporacion.tecnica.entity.EstadoRegistro;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MateriaRequest {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    private Integer intensidadHoraria;

    @NotNull(message = "El semestre es obligatorio")
    private Long semestreId;

    private Long institucionId;

    private Long sedeId;

    private Long docenteId;

    private EstadoRegistro estado;

    @NotNull(message = "El día de la semana es obligatorio")
    private DiaSemana diaSemana;

    @NotBlank(message = "La hora de inicio es obligatoria")
    @Pattern(
            regexp = "^([01]\\d|2[0-3]):[0-5]\\d$",
            message = "La hora de inicio debe tener formato HH:mm"
    )
    private String horaInicio;

    @NotBlank(message = "La hora de finalización es obligatoria")
    @Pattern(
            regexp = "^([01]\\d|2[0-3]):[0-5]\\d$",
            message = "La hora de finalización debe tener formato HH:mm"
    )
    private String horaFin;
}