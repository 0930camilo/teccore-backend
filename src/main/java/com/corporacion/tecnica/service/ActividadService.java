package com.corporacion.tecnica.service;

import com.corporacion.tecnica.dto.PageResponse;
import com.corporacion.tecnica.dto.actividad.ActividadRequest;
import com.corporacion.tecnica.dto.actividad.ActividadResponse;

public interface ActividadService {
    ActividadResponse crear(ActividadRequest request);
    PageResponse<ActividadResponse> listar(String q, int page, int size);
}

