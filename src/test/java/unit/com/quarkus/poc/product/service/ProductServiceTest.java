package unit.com.quarkus.poc.product.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.quarkus.poc.product.domain.products.Product;
import com.quarkus.poc.product.dto.ProductRequest;
import com.quarkus.poc.product.dto.ProductResponse;
import com.quarkus.poc.product.exception.ProductNotFoundException;
import com.quarkus.poc.product.kafka.ProductEventProducer;
import com.quarkus.poc.product.persistence.ProductRepository;
import com.quarkus.poc.product.service.ProductService;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    ProductRepository productRepository;

    @Mock
    ProductEventProducer productEventProducer;

    ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductService(productRepository, productEventProducer);
    }

    @Test
    void getById_whenProductExists_shouldReturnResponse() {
        Product product = new Product("PRD-1", "Widget", "SKU-001", "SUP-001", 1, OffsetDateTime.now());
        when(productRepository.findById("PRD-1")).thenReturn(Optional.of(product));

        ProductResponse response = productService.getById("PRD-1");

        assertThat(response.id()).isEqualTo("PRD-1");
        assertThat(response.name()).isEqualTo("Widget");
        assertThat(response.supplierId()).isEqualTo("SUP-001");
    }

    @Test
    void getById_whenMissing_shouldThrowProductNotFound() {
        when(productRepository.findById("MISSING")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getById("MISSING"))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("MISSING");
    }

    @Test
    void create_whenValid_shouldPersistAndPublishEvent() {
        when(productRepository.persist(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        ProductResponse created = productService.create(ProductRequest.builder()
                .name("Gear")
                .sku("SKU-GEAR-01")
                .supplierId("SUP-001")
                .build());

        assertThat(created.id()).startsWith("PRD-");
        assertThat(created.name()).isEqualTo("Gear");
        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).persist(captor.capture());
        assertThat(captor.getValue().getSku()).isEqualTo("SKU-GEAR-01");
        verify(productEventProducer).sendProductCreated(any());
    }

    @Test
    void list_whenEmpty_shouldReturnEmptyList() {
        when(productRepository.findAll()).thenReturn(List.of());
        assertThat(productService.list()).isEmpty();
    }
}
