package com.corporacion.tecnica.service;

import com.corporacion.tecnica.dto.PageResponse;
import com.corporacion.tecnica.dto.materia.MateriaRequest;
import com.corporacion.tecnica.dto.materia.MateriaResponse;

public interface MateriaService {
    MateriaResponse crear(MateriaRequest request);
    PageResponse<MateriaResponse> listar(String q, int page, int size);
}

