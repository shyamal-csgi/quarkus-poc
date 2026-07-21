package com.quarkus.poc.product.client;

import com.quarkus.poc.product.dto.EnrichmentResponse;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

/**
 * Resilient outbound REST client for product enrichment.
 * Returns Mutiny {@link Uni}; fault tolerance via SmallRye annotations.
 */
@Path("/api/v1/enrichment")
@RegisterRestClient(configKey = "enrichment-api")
public interface EnrichmentClient {

    @GET
    @Path("/students/{studentId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Timeout(2000)
    @Retry(maxRetries = 2, delay = 200)
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.5, delay = 5000)
    @Fallback(fallbackMethod = "fallbackEnrichment")
    Uni<EnrichmentResponse> enrich(@PathParam("studentId") String studentId);

    /**
     * Fallback when enrichment is unavailable.
     */
    default Uni<EnrichmentResponse> fallbackEnrichment(String studentId) {
        return Uni.createFrom().item(
                EnrichmentResponse.builder()
                        .studentId(studentId)
                        .note("Enrichment unavailable; using fallback")
                        .source("fallback")
                        .build());
    }
}
