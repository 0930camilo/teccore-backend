package com.corporacion.tecnica.mapper;

import com.corporacion.tecnica.dto.materia.MateriaRequest;
import com.corporacion.tecnica.dto.materia.MateriaResponse;
import com.corporacion.tecnica.entity.Materia;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MateriaMapper {

    @Mapping(target = "curso", ignore = true)
    Materia toEntity(MateriaRequest request);

    @Mapping(target = "cursoId", source = "curso.id")
    @Mapping(target = "institucionId", source = "institucion.id")
    MateriaResponse toResponse(Materia materia);
}

