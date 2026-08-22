package com.corporacion.tecnica.dto.nota;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotaResponse {
    private Long id;
    private Long alumnoId;
    private Long materiaId;
    private String periodo;
    private BigDecimal valor;
    private Long institucionId;
}
