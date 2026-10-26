package com.corporacion.tecnica.repository;

import com.corporacion.tecnica.entity.Asistencia;
import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AsistenciaRepository extends JpaRepository<Asistencia, Long> {
    boolean existsByAlumnoIdAndMateriaIdAndFecha(Long alumnoId, Long materiaId, LocalDate fecha);

    Page<Asistencia> findByInstitucionIdAndMateriaId(Long institucionId, Long materiaId, Pageable pageable);

    Page<Asistencia> findByInstitucionIdAndAlumnoId(Long institucionId, Long alumnoId, Pageable pageable);
}

