package com.corporacion.tecnica.mapper;

import com.corporacion.tecnica.dto.docente.DocenteRequest;
import com.corporacion.tecnica.dto.docente.DocenteResponse;
import com.corporacion.tecnica.entity.Docente;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DocenteMapper {

    Docente toEntity(DocenteRequest request);

    @Mapping(target = "institucionId", source = "institucion.id")
    DocenteResponse toResponse(Docente docente);
}
