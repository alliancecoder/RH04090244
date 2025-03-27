package org.electricaltrainingalliance.testutils;

import java.util.Map;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

// @QuarkusTestResource(AzureIdentityServiceTestResource.class)
public class AzureIdentityServiceTestResource implements QuarkusTestResourceLifecycleManager {

    @Override
    public Map<String, String> start() {
        return Map.of(
            "microsoft.graph.tenant-id", "test-tenant-id",
            "microsoft.graph.client-id", "test-client-id",
            "microsoft.graph.client-secret", "test-client-secret"
        );
    }

    @Override
    public void stop() {
        // No cleanup required
    }
}
