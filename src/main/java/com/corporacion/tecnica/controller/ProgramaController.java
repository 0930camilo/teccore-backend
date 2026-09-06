package com.corporacion.tecnica.controller;

import com.corporacion.tecnica.dto.ApiResponse;
import com.corporacion.tecnica.dto.PageResponse;
import com.corporacion.tecnica.dto.programa.ProgramaRequest;
import com.corporacion.tecnica.dto.programa.ProgramaResponse;
import com.corporacion.tecnica.dto.programa.ProgramaUpdateRequest;
import com.corporacion.tecnica.entity.EstadoRegistro;
import com.corporacion.tecnica.service.ProgramaService;
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
@RequestMapping("/programas")
@RequiredArgsConstructor
public class ProgramaController {

    private final ProgramaService programaService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN_INSTITUCION')")
    public ResponseEntity<ApiResponse<ProgramaResponse>> crear(@Valid @RequestBody ProgramaRequest request) {
        return ResponseEntity.status(201).body(ApiResponseFactory.created("Programa creado", programaService.crear(request)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN_INSTITUCION')")
    public ResponseEntity<ApiResponse<ProgramaResponse>> actualizar(@PathVariable Long id, @Valid @RequestBody ProgramaUpdateRequest request) {
        return ResponseEntity.ok(ApiResponseFactory.ok("Programa actualizado", programaService.actualizar(id, request)));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN_INSTITUCION','DOCENTE')")
    public ResponseEntity<ApiResponse<PageResponse<ProgramaResponse>>> listar(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) EstadoRegistro estado,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponseFactory.ok("Listado de programas", programaService.listar(q, nombre, estado, page, size)));
    }
}

