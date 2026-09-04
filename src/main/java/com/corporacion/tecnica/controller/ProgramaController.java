package com.corporacion.tecnica.controller;

import com.corporacion.tecnica.dto.ApiResponse;
import com.corporacion.tecnica.dto.PageResponse;
import com.corporacion.tecnica.dto.programa.ProgramaRequest;
import com.corporacion.tecnica.dto.programa.ProgramaResponse;
import com.corporacion.tecnica.service.ProgramaService;
import com.corporacion.tecnica.util.ApiResponseFactory;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN_INSTITUCION')")
    public ResponseEntity<ApiResponse<ProgramaResponse>> crear(@Valid @RequestBody ProgramaRequest request) {
        return ResponseEntity.status(201).body(ApiResponseFactory.created("Programa creado", programaService.crear(request)));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN_INSTITUCION','DOCENTE')")
    public ResponseEntity<ApiResponse<PageResponse<ProgramaResponse>>> listar(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponseFactory.ok("Listado de programas", programaService.listar(q, page, size)));
    }
}

