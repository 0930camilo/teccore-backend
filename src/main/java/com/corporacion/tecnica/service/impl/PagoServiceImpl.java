package com.corporacion.tecnica.service.impl;

import com.corporacion.tecnica.dto.PageResponse;
import com.corporacion.tecnica.dto.contabilidad.PagoRequest;
import com.corporacion.tecnica.dto.contabilidad.PagoResponse;
import com.corporacion.tecnica.entity.Pago;
import com.corporacion.tecnica.mapper.PagoMapper;
import com.corporacion.tecnica.repository.AlumnoRepository;
import com.corporacion.tecnica.repository.PagoRepository;
import com.corporacion.tecnica.service.PagoService;
import com.corporacion.tecnica.util.ApiResponseFactory;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PagoServiceImpl implements PagoService {

    private final PagoRepository pagoRepository;
    private final AlumnoRepository alumnoRepository;
    private final PagoMapper pagoMapper;
    private final InstitutionScopeResolver institutionScopeResolver;

    @Override
    @Transactional
    public PagoResponse crear(PagoRequest request) {
        Pago pago = pagoMapper.toEntity(request);
        pago.setInstitucion(institutionScopeResolver.getRequiredInstitution(request.getInstitucionId()));
        pago.setAlumno(alumnoRepository.findById(request.getAlumnoId()).orElseThrow());
        pago.setFechaPago(request.getFechaPago() == null ? LocalDate.now() : request.getFechaPago());
        return pagoMapper.toResponse(pagoRepository.save(pago));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<PagoResponse> listar(String q, int page, int size) {
        Long institucionId = institutionScopeResolver.resolveInstitutionId(null);
        Page<PagoResponse> result = pagoRepository
                .findByInstitucionIdAndConceptoContainingIgnoreCase(institucionId, q == null ? "" : q, PageRequest.of(page, size))
                .map(pagoMapper::toResponse);
        return ApiResponseFactory.page(result);
    }
}

