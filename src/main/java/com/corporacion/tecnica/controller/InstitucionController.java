package com.corporacion.tecnica.controller;

import com.corporacion.tecnica.dto.ApiResponse;
import com.corporacion.tecnica.dto.PageResponse;
import com.corporacion.tecnica.dto.institucion.InstitucionRequest;
import com.corporacion.tecnica.dto.institucion.InstitucionResponse;
import com.corporacion.tecnica.dto.institucion.InstitucionUpdateRequest;
import com.corporacion.tecnica.entity.EstadoRegistro;
import com.corporacion.tecnica.service.InstitucionService;
import com.corporacion.tecnica.util.ApiResponseFactory;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/instituciones")
@RequiredArgsConstructor
public class InstitucionController {

    private final InstitucionService institucionService;

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<InstitucionResponse>> crear(@Valid @RequestBody InstitucionRequest request) {
        return ResponseEntity.status(201).body(ApiResponseFactory.created("Institucion creada", institucionService.crear(request)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<InstitucionResponse>> actualizar(@PathVariable Long id, @Valid @RequestBody InstitucionUpdateRequest request) {
        return ResponseEntity.ok(ApiResponseFactory.ok("Institucion actualizada", institucionService.actualizar(id, request)));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN_INSTITUCION')")
    public ResponseEntity<ApiResponse<PageResponse<InstitucionResponse>>> listar(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String codigo,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String nit,
            @RequestParam(required = false) EstadoRegistro estado,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponseFactory.ok("Listado de instituciones", institucionService.listar(q, codigo, nombre, nit, estado, page, size)));
    }
}
