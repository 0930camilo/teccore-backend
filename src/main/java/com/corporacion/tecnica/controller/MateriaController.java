package com.corporacion.tecnica.controller;

import com.corporacion.tecnica.dto.ApiResponse;
import com.corporacion.tecnica.dto.PageResponse;
import com.corporacion.tecnica.dto.materia.MateriaRequest;
import com.corporacion.tecnica.dto.materia.MateriaResponse;
import com.corporacion.tecnica.service.MateriaService;
import com.corporacion.tecnica.util.ApiResponseFactory;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/materias")
@RequiredArgsConstructor
public class MateriaController {

    private final MateriaService materiaService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN_INSTITUCION','ADMIN_SEDE')")
    public ResponseEntity<ApiResponse<MateriaResponse>> crear(@Valid @RequestBody MateriaRequest request) {
        return ResponseEntity.status(201).body(ApiResponseFactory.created("Materia creada", materiaService.crear(request)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN_INSTITUCION','ADMIN_SEDE','DOCENTE')")
    public ResponseEntity<ApiResponse<MateriaResponse>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponseFactory.ok("Materia encontrada", materiaService.obtenerPorId(id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN_INSTITUCION','ADMIN_SEDE')")
    public ResponseEntity<ApiResponse<MateriaResponse>> actualizar(@PathVariable Long id, @Valid @RequestBody MateriaRequest request) {
        return ResponseEntity.ok(ApiResponseFactory.ok("Materia actualizada", materiaService.actualizar(id, request)));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN_INSTITUCION','ADMIN_SEDE','DOCENTE')")
    public ResponseEntity<ApiResponse<PageResponse<MateriaResponse>>> listar(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponseFactory.ok("Listado de materias", materiaService.listar(q, page, size)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN_INSTITUCION','ADMIN_SEDE')")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        materiaService.eliminar(id);
        return ResponseEntity.ok(ApiResponseFactory.ok("Materia eliminada", null));
    }
}
