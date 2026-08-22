package com.corporacion.tecnica.repository;

import com.corporacion.tecnica.entity.Materia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MateriaRepository extends JpaRepository<Materia, Long> {
    Page<Materia> findByInstitucionIdAndNombreContainingIgnoreCase(Long institucionId, String nombre, Pageable pageable);
}
