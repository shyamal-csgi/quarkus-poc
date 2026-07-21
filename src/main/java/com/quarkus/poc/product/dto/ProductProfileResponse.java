package com.quarkus.poc.product.dto;

import lombok.Builder;

/**
 * Aggregated async profile: product + supplier + enrichment.
 */
@Builder
public record ProductProfileResponse(
        String id,
        String name,
        String sku,
        String supplierId,
        String supplierName,
        String supplierCity,
        String enrichmentNote,
        Integer status
) {
}
