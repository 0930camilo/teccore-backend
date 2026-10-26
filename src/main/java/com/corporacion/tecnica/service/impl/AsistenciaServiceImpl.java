package com.corporacion.tecnica.service.impl;

import com.corporacion.tecnica.dto.PageResponse;
import com.corporacion.tecnica.dto.asistencia.AsistenciaRequest;
import com.corporacion.tecnica.dto.asistencia.AsistenciaResponse;
import com.corporacion.tecnica.entity.Alumno;
import com.corporacion.tecnica.entity.Asistencia;
import com.corporacion.tecnica.entity.Institucion;
import com.corporacion.tecnica.entity.Materia;
import com.corporacion.tecnica.exception.BusinessException;
import com.corporacion.tecnica.mapper.AsistenciaMapper;
import com.corporacion.tecnica.repository.AlumnoRepository;
import com.corporacion.tecnica.repository.AsistenciaRepository;
import com.corporacion.tecnica.repository.MateriaRepository;
import com.corporacion.tecnica.service.AsistenciaService;
import com.corporacion.tecnica.util.ApiResponseFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AsistenciaServiceImpl implements AsistenciaService {

    private final AsistenciaRepository asistenciaRepository;
    private final AlumnoRepository alumnoRepository;
    private final MateriaRepository materiaRepository;
    private final AsistenciaMapper asistenciaMapper;
    private final InstitutionScopeResolver institutionScopeResolver;

    @Override
    @Transactional
    public AsistenciaResponse crear(AsistenciaRequest request) {
        Institucion institucion = institutionScopeResolver.getRequiredInstitution(request.getInstitucionId());

        Alumno alumno = alumnoRepository.findById(request.getAlumnoId())
                .orElseThrow(() -> new BusinessException("Alumno no encontrado"));
        if (alumno.getInstitucion() != null && !alumno.getInstitucion().getId().equals(institucion.getId())) {
            throw new BusinessException("El alumno no pertenece a la institucion indicada");
        }

        Materia materia = materiaRepository.findById(request.getMateriaId())
                .orElseThrow(() -> new BusinessException("Materia no encontrada"));
        if (materia.getInstitucion() != null && !materia.getInstitucion().getId().equals(institucion.getId())) {
            throw new BusinessException("La materia no pertenece a la institucion indicada");
        }

        if (asistenciaRepository.existsByAlumnoIdAndMateriaIdAndFecha(request.getAlumnoId(), request.getMateriaId(), request.getFecha())) {
            throw new BusinessException("Ya existe una asistencia registrada para este alumno, materia y fecha");
        }

        Asistencia asistencia = asistenciaMapper.toEntity(request);
        asistencia.setInstitucion(institucion);
        asistencia.setAlumno(alumno);
        asistencia.setMateria(materia);

        return asistenciaMapper.toResponse(asistenciaRepository.save(asistencia));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<AsistenciaResponse> listarPorMateria(Long materiaId, int page, int size) {
        Long institucionId = institutionScopeResolver.resolveInstitutionId(null);
        Page<AsistenciaResponse> result = asistenciaRepository
                .findByInstitucionIdAndMateriaId(institucionId, materiaId, PageRequest.of(page, size))
                .map(asistenciaMapper::toResponse);
        return ApiResponseFactory.page(result);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<AsistenciaResponse> listarPorAlumno(Long alumnoId, int page, int size) {
        Long institucionId = institutionScopeResolver.resolveInstitutionId(null);
        Page<AsistenciaResponse> result = asistenciaRepository
                .findByInstitucionIdAndAlumnoId(institucionId, alumnoId, PageRequest.of(page, size))
                .map(asistenciaMapper::toResponse);
        return ApiResponseFactory.page(result);
    }
}
