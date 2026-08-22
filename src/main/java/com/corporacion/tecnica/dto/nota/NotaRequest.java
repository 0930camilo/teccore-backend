package com.corporacion.tecnica.dto.nota;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotaRequest {

    @NotNull
    private Long alumnoId;

    @NotNull
    private Long materiaId;

    @NotBlank
    private String periodo;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal valor;

    @NotNull
    private Long institucionId;
}
