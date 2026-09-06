package com.corporacion.tecnica.service.impl;

import com.corporacion.tecnica.entity.Sede;
import com.corporacion.tecnica.exception.BusinessException;
import com.corporacion.tecnica.exception.ResourceNotFoundException;
import com.corporacion.tecnica.repository.SedeRepository;
import com.corporacion.tecnica.util.TenantContext;
import org.springframework.stereotype.Component;

@Component
public class SedeScopeResolver {

    private final SedeRepository sedeRepository;
    private final InstitutionScopeResolver institutionScopeResolver;

    public SedeScopeResolver(SedeRepository sedeRepository, InstitutionScopeResolver institutionScopeResolver) {
        this.sedeRepository = sedeRepository;
        this.institutionScopeResolver = institutionScopeResolver;
    }

    public Long resolveSedeId(Long requestSedeId) {
        Long scopedSedeId = getCurrentSedeScope();
        if (scopedSedeId != null) {
            if (requestSedeId != null && !scopedSedeId.equals(requestSedeId)) {
                throw new BusinessException("La sede del request no coincide con el contexto autenticado");
            }
            return scopedSedeId;
        }
        if (requestSedeId == null) {
            throw new BusinessException("Debe enviar sedeId o cabecera X-Sede-Id");
        }
        return requestSedeId;
    }

    public Sede getRequiredSede(Long requestSedeId) {
        Long sedeId = resolveSedeId(requestSedeId);
        Sede sede = sedeRepository.findById(sedeId)
                .orElseThrow(() -> new ResourceNotFoundException("Sede no encontrada"));

        Long institutionId = institutionScopeResolver.resolveInstitutionId(sede.getInstitucion() != null ? sede.getInstitucion().getId() : null);
        if (sede.getInstitucion() == null || !institutionId.equals(sede.getInstitucion().getId())) {
            throw new BusinessException("La sede no pertenece a la institucion autenticada");
        }
        return sede;
    }

    public Long getCurrentSedeScope() {
        if (institutionScopeResolver.isSuperAdminAuthenticated()) {
            return null;
        }
        return TenantContext.getSedeId();
    }
}

