package com.corporacion.tecnica.repository;

import com.corporacion.tecnica.entity.Docente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DocenteRepository extends JpaRepository<Docente, Long> {

    /**
     * Método utilizado por otros servicios, como ReporteServiceImpl.
     */
    Page<Docente> findByInstitucionIdAndNombresContainingIgnoreCase(
            Long institucionId,
            String nombres,
            Pageable pageable
    );

    /**
     * Busca docentes por nombres o documento,
     * manteniendo el filtro por institución.
     */
    @Query("""
            SELECT d
            FROM Docente d
            WHERE d.institucion.id = :institucionId
              AND (
                    LOWER(d.nombres) LIKE LOWER(CONCAT('%', :filtro, '%'))
                    OR LOWER(d.documento) LIKE LOWER(CONCAT('%', :filtro, '%'))
                  )
            """)
    Page<Docente> buscarPorNombreODocumento(
            @Param("institucionId") Long institucionId,
            @Param("filtro") String filtro,
            Pageable pageable
    );
}