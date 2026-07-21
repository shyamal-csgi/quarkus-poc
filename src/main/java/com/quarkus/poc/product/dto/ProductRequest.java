package com.quarkus.poc.product.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

/**
 * Request body for creating a product.
 */
@Builder
public record ProductRequest(
        @NotBlank String name,
        @NotBlank String sku,
        @NotBlank String supplierId
) {
}
