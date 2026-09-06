package com.corporacion.tecnica.dto.sede;

import com.corporacion.tecnica.entity.EstadoRegistro;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SedeRequest {

    @NotBlank
    private String nombre;

    private String ciudad;

    private String direccion;

    @NotNull
    private Long institucionId;

    private EstadoRegistro estado;
}

