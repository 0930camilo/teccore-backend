package com.corporacion.tecnica.repository;

import com.corporacion.tecnica.entity.EstadoRegistro;
import com.corporacion.tecnica.entity.Sede;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SedeRepository extends JpaRepository<Sede, Long> {
    Page<Sede> findByInstitucionIdAndNombreContainingIgnoreCaseAndCiudadContainingIgnoreCaseAndEstado(
            Long institucionId,
            String nombre,
            String ciudad,
            EstadoRegistro estado,
            Pageable pageable);

    Page<Sede> findByInstitucionIdAndNombreContainingIgnoreCaseAndCiudadContainingIgnoreCase(
            Long institucionId,
            String nombre,
            String ciudad,
            Pageable pageable);
}

