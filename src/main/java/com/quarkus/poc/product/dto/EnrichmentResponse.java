package com.quarkus.poc.product.dto;

import lombok.Builder;

/**
 * Response from the external enrichment API.
 */
@Builder
public record EnrichmentResponse(
        String studentId,
        String note,
        String source
) {
}
