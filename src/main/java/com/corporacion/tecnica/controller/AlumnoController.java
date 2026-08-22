package com.corporacion.tecnica.controller;

import com.corporacion.tecnica.dto.ApiResponse;
import com.corporacion.tecnica.dto.PageResponse;
import com.corporacion.tecnica.dto.alumno.AlumnoRequest;
import com.corporacion.tecnica.dto.alumno.AlumnoResponse;
import com.corporacion.tecnica.service.AlumnoService;
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
@RequestMapping("/alumnos")
@RequiredArgsConstructor
public class AlumnoController {

    private final AlumnoService alumnoService;

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN_INSTITUCION')")
    public ResponseEntity<ApiResponse<AlumnoResponse>> crear(@Valid @RequestBody AlumnoRequest request) {
        return ResponseEntity.status(201).body(ApiResponseFactory.created("Alumno creado", alumnoService.crear(request)));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN_INSTITUCION','DOCENTE')")
    public ResponseEntity<ApiResponse<PageResponse<AlumnoResponse>>> listar(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponseFactory.ok("Listado de alumnos", alumnoService.listar(q, page, size)));
    }
}

