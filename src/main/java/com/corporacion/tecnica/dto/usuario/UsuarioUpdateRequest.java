package com.corporacion.tecnica.dto.usuario;

import com.corporacion.tecnica.entity.EstadoRegistro;
import com.corporacion.tecnica.entity.RolNombre;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioUpdateRequest {

    @NotBlank
    private String nombre;

    @NotBlank
    private String email;

    @NotNull
    private RolNombre rol;

    private Long institucionId;
    private Long sedeId;
    private EstadoRegistro estado;
}
