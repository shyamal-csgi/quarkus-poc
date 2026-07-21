package com.quarkus.poc.product.dto;

import lombok.Builder;

/**
 * Kafka event published when a product is created.
 */
@Builder
public record ProductCreatedEvent(
        String productId,
        String name,
        String supplierId,
        String eventType
) {
}
