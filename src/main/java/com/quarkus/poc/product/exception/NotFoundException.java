package com.quarkus.poc.product.exception;

/**
 * Sealed hierarchy for domain not-found errors (Java 25 sealed types).
 */
public sealed abstract class NotFoundException extends RuntimeException
        permits ProductNotFoundException, SupplierNotFoundException {

    private final String entityType;
    private final String entityId;

    protected NotFoundException(String entityType, String entityId, String message) {
        super(message);
        this.entityType = entityType;
        this.entityId = entityId;
    }

    public String entityType() {
        return entityType;
    }

    public String entityId() {
        return entityId;
    }
}
