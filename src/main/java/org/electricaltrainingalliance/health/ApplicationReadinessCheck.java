package org.electricaltrainingalliance.health;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

import jakarta.enterprise.context.ApplicationScoped;

@Readiness
@ApplicationScoped
public class ApplicationReadinessCheck implements HealthCheck {

    @Override
    public HealthCheckResponse call() {
        // Add logic here if you need to check e.g., database connectivity, etc.
        return HealthCheckResponse.up("Application is ready");
    }
}