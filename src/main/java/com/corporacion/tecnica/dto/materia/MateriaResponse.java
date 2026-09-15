package com.corporacion.tecnica.dto.materia;

import com.corporacion.tecnica.entity.DiaSemana;
import com.corporacion.tecnica.entity.EstadoRegistro;
import java.time.LocalTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MateriaResponse {

    private Long id;
    private String nombre;
    private Integer intensidadHoraria;
    private EstadoRegistro estado;

    private DiaSemana diaSemana;
    private LocalTime horaInicio;
    private LocalTime horaFin;

    private Long semestreId;
    private String semestreNombre;

    private Long programaId;
    private String programaNombre;

    private Long institucionId;
    private String institucionNombre;

    private Long docenteId;
    private String docenteNombre;
}