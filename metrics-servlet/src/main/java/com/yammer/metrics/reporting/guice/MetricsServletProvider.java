package com.yammer.metrics.reporting.guice;

import java.util.Set;

import org.codehaus.jackson.JsonFactory;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import com.yammer.metrics.HealthCheckRegistry;
import com.yammer.metrics.MetricsRegistry;
import com.yammer.metrics.core.HealthCheck;
import com.yammer.metrics.reporting.MetricsServlet;

public class MetricsServletProvider implements Provider<MetricsServlet>
{
    private final MetricsRegistry metricsRegistry;
    private final HealthCheckRegistry healthCheckRegistry;
    private final Set<HealthCheck> healthChecks;
    private final String healthcheckUri;
    private final String metricsUri;
    private final String pingUri;
    private final String threadsUri;
    private JsonFactory jsonFactory;

    @Inject
    public MetricsServletProvider(Set<HealthCheck> healthChecks,
                                  MetricsRegistry metricsRegistry,
                                  HealthCheckRegistry healthCheckRegistry,
                                  @Named("MetricsServlet.HEALTHCHECK_URI") String healthcheckUri,
                                  @Named("MetricsServlet.METRICS_URI") String metricsUri,
                                  @Named("MetricsServlet.PING_URI") String pingUri,
                                  @Named("MetricsServlet.THREADS_URI") String threadsUri) {
        this.metricsRegistry = metricsRegistry;
        this.healthCheckRegistry = healthCheckRegistry;
        this.healthcheckUri = healthcheckUri;
        this.metricsUri = metricsUri;
        this.pingUri = pingUri;
        this.threadsUri = threadsUri;
        this.healthChecks = healthChecks;
    }

    @Inject(optional = true)
    public void setJsonFactory(@Named("MetricsServlet.JSON_FACTORY") JsonFactory jsonFactory) {
        this.jsonFactory = jsonFactory;
    }

    @Override
    public MetricsServlet get()
    {
        for (HealthCheck healthCheck : healthChecks) {
            healthCheckRegistry.register(healthCheck);
        }
        if (jsonFactory != null) {
            return new MetricsServlet(metricsRegistry, healthCheckRegistry, jsonFactory, healthcheckUri, metricsUri, pingUri, threadsUri, true);
        }
        else {
            return new MetricsServlet(metricsRegistry, healthCheckRegistry, healthcheckUri, metricsUri, pingUri, threadsUri, true);
        }
    }
}