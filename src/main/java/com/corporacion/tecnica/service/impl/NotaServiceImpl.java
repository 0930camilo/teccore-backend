package com.corporacion.tecnica.service.impl;

import com.corporacion.tecnica.dto.PageResponse;
import com.corporacion.tecnica.dto.nota.NotaRequest;
import com.corporacion.tecnica.dto.nota.NotaResponse;
import com.corporacion.tecnica.entity.Nota;
import com.corporacion.tecnica.mapper.NotaMapper;
import com.corporacion.tecnica.repository.AlumnoRepository;
import com.corporacion.tecnica.repository.MateriaRepository;
import com.corporacion.tecnica.repository.NotaRepository;
import com.corporacion.tecnica.service.NotaService;
import com.corporacion.tecnica.util.ApiResponseFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotaServiceImpl implements NotaService {

    private final NotaRepository notaRepository;
    private final AlumnoRepository alumnoRepository;
    private final MateriaRepository materiaRepository;
    private final NotaMapper notaMapper;
    private final InstitutionScopeResolver institutionScopeResolver;

    @Override
    @Transactional
    public NotaResponse crear(NotaRequest request) {
        Nota nota = notaMapper.toEntity(request);
        nota.setInstitucion(institutionScopeResolver.getRequiredInstitution(request.getInstitucionId()));
        nota.setAlumno(alumnoRepository.findById(request.getAlumnoId()).orElseThrow());
        nota.setMateria(materiaRepository.findById(request.getMateriaId()).orElseThrow());
        return notaMapper.toResponse(notaRepository.save(nota));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<NotaResponse> listar(String periodo, int page, int size) {
        Long institucionId = institutionScopeResolver.resolveInstitutionId(null);
        Page<NotaResponse> result = notaRepository
                .findByInstitucionIdAndPeriodoContainingIgnoreCase(institucionId, periodo == null ? "" : periodo, PageRequest.of(page, size))
                .map(notaMapper::toResponse);
        return ApiResponseFactory.page(result);
    }
}

