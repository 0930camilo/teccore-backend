package com.corporacion.tecnica.repository;

import com.corporacion.tecnica.entity.Alumno;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlumnoRepository extends JpaRepository<Alumno, Long> {
    Page<Alumno> findByInstitucionIdAndNombresContainingIgnoreCase(Long institucionId, String nombres, Pageable pageable);
}

