package com.corporacion.tecnica.repository;

import com.corporacion.tecnica.entity.Semestre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
public interface SemestreRepository extends JpaRepository<Semestre, Long> {

    @EntityGraph(attributePaths = {
            "programa",
            "institucion"
    })
    Page<Semestre> findByInstitucionIdAndNombreContainingIgnoreCase(
            Long institucionId,
            String nombre,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {
            "programa",
            "programa.sede",
            "institucion"
    })
    Page<Semestre> findByProgramaSedeIdAndNombreContainingIgnoreCase(
            Long sedeId,
            String nombre,
            Pageable pageable
    );
}