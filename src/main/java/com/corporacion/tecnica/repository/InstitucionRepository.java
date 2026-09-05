package com.corporacion.tecnica.repository;

import com.corporacion.tecnica.entity.Institucion;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface InstitucionRepository extends JpaRepository<Institucion, Long>, JpaSpecificationExecutor<Institucion> {
    Optional<Institucion> findByCodigo(String codigo);
    Page<Institucion> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);
    Page<Institucion> findByCodigoContainingIgnoreCase(String codigo, Pageable pageable);
}

