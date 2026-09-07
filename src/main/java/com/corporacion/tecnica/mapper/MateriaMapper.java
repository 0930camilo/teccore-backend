package com.corporacion.tecnica.mapper;

import com.corporacion.tecnica.dto.materia.MateriaRequest;
import com.corporacion.tecnica.dto.materia.MateriaResponse;
import com.corporacion.tecnica.entity.Materia;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MateriaMapper {

    @Mapping(target = "semestre", ignore = true)
    Materia toEntity(MateriaRequest request);

    @Mapping(target = "semestreId", source = "semestre.id")
    @Mapping(target = "semestreNombre", source = "semestre.nombre")
    @Mapping(target = "programaId", source = "semestre.programa.id")
    @Mapping(target = "programaNombre", source = "semestre.programa.nombre")
    @Mapping(target = "institucionId", source = "institucion.id")
    @Mapping(target = "institucionNombre", source = "institucion.nombre")
    MateriaResponse toResponse(Materia materia);
}

