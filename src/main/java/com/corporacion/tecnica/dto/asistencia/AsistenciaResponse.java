package com.corporacion.tecnica.dto.asistencia;

import com.corporacion.tecnica.entity.EstadoAsistencia;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AsistenciaResponse {
    private Long id;
    private Long alumnoId;
    private Long materiaId;
    private LocalDate fecha;
    private EstadoAsistencia estadoAsistencia;
    private String observacion;
    private Long institucionId;
}
