package com.corporacion.tecnica.dto.contabilidad;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PagoRequest {

    @NotNull
    private Long alumnoId;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal valor;

    @NotBlank
    private String concepto;

    private LocalDate fechaPago;

    @NotNull
    private Long institucionId;
}

