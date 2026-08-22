package com.corporacion.tecnica.service;

import com.corporacion.tecnica.dto.institucion.InstitucionRequest;
import com.corporacion.tecnica.dto.institucion.InstitucionResponse;
import java.util.List;

public interface InstitucionService {
    InstitucionResponse crear(InstitucionRequest request);
    List<InstitucionResponse> listar();
}

