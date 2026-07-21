package component.com.quarkus.poc.product.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.quarkus.poc.product.domain.products.Product;
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
}
