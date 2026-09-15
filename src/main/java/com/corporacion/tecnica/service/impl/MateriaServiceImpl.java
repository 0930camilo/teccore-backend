package com.corporacion.tecnica.service.impl;

import com.corporacion.tecnica.dto.PageResponse;
import com.corporacion.tecnica.dto.materia.MateriaRequest;
import com.corporacion.tecnica.dto.materia.MateriaResponse;
import com.corporacion.tecnica.entity.Docente;
import com.corporacion.tecnica.entity.Institucion;
import com.corporacion.tecnica.entity.Materia;
import com.corporacion.tecnica.entity.Semestre;
import com.corporacion.tecnica.exception.BusinessException;
import com.corporacion.tecnica.exception.ResourceNotFoundException;
import com.corporacion.tecnica.mapper.MateriaMapper;
import com.corporacion.tecnica.repository.DocenteRepository;
import com.corporacion.tecnica.repository.MateriaRepository;
import com.corporacion.tecnica.repository.SemestreRepository;
import com.corporacion.tecnica.service.MateriaService;
import com.corporacion.tecnica.util.ApiResponseFactory;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MateriaServiceImpl implements MateriaService {

    private static final DateTimeFormatter HORA_FORMATTER =
            DateTimeFormatter.ofPattern("HH:mm");

    private final MateriaRepository materiaRepository;
    private final SemestreRepository semestreRepository;
    private final DocenteRepository docenteRepository;
    private final MateriaMapper materiaMapper;
    private final InstitutionScopeResolver institutionScopeResolver;
    private final SedeScopeResolver sedeScopeResolver;

    @Override
    @Transactional
    public MateriaResponse crear(MateriaRequest request) {
        Materia materia = materiaMapper.toEntity(request);

        Semestre semestre = semestreRepository.findById(request.getSemestreId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Semestre no encontrado"));

        Long institucionId = request.getInstitucionId() != null
                ? request.getInstitucionId()
                : semestre.getInstitucion() != null
                ? semestre.getInstitucion().getId()
                : null;

        Institucion institucion =
                institutionScopeResolver.getRequiredInstitution(institucionId);

        materia.setInstitucion(institucion);

        validarInstitucionDelSemestre(semestre, institucion);
        validarSedeDelSemestre(semestre);

        materia.setSemestre(semestre);

        if (request.getEstado() != null) {
            materia.setEstado(request.getEstado());
        }

        asignarYValidarHorario(materia, request);
        asignarDocente(materia, request.getDocenteId(), institucion);

        return materiaMapper.toResponse(
                materiaRepository.save(materia)
        );
    }

    @Override
    @Transactional
    public MateriaResponse actualizar(Long id, MateriaRequest request) {
        Materia materia = findMateriaScoped(id);

        Semestre semestre = semestreRepository.findById(request.getSemestreId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Semestre no encontrado"));

        Long institucionId = request.getInstitucionId() != null
                ? request.getInstitucionId()
                : semestre.getInstitucion() != null
                ? semestre.getInstitucion().getId()
                : null;

        Institucion institucion =
                institutionScopeResolver.getRequiredInstitution(institucionId);

        validarInstitucionDelSemestre(semestre, institucion);
        validarSedeDelSemestre(semestre);

        materia.setNombre(request.getNombre());

        if (request.getIntensidadHoraria() != null) {
            materia.setIntensidadHoraria(request.getIntensidadHoraria());
        }

        materia.setSemestre(semestre);
        materia.setInstitucion(institucion);

        if (request.getEstado() != null) {
            materia.setEstado(request.getEstado());
        }

        asignarYValidarHorario(materia, request);
        asignarDocente(materia, request.getDocenteId(), institucion);

        return materiaMapper.toResponse(
                materiaRepository.save(materia)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public MateriaResponse obtenerPorId(Long id) {
        Materia materia = findMateriaScoped(id);
        return materiaMapper.toResponse(materia);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<MateriaResponse> listar(String q, int page, int size) {
        String nombre = q == null ? "" : q;
        PageRequest pageable = PageRequest.of(page, size);
        Long sedeId = sedeScopeResolver.getCurrentSedeScope();

        Page<MateriaResponse> result;

        if (sedeId != null) {
            result = materiaRepository
                    .findBySemestreProgramaSedeIdAndNombreContainingIgnoreCase(
                            sedeId,
                            nombre,
                            pageable
                    )
                    .map(materiaMapper::toResponse);
        } else {
            Long institucionId =
                    institutionScopeResolver.resolveInstitutionId(null);

            result = materiaRepository
                    .findByInstitucionIdAndNombreContainingIgnoreCase(
                            institucionId,
                            nombre,
                            pageable
                    )
                    .map(materiaMapper::toResponse);
        }

        return ApiResponseFactory.page(result);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Materia materia = findMateriaScoped(id);
        materiaRepository.delete(materia);
    }

    private void asignarYValidarHorario(
            Materia materia,
            MateriaRequest request
    ) {
        LocalTime horaInicio = parseHora(
                request.getHoraInicio(),
                "horaInicio"
        );

        LocalTime horaFin = parseHora(
                request.getHoraFin(),
                "horaFin"
        );

        if (!horaFin.isAfter(horaInicio)) {
            throw new BusinessException(
                    "La hora de finalización debe ser posterior a la hora de inicio"
            );
        }

        materia.setDiaSemana(request.getDiaSemana());
        materia.setHoraInicio(horaInicio);
        materia.setHoraFin(horaFin);
    }

    private LocalTime parseHora(String hora, String campo) {
        try {
            return LocalTime.parse(hora, HORA_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new BusinessException(
                    "El campo " + campo + " debe tener formato HH:mm"
            );
        }
    }

    private void asignarDocente(
            Materia materia,
            Long docenteId,
            Institucion institucion
    ) {
        if (docenteId == null) {
            materia.setDocente(null);
            return;
        }

        Docente docente = docenteRepository.findById(docenteId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Docente no encontrado"));

        if (docente.getInstitucion() != null
                && !docente.getInstitucion().getId()
                .equals(institucion.getId())) {
            throw new BusinessException(
                    "El docente no pertenece a la institucion"
            );
        }

        materia.setDocente(docente);
    }

    private void validarInstitucionDelSemestre(
            Semestre semestre,
            Institucion institucion
    ) {
        if (semestre.getInstitucion() == null
                || !semestre.getInstitucion().getId()
                .equals(institucion.getId())) {
            throw new BusinessException(
                    "El semestre no pertenece a la institucion enviada"
            );
        }
    }

    private void validarSedeDelSemestre(Semestre semestre) {
        Long scopedSedeId = sedeScopeResolver.getCurrentSedeScope();

        if (scopedSedeId != null
                && semestre.getPrograma() != null
                && semestre.getPrograma().getSede() != null
                && !scopedSedeId.equals(
                semestre.getPrograma().getSede().getId()
        )) {
            throw new BusinessException(
                    "El semestre no pertenece a la sede autenticada"
            );
        }
    }

    private Materia findMateriaScoped(Long id) {
        Materia materia = materiaRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Materia no encontrada"));

        Long institucionId =
                institutionScopeResolver.resolveInstitutionId(null);

        if (institucionId != null
                && materia.getInstitucion() != null
                && !institucionId.equals(
                materia.getInstitucion().getId()
        )) {
            throw new BusinessException(
                    "La materia no pertenece a la institucion autenticada"
            );
        }

        Long sedeId = sedeScopeResolver.getCurrentSedeScope();

        if (sedeId != null
                && materia.getSemestre() != null
                && materia.getSemestre().getPrograma() != null
                && materia.getSemestre().getPrograma().getSede() != null
                && !sedeId.equals(
                materia.getSemestre()
                        .getPrograma()
                        .getSede()
                        .getId()
        )) {
            throw new BusinessException(
                    "La materia no pertenece a la sede autenticada"
            );
        }

        return materia;
    }
}