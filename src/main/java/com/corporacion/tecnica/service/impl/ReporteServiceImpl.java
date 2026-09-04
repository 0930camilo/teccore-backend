package com.corporacion.tecnica.service.impl;

import com.corporacion.tecnica.dto.reportes.ReporteResumenResponse;
import com.corporacion.tecnica.repository.AlumnoRepository;
import com.corporacion.tecnica.repository.DocenteRepository;
import com.corporacion.tecnica.repository.PagoRepository;
import com.corporacion.tecnica.repository.SemestreRepository;
import com.corporacion.tecnica.service.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReporteServiceImpl implements ReporteService {

    private final AlumnoRepository alumnoRepository;
    private final DocenteRepository docenteRepository;
    private final SemestreRepository semestreRepository;
    private final PagoRepository pagoRepository;
    private final InstitutionScopeResolver institutionScopeResolver;

    @Override
    @Transactional(readOnly = true)
    public ReporteResumenResponse resumenInstitucion(Long institucionId) {
        Long scopedInstitutionId = institutionScopeResolver.resolveInstitutionId(institucionId);
        long totalSemestres = semestreRepository
                .findByInstitucionIdAndNombreContainingIgnoreCase(scopedInstitutionId, "", org.springframework.data.domain.PageRequest.of(0, 1))
                .getTotalElements();

        return ReporteResumenResponse.builder()
                .totalAlumnos(alumnoRepository.findByInstitucionIdAndNombresContainingIgnoreCase(scopedInstitutionId, "", org.springframework.data.domain.PageRequest.of(0, 1)).getTotalElements())
                .totalDocentes(docenteRepository.findByInstitucionIdAndNombresContainingIgnoreCase(scopedInstitutionId, "", org.springframework.data.domain.PageRequest.of(0, 1)).getTotalElements())
                .totalCursos(totalSemestres)
                .totalSemestres(totalSemestres)
                .totalPagos(pagoRepository.findByInstitucionIdAndConceptoContainingIgnoreCase(scopedInstitutionId, "", org.springframework.data.domain.PageRequest.of(0, 1)).getTotalElements())
                .build();
    }
}

