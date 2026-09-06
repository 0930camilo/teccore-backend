package com.corporacion.tecnica.repository;

import com.corporacion.tecnica.entity.EstadoRegistro;
import com.corporacion.tecnica.entity.Programa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProgramaRepository extends JpaRepository<Programa, Long> {
    Page<Programa> findBySedeIdAndNombreContainingIgnoreCase(Long sedeId, String nombre, Pageable pageable);
    Page<Programa> findBySedeIdAndEstadoAndNombreContainingIgnoreCase(Long sedeId, EstadoRegistro estado, String nombre, Pageable pageable);
}

