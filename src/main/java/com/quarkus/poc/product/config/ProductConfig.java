package com.quarkus.poc.product.config;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

/**
 * Application configuration mapped from {@code product.*} properties.
 */
@ConfigMapping(prefix = "product")
public interface ProductConfig {

    Enrichment enrichment();

    Kafka kafka();

    interface Enrichment {
        @WithDefault("true")
        boolean enabled();
    }

    interface Kafka {
        @WithDefault("quarkus.poc.signal.product.products")
        String productTopic();

        @WithDefault("quarkus.poc.signal.product.stock-movements")
        String stockMovementTopic();
    }
}
