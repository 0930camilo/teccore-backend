package com.corporacion.tecnica.service.impl;

import com.corporacion.tecnica.dto.PageResponse;
import com.corporacion.tecnica.dto.programa.ProgramaRequest;
import com.corporacion.tecnica.dto.programa.ProgramaResponse;
import com.corporacion.tecnica.entity.Programa;
import com.corporacion.tecnica.mapper.ProgramaMapper;
import com.corporacion.tecnica.repository.ProgramaRepository;
import com.corporacion.tecnica.service.ProgramaService;
import com.corporacion.tecnica.util.ApiResponseFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProgramaServiceImpl implements ProgramaService {

    private final ProgramaRepository programaRepository;
    private final ProgramaMapper programaMapper;
    private final InstitutionScopeResolver institutionScopeResolver;

    @Override
    @Transactional
    public ProgramaResponse crear(ProgramaRequest request) {
        Programa programa = programaMapper.toEntity(request);
        programa.setInstitucion(institutionScopeResolver.getRequiredInstitution(request.getInstitucionId()));
        return programaMapper.toResponse(programaRepository.save(programa));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProgramaResponse> listar(String q, int page, int size) {
        Long institucionId = institutionScopeResolver.resolveInstitutionId(null);
        Page<ProgramaResponse> result = programaRepository
                .findByInstitucionIdAndNombreContainingIgnoreCase(institucionId, q == null ? "" : q, PageRequest.of(page, size))
                .map(programaMapper::toResponse);
        return ApiResponseFactory.page(result);
    }
}

