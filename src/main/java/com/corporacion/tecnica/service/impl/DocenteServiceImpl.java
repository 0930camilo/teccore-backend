package com.corporacion.tecnica.service.impl;

import com.corporacion.tecnica.dto.PageResponse;
import com.corporacion.tecnica.dto.docente.DocenteRequest;
import com.corporacion.tecnica.dto.docente.DocenteResponse;
import com.corporacion.tecnica.entity.Docente;
import com.corporacion.tecnica.mapper.DocenteMapper;
import com.corporacion.tecnica.repository.DocenteRepository;
import com.corporacion.tecnica.service.DocenteService;
import com.corporacion.tecnica.util.ApiResponseFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DocenteServiceImpl implements DocenteService {

    private final DocenteRepository docenteRepository;
    private final DocenteMapper docenteMapper;
    private final InstitutionScopeResolver institutionScopeResolver;

    @Override
    @Transactional
    public DocenteResponse crear(DocenteRequest request) {
        Docente docente = docenteMapper.toEntity(request);
        docente.setInstitucion(institutionScopeResolver.getRequiredInstitution(request.getInstitucionId()));
        return docenteMapper.toResponse(docenteRepository.save(docente));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<DocenteResponse> listar(String q, int page, int size) {
        Long institucionId = institutionScopeResolver.resolveInstitutionId(null);
        Page<DocenteResponse> result = docenteRepository
                .findByInstitucionIdAndNombresContainingIgnoreCase(institucionId, q == null ? "" : q, PageRequest.of(page, size))
                .map(docenteMapper::toResponse);
        return ApiResponseFactory.page(result);
    }
}
