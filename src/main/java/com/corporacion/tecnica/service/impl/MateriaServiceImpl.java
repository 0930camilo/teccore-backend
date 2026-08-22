package com.corporacion.tecnica.service.impl;

import com.corporacion.tecnica.dto.PageResponse;
import com.corporacion.tecnica.dto.materia.MateriaRequest;
import com.corporacion.tecnica.dto.materia.MateriaResponse;
import com.corporacion.tecnica.entity.Materia;
import com.corporacion.tecnica.mapper.MateriaMapper;
import com.corporacion.tecnica.repository.CursoRepository;
import com.corporacion.tecnica.repository.MateriaRepository;
import com.corporacion.tecnica.service.MateriaService;
import com.corporacion.tecnica.util.ApiResponseFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MateriaServiceImpl implements MateriaService {

    private final MateriaRepository materiaRepository;
    private final CursoRepository cursoRepository;
    private final MateriaMapper materiaMapper;
    private final InstitutionScopeResolver institutionScopeResolver;

    @Override
    @Transactional
    public MateriaResponse crear(MateriaRequest request) {
        Materia materia = materiaMapper.toEntity(request);
        materia.setInstitucion(institutionScopeResolver.getRequiredInstitution(request.getInstitucionId()));
        if (request.getCursoId() != null) {
            materia.setCurso(cursoRepository.findById(request.getCursoId()).orElse(null));
        }
        return materiaMapper.toResponse(materiaRepository.save(materia));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<MateriaResponse> listar(String q, int page, int size) {
        Long institucionId = institutionScopeResolver.resolveInstitutionId(null);
        Page<MateriaResponse> result = materiaRepository
                .findByInstitucionIdAndNombreContainingIgnoreCase(institucionId, q == null ? "" : q, PageRequest.of(page, size))
                .map(materiaMapper::toResponse);
        return ApiResponseFactory.page(result);
    }
}

