package com.corporacion.tecnica.repository;

import com.corporacion.tecnica.entity.Actividad;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActividadRepository extends JpaRepository<Actividad, Long> {
    Page<Actividad> findByInstitucionIdAndTituloContainingIgnoreCase(Long institucionId, String titulo, Pageable pageable);
}

