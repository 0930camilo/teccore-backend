package com.corporacion.tecnica.service.impl;

import com.corporacion.tecnica.dto.PageResponse;
import com.corporacion.tecnica.dto.actividad.ActividadRequest;
import com.corporacion.tecnica.dto.actividad.ActividadResponse;
import com.corporacion.tecnica.entity.Actividad;
import com.corporacion.tecnica.entity.Docente;
import com.corporacion.tecnica.entity.Materia;
import com.corporacion.tecnica.exception.BusinessException;
import com.corporacion.tecnica.mapper.ActividadMapper;
import com.corporacion.tecnica.repository.ActividadRepository;
import com.corporacion.tecnica.repository.DocenteRepository;
import com.corporacion.tecnica.repository.MateriaRepository;
import com.corporacion.tecnica.service.ActividadService;
import com.corporacion.tecnica.util.ApiResponseFactory;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ActividadServiceImpl implements ActividadService {

    private final ActividadRepository actividadRepository;
    private final MateriaRepository materiaRepository;
    private final DocenteRepository docenteRepository;
    private final ActividadMapper actividadMapper;
    private final InstitutionScopeResolver institutionScopeResolver;

    @Override
    @Transactional
    public ActividadResponse crear(ActividadRequest request) {
        Actividad actividad = actividadMapper.toEntity(request);
        Long institucionId = institutionScopeResolver.getRequiredInstitution(request.getInstitucionId()).getId();
        actividad.setInstitucion(institucionId != null ? institutionScopeResolver.getRequiredInstitution(request.getInstitucionId()) : null);

        if (request.getMateriaId() != null) {
            Materia materia = materiaRepository.findById(request.getMateriaId())
                    .orElseThrow(() -> new BusinessException("Materia no encontrada"));
            validarDocentePuedeGestionarMateria(materia);
            actividad.setMateria(materia);
        }

        return actividadMapper.toResponse(actividadRepository.save(actividad));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ActividadResponse> listar(String q, int page, int size) {
        Long institucionId = institutionScopeResolver.resolveInstitutionId(null);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Page<ActividadResponse> result;

        if (authentication != null && authentication.isAuthenticated() && authentication.getAuthorities().stream().anyMatch(a -> "ROLE_DOCENTE".equals(a.getAuthority()))) {
            Long docenteId = obtenerDocenteAutenticadoId(institucionId);
            Page<Actividad> pagina = actividadRepository.findByInstitucionIdAndTituloContainingIgnoreCase(institucionId, q == null ? "" : q, PageRequest.of(page, size));
            List<ActividadResponse> filtradas = new ArrayList<>();
            for (Actividad actividad : pagina.getContent()) {
                if (actividad.getMateria() == null || actividad.getMateria().getDocente() == null) {
                    continue;
                }
                if (actividad.getMateria().getDocente().getId().equals(docenteId)) {
                    filtradas.add(actividadMapper.toResponse(actividad));
                }
            }
            result = new PageImpl<>(filtradas, PageRequest.of(page, size), pagina.getTotalElements());
        } else {
            result = actividadRepository
                    .findByInstitucionIdAndTituloContainingIgnoreCase(institucionId, q == null ? "" : q, PageRequest.of(page, size))
                    .map(actividadMapper::toResponse);
        }

        return ApiResponseFactory.page(result);
    }

    private void validarDocentePuedeGestionarMateria(Materia materia) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return;
        }
        if (authentication.getAuthorities().stream().noneMatch(a -> "ROLE_DOCENTE".equals(a.getAuthority()))) {
            return;
        }

        Long institucionId = institutionScopeResolver.resolveInstitutionId(null);
        Long docenteId = obtenerDocenteAutenticadoId(institucionId);

        if (materia.getDocente() == null || !materia.getDocente().getId().equals(docenteId)) {
            throw new BusinessException("No puedes gestionar actividades de una materia que no te corresponde");
        }
    }

    private Long obtenerDocenteAutenticadoId(Long institucionId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException("No hay un docente autenticado");
        }
        String correo = authentication.getName();
        return docenteRepository.findByCorreoAndInstitucionId(correo, institucionId)
                .map(Docente::getId)
                .orElseThrow(() -> new BusinessException("El docente autenticado no tiene una asignación válida en esta institución"));
    }
}
