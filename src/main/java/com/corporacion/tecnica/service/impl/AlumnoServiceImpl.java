package com.corporacion.tecnica.service.impl;

import com.corporacion.tecnica.dto.PageResponse;
import com.corporacion.tecnica.dto.alumno.AlumnoRequest;
import com.corporacion.tecnica.dto.alumno.AlumnoResponse;
import com.corporacion.tecnica.entity.Alumno;
import com.corporacion.tecnica.entity.Institucion;
import com.corporacion.tecnica.entity.Materia;
import com.corporacion.tecnica.entity.Sede;
import com.corporacion.tecnica.entity.Semestre;
import com.corporacion.tecnica.exception.BusinessException;
import com.corporacion.tecnica.exception.ResourceNotFoundException;
import com.corporacion.tecnica.mapper.AlumnoMapper;
import com.corporacion.tecnica.repository.AlumnoRepository;
import com.corporacion.tecnica.repository.MateriaRepository;
import com.corporacion.tecnica.repository.SemestreRepository;
import com.corporacion.tecnica.service.AlumnoService;
import com.corporacion.tecnica.util.ApiResponseFactory;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AlumnoServiceImpl implements AlumnoService {

    private final AlumnoRepository alumnoRepository;
    private final MateriaRepository materiaRepository;
    private final SemestreRepository semestreRepository;
    private final AlumnoMapper alumnoMapper;
    private final InstitutionScopeResolver institutionScopeResolver;
    private final SedeScopeResolver sedeScopeResolver;

    @Override
    @Transactional
    public AlumnoResponse crear(AlumnoRequest request) {
        Alumno alumno = alumnoMapper.toEntity(request);
        Sede sede = sedeScopeResolver.getRequiredSede(normalizeId(request.getSedeId()));
        Institucion institucion = sede.getInstitucion();
        Semestre semestre = resolveSemestre(request.getSemestreId(), institucion.getId(), sede.getId());
        alumno.setInstitucion(institucion);
        alumno.setSede(sede);
        alumno.setSemestre(semestre);
        alumno.setMaterias(resolveMaterias(request.getMateriaIds(), semestre, institucion.getId(), sede.getId()));
        return alumnoMapper.toResponse(alumnoRepository.save(alumno));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<AlumnoResponse> listar(String q, int page, int size) {
        String query = q == null ? "" : q;
        Page<AlumnoResponse> result;
        Long sedeId = sedeScopeResolver.getCurrentSedeScope();
        if (sedeId != null) {
            result = alumnoRepository
                    .findBySedeIdAndNombresContainingIgnoreCase(sedeId, query, PageRequest.of(page, size))
                    .map(alumnoMapper::toResponse);
        } else {
            Long institucionId = institutionScopeResolver.resolveInstitutionId(null);
            result = alumnoRepository
                    .findByInstitucionIdAndNombresContainingIgnoreCase(institucionId, query, PageRequest.of(page, size))
                    .map(alumnoMapper::toResponse);
        }
        return ApiResponseFactory.page(result);
    }

    private Long normalizeId(Long id) {
        return id != null && id == 0 ? null : id;
    }

    private Semestre resolveSemestre(Long semestreId, Long institucionId, Long sedeId) {
        Long normalizedSemestreId = normalizeId(semestreId);
        if (normalizedSemestreId == null) {
            return null;
        }

        Semestre semestre = semestreRepository.findById(normalizedSemestreId)
                .orElseThrow(() -> new ResourceNotFoundException("Semestre no encontrado"));
        if (semestre.getInstitucion() == null || !institucionId.equals(semestre.getInstitucion().getId())) {
            throw new BusinessException("El semestre no pertenece a la institucion del alumno");
        }
        if (semestre.getPrograma() == null
                || semestre.getPrograma().getSede() == null
                || !sedeId.equals(semestre.getPrograma().getSede().getId())) {
            throw new BusinessException("El semestre no pertenece a la sede del alumno");
        }
        return semestre;
    }

    private Set<Materia> resolveMaterias(List<Long> materiaIds, Semestre semestre, Long institucionId, Long sedeId) {
        Set<Materia> materias = new HashSet<>();
        if (semestre != null) {
            materias.addAll(materiaRepository.findBySemestreId(semestre.getId()));
        }

        if (materiaIds == null || materiaIds.isEmpty()) {
            return materias;
        }

        for (Long materiaId : materiaIds) {
            Materia materia = materiaRepository.findById(materiaId)
                    .orElseThrow(() -> new ResourceNotFoundException("Materia no encontrada"));
            if (materia.getInstitucion() == null || !institucionId.equals(materia.getInstitucion().getId())) {
                throw new BusinessException("La materia no pertenece a la institucion del alumno");
            }
            if (materia.getSemestre() == null
                    || materia.getSemestre().getPrograma() == null
                    || materia.getSemestre().getPrograma().getSede() == null
                    || !sedeId.equals(materia.getSemestre().getPrograma().getSede().getId())) {
                throw new BusinessException("La materia no pertenece a la sede del alumno");
            }
            materias.add(materia);
        }
        return materias;
    }
}
