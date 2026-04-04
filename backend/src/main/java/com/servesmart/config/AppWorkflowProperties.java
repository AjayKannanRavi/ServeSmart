package com.servesmart.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "app")
public class AppWorkflowProperties {

    private final Tenant tenant = new Tenant();
    private final Seed seed = new Seed();
    private final Url url = new Url();
    private final Auth auth = new Auth();

    public Tenant getTenant() {
        return tenant;
    }

    public Seed getSeed() {
        return seed;
    }

    public Url getUrl() {
        return url;
    }

    public Auth getAuth() {
        return auth;
    }

    public static class Tenant {
        private boolean requireHeaderForOperationalApis = true;
        private List<String> masterPathPrefixes = new ArrayList<>(List.of("/api/saas", "/api/health"));

        public boolean isRequireHeaderForOperationalApis() {
            return requireHeaderForOperationalApis;
        }

        public void setRequireHeaderForOperationalApis(boolean requireHeaderForOperationalApis) {
            this.requireHeaderForOperationalApis = requireHeaderForOperationalApis;
        }

        public List<String> getMasterPathPrefixes() {
            return masterPathPrefixes;
        }

        public void setMasterPathPrefixes(List<String> masterPathPrefixes) {
            this.masterPathPrefixes = masterPathPrefixes;
        }
    }

    public static class Seed {
        private boolean enabled = true;
        private String defaultTenantId = "1";

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getDefaultTenantId() {
            return defaultTenantId;
        }

        public void setDefaultTenantId(String defaultTenantId) {
            this.defaultTenantId = defaultTenantId;
        }
    }

    public static class Url {
        private String backendBase = "http://localhost:8085";

        public String getBackendBase() {
            return backendBase;
        }

        public void setBackendBase(String backendBase) {
            this.backendBase = backendBase;
        }
    }

    public static class Auth {
        private String mockOtp = "123456";

        public String getMockOtp() {
            return mockOtp;
        }

        public void setMockOtp(String mockOtp) {
            this.mockOtp = mockOtp;
        }
    }
}