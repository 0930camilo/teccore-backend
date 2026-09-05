package com.corporacion.tecnica.service.impl;

import com.corporacion.tecnica.dto.PageResponse;
import com.corporacion.tecnica.dto.institucion.InstitucionRequest;
import com.corporacion.tecnica.dto.institucion.InstitucionResponse;
import com.corporacion.tecnica.dto.institucion.InstitucionUpdateRequest;
import com.corporacion.tecnica.entity.EstadoRegistro;
import com.corporacion.tecnica.entity.Institucion;
import com.corporacion.tecnica.exception.BusinessException;
import com.corporacion.tecnica.exception.ResourceNotFoundException;
import com.corporacion.tecnica.mapper.InstitucionMapper;
import com.corporacion.tecnica.repository.InstitucionRepository;
import com.corporacion.tecnica.service.InstitucionService;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import com.corporacion.tecnica.util.ApiResponseFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
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
        String codigo = request.getCodigo().trim();
        institucionRepository.findByCodigo(codigo).ifPresent(i -> {
            throw new BusinessException("Ya existe una institucion con ese codigo");
        });
        Institucion institucion = institucionMapper.toEntity(request);
        institucion.setCodigo(codigo);
        institucion.setNombre(request.getNombre().trim());
        institucion.setNit(normalize(request.getNit()));
        institucion.setCorreo(normalize(request.getCorreo()));
        institucion.setTelefono(normalize(request.getTelefono()));
        institucion.setDireccion(normalize(request.getDireccion()));
        return institucionMapper.toResponse(institucionRepository.save(institucion));
    }

    @Override
    @Transactional
    public InstitucionResponse actualizar(Long id, InstitucionUpdateRequest request) {
        Institucion institucion = institucionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Institucion no encontrada"));

        institucionRepository.findByCodigo(request.getCodigo())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new BusinessException("Ya existe una institucion con ese codigo");
                });

        institucion.setCodigo(request.getCodigo().trim());
        institucion.setNombre(request.getNombre().trim());
        institucion.setNit(normalize(request.getNit()));
        institucion.setCorreo(normalize(request.getCorreo()));
        institucion.setTelefono(normalize(request.getTelefono()));
        institucion.setDireccion(normalize(request.getDireccion()));
        if (request.getEstado() != null) {
            institucion.setEstado(request.getEstado());
        }

        return institucionMapper.toResponse(institucionRepository.save(institucion));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<InstitucionResponse> listar(String q, String codigo, String nombre, String nit, EstadoRegistro estado, int page, int size) {
        Page<InstitucionResponse> result = institucionRepository.findAll(buildSpecification(q, codigo, nombre, nit, estado), PageRequest.of(page, size))
                .map(institucionMapper::toResponse);
        return ApiResponseFactory.page(result);
    }

    private Specification<Institucion> buildSpecification(String q, String codigo, String nombre, String nit, EstadoRegistro estado) {
        String query = normalize(q);
        String codigoFilter = normalize(codigo);
        String nombreFilter = normalize(nombre);
        String nitFilter = normalize(nit);

        return (root, querySpec, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (estado != null) {
                predicates.add(criteriaBuilder.equal(root.get("estado"), estado));
            }
            if (codigoFilter != null) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("codigo")), "%" + codigoFilter.toLowerCase() + "%"));
            }
            if (nombreFilter != null) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("nombre")), "%" + nombreFilter.toLowerCase() + "%"));
            }
            if (nitFilter != null) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(criteriaBuilder.coalesce(root.get("nit"), "")), "%" + nitFilter.toLowerCase() + "%"));
            }
            if (query != null) {
                String general = "%" + query.toLowerCase() + "%";
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("codigo")), general),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("nombre")), general),
                        criteriaBuilder.like(criteriaBuilder.lower(criteriaBuilder.coalesce(root.get("nit"), "")), general)
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}

