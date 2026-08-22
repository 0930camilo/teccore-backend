package com.corporacion.tecnica.repository;

import com.corporacion.tecnica.entity.Institucion;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InstitucionRepository extends JpaRepository<Institucion, Long> {
    Optional<Institucion> findByCodigo(String codigo);
}

