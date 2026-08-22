package com.corporacion.tecnica.repository;

import com.corporacion.tecnica.entity.Docente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocenteRepository extends JpaRepository<Docente, Long> {
    Page<Docente> findByInstitucionIdAndNombresContainingIgnoreCase(Long institucionId, String nombres, Pageable pageable);
}
