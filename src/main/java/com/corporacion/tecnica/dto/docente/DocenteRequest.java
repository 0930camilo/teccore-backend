package com.corporacion.tecnica.dto.docente;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DocenteRequest {

    @NotBlank
    private String nombres;

    @NotBlank
    private String apellidos;

    @NotBlank
    private String documento;

    @Email
    private String correo;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String password;

    private Integer cargaHorariaSemanal;

    @NotNull
    private Long institucionId;
}
