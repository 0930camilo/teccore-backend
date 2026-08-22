package com.corporacion.tecnica.mapper;

import com.corporacion.tecnica.dto.institucion.InstitucionRequest;
import com.corporacion.tecnica.dto.institucion.InstitucionResponse;
import com.corporacion.tecnica.entity.Institucion;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InstitucionMapper {

    Institucion toEntity(InstitucionRequest request);

    InstitucionResponse toResponse(Institucion institucion);
}

