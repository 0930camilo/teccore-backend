package com.corporacion.tecnica.dto.docente;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DocenteResponse {
    private Long id;
    private String nombres;
    private String apellidos;
    private String documento;
    private String correo;
    private Integer cargaHorariaSemanal;
    private Long institucionId;
}
