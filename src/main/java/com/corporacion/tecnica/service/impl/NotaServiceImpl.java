package com.corporacion.tecnica.service.impl;

import com.corporacion.tecnica.dto.PageResponse;
import com.corporacion.tecnica.dto.nota.NotaRequest;
import com.corporacion.tecnica.dto.nota.NotaResponse;
import com.corporacion.tecnica.entity.Alumno;
import com.corporacion.tecnica.entity.Docente;
import com.corporacion.tecnica.entity.Materia;
import com.corporacion.tecnica.entity.Nota;
import com.corporacion.tecnica.exception.BusinessException;
import com.corporacion.tecnica.mapper.NotaMapper;
import com.corporacion.tecnica.repository.AlumnoRepository;
import com.corporacion.tecnica.repository.DocenteRepository;
import com.corporacion.tecnica.repository.MateriaRepository;
import com.corporacion.tecnica.repository.NotaRepository;
import com.corporacion.tecnica.service.NotaService;
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
public class NotaServiceImpl implements NotaService {

    private final NotaRepository notaRepository;
    private final AlumnoRepository alumnoRepository;
    private final MateriaRepository materiaRepository;
    private final DocenteRepository docenteRepository;
    private final NotaMapper notaMapper;
    private final InstitutionScopeResolver institutionScopeResolver;

    @Override
    @Transactional
    public NotaResponse crear(NotaRequest request) {
        Alumno alumno = alumnoRepository.findById(request.getAlumnoId())
                .orElseThrow(() -> new BusinessException("Alumno no encontrado"));
        Materia materia = materiaRepository.findById(request.getMateriaId())
                .orElseThrow(() -> new BusinessException("Materia no encontrada"));

        validarDocentePuedeGestionarMateria(materia);
        validarAlumnoPerteneceAMateria(alumno, materia);

        Nota nota = notaMapper.toEntity(request);
        nota.setInstitucion(institutionScopeResolver.getRequiredInstitution(request.getInstitucionId()));
        nota.setAlumno(alumno);
        nota.setMateria(materia);
        return notaMapper.toResponse(notaRepository.save(nota));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<NotaResponse> listar(String periodo, int page, int size) {
        Long institucionId = institutionScopeResolver.resolveInstitutionId(null);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Page<NotaResponse> result;
        if (authentication != null && authentication.isAuthenticated() && authentication.getAuthorities().stream().anyMatch(a -> "ROLE_DOCENTE".equals(a.getAuthority()))) {
            Long docenteId = obtenerDocenteAutenticadoId(institucionId);
            Page<Nota> pagina = notaRepository.findByInstitucionIdAndPeriodoContainingIgnoreCase(institucionId, periodo == null ? "" : periodo, PageRequest.of(page, size));
            List<NotaResponse> filtradas = new ArrayList<>();
            for (Nota nota : pagina.getContent()) {
                if (nota.getMateria() == null || nota.getMateria().getDocente() == null) {
                    continue;
                }
                if (nota.getMateria().getDocente().getId().equals(docenteId)) {
                    filtradas.add(notaMapper.toResponse(nota));
                }
            }
            result = new PageImpl<>(filtradas, PageRequest.of(page, size), pagina.getTotalElements());
        } else {
            result = notaRepository
                    .findByInstitucionIdAndPeriodoContainingIgnoreCase(institucionId, periodo == null ? "" : periodo, PageRequest.of(page, size))
                    .map(notaMapper::toResponse);
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
            throw new BusinessException("No puedes calificar notas de una materia que no te corresponde");
        }
    }

    private void validarAlumnoPerteneceAMateria(Alumno alumno, Materia materia) {
        if (alumno.getMaterias() == null || alumno.getMaterias().stream().noneMatch(m -> m.getId().equals(materia.getId()))) {
            throw new BusinessException("El alumno no está vinculado a la materia seleccionada");
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
