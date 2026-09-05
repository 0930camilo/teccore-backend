package com.corporacion.tecnica.service;

import com.corporacion.tecnica.dto.PageResponse;
import com.corporacion.tecnica.dto.institucion.InstitucionRequest;
import com.corporacion.tecnica.dto.institucion.InstitucionResponse;
import com.corporacion.tecnica.dto.institucion.InstitucionUpdateRequest;
import com.corporacion.tecnica.entity.EstadoRegistro;

public interface InstitucionService {
    InstitucionResponse crear(InstitucionRequest request);
    InstitucionResponse actualizar(Long id, InstitucionUpdateRequest request);
    PageResponse<InstitucionResponse> listar(String q, String codigo, String nombre, String nit, EstadoRegistro estado, int page, int size);
}

