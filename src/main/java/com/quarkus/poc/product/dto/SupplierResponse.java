package com.quarkus.poc.product.dto;

import lombok.Builder;

/**
 * Supplier catalog response.
 */
@Builder
public record SupplierResponse(
        String id,
        String name,
        String city,
        String country,
        Integer status
) {
}
