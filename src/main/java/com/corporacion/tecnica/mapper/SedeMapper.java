package com.corporacion.tecnica.mapper;

import com.corporacion.tecnica.dto.sede.SedeRequest;
import com.corporacion.tecnica.dto.sede.SedeResponse;
import com.corporacion.tecnica.entity.Sede;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SedeMapper {

    Sede toEntity(SedeRequest request);

    @Mapping(target = "institucionId", source = "institucion.id")
    @Mapping(target = "institucionNombre", source = "institucion.nombre")
    SedeResponse toResponse(Sede sede);
}

