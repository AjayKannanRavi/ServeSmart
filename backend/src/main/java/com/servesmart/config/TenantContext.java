package com.servesmart.config;

public class TenantContext {
    private static final ThreadLocal<String> currentTenant = new ThreadLocal<>();

    public static void setCurrentTenant(String tenant) {
        currentTenant.set(tenant);
    }

    public static String getCurrentTenant() {
        return currentTenant.get();
    }

    public static Long getCurrentTenantAsLong() {
        String tenant = currentTenant.get();
        if (tenant == null || "master".equals(tenant)) {
            return null;
        }
        try {
            return Long.parseLong(tenant);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static Long requireCurrentTenantAsLong() {
        Long tenantId = getCurrentTenantAsLong();
        if (tenantId == null) {
            throw new IllegalStateException("Missing tenant context. Provide X-Hotel-Id header.");
        }
        return tenantId;
    }

    public static void clear() {
        currentTenant.remove();
    }
}
