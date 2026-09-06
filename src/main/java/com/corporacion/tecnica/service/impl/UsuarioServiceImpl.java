package com.corporacion.tecnica.service.impl;

import com.corporacion.tecnica.dto.PageResponse;
import com.corporacion.tecnica.dto.usuario.UsuarioResponse;
import com.corporacion.tecnica.dto.usuario.UsuarioUpdateRequest;
import com.corporacion.tecnica.entity.RolNombre;
import com.corporacion.tecnica.entity.Sede;
import com.corporacion.tecnica.entity.Usuario;
import com.corporacion.tecnica.exception.BusinessException;
import com.corporacion.tecnica.exception.ResourceNotFoundException;
import com.corporacion.tecnica.mapper.UsuarioMapper;
import com.corporacion.tecnica.repository.RolRepository;
import com.corporacion.tecnica.repository.UsuarioRepository;
import com.corporacion.tecnica.service.UsuarioService;
import com.corporacion.tecnica.util.ApiResponseFactory;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final RolRepository rolRepository;
    private final InstitutionScopeResolver institutionScopeResolver;
    private final SedeScopeResolver sedeScopeResolver;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<UsuarioResponse> listar(String q, RolNombre rol, String nombre, Long institucionId, int page, int size) {
        Long currentScope = institutionScopeResolver.getCurrentInstitutionScope();
        Long institutionFilter = resolveInstitutionFilter(currentScope, institucionId);
        Page<UsuarioResponse> result = usuarioRepository
                .findAll(buildSpecification(q, rol, nombre, institutionFilter), PageRequest.of(page, size))
                .map(usuarioMapper::toResponse);
        return ApiResponseFactory.page(result);
    }

    @Override
    @Transactional
    public UsuarioResponse actualizar(Long id, UsuarioUpdateRequest request) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        String nombre = normalize(request.getNombre());
        String email = normalize(request.getEmail());

        usuarioRepository.findByEmail(email)
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new BusinessException("Ya existe un usuario con ese email");
                });

        usuario.setNombre(nombre);
        usuario.setEmail(email);
        usuario.setRol(rolRepository.findByNombre(request.getRol())
                .orElseThrow(() -> new BusinessException("Rol no configurado")));

        if (request.getRol() == RolNombre.SUPER_ADMIN) {
            if (request.getInstitucionId() != null || request.getSedeId() != null) {
                throw new BusinessException("El SUPER_ADMIN no debe tener institucion ni sede asignada");
            }
            usuario.setInstitucion(null);
            usuario.setSede(null);
        } else if (request.getRol() == RolNombre.ADMIN_INSTITUCION) {
            if (request.getInstitucionId() == null) {
                throw new BusinessException("Debe enviar institucionId para un ADMIN_INSTITUCION");
            }
            if (request.getSedeId() != null) {
                throw new BusinessException("ADMIN_INSTITUCION no debe tener sede asignada");
            }
            usuario.setInstitucion(institutionScopeResolver.getRequiredInstitution(request.getInstitucionId()));
            usuario.setSede(null);
        } else {
            Sede sede = sedeScopeResolver.getRequiredSede(request.getSedeId());
            usuario.setInstitucion(sede.getInstitucion());
            usuario.setSede(sede);
        }

        if (request.getEstado() != null) {
            usuario.setEstado(request.getEstado());
        }

        return usuarioMapper.toResponse(usuarioRepository.save(usuario));
    }

    private Long resolveInstitutionFilter(Long currentScope, Long requestInstitutionId) {
        if (currentScope == null) {
            return requestInstitutionId;
        }
        if (requestInstitutionId != null && !currentScope.equals(requestInstitutionId)) {
            throw new BusinessException("La institucion del filtro no coincide con el contexto autenticado");
        }
        return currentScope;
    }

    private Specification<Usuario> buildSpecification(String q, RolNombre rol, String nombre, Long institucionId) {
        String query = normalize(q);
        String nombreFilter = normalize(nombre);

        return (root, querySpec, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (rol != null) {
                predicates.add(criteriaBuilder.equal(root.get("rol").get("nombre"), rol));
            }
            if (nombreFilter != null) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("nombre")), "%" + nombreFilter.toLowerCase() + "%"));
            }
            if (institucionId != null) {
                predicates.add(criteriaBuilder.equal(root.join("institucion", JoinType.LEFT).get("id"), institucionId));
            }
            if (query != null) {
                String general = "%" + query.toLowerCase() + "%";
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("nombre")), general),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), general),
                        criteriaBuilder.like(criteriaBuilder.lower(criteriaBuilder.coalesce(root.join("institucion", JoinType.LEFT).get("nombre"), "")), general)
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

