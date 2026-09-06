package com.corporacion.tecnica.dto.usuario;

import com.corporacion.tecnica.entity.EstadoRegistro;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioResponse {
    private Long id;
    private String nombre;
    private String email;
    private String rol;
    private Long institucionId;
    private String institucionNombre;
    private EstadoRegistro estado;
}

