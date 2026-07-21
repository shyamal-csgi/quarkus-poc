package com.quarkus.poc.product.exception;

import com.quarkus.poc.product.dto.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

/**
 * Catch-all mapper for unexpected errors and validation failures.
 */
@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Exception> {

    private static final Logger LOG = Logger.getLogger(GlobalExceptionMapper.class);

    @Override
    public Response toResponse(Exception exception) {
        if (exception instanceof ConstraintViolationException cve) {
            ErrorResponse body = ErrorResponse.builder()
                    .code("400")
                    .message("Validation failed")
                    .reason(cve.getMessage())
                    .build();
            return Response.status(Response.Status.BAD_REQUEST).entity(body).build();
        }
        LOG.error("Unhandled exception", exception);
        ErrorResponse body = ErrorResponse.builder()
                .code("500")
                .message("Internal server error")
                .reason(exception.getMessage())
                .build();
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(body).build();
    }
}
