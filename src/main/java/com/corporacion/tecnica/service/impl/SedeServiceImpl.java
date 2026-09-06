package com.corporacion.tecnica.service.impl;

import com.corporacion.tecnica.dto.PageResponse;
import com.corporacion.tecnica.dto.sede.SedeRequest;
import com.corporacion.tecnica.dto.sede.SedeResponse;
import com.corporacion.tecnica.dto.sede.SedeUpdateRequest;
import com.corporacion.tecnica.entity.EstadoRegistro;
import com.corporacion.tecnica.entity.Sede;
import com.corporacion.tecnica.exception.BusinessException;
import com.corporacion.tecnica.exception.ResourceNotFoundException;
import com.corporacion.tecnica.mapper.SedeMapper;
import com.corporacion.tecnica.repository.SedeRepository;
import com.corporacion.tecnica.service.SedeService;
import com.corporacion.tecnica.util.ApiResponseFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SedeServiceImpl implements SedeService {

    private final SedeRepository sedeRepository;
    private final SedeMapper sedeMapper;
    private final InstitutionScopeResolver institutionScopeResolver;

    @Override
    @Transactional
    public SedeResponse crear(SedeRequest request) {
        Sede sede = sedeMapper.toEntity(request);
        sede.setNombre(request.getNombre().trim());
        sede.setCiudad(normalize(request.getCiudad()));
        sede.setDireccion(normalize(request.getDireccion()));
        sede.setInstitucion(institutionScopeResolver.getRequiredInstitution(request.getInstitucionId()));
        sede.setEstado(request.getEstado() != null ? request.getEstado() : EstadoRegistro.ACTIVO);
        return sedeMapper.toResponse(sedeRepository.save(sede));
    }

    @Override
    @Transactional
    public SedeResponse actualizar(Long id, SedeUpdateRequest request) {
        Sede sede = sedeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sede no encontrada"));

        Long scopedInstitutionId = institutionScopeResolver.resolveInstitutionId(request.getInstitucionId());
        if (sede.getInstitucion() == null || !scopedInstitutionId.equals(sede.getInstitucion().getId())) {
            throw new BusinessException("La sede no pertenece a la institucion autenticada");
        }

        sede.setNombre(request.getNombre().trim());
        sede.setCiudad(normalize(request.getCiudad()));
        sede.setDireccion(normalize(request.getDireccion()));
        sede.setInstitucion(institutionScopeResolver.getRequiredInstitution(request.getInstitucionId()));
        if (request.getEstado() != null) {
            sede.setEstado(request.getEstado());
        }

        return sedeMapper.toResponse(sedeRepository.save(sede));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<SedeResponse> listar(String q, String nombre, String ciudad, EstadoRegistro estado, int page, int size) {
        Long institutionId = institutionScopeResolver.resolveInstitutionId(null);
        String nombreFilter = normalize(nombre);
        if (nombreFilter == null) {
            nombreFilter = normalize(q);
        }
        String ciudadFilter = normalize(ciudad);

        Page<SedeResponse> result;
        if (estado == null) {
            result = sedeRepository
                    .findByInstitucionIdAndNombreContainingIgnoreCaseAndCiudadContainingIgnoreCase(
                            institutionId,
                            nombreFilter == null ? "" : nombreFilter,
                            ciudadFilter == null ? "" : ciudadFilter,
                            PageRequest.of(page, size))
                    .map(sedeMapper::toResponse);
        } else {
            result = sedeRepository
                    .findByInstitucionIdAndNombreContainingIgnoreCaseAndCiudadContainingIgnoreCaseAndEstado(
                            institutionId,
                            nombreFilter == null ? "" : nombreFilter,
                            ciudadFilter == null ? "" : ciudadFilter,
                            estado,
                            PageRequest.of(page, size))
                    .map(sedeMapper::toResponse);
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

