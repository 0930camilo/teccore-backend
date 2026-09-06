package com.corporacion.tecnica.service;

import com.corporacion.tecnica.dto.PageResponse;
import com.corporacion.tecnica.dto.usuario.UsuarioResponse;
import com.corporacion.tecnica.dto.usuario.UsuarioUpdateRequest;
import com.corporacion.tecnica.entity.RolNombre;

public interface UsuarioService {
    PageResponse<UsuarioResponse> listar(String q, RolNombre rol, String nombre, Long institucionId, int page, int size);
    UsuarioResponse actualizar(Long id, UsuarioUpdateRequest request);
}

