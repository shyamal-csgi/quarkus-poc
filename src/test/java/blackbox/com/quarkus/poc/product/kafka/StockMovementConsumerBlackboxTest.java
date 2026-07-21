package blackbox.com.quarkus.poc.product.kafka;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quarkus.poc.product.domain.products.Product;
import com.quarkus.poc.product.dto.StockMovementEvent;
import com.quarkus.poc.product.persistence.ProductRepository;
import component.com.quarkus.poc.product.db.PostgresTestResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.reactive.messaging.memory.InMemoryConnector;
import jakarta.inject.Inject;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.spi.Connector;
import org.junit.jupiter.api.Test;

@QuarkusTest
@QuarkusTestResource(PostgresTestResource.class)
class StockMovementConsumerBlackboxTest {

    @Inject
    @Connector("smallrye-in-memory")
    InMemoryConnector connector;

    @Inject
    ProductRepository productRepository;

    @Inject
    ObjectMapper objectMapper;

    @Test
    void stockMovementEvent_whenSentViaInMemoryConnector_shouldPersistStockMovement() throws Exception {
        productRepository.persist(new Product(
                "PRD-BB-1",
                "Blackbox Widget",
                "SKU-BB-SMV-01",
                "SUP-001",
                1,
                OffsetDateTime.now(ZoneOffset.UTC)));

        StockMovementEvent event = StockMovementEvent.builder()
                .stockMovementId("SMV-BB-1")
                .productId("PRD-BB-1")
                .warehouseId("WH-101")
                .build();

        connector.source("stock-events").send(Message.of(objectMapper.writeValueAsString(event)));

        await().atMost(Duration.ofSeconds(30)).untilAsserted(() ->
                assertThat(productRepository.findStockMovementById("SMV-BB-1"))
                        .isPresent()
                        .get()
                        .satisfies(movement -> {
                            assertThat(movement.getProductId()).isEqualTo("PRD-BB-1");
                            assertThat(movement.getWarehouseId()).isEqualTo("WH-101");
                        }));
    }
}
