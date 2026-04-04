package com.servesmart.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class TenantInterceptor implements HandlerInterceptor {

    private static final String TENANT_HEADER = "X-Hotel-Id";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String tenantIdStr = request.getHeader(TENANT_HEADER);
        if (tenantIdStr != null && !tenantIdStr.isEmpty()) {
            try {
                TenantContext.setCurrentTenant(tenantIdStr);
            } catch (NumberFormatException e) {
                // Invalid tenant ID format
            }
        }
        return true;
    }

    @Override
    public void afterCompletion(jakarta.servlet.http.HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response, Object handler, @org.springframework.lang.Nullable Exception ex) throws Exception {
        TenantContext.clear();
    }
}
