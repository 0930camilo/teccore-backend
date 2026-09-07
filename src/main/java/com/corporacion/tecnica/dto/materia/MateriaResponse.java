package com.corporacion.tecnica.dto.materia;

import com.corporacion.tecnica.entity.EstadoRegistro;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MateriaResponse {
    private Long id;
    private String nombre;
    private Integer intensidadHoraria;
    private EstadoRegistro estado;
    private Long semestreId;
    private String semestreNombre;
    private Long programaId;
    private String programaNombre;
    private Long institucionId;
    private String institucionNombre;
}

