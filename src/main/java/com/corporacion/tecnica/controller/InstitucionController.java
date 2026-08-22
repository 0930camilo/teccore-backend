package com.corporacion.tecnica.controller;

import com.corporacion.tecnica.dto.ApiResponse;
import com.corporacion.tecnica.dto.institucion.InstitucionRequest;
import com.corporacion.tecnica.dto.institucion.InstitucionResponse;
import com.corporacion.tecnica.service.InstitucionService;
import com.corporacion.tecnica.util.ApiResponseFactory;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN_INSTITUCION')")
    public ResponseEntity<ApiResponse<List<InstitucionResponse>>> listar() {
        return ResponseEntity.ok(ApiResponseFactory.ok("Listado de instituciones", institucionService.listar()));
    }
}
