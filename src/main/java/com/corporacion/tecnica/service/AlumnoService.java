package com.corporacion.tecnica.service;

import com.corporacion.tecnica.dto.PageResponse;
import com.corporacion.tecnica.dto.alumno.AlumnoRequest;
import com.corporacion.tecnica.dto.alumno.AlumnoResponse;

public interface AlumnoService {
    AlumnoResponse crear(AlumnoRequest request);
    PageResponse<AlumnoResponse> listar(String q, int page, int size);
}
