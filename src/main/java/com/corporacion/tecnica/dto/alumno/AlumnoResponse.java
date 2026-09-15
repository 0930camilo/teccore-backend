package com.corporacion.tecnica.dto.alumno;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AlumnoResponse {
    private Long id;
    private String nombres;
    private String apellidos;
    private String documento;
    private String correo;
    private String telefono;
    private Long institucionId;
    private Long sedeId;
    private Long semestreId;
    private List<Long> materiaIds;
}
