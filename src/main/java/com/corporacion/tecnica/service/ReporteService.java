package com.corporacion.tecnica.service;

import com.corporacion.tecnica.dto.reportes.ReporteResumenResponse;

public interface ReporteService {
    ReporteResumenResponse resumenInstitucion(Long institucionId);
}

