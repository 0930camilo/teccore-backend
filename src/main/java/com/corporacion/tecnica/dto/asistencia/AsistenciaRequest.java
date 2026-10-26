package com.corporacion.tecnica.dto.asistencia;

import com.corporacion.tecnica.entity.EstadoAsistencia;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AsistenciaRequest {

    @NotNull(message = "El alumno es obligatorio")
    private Long alumnoId;

    @NotNull(message = "La materia es obligatoria")
    private Long materiaId;

    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fecha;

    @NotNull(message = "El estado de asistencia es obligatorio")
    private EstadoAsistencia estadoAsistencia;

    private String observacion;

    @NotNull(message = "La institucion es obligatoria")
    private Long institucionId;
}
