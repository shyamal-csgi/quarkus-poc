package com.quarkus.poc.product.dto;

import lombok.Builder;

/**
 * Kafka event consumed to create a stock movement.
 */
@Builder
public record StockMovementEvent(
        String stockMovementId,
        String productId,
        String warehouseId
) {
}
