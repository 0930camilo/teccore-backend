package com.corporacion.tecnica.service;

import com.corporacion.tecnica.dto.PageResponse;
import com.corporacion.tecnica.dto.asistencia.AsistenciaRequest;
import com.corporacion.tecnica.dto.asistencia.AsistenciaResponse;

public interface AsistenciaService {
    AsistenciaResponse crear(AsistenciaRequest request);
    PageResponse<AsistenciaResponse> listarPorMateria(Long materiaId, int page, int size);
    PageResponse<AsistenciaResponse> listarPorAlumno(Long alumnoId, int page, int size);
}

