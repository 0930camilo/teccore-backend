package com.corporacion.tecnica.repository;

import com.corporacion.tecnica.entity.Materia;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MateriaRepository extends JpaRepository<Materia, Long> {

    @EntityGraph(attributePaths = {
            "semestre",
            "semestre.programa",
            "semestre.programa.sede",
            "institucion"
    })
    Optional<Materia> findById(Long id);

    @EntityGraph(attributePaths = {
            "semestre",
            "semestre.programa",
            "institucion"
    })
    Page<Materia> findByInstitucionIdAndNombreContainingIgnoreCase(
            Long institucionId,
            String nombre,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {
            "semestre",
            "semestre.programa",
            "semestre.programa.sede",
            "institucion"
    })
    Page<Materia> findBySemestreProgramaSedeIdAndNombreContainingIgnoreCase(
            Long sedeId,
            String nombre,
            Pageable pageable
    );
}
