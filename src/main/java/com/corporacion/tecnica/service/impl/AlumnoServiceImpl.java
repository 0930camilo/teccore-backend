package com.corporacion.tecnica.service.impl;

import com.corporacion.tecnica.dto.PageResponse;
import com.corporacion.tecnica.dto.alumno.AlumnoRequest;
import com.corporacion.tecnica.dto.alumno.AlumnoResponse;
import com.corporacion.tecnica.entity.Alumno;
import com.corporacion.tecnica.mapper.AlumnoMapper;
import com.corporacion.tecnica.repository.AlumnoRepository;
import com.corporacion.tecnica.service.AlumnoService;
import com.corporacion.tecnica.util.ApiResponseFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AlumnoServiceImpl implements AlumnoService {

    private final AlumnoRepository alumnoRepository;
    private final AlumnoMapper alumnoMapper;
    private final InstitutionScopeResolver institutionScopeResolver;

    @Override
    @Transactional
    public AlumnoResponse crear(AlumnoRequest request) {
        Alumno alumno = alumnoMapper.toEntity(request);
        alumno.setInstitucion(institutionScopeResolver.getRequiredInstitution(request.getInstitucionId()));
        return alumnoMapper.toResponse(alumnoRepository.save(alumno));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<AlumnoResponse> listar(String q, int page, int size) {
        Long institucionId = institutionScopeResolver.resolveInstitutionId(null);
        Page<AlumnoResponse> result = alumnoRepository
                .findByInstitucionIdAndNombresContainingIgnoreCase(institucionId, q == null ? "" : q, PageRequest.of(page, size))
                .map(alumnoMapper::toResponse);
        return ApiResponseFactory.page(result);
    }
}
