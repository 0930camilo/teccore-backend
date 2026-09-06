package com.corporacion.tecnica.dto.sede;

import com.corporacion.tecnica.entity.EstadoRegistro;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SedeResponse {
    private Long id;
    private String nombre;
    private String ciudad;
    private String direccion;
    private Long institucionId;
    private String institucionNombre;
    private EstadoRegistro estado;
}

