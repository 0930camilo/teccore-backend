package com.corporacion.tecnica.mapper;

import com.corporacion.tecnica.dto.asistencia.AsistenciaRequest;
import com.corporacion.tecnica.dto.asistencia.AsistenciaResponse;
import com.corporacion.tecnica.entity.Asistencia;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AsistenciaMapper {

    @Mapping(target = "alumno", ignore = true)
    @Mapping(target = "materia", ignore = true)
    Asistencia toEntity(AsistenciaRequest request);

    @Mapping(target = "alumnoId", source = "alumno.id")
    @Mapping(target = "materiaId", source = "materia.id")
    @Mapping(target = "institucionId", source = "institucion.id")
    AsistenciaResponse toResponse(Asistencia asistencia);
}

