package com.corporacion.tecnica.mapper;

import com.corporacion.tecnica.dto.alumno.AlumnoRequest;
import com.corporacion.tecnica.dto.alumno.AlumnoResponse;
import com.corporacion.tecnica.entity.Alumno;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AlumnoMapper {

    Alumno toEntity(AlumnoRequest request);

    @Mapping(target = "institucionId", source = "institucion.id")
    AlumnoResponse toResponse(Alumno alumno);
}
