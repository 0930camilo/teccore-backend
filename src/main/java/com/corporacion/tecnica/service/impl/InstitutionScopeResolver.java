package com.corporacion.tecnica.service.impl;

import com.corporacion.tecnica.entity.Institucion;
import com.corporacion.tecnica.exception.BusinessException;
import com.corporacion.tecnica.exception.ResourceNotFoundException;
import com.corporacion.tecnica.repository.InstitucionRepository;
import com.corporacion.tecnica.util.TenantContext;
import org.springframework.stereotype.Component;

@Component
public class InstitutionScopeResolver {

    private final InstitucionRepository institucionRepository;

    public InstitutionScopeResolver(InstitucionRepository institucionRepository) {
        this.institucionRepository = institucionRepository;
    }

    public Long resolveInstitutionId(Long requestInstitutionId) {
        Long tenantId = TenantContext.getInstitutionId();
        if (tenantId != null) {
            if (requestInstitutionId != null && !tenantId.equals(requestInstitutionId)) {
                throw new BusinessException("La institucion del request no coincide con el contexto autenticado");
            }
            return tenantId;
        }
        if (requestInstitutionId == null) {
            throw new BusinessException("Debe enviar institucionId o cabecera X-Institucion-Id");
        }
        return requestInstitutionId;
    }

    public Institucion getRequiredInstitution(Long requestInstitutionId) {
        Long institutionId = resolveInstitutionId(requestInstitutionId);
        return institucionRepository.findById(institutionId)
                .orElseThrow(() -> new ResourceNotFoundException("Institucion no encontrada"));
    }
}

