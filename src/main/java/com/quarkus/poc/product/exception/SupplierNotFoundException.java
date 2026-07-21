package com.quarkus.poc.product.exception;

/**
 * Thrown when a supplier id is not found.
 */
public final class SupplierNotFoundException extends NotFoundException {

    public SupplierNotFoundException(String id) {
        super("Supplier", id, "Supplier not found: " + id);
    }
}
