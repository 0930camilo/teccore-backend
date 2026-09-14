package com.corporacion.tecnica.controller;

import com.corporacion.tecnica.dto.ApiResponse;
import com.corporacion.tecnica.dto.PageResponse;
import com.corporacion.tecnica.dto.docente.DocenteRequest;
import com.corporacion.tecnica.dto.docente.DocenteResponse;
import com.corporacion.tecnica.service.DocenteService;
import com.corporacion.tecnica.util.ApiResponseFactory;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/docentes")
@RequiredArgsConstructor
public class DocenteController {

    private final DocenteService docenteService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN_INSTITUCION', 'ADMIN_SEDE')")
    public ResponseEntity<ApiResponse<DocenteResponse>> crear(
            @Valid @RequestBody DocenteRequest request) {

        return ResponseEntity
                .status(201)
                .body(
                        ApiResponseFactory.created(
                                "Docente creado",
                                docenteService.crear(request)
                        )
                );
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN_INSTITUCION', 'ADMIN_SEDE')")
    public ResponseEntity<ApiResponse<PageResponse<DocenteResponse>>> listar(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                ApiResponseFactory.ok(
                        "Listado de docentes",
                        docenteService.listar(q, page, size)
                )
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN_INSTITUCION', 'ADMIN_SEDE')")
    public ResponseEntity<ApiResponse<DocenteResponse>> obtener(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponseFactory.ok(
                        "Docente encontrado",
                        docenteService.obtener(id)
                )
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN_INSTITUCION', 'ADMIN_SEDE')")
    public ResponseEntity<ApiResponse<DocenteResponse>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody DocenteRequest request) {

        return ResponseEntity.ok(
                ApiResponseFactory.ok(
                        "Docente actualizado",
                        docenteService.actualizar(id, request)
                )
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN_INSTITUCION', 'ADMIN_SEDE')")
    public ResponseEntity<ApiResponse<Void>> eliminar(
            @PathVariable Long id) {

        docenteService.eliminar(id);

        return ResponseEntity.ok(
                ApiResponseFactory.ok(
                        "Docente eliminado",
                        null
                )
        );
    }
}