package com.corporacion.tecnica.service;

import com.corporacion.tecnica.dto.PageResponse;
import com.corporacion.tecnica.dto.semestre.SemestreRequest;
import com.corporacion.tecnica.dto.semestre.SemestreResponse;

public interface SemestreService {
    SemestreResponse crear(SemestreRequest request);
    PageResponse<SemestreResponse> listar(String q, int page, int size);
}

