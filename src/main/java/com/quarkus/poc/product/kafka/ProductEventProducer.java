package com.quarkus.poc.product.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quarkus.poc.product.dto.ProductCreatedEvent;
import io.smallrye.reactive.messaging.MutinyEmitter;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.jboss.logging.Logger;

/**
 * Publishes product-created events to Kafka (or in-memory connector in tests).
 */
@ApplicationScoped
public class ProductEventProducer {

    private static final Logger LOG = Logger.getLogger(ProductEventProducer.class);

    private final MutinyEmitter<String> emitter;
    private final ObjectMapper objectMapper;

    @Inject
    public ProductEventProducer(
            @Channel("product-events") MutinyEmitter<String> emitter,
            ObjectMapper objectMapper) {
        this.emitter = emitter;
        this.objectMapper = objectMapper;
    }

    public void sendProductCreated(ProductCreatedEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            emitter.sendAndForget(payload);
            LOG.infof("Published product-created event for %s", event.productId());
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize product event", e);
        }
    }
}
