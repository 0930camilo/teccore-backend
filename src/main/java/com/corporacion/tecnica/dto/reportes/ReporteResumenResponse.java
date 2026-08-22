package com.corporacion.tecnica.dto.reportes;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReporteResumenResponse {
    private final long totalAlumnos;
    private final long totalDocentes;
    private final long totalCursos;
    private final long totalPagos;
}

