package component.com.quarkus.poc.product.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.quarkus.poc.product.domain.products.Product;
import com.quarkus.poc.product.domain.products.StockMovement;
import com.quarkus.poc.product.persistence.ProductRepository;
import component.com.quarkus.poc.product.db.PostgresTestResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;

@QuarkusTest
@QuarkusTestResource(PostgresTestResource.class)
class ProductRepositoryComponentTest {

    @Inject
    ProductRepository productRepository;

    @Test
    void persist_andFindById_shouldRoundTrip() {
        Product product = new Product(
                "PRD-COMP-1",
                "Component Widget",
                "SKU-COMP-01",
                "SUP-001",
                1,
                OffsetDateTime.now(ZoneOffset.UTC));

        productRepository.persist(product);

        assertThat(productRepository.findById("PRD-COMP-1"))
                .isPresent()
                .get()
                .extracting(Product::getName, Product::getSku)
                .containsExactly("Component Widget", "SKU-COMP-01");
    }

    @Test
    void persistStockMovement_andFindById_shouldRoundTrip() {
        productRepository.persist(new Product(
                "PRD-COMP-2",
                "Stock Widget",
                "SKU-COMP-02",
                "SUP-001",
                1,
                OffsetDateTime.now(ZoneOffset.UTC)));

        StockMovement movement = new StockMovement(
                "SMV-COMP-1",
                "PRD-COMP-2",
                "WH-101",
                1,
                OffsetDateTime.now(ZoneOffset.UTC));

        productRepository.persistStockMovement(movement);

        assertThat(productRepository.findStockMovementById("SMV-COMP-1"))
                .isPresent()
                .get()
                .extracting(StockMovement::getProductId, StockMovement::getWarehouseId)
                .containsExactly("PRD-COMP-2", "WH-101");
    }
}
