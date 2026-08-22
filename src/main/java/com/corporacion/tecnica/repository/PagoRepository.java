package com.corporacion.tecnica.repository;

import com.corporacion.tecnica.entity.Pago;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PagoRepository extends JpaRepository<Pago, Long> {
    Page<Pago> findByInstitucionIdAndConceptoContainingIgnoreCase(Long institucionId, String concepto, Pageable pageable);
}

