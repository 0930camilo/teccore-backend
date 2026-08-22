package com.corporacion.tecnica.mapper;

import com.corporacion.tecnica.dto.actividad.ActividadRequest;
import com.corporacion.tecnica.dto.actividad.ActividadResponse;
import com.corporacion.tecnica.entity.Actividad;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ActividadMapper {

    @Mapping(target = "materia", ignore = true)
    Actividad toEntity(ActividadRequest request);

    @Mapping(target = "materiaId", source = "materia.id")
    @Mapping(target = "institucionId", source = "institucion.id")
    ActividadResponse toResponse(Actividad actividad);
}

