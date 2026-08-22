package com.corporacion.tecnica.mapper;

import com.corporacion.tecnica.dto.curso.CursoRequest;
import com.corporacion.tecnica.dto.curso.CursoResponse;
import com.corporacion.tecnica.entity.Curso;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CursoMapper {

    @Mapping(target = "programa", ignore = true)
    Curso toEntity(CursoRequest request);

    @Mapping(target = "programaId", source = "programa.id")
    @Mapping(target = "institucionId", source = "institucion.id")
    CursoResponse toResponse(Curso curso);
}
