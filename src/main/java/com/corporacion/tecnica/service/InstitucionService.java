package com.corporacion.tecnica.service;

import com.corporacion.tecnica.dto.PageResponse;
import com.corporacion.tecnica.dto.institucion.InstitucionRequest;
import com.corporacion.tecnica.dto.institucion.InstitucionResponse;

public interface InstitucionService {
    InstitucionResponse crear(InstitucionRequest request);
    PageResponse<InstitucionResponse> listar(String q, int page, int size);
}

