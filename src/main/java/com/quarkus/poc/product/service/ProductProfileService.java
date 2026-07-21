package com.quarkus.poc.product.service;

import com.quarkus.poc.product.client.EnrichmentClient;
import com.quarkus.poc.product.config.ProductConfig;
import com.quarkus.poc.product.dto.EnrichmentResponse;
import com.quarkus.poc.product.dto.ProductProfileResponse;
import com.quarkus.poc.product.dto.ProductResponse;
import com.quarkus.poc.product.dto.SupplierResponse;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;

/**
 * Async profile assembly: product DB + cached supplier + resilient enrichment client.
 */
@ApplicationScoped
public class ProductProfileService {

    private final ProductService productService;
    private final SupplierCatalogService supplierCatalogService;
    private final EnrichmentClient enrichmentClient;
    private final ProductConfig productConfig;

    @Inject
    public ProductProfileService(
            ProductService productService,
            SupplierCatalogService supplierCatalogService,
            @RestClient EnrichmentClient enrichmentClient,
            ProductConfig productConfig) {
        this.productService = productService;
        this.supplierCatalogService = supplierCatalogService;
        this.enrichmentClient = enrichmentClient;
        this.productConfig = productConfig;
    }

    public Uni<ProductProfileResponse> getProfile(String productId) {
        return Uni.createFrom().item(() -> productService.getById(productId))
                .runSubscriptionOn(Infrastructure.getDefaultWorkerPool())
                .flatMap(product -> {
                    Uni<SupplierResponse> supplierUni = Uni.createFrom()
                            .item(() -> supplierCatalogService.getById(product.supplierId()))
                            .runSubscriptionOn(Infrastructure.getDefaultWorkerPool());
                    Uni<EnrichmentResponse> enrichmentUni = enrichmentUni(product);
                    return Uni.combine().all().unis(supplierUni, enrichmentUni)
                            .asTuple()
                            .map(tuple -> buildProfile(product, tuple.getItem1(), tuple.getItem2()));
                });
    }

    private Uni<EnrichmentResponse> enrichmentUni(ProductResponse product) {
        if (!productConfig.enrichment().enabled()) {
            return Uni.createFrom().item(EnrichmentResponse.builder()
                    .studentId(product.id())
                    .note("Enrichment disabled")
                    .source("disabled")
                    .build());
        }
        return enrichmentClient.enrich(product.id());
    }

    private ProductProfileResponse buildProfile(
            ProductResponse product, SupplierResponse supplier, EnrichmentResponse enrichment) {
        return ProductProfileResponse.builder()
                .id(product.id())
                .name(product.name())
                .sku(product.sku())
                .supplierId(product.supplierId())
                .supplierName(supplier.name())
                .supplierCity(supplier.city())
                .enrichmentNote(enrichment.note())
                .status(product.status())
                .build();
    }
}
