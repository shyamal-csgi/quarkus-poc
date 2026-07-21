package unit.com.quarkus.poc.product.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.quarkus.poc.product.domain.suppliers.Supplier;
import com.quarkus.poc.product.dto.SupplierResponse;
import com.quarkus.poc.product.exception.SupplierNotFoundException;
import com.quarkus.poc.product.persistence.SupplierRepository;
import com.quarkus.poc.product.service.SupplierCatalogService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SupplierCatalogServiceTest {

    @Mock
    SupplierRepository supplierRepository;

    SupplierCatalogService supplierCatalogService;

    @BeforeEach
    void setUp() {
        supplierCatalogService = new SupplierCatalogService(supplierRepository);
    }

    @Test
    void getById_whenExists_shouldMapToResponse() {
        when(supplierRepository.findById("SUP-001"))
                .thenReturn(Optional.of(new Supplier("SUP-001", "Acme Supplies", "Sydney", "AU", 1)));

        SupplierResponse response = supplierCatalogService.getById("SUP-001");

        assertThat(response.name()).isEqualTo("Acme Supplies");
        assertThat(response.city()).isEqualTo("Sydney");
        verify(supplierRepository, times(1)).findById("SUP-001");
    }

    @Test
    void getById_whenMissing_shouldThrowSupplierNotFound() {
        when(supplierRepository.findById("NONE")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> supplierCatalogService.getById("NONE"))
                .isInstanceOf(SupplierNotFoundException.class);
    }
}
