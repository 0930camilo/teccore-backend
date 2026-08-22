package com.corporacion.tecnica.controller;

import com.corporacion.tecnica.dto.ApiResponse;
import com.corporacion.tecnica.dto.reportes.ReporteResumenResponse;
import com.corporacion.tecnica.service.ReporteService;
import com.corporacion.tecnica.util.ApiResponseFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService reporteService;

    @GetMapping("/resumen-institucion")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN_INSTITUCION','AUXILIAR_CONTABLE')")
    public ResponseEntity<ApiResponse<ReporteResumenResponse>> resumen(@RequestParam(required = false) Long institucionId) {
        return ResponseEntity.ok(ApiResponseFactory.ok("Resumen institucional", reporteService.resumenInstitucion(institucionId)));
    }
}

