package com.corporacion.tecnica.dto.alumno;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AlumnoRequest {

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

    private String telefono;

    private Long institucionId;

    private Long sedeId;

    private Long semestreId;

    private List<Long> materiaIds;
}
