package com.corporacion.tecnica.service;

import com.corporacion.tecnica.dto.PageResponse;
import com.corporacion.tecnica.dto.materia.MateriaRequest;
import com.corporacion.tecnica.dto.materia.MateriaResponse;

public interface MateriaService {
    MateriaResponse crear(MateriaRequest request);
    MateriaResponse actualizar(Long id, MateriaRequest request);
    MateriaResponse obtenerPorId(Long id);
    PageResponse<MateriaResponse> listar(String q, int page, int size);
    void eliminar(Long id);
}

