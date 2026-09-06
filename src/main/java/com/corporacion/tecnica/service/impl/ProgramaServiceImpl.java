package com.corporacion.tecnica.service.impl;

import com.corporacion.tecnica.dto.PageResponse;
import com.corporacion.tecnica.dto.programa.ProgramaRequest;
import com.corporacion.tecnica.dto.programa.ProgramaResponse;
import com.corporacion.tecnica.dto.programa.ProgramaUpdateRequest;
import com.corporacion.tecnica.entity.EstadoRegistro;
import com.corporacion.tecnica.entity.Programa;
import com.corporacion.tecnica.exception.BusinessException;
import com.corporacion.tecnica.exception.ResourceNotFoundException;
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
        programa.setNombre(request.getNombre().trim());
        if (request.getEstado() != null) {
            programa.setEstado(request.getEstado());
        }
        programa.setInstitucion(institutionScopeResolver.getRequiredInstitution(request.getInstitucionId()));
        return programaMapper.toResponse(programaRepository.save(programa));
    }

    @Override
    @Transactional
    public ProgramaResponse actualizar(Long id, ProgramaUpdateRequest request) {
        Programa programa = programaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Programa no encontrado"));

        Long scopedInstitutionId = institutionScopeResolver.resolveInstitutionId(request.getInstitucionId());
        if (programa.getInstitucion() == null || !scopedInstitutionId.equals(programa.getInstitucion().getId())) {
            throw new BusinessException("El programa no pertenece a la institucion autenticada");
        }

        programa.setNombre(request.getNombre().trim());
        programa.setDuracionSemestres(request.getDuracionSemestres());
        programa.setNivel(request.getNivel());
        programa.setCostoSemestral(request.getCostoSemestral());
        if (request.getEstado() != null) {
            programa.setEstado(request.getEstado());
        }
        programa.setInstitucion(institutionScopeResolver.getRequiredInstitution(request.getInstitucionId()));

        return programaMapper.toResponse(programaRepository.save(programa));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProgramaResponse> listar(String q, String nombre, EstadoRegistro estado, int page, int size) {
        Long institucionId = institutionScopeResolver.resolveInstitutionId(null);
        String query = normalize(nombre);
        if (query == null) {
            query = normalize(q);
        }
        Page<ProgramaResponse> result;
        if (estado == null) {
            result = programaRepository
                    .findByInstitucionIdAndNombreContainingIgnoreCase(institucionId, query == null ? "" : query, PageRequest.of(page, size))
                    .map(programaMapper::toResponse);
        } else {
            result = programaRepository
                    .findByInstitucionIdAndEstadoAndNombreContainingIgnoreCase(institucionId, estado, query == null ? "" : query, PageRequest.of(page, size))
                    .map(programaMapper::toResponse);
        }
        return ApiResponseFactory.page(result);
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}

