package component.com.quarkus.poc.product.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.quarkus.poc.product.domain.suppliers.Supplier;
import com.quarkus.poc.product.persistence.SupplierRepository;
import component.com.quarkus.poc.product.db.PostgresTestResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

@QuarkusTest
@QuarkusTestResource(PostgresTestResource.class)
class SupplierRepositoryComponentTest {

    @Inject
    SupplierRepository supplierRepository;

    @Test
    void findById_whenSeededByFlyway_shouldReturnSupplier() {
        assertThat(supplierRepository.findById("SUP-001"))
                .isPresent()
                .get()
                .extracting(Supplier::getName, Supplier::getCity)
                .containsExactly("Acme Supplies", "Sydney");
    }

    @Test
    void findSupplierProductsBySupplierId_whenSeeded_shouldReturnProducts() {
        assertThat(supplierRepository.findSupplierProductsBySupplierId("SUP-001")).hasSize(2);
    }
}
