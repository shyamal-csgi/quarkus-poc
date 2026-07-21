package com.quarkus.poc.product.exception;

import com.quarkus.poc.product.dto.ErrorResponse;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Maps {@link NotFoundException} subtypes to HTTP 404 with a standard body.
 */
@Provider
public class NotFoundExceptionMapper implements ExceptionMapper<NotFoundException> {

    @Override
    public Response toResponse(NotFoundException exception) {
        String reason = switch (exception) {
            case ProductNotFoundException e -> "Product not found: " + e.entityId();
            case SupplierNotFoundException e -> "Supplier not found: " + e.entityId();
        };
        ErrorResponse body = ErrorResponse.builder()
                .code("404")
                .message("Entity not found")
                .reason(reason)
                .build();
        return Response.status(Response.Status.NOT_FOUND).entity(body).build();
    }
}
