package com.corporacion.tecnica.mapper;

import com.corporacion.tecnica.dto.materia.MateriaRequest;
import com.corporacion.tecnica.dto.materia.MateriaResponse;
import com.corporacion.tecnica.entity.Docente;
import com.corporacion.tecnica.entity.Materia;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface MateriaMapper {

    @Mapping(target = "semestre", ignore = true)
    @Mapping(target = "docente", ignore = true)
    @Mapping(target = "horaInicio", ignore = true)
    @Mapping(target = "horaFin", ignore = true)
    Materia toEntity(MateriaRequest request);

    @Mapping(target = "semestreId", source = "semestre.id")
    @Mapping(target = "semestreNombre", source = "semestre.nombre")
    @Mapping(target = "programaId", source = "semestre.programa.id")
    @Mapping(target = "programaNombre", source = "semestre.programa.nombre")
    @Mapping(target = "institucionId", source = "institucion.id")
    @Mapping(target = "institucionNombre", source = "institucion.nombre")
    @Mapping(target = "docenteId", source = "docente.id")
    @Mapping(
            target = "docenteNombre",
            source = "docente",
            qualifiedByName = "mapDocenteNombre"
    )
    MateriaResponse toResponse(Materia materia);

    @Named("mapDocenteNombre")
    default String mapDocenteNombre(Docente docente) {
        if (docente == null) {
            return null;
        }

        String nombres = docente.getNombres() != null
                ? docente.getNombres().trim()
                : "";

        String apellidos = docente.getApellidos() != null
                ? docente.getApellidos().trim()
                : "";

        String nombreCompleto = (nombres + " " + apellidos).trim();

        return nombreCompleto.isEmpty() ? null : nombreCompleto;
    }
}