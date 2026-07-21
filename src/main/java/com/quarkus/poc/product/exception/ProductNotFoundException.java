package com.quarkus.poc.product.exception;

/**
 * Thrown when a product id is not found.
 */
public final class ProductNotFoundException extends NotFoundException {

    public ProductNotFoundException(String id) {
        super("Product", id, "Product not found: " + id);
    }
}
