package com.corporacion.tecnica.service.impl;

import com.corporacion.tecnica.dto.PageResponse;
import com.corporacion.tecnica.dto.actividad.ActividadRequest;
import com.corporacion.tecnica.dto.actividad.ActividadResponse;
import com.corporacion.tecnica.entity.Actividad;
import com.corporacion.tecnica.mapper.ActividadMapper;
import com.corporacion.tecnica.repository.ActividadRepository;
import com.corporacion.tecnica.repository.MateriaRepository;
import com.corporacion.tecnica.service.ActividadService;
import com.corporacion.tecnica.util.ApiResponseFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ActividadServiceImpl implements ActividadService {

    private final ActividadRepository actividadRepository;
    private final MateriaRepository materiaRepository;
    private final ActividadMapper actividadMapper;
    private final InstitutionScopeResolver institutionScopeResolver;

    @Override
    @Transactional
    public ActividadResponse crear(ActividadRequest request) {
        Actividad actividad = actividadMapper.toEntity(request);
        actividad.setInstitucion(institutionScopeResolver.getRequiredInstitution(request.getInstitucionId()));
        if (request.getMateriaId() != null) {
            actividad.setMateria(materiaRepository.findById(request.getMateriaId()).orElse(null));
        }
        return actividadMapper.toResponse(actividadRepository.save(actividad));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ActividadResponse> listar(String q, int page, int size) {
        Long institucionId = institutionScopeResolver.resolveInstitutionId(null);
        Page<ActividadResponse> result = actividadRepository
                .findByInstitucionIdAndTituloContainingIgnoreCase(institucionId, q == null ? "" : q, PageRequest.of(page, size))
                .map(actividadMapper::toResponse);
        return ApiResponseFactory.page(result);
    }
}
