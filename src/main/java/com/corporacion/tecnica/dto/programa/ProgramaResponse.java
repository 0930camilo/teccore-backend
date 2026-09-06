package com.corporacion.tecnica.dto.programa;

import com.corporacion.tecnica.entity.EstadoRegistro;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProgramaResponse {
    private Long id;
    private String nombre;
    private Integer duracionSemestres;
    private String nivel;
    private BigDecimal costoSemestral;
    private EstadoRegistro estado;
    private Long institucionId;
    private Long sedeId;
}

