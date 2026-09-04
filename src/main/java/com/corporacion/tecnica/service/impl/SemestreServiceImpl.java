package com.corporacion.tecnica.service.impl;

import com.corporacion.tecnica.dto.PageResponse;
import com.corporacion.tecnica.dto.semestre.SemestreRequest;
import com.corporacion.tecnica.dto.semestre.SemestreResponse;
import com.corporacion.tecnica.entity.Programa;
import com.corporacion.tecnica.entity.Semestre;
import com.corporacion.tecnica.exception.BusinessException;
import com.corporacion.tecnica.exception.ResourceNotFoundException;
import com.corporacion.tecnica.mapper.SemestreMapper;
import com.corporacion.tecnica.repository.ProgramaRepository;
import com.corporacion.tecnica.repository.SemestreRepository;
import com.corporacion.tecnica.service.SemestreService;
import com.corporacion.tecnica.util.ApiResponseFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SemestreServiceImpl implements SemestreService {

    private final SemestreRepository semestreRepository;
    private final ProgramaRepository programaRepository;
    private final SemestreMapper semestreMapper;
    private final InstitutionScopeResolver institutionScopeResolver;

    @Override
    @Transactional
    public SemestreResponse crear(SemestreRequest request) {
        Semestre semestre = semestreMapper.toEntity(request);
        semestre.setInstitucion(institutionScopeResolver.getRequiredInstitution(request.getInstitucionId()));

        Programa programa = programaRepository.findById(request.getProgramaId())
                .orElseThrow(() -> new ResourceNotFoundException("Programa no encontrado"));

        if (!programa.getInstitucion().getId().equals(semestre.getInstitucion().getId())) {
            throw new BusinessException("El programa no pertenece a la institucion enviada");
        }

        semestre.setPrograma(programa);
        if (semestre.getNombre() == null || semestre.getNombre().isBlank()) {
            semestre.setNombre("Semestre " + semestre.getNumero());
        }

        return semestreMapper.toResponse(semestreRepository.save(semestre));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<SemestreResponse> listar(String q, int page, int size) {
        Long institucionId = institutionScopeResolver.resolveInstitutionId(null);
        Page<SemestreResponse> result = semestreRepository
                .findByInstitucionIdAndNombreContainingIgnoreCase(institucionId, q == null ? "" : q, PageRequest.of(page, size))
                .map(semestreMapper::toResponse);
        return ApiResponseFactory.page(result);
    }
}


