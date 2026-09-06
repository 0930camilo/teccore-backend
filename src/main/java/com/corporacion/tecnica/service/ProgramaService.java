package com.corporacion.tecnica.service;

import com.corporacion.tecnica.dto.PageResponse;
import com.corporacion.tecnica.dto.programa.ProgramaRequest;
import com.corporacion.tecnica.dto.programa.ProgramaResponse;
import com.corporacion.tecnica.dto.programa.ProgramaUpdateRequest;
import com.corporacion.tecnica.entity.EstadoRegistro;

public interface ProgramaService {
    ProgramaResponse crear(ProgramaRequest request);
    ProgramaResponse actualizar(Long id, ProgramaUpdateRequest request);
    PageResponse<ProgramaResponse> listar(String q, String nombre, EstadoRegistro estado, int page, int size);
}

