package com.corporacion.tecnica.dto.auth;

import com.corporacion.tecnica.entity.RolNombre;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

    @NotBlank
    private String nombre;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String password;

    private Long institucionId;

    private Long sedeId;

    @NotNull
    private RolNombre rol;
}
