package com.quarkus.poc.product.service;

import com.quarkus.poc.product.domain.products.Product;
import com.quarkus.poc.product.dto.ProductCreatedEvent;
import com.quarkus.poc.product.dto.ProductRequest;
import com.quarkus.poc.product.dto.ProductResponse;
import com.quarkus.poc.product.exception.ProductNotFoundException;
import com.quarkus.poc.product.kafka.ProductEventProducer;
import com.quarkus.poc.product.persistence.ProductRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

/**
 * Synchronous product CRUD against the products datasource.
 */
@ApplicationScoped
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductEventProducer productEventProducer;

    @Inject
    public ProductService(ProductRepository productRepository, ProductEventProducer productEventProducer) {
        this.productRepository = productRepository;
        this.productEventProducer = productEventProducer;
    }

    public ProductResponse getById(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        return toResponse(product);
    }

    public List<ProductResponse> list() {
        return productRepository.findAll().stream().map(this::toResponse).toList();
    }

    public ProductResponse create(ProductRequest request) {
        String id = "PRD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Product product = new Product(
                id,
                request.name(),
                request.sku(),
                request.supplierId(),
                1,
                OffsetDateTime.now(ZoneOffset.UTC));
        productRepository.persist(product);
        productEventProducer.sendProductCreated(ProductCreatedEvent.builder()
                .productId(id)
                .name(request.name())
                .supplierId(request.supplierId())
                .eventType("PRODUCT_CREATED")
                .build());
        return toResponse(product);
    }

    private ProductResponse toResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .sku(product.getSku())
                .supplierId(product.getSupplierId())
                .status(product.getStatus())
                .build();
    }
}
