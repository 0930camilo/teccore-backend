package com.corporacion.tecnica.dto.institucion;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InstitucionRequest {

    @NotBlank
    private String codigo;

    @NotBlank
    private String nombre;

    private String nit;
    private String correo;
    private String telefono;
    private String direccion;
}

