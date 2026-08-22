package com.corporacion.tecnica.mapper;

import com.corporacion.tecnica.dto.nota.NotaRequest;
import com.corporacion.tecnica.dto.nota.NotaResponse;
import com.corporacion.tecnica.entity.Nota;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotaMapper {

    @Mapping(target = "alumno", ignore = true)
    @Mapping(target = "materia", ignore = true)
    Nota toEntity(NotaRequest request);

    @Mapping(target = "alumnoId", source = "alumno.id")
    @Mapping(target = "materiaId", source = "materia.id")
    @Mapping(target = "institucionId", source = "institucion.id")
    NotaResponse toResponse(Nota nota);
}

