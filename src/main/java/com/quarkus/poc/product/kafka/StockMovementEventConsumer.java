package com.quarkus.poc.product.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quarkus.poc.product.domain.products.StockMovement;
import com.quarkus.poc.product.dto.StockMovementEvent;
import com.quarkus.poc.product.persistence.ProductRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

/**
 * Consumes stock-movement events and persists stock movement rows.
 */
@ApplicationScoped
public class StockMovementEventConsumer {

    private static final Logger LOG = Logger.getLogger(StockMovementEventConsumer.class);

    private final ProductRepository productRepository;
    private final ObjectMapper objectMapper;

    @Inject
    public StockMovementEventConsumer(ProductRepository productRepository, ObjectMapper objectMapper) {
        this.productRepository = productRepository;
        this.objectMapper = objectMapper;
    }

    @Incoming("stock-events")
    public void onStockMovement(String payload) {
        try {
            StockMovementEvent event = objectMapper.readValue(payload, StockMovementEvent.class);
            StockMovement stockMovement = new StockMovement(
                    event.stockMovementId(),
                    event.productId(),
                    event.warehouseId(),
                    1,
                    OffsetDateTime.now(ZoneOffset.UTC));
            productRepository.persistStockMovement(stockMovement);
            LOG.infof("Persisted stock movement %s for product %s",
                    event.stockMovementId(), event.productId());
        } catch (Exception e) {
            LOG.errorf(e, "Failed to process stock movement event: %s", payload);
            throw new IllegalStateException("Stock movement consume failed", e);
        }
    }
}
