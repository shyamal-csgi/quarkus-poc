package unit.com.quarkus.poc.product.client;

import static org.assertj.core.api.Assertions.assertThat;

import com.quarkus.poc.product.client.EnrichmentClient;
import com.quarkus.poc.product.dto.EnrichmentResponse;
import org.junit.jupiter.api.Test;

/**
 * Verifies the EnrichmentClient default fallback method (fault-tolerance path).
 */
class EnrichmentClientFallbackTest {

    @Test
    void fallbackEnrichment_whenCalled_shouldReturnFallbackSource() {
        EnrichmentClient client = new EnrichmentClient() {
            @Override
            public io.smallrye.mutiny.Uni<EnrichmentResponse> enrich(String studentId) {
                return fallbackEnrichment(studentId);
            }
        };

        EnrichmentResponse response = client.fallbackEnrichment("PRD-1").await().indefinitely();

        assertThat(response.studentId()).isEqualTo("PRD-1");
        assertThat(response.source()).isEqualTo("fallback");
        assertThat(response.note()).contains("unavailable");
    }
}
