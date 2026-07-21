package com.quarkus.poc.product.dto;

import lombok.Builder;

/**
 * Product API response.
 */
@Builder
public record ProductResponse(
        String id,
        String name,
        String sku,
        String supplierId,
        Integer status
) {
}
