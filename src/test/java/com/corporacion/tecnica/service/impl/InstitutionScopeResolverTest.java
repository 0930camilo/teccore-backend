package com.corporacion.tecnica.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;

import com.corporacion.tecnica.repository.InstitucionRepository;
import com.corporacion.tecnica.util.TenantContext;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

class InstitutionScopeResolverTest {

    private final InstitucionRepository institucionRepository = mock(InstitucionRepository.class);
    private final InstitutionScopeResolver resolver = new InstitutionScopeResolver(institucionRepository);

    @AfterEach
    void cleanUp() {
        SecurityContextHolder.clearContext();
        TenantContext.clear();
    }

    @Test
    void superAdminDebeIgnorarTenantEnListadoDeInstituciones() {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                "owner@teccore.com",
                null,
                List.of(new SimpleGrantedAuthority("ROLE_SUPER_ADMIN"))));
        TenantContext.setInstitutionId(1L);

        Long scope = resolver.getCurrentInstitutionScope();

        assertNull(scope);
    }

    @Test
    void superAdminDebePoderSeleccionarOtraInstitucionAunqueTengaTenantLegado() {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                "owner@teccore.com",
                null,
                List.of(new SimpleGrantedAuthority("ROLE_SUPER_ADMIN"))));
        TenantContext.setInstitutionId(1L);

        Long institutionId = resolver.resolveInstitutionId(5L);

        assertEquals(5L, institutionId);
    }
}

