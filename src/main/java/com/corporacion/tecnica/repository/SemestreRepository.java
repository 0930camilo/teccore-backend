package com.corporacion.tecnica.repository;

import com.corporacion.tecnica.entity.Semestre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SemestreRepository extends JpaRepository<Semestre, Long> {
    @EntityGraph(attributePaths = {"programa", "institucion"})
    Page<Semestre> findByInstitucionIdAndNombreContainingIgnoreCase(Long institucionId, String nombre, Pageable pageable);
}


