package com.quarkus.poc.product.dto;

import lombok.Builder;

/**
 * Standard error body.
 */
@Builder
public record ErrorResponse(
        String code,
        String message,
        String reason
) {
}
