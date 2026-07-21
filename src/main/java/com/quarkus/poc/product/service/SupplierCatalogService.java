package com.quarkus.poc.product.service;

import com.quarkus.poc.product.domain.suppliers.Supplier;
import com.quarkus.poc.product.dto.SupplierResponse;
import com.quarkus.poc.product.exception.SupplierNotFoundException;
import com.quarkus.poc.product.persistence.SupplierRepository;
import io.quarkus.cache.CacheResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;

/**
 * Cached supplier catalog lookups against the suppliers datasource.
 */
@ApplicationScoped
public class SupplierCatalogService {

    private final SupplierRepository supplierRepository;

    @Inject
    public SupplierCatalogService(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    @CacheResult(cacheName = "suppliers")
    public SupplierResponse getById(String id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new SupplierNotFoundException(id));
        return toResponse(supplier);
    }

    public List<SupplierResponse> list() {
        return supplierRepository.findAll().stream().map(this::toResponse).toList();
    }

    private SupplierResponse toResponse(Supplier supplier) {
        return SupplierResponse.builder()
                .id(supplier.getId())
                .name(supplier.getName())
                .city(supplier.getCity())
                .country(supplier.getCountry())
                .status(supplier.getStatus())
                .build();
    }
}
