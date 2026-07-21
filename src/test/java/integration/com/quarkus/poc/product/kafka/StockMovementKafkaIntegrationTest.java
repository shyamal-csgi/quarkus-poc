package integration.com.quarkus.poc.product.kafka;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quarkus.poc.product.domain.products.Product;
import com.quarkus.poc.product.dto.StockMovementEvent;
import com.quarkus.poc.product.persistence.ProductRepository;
import component.com.quarkus.poc.product.db.PostgresTestResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Test;

/**
 * End-to-end Kafka consume via Testcontainers Kafka.
 * Run with: {@code ./gradlew integrationTest}
 */
@QuarkusTest
@QuarkusTestResource(PostgresTestResource.class)
@QuarkusTestResource(KafkaIntegrationTestResource.class)
class StockMovementKafkaIntegrationTest {

    @Inject
    ProductRepository productRepository;

    @Inject
    ObjectMapper objectMapper;

    @ConfigProperty(name = "kafka.bootstrap.servers")
    String bootstrapServers;

    @Test
    void stockMovementEvent_whenPublished_shouldPersistStockMovement() throws Exception {
        productRepository.persist(new Product(
                "PRD-INT-1",
                "Integration Widget",
                "SKU-INT-01",
                "SUP-001",
                1,
                OffsetDateTime.now(ZoneOffset.UTC)));

        StockMovementEvent event = StockMovementEvent.builder()
                .stockMovementId("SMV-INT-1")
                .productId("PRD-INT-1")
                .warehouseId("SPR-101")
                .build();

        try (KafkaProducer<String, String> producer = new KafkaProducer<>(producerProps())) {
            producer.send(new ProducerRecord<>(
                    "quarkus.poc.signal.product.stock-movements",
                    objectMapper.writeValueAsString(event))).get(10, TimeUnit.SECONDS);
        }

        await().atMost(Duration.ofSeconds(30)).untilAsserted(() ->
                assertThat(productRepository.findStockMovementById("SMV-INT-1")).isPresent());
    }

    private Map<String, Object> producerProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        return props;
    }
}
