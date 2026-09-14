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

    @EntityGraph(attributePaths = {
            "programa",
            "programa.sede",
            "institucion"
    })
    @Query("SELECT s FROM Semestre s WHERE s.programa.sede.id = :sedeId"
            + " AND (:programaId IS NULL OR s.programa.id = :programaId)"
            + " AND (:anio IS NULL OR s.anio = :anio)"
            + " AND LOWER(s.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    Page<Semestre> searchBySedeAndFilters(
            @Param("sedeId") Long sedeId,
            @Param("programaId") Long programaId,
            @Param("anio") Integer anio,
            @Param("nombre") String nombre,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {
            "programa",
            "institucion"
    })
    @Query("SELECT s FROM Semestre s WHERE s.institucion.id = :institucionId"
            + " AND (:programaId IS NULL OR s.programa.id = :programaId)"
            + " AND (:anio IS NULL OR s.anio = :anio)"
            + " AND LOWER(s.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    Page<Semestre> searchByInstitucionAndFilters(
            @Param("institucionId") Long institucionId,
            @Param("programaId") Long programaId,
            @Param("anio") Integer anio,
            @Param("nombre") String nombre,
            Pageable pageable
    );
}