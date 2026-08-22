package com.corporacion.tecnica.service;

import com.corporacion.tecnica.dto.PageResponse;
import com.corporacion.tecnica.dto.contabilidad.PagoRequest;
import com.corporacion.tecnica.dto.contabilidad.PagoResponse;

public interface PagoService {
    PagoResponse crear(PagoRequest request);
    PageResponse<PagoResponse> listar(String q, int page, int size);
}
