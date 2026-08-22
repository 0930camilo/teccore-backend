package com.corporacion.tecnica.service;

import com.corporacion.tecnica.dto.PageResponse;
import com.corporacion.tecnica.dto.curso.CursoRequest;
import com.corporacion.tecnica.dto.curso.CursoResponse;

public interface CursoService {
    CursoResponse crear(CursoRequest request);
    PageResponse<CursoResponse> listar(String q, int page, int size);
}
