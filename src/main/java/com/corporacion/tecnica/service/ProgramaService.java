package com.corporacion.tecnica.service;

import com.corporacion.tecnica.dto.PageResponse;
import com.corporacion.tecnica.dto.programa.ProgramaRequest;
import com.corporacion.tecnica.dto.programa.ProgramaResponse;

public interface ProgramaService {
    ProgramaResponse crear(ProgramaRequest request);
    PageResponse<ProgramaResponse> listar(String q, int page, int size);
}

