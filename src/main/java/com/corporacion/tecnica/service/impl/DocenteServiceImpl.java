package com.corporacion.tecnica.service.impl;

import com.corporacion.tecnica.dto.PageResponse;
import com.corporacion.tecnica.dto.docente.DocenteRequest;
import com.corporacion.tecnica.dto.docente.DocenteResponse;
import com.corporacion.tecnica.entity.Docente;
import com.corporacion.tecnica.entity.Institucion;
import com.corporacion.tecnica.exception.BusinessException;
import com.corporacion.tecnica.exception.ResourceNotFoundException;
import com.corporacion.tecnica.mapper.DocenteMapper;
import com.corporacion.tecnica.repository.DocenteRepository;
import com.corporacion.tecnica.service.DocenteService;
import com.corporacion.tecnica.util.ApiResponseFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DocenteServiceImpl implements DocenteService {

    private final DocenteRepository docenteRepository;
    private final DocenteMapper docenteMapper;
    private final InstitutionScopeResolver institutionScopeResolver;

    @Override
    @Transactional
    public DocenteResponse crear(DocenteRequest request) {

        Docente docente = docenteMapper.toEntity(request);

        Institucion institucion =
                institutionScopeResolver.getRequiredInstitution(
                        request.getInstitucionId()
                );

        docente.setInstitucion(institucion);

        return docenteMapper.toResponse(
                docenteRepository.save(docente)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<DocenteResponse> listar(
            String q,
            int page,
            int size) {

        Long institucionId =
                institutionScopeResolver.resolveInstitutionId(null);

        String nombre = q == null ? "" : q.trim();

        Page<DocenteResponse> result =
                docenteRepository
                        .findByInstitucionIdAndNombresContainingIgnoreCase(
                                institucionId,
                                nombre,
                                PageRequest.of(page, size)
                        )
                        .map(docenteMapper::toResponse);

        return ApiResponseFactory.page(result);
    }

    @Override
    @Transactional(readOnly = true)
    public DocenteResponse obtener(Long id) {

        Long institucionId =
                institutionScopeResolver.resolveInstitutionId(null);

        Docente docente =
                docenteRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Docente no encontrado"
                                )
                        );

        validarInstitucion(docente, institucionId);

        return docenteMapper.toResponse(docente);
    }

    @Override
    @Transactional
    public DocenteResponse actualizar(
            Long id,
            DocenteRequest request) {

        Long institucionId =
                institutionScopeResolver.resolveInstitutionId(null);

        Docente docente =
                docenteRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Docente no encontrado"
                                )
                        );

        validarInstitucion(docente, institucionId);

        Institucion institucion =
                institutionScopeResolver.getRequiredInstitution(
                        request.getInstitucionId()
                );

        if (!institucion.getId().equals(institucionId)) {
            throw new BusinessException(
                    "El docente no puede asignarse a otra institución"
            );
        }

        docente.setNombres(request.getNombres().trim());
        docente.setApellidos(request.getApellidos().trim());
        docente.setDocumento(request.getDocumento().trim());
        docente.setCorreo(
                request.getCorreo() != null
                        ? request.getCorreo().trim()
                        : null
        );
        docente.setCargaHorariaSemanal(
                request.getCargaHorariaSemanal()
        );

        docente.setInstitucion(institucion);

        return docenteMapper.toResponse(
                docenteRepository.save(docente)
        );
    }

    @Override
    @Transactional
    public void eliminar(Long id) {

        Long institucionId =
                institutionScopeResolver.resolveInstitutionId(null);

        Docente docente =
                docenteRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Docente no encontrado"
                                )
                        );

        validarInstitucion(docente, institucionId);

        docenteRepository.delete(docente);
    }

    private void validarInstitucion(
            Docente docente,
            Long institucionId) {

        if (docente.getInstitucion() == null
                || docente.getInstitucion().getId() == null
                || !docente.getInstitucion().getId().equals(institucionId)) {

            throw new BusinessException(
                    "El docente no pertenece a la institución autenticada"
            );
        }
    }
}