package com.corporacion.tecnica.mapper;

import com.corporacion.tecnica.dto.contabilidad.PagoRequest;
import com.corporacion.tecnica.dto.contabilidad.PagoResponse;
import com.corporacion.tecnica.entity.Pago;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PagoMapper {

    @Mapping(target = "alumno", ignore = true)
    Pago toEntity(PagoRequest request);

    @Mapping(target = "alumnoId", source = "alumno.id")
    @Mapping(target = "institucionId", source = "institucion.id")
    PagoResponse toResponse(Pago pago);
}

