package com.corporacion.tecnica.repository;

import com.corporacion.tecnica.entity.Curso;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CursoRepository extends JpaRepository<Curso, Long> {
    Page<Curso> findByInstitucionIdAndNombreContainingIgnoreCase(Long institucionId, String nombre, Pageable pageable);
}

