package com.corporacion.tecnica.util;

public final class TenantContext {

    private static final ThreadLocal<Long> TENANT = new ThreadLocal<>();

    private TenantContext() {
    }

    public static void setInstitutionId(Long institutionId) {
        TENANT.set(institutionId);
    }

    public static Long getInstitutionId() {
        return TENANT.get();
    }

    public static void clear() {
        TENANT.remove();
    }
}

