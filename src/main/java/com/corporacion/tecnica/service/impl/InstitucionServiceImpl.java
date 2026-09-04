package com.corporacion.tecnica.service.impl;

import com.corporacion.tecnica.dto.PageResponse;
import com.corporacion.tecnica.dto.institucion.InstitucionRequest;
import com.corporacion.tecnica.dto.institucion.InstitucionResponse;
import com.corporacion.tecnica.entity.Institucion;
import com.corporacion.tecnica.exception.BusinessException;
import com.corporacion.tecnica.mapper.InstitucionMapper;
import com.corporacion.tecnica.repository.InstitucionRepository;
import com.corporacion.tecnica.service.InstitucionService;
import com.corporacion.tecnica.util.ApiResponseFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InstitucionServiceImpl implements InstitucionService {

    private final InstitucionRepository institucionRepository;
    private final InstitucionMapper institucionMapper;

    @Override
    @Transactional
    public InstitucionResponse crear(InstitucionRequest request) {
        institucionRepository.findByCodigo(request.getCodigo()).ifPresent(i -> {
            throw new BusinessException("Ya existe una institucion con ese codigo");
        });
        Institucion institucion = institucionMapper.toEntity(request);
        return institucionMapper.toResponse(institucionRepository.save(institucion));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<InstitucionResponse> listar(String q, int page, int size) {
        String query = q == null ? "" : q.trim();
        Page<InstitucionResponse> result;

        if (query.isEmpty()) {
            result = institucionRepository.findAll(PageRequest.of(page, size)).map(institucionMapper::toResponse);
        } else {
            result = institucionRepository.findByNombreContainingIgnoreCase(query, PageRequest.of(page, size))
                    .map(institucionMapper::toResponse);

            if (result.isEmpty()) {
                result = institucionRepository.findByCodigoContainingIgnoreCase(query, PageRequest.of(page, size))
                        .map(institucionMapper::toResponse);
            }
        }

        return ApiResponseFactory.page(result);
    }
}

