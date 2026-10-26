package com.corporacion.tecnica.controller;

import com.corporacion.tecnica.dto.ApiResponse;
import com.corporacion.tecnica.dto.PageResponse;
import com.corporacion.tecnica.dto.asistencia.AsistenciaRequest;
import com.corporacion.tecnica.dto.asistencia.AsistenciaResponse;
import com.corporacion.tecnica.service.AsistenciaService;
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
@RequestMapping("/asistencias")
@RequiredArgsConstructor
public class AsistenciaController {

    private final AsistenciaService asistenciaService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN_INSTITUCION','ADMIN_SEDE','DOCENTE')")
    public ResponseEntity<ApiResponse<AsistenciaResponse>> crear(@Valid @RequestBody AsistenciaRequest request) {
        return ResponseEntity.status(201)
                .body(ApiResponseFactory.created("Asistencia registrada", asistenciaService.crear(request)));
    }

    @GetMapping("/materia")
    @PreAuthorize("hasAnyRole('ADMIN_INSTITUCION','ADMIN_SEDE','DOCENTE')")
    public ResponseEntity<ApiResponse<PageResponse<AsistenciaResponse>>> listarPorMateria(
            @RequestParam Long materiaId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponseFactory.ok("Listado de asistencias por materia", asistenciaService.listarPorMateria(materiaId, page, size)));
    }

    @GetMapping("/alumno")
    @PreAuthorize("hasAnyRole('ADMIN_INSTITUCION','ADMIN_SEDE','DOCENTE','ESTUDIANTE')")
    public ResponseEntity<ApiResponse<PageResponse<AsistenciaResponse>>> listarPorAlumno(
            @RequestParam Long alumnoId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponseFactory.ok("Listado de asistencias por alumno", asistenciaService.listarPorAlumno(alumnoId, page, size)));
    }
}

