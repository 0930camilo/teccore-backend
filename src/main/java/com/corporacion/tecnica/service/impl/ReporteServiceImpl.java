package com.corporacion.tecnica.service.impl;

import com.corporacion.tecnica.dto.reportes.ReporteResumenResponse;
import com.corporacion.tecnica.repository.AlumnoRepository;
import com.corporacion.tecnica.repository.CursoRepository;
import com.corporacion.tecnica.repository.DocenteRepository;
import com.corporacion.tecnica.repository.PagoRepository;
import com.corporacion.tecnica.service.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReporteServiceImpl implements ReporteService {

    private final AlumnoRepository alumnoRepository;
    private final DocenteRepository docenteRepository;
    private final CursoRepository cursoRepository;
    private final PagoRepository pagoRepository;
    private final InstitutionScopeResolver institutionScopeResolver;

    @Override
    @Transactional(readOnly = true)
    public ReporteResumenResponse resumenInstitucion(Long institucionId) {
        Long scopedInstitutionId = institutionScopeResolver.resolveInstitutionId(institucionId);

        return ReporteResumenResponse.builder()
                .totalAlumnos(alumnoRepository.findByInstitucionIdAndNombresContainingIgnoreCase(scopedInstitutionId, "", org.springframework.data.domain.PageRequest.of(0, 1)).getTotalElements())
                .totalDocentes(docenteRepository.findByInstitucionIdAndNombresContainingIgnoreCase(scopedInstitutionId, "", org.springframework.data.domain.PageRequest.of(0, 1)).getTotalElements())
                .totalCursos(cursoRepository.findByInstitucionIdAndNombreContainingIgnoreCase(scopedInstitutionId, "", org.springframework.data.domain.PageRequest.of(0, 1)).getTotalElements())
                .totalPagos(pagoRepository.findByInstitucionIdAndConceptoContainingIgnoreCase(scopedInstitutionId, "", org.springframework.data.domain.PageRequest.of(0, 1)).getTotalElements())
                .build();
    }
}

