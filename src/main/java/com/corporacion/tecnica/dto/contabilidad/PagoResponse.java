package com.corporacion.tecnica.dto.contabilidad;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PagoResponse {
    private Long id;
    private Long alumnoId;
    private BigDecimal valor;
    private String concepto;
    private LocalDate fechaPago;
    private Long institucionId;
}

