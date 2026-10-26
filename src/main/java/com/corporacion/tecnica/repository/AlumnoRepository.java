package com.corporacion.tecnica.repository;

import com.corporacion.tecnica.entity.Alumno;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AlumnoRepository extends JpaRepository<Alumno, Long> {

    List<Alumno> findByMateriasIdIn(List<Long> materiaIds);

    @Query("""
            select a
            from Alumno a
            join a.materias m
            where m.docente.id = :docenteId
            and a.institucion.id = :institucionId
            """)
    Page<Alumno> findByDocenteIdAndInstitucionId(@Param("docenteId") Long docenteId, @Param("institucionId") Long institucionId, Pageable pageable);

    // Métodos existentes: otros servicios todavía los utilizan

    Page<Alumno> findByInstitucionIdAndNombresContainingIgnoreCase(
            Long institucionId,
            String nombres,
            Pageable pageable
    );

    Page<Alumno> findBySedeIdAndNombresContainingIgnoreCase(
            Long sedeId,
            String nombres,
            Pageable pageable
    );

    // Búsqueda por nombre o documento dentro de una institución

    Page<Alumno> findByInstitucionIdAndNombresContainingIgnoreCaseOrInstitucionIdAndDocumentoContainingIgnoreCase(
            Long institucionId,
            String nombres,
            Long institucionIdDocumento,
            String documento,
            Pageable pageable
    );

    // Búsqueda por nombre o documento dentro de una sede

    Page<Alumno> findBySedeIdAndNombresContainingIgnoreCaseOrSedeIdAndDocumentoContainingIgnoreCase(
            Long sedeId,
            String nombres,
            Long sedeIdDocumento,
            String documento,
            Pageable pageable
    );
}