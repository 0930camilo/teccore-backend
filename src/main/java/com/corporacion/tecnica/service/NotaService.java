package com.corporacion.tecnica.service;

import com.corporacion.tecnica.dto.PageResponse;
import com.corporacion.tecnica.dto.nota.NotaRequest;
import com.corporacion.tecnica.dto.nota.NotaResponse;

public interface NotaService {
    NotaResponse crear(NotaRequest request);
    PageResponse<NotaResponse> listar(String periodo, int page, int size);
}

