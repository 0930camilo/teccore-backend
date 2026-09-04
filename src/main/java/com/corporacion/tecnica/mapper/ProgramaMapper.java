package com.corporacion.tecnica.mapper;

import com.corporacion.tecnica.dto.programa.ProgramaRequest;
import com.corporacion.tecnica.dto.programa.ProgramaResponse;
import com.corporacion.tecnica.entity.Programa;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProgramaMapper {

    Programa toEntity(ProgramaRequest request);

    @Mapping(target = "institucionId", source = "institucion.id")
    ProgramaResponse toResponse(Programa programa);
}

