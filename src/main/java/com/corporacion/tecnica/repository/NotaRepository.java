package com.corporacion.tecnica.repository;

import com.corporacion.tecnica.entity.Nota;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotaRepository extends JpaRepository<Nota, Long> {
    Page<Nota> findByInstitucionIdAndPeriodoContainingIgnoreCase(Long institucionId, String periodo, Pageable pageable);
}
