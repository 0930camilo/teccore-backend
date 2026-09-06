package com.corporacion.tecnica.service;

import com.corporacion.tecnica.dto.PageResponse;
import com.corporacion.tecnica.dto.sede.SedeRequest;
import com.corporacion.tecnica.dto.sede.SedeResponse;
import com.corporacion.tecnica.dto.sede.SedeUpdateRequest;
import com.corporacion.tecnica.entity.EstadoRegistro;

public interface SedeService {
    SedeResponse crear(SedeRequest request);
    SedeResponse actualizar(Long id, SedeUpdateRequest request);
    PageResponse<SedeResponse> listar(String q, String nombre, String ciudad, EstadoRegistro estado, int page, int size);
}

