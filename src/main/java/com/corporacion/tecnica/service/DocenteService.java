package com.corporacion.tecnica.service;

import com.corporacion.tecnica.dto.PageResponse;
import com.corporacion.tecnica.dto.docente.DocenteRequest;
import com.corporacion.tecnica.dto.docente.DocenteResponse;

public interface DocenteService {
    DocenteResponse crear(DocenteRequest request);
    PageResponse<DocenteResponse> listar(String q, int page, int size);
}
