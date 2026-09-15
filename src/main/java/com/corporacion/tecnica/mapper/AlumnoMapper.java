package com.corporacion.tecnica.mapper;

import com.corporacion.tecnica.dto.alumno.AlumnoRequest;
import com.corporacion.tecnica.dto.alumno.AlumnoResponse;
import com.corporacion.tecnica.entity.Alumno;
import com.corporacion.tecnica.entity.Materia;
import java.util.Collections;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AlumnoMapper {

    @Mapping(target = "materias", ignore = true)
    Alumno toEntity(AlumnoRequest request);

    @Mapping(target = "institucionId", source = "institucion.id")
    @Mapping(target = "sedeId", source = "sede.id")
    @Mapping(target = "materiaIds", expression = "java(toMateriaIds(alumno))")
    AlumnoResponse toResponse(Alumno alumno);

    default List<Long> toMateriaIds(Alumno alumno) {
        if (alumno.getMaterias() == null) {
            return Collections.emptyList();
        }
        return alumno.getMaterias().stream()
                .map(Materia::getId)
                .toList();
    }
}
