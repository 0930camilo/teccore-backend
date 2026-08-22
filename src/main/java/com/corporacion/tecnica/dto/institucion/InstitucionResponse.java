package com.corporacion.tecnica.dto.institucion;

import com.corporacion.tecnica.entity.EstadoRegistro;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InstitucionResponse {
    private Long id;
    private String codigo;
    private String nombre;
    private String nit;
    private String correo;
    private String telefono;
    private String direccion;
    private EstadoRegistro estado;
}

