package com.corporacion.tecnica.util;

public final class TenantContext {

    private static final ThreadLocal<Long> TENANT = new ThreadLocal<>();
    private static final ThreadLocal<Long> SEDE = new ThreadLocal<>();

    private TenantContext() {
    }

    public static void setInstitutionId(Long institutionId) {
        TENANT.set(institutionId);
    }

    public static Long getInstitutionId() {
        return TENANT.get();
    }

    public static void setSedeId(Long sedeId) {
        SEDE.set(sedeId);
    }

    public static Long getSedeId() {
        return SEDE.get();
    }

    public static void clear() {
        TENANT.remove();
        SEDE.remove();
    }
}

