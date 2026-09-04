package com.corporacion.tecnica.mapper;

import com.corporacion.tecnica.dto.semestre.SemestreRequest;
import com.corporacion.tecnica.dto.semestre.SemestreResponse;
import com.corporacion.tecnica.entity.Semestre;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SemestreMapper {

    @Mapping(target = "programa", ignore = true)
    Semestre toEntity(SemestreRequest request);

    @Mapping(target = "programaId", source = "programa.id")
    @Mapping(target = "institucionId", source = "institucion.id")
    SemestreResponse toResponse(Semestre semestre);
}

