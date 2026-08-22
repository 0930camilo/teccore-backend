package com.corporacion.tecnica.repository;

import com.corporacion.tecnica.entity.Rol;
import com.corporacion.tecnica.entity.RolNombre;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolRepository extends JpaRepository<Rol, Long> {
    Optional<Rol> findByNombre(RolNombre nombre);
}
