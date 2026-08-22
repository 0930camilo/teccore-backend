package com.corporacion.tecnica.repository;

import com.corporacion.tecnica.entity.Programa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProgramaRepository extends JpaRepository<Programa, Long> {
}

