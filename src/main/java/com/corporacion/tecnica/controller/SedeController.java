package com.corporacion.tecnica.controller;

import com.corporacion.tecnica.dto.ApiResponse;
import com.corporacion.tecnica.dto.PageResponse;
import com.corporacion.tecnica.dto.sede.SedeRequest;
import com.corporacion.tecnica.dto.sede.SedeResponse;
import com.corporacion.tecnica.dto.sede.SedeUpdateRequest;
import com.corporacion.tecnica.entity.EstadoRegistro;
import com.corporacion.tecnica.service.SedeService;
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
@RequestMapping("/sedes")
@RequiredArgsConstructor
public class SedeController {

    private final SedeService sedeService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN_INSTITUCION')")
    public ResponseEntity<ApiResponse<SedeResponse>> crear(@Valid @RequestBody SedeRequest request) {
        return ResponseEntity.status(201).body(ApiResponseFactory.created("Sede creada", sedeService.crear(request)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN_INSTITUCION')")
    public ResponseEntity<ApiResponse<SedeResponse>> actualizar(@PathVariable Long id, @Valid @RequestBody SedeUpdateRequest request) {
        return ResponseEntity.ok(ApiResponseFactory.ok("Sede actualizada", sedeService.actualizar(id, request)));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN_INSTITUCION')")
    public ResponseEntity<ApiResponse<PageResponse<SedeResponse>>> listar(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String ciudad,
            @RequestParam(required = false) EstadoRegistro estado,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponseFactory.ok("Listado de sedes", sedeService.listar(q, nombre, ciudad, estado, page, size)));
    }
}

