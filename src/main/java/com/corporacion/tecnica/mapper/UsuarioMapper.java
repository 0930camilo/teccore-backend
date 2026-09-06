package com.corporacion.tecnica.mapper;

import com.corporacion.tecnica.dto.usuario.UsuarioResponse;
import com.corporacion.tecnica.entity.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    @Mapping(target = "rol", source = "rol.nombre")
    @Mapping(target = "institucionId", source = "institucion.id")
    @Mapping(target = "institucionNombre", source = "institucion.nombre")
    @Mapping(target = "sedeId", source = "sede.id")
    @Mapping(target = "sedeNombre", source = "sede.nombre")
    UsuarioResponse toResponse(Usuario usuario);
}

