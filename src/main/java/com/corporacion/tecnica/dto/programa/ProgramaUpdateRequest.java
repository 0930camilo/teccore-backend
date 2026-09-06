package com.corporacion.tecnica.dto.programa;

import com.corporacion.tecnica.entity.EstadoRegistro;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProgramaUpdateRequest {

    @NotBlank
    private String nombre;

    private Integer duracionSemestres;

    private String nivel;

    @DecimalMin("0.0")
    private BigDecimal costoSemestral;

    private EstadoRegistro estado;

    @NotNull
    private Long sedeId;
}


