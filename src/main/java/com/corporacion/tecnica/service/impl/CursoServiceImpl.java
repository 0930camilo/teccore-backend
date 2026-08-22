package com.corporacion.tecnica.service.impl;

import com.corporacion.tecnica.dto.PageResponse;
import com.corporacion.tecnica.dto.curso.CursoRequest;
import com.corporacion.tecnica.dto.curso.CursoResponse;
import com.corporacion.tecnica.entity.Curso;
import com.corporacion.tecnica.mapper.CursoMapper;
import com.corporacion.tecnica.repository.CursoRepository;
import com.corporacion.tecnica.repository.ProgramaRepository;
import com.corporacion.tecnica.service.CursoService;
import com.corporacion.tecnica.util.ApiResponseFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CursoServiceImpl implements CursoService {

    private final CursoRepository cursoRepository;
    private final ProgramaRepository programaRepository;
    private final CursoMapper cursoMapper;
    private final InstitutionScopeResolver institutionScopeResolver;

    @Override
    @Transactional
    public CursoResponse crear(CursoRequest request) {
        Curso curso = cursoMapper.toEntity(request);
        curso.setInstitucion(institutionScopeResolver.getRequiredInstitution(request.getInstitucionId()));
        if (request.getProgramaId() != null) {
            curso.setPrograma(programaRepository.findById(request.getProgramaId()).orElse(null));
        }
        return cursoMapper.toResponse(cursoRepository.save(curso));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<CursoResponse> listar(String q, int page, int size) {
        Long institucionId = institutionScopeResolver.resolveInstitutionId(null);
        Page<CursoResponse> result = cursoRepository
                .findByInstitucionIdAndNombreContainingIgnoreCase(institucionId, q == null ? "" : q, PageRequest.of(page, size))
                .map(cursoMapper::toResponse);
        return ApiResponseFactory.page(result);
    }
}

