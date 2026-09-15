package com.corporacion.tecnica.service.impl;

import com.corporacion.tecnica.dto.PageResponse;
import com.corporacion.tecnica.dto.semestre.SemestreRequest;
import com.corporacion.tecnica.dto.semestre.SemestreResponse;
import com.corporacion.tecnica.entity.EstadoRegistro;
import com.corporacion.tecnica.entity.Institucion;
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
    private final SedeScopeResolver sedeScopeResolver;

    @Override
    @Transactional
    public SemestreResponse crear(SemestreRequest request) {

        Semestre semestre = semestreMapper.toEntity(request);

        Programa programa = programaRepository.findById(request.getProgramaId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Programa no encontrado"));

        if (programa.getInstitucion() == null) {
            throw new BusinessException("El programa no tiene institucion asociada");
        }

        if (programa.getSede() == null) {
            throw new BusinessException("El programa no tiene sede asociada");
        }

        if (request.getSedeId() != null && request.getSedeId() != 0
                && !request.getSedeId().equals(programa.getSede().getId())) {
            throw new BusinessException("La sede enviada no coincide con la sede del programa");
        }

        if (request.getInstitucionId() != null && request.getInstitucionId() != 0
                && !request.getInstitucionId().equals(programa.getInstitucion().getId())) {
            throw new BusinessException("La institucion enviada no coincide con la institucion del programa");
        }

        if (semestreRepository.existsByProgramaIdAndNumeroAndAnio(
                programa.getId(),
                request.getNumero(),
                request.getAnio())) {
            throw new BusinessException("Ya existe un semestre con ese numero y anio para el programa");
        }

        Long institucionId = programa.getInstitucion().getId();

        Institucion institucion =
                institutionScopeResolver.getRequiredInstitution(institucionId);

        semestre.setInstitucion(institucion);

        if (!programa.getInstitucion().getId()
                .equals(semestre.getInstitucion().getId())) {

            throw new BusinessException(
                    "El programa no pertenece a la institucion enviada"
            );
        }

        Long scopedSedeId =
                sedeScopeResolver.getCurrentSedeScope();

        if (scopedSedeId != null
                && programa.getSede() != null
                && !scopedSedeId.equals(programa.getSede().getId())) {

            throw new BusinessException(
                    "El programa no pertenece a la sede autenticada"
            );
        }

        semestre.setPrograma(programa);

        if (semestre.getNombre() == null
                || semestre.getNombre().isBlank()) {

            semestre.setNombre(
                    "Semestre " + semestre.getNumero()
            );
        }

        if (request.getEstado() != null) {
            semestre.setEstado(request.getEstado());

        } else if (semestre.getEstado() == null) {
            semestre.setEstado(EstadoRegistro.ACTIVO);
        }

        if (request.getAnio() != null) {
            semestre.setAnio(request.getAnio());
        }

        return semestreMapper.toResponse(
                semestreRepository.save(semestre)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<SemestreResponse> listar(
            String q,
            Integer programaId,
            Integer anio,
            int page,
            int size) {

        String nombre = q == null ? "" : q;

        PageRequest pageable =
                PageRequest.of(page, size);

        Long sedeId =
                sedeScopeResolver.getCurrentSedeScope();

        Page<SemestreResponse> result;

        if (sedeId != null) {

            result = semestreRepository
                    .searchBySedeAndFilters(
                            sedeId,
                            programaId == null ? null : programaId.longValue(),
                            anio,
                            nombre,
                            pageable
                    )
                    .map(semestreMapper::toResponse);

        } else {

            Long institucionId =
                    institutionScopeResolver.resolveInstitutionId(null);

            result = semestreRepository
                    .searchByInstitucionAndFilters(
                            institucionId,
                            programaId == null ? null : programaId.longValue(),
                            anio,
                            nombre,
                            pageable
                    )
                    .map(semestreMapper::toResponse);
        }

        return ApiResponseFactory.page(result);
    }

    @Override
    @Transactional
    public SemestreResponse actualizar(Long id, SemestreRequest request) {

        Semestre semestre = semestreRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Semestre no encontrado"));

        Programa programa = programaRepository.findById(request.getProgramaId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Programa no encontrado"));

        Long scopedSedeId = sedeScopeResolver.resolveSedeId(null);

        if (programa.getSede() == null
                || !scopedSedeId.equals(programa.getSede().getId())) {

            throw new BusinessException(
                    "El programa no pertenece a la sede autenticada"
            );
        }

        Long institucionId = programa.getInstitucion() != null
                ? programa.getInstitucion().getId()
                : null;

        Institucion institucion =
                institutionScopeResolver.getRequiredInstitution(institucionId);

        if (!programa.getInstitucion().getId()
                .equals(institucion.getId())) {

            throw new BusinessException(
                    "El programa no pertenece a la institucion autenticada"
            );
        }

        semestre.setPrograma(programa);
        semestre.setInstitucion(institucion);

        semestre.setNumero(request.getNumero());

        if (request.getNombre() == null
                || request.getNombre().isBlank()) {

            semestre.setNombre(
                    "Semestre " + request.getNumero()
            );

        } else {

            semestre.setNombre(request.getNombre().trim());
        }

        if (request.getEstado() != null) {
            semestre.setEstado(request.getEstado());
        }

        if (request.getAnio() != null) {
            semestre.setAnio(request.getAnio());
        }

        return semestreMapper.toResponse(
                semestreRepository.save(semestre)
        );
    }
}

