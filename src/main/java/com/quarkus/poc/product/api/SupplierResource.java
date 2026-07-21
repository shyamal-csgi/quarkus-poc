package com.quarkus.poc.product.api;

import com.quarkus.poc.product.dto.SupplierResponse;
import com.quarkus.poc.product.service.SupplierCatalogService;
import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * Supplier catalog REST API (cached reference reads).
 */
@Path("/api/v1/suppliers")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Suppliers")
public class SupplierResource {

    private final SupplierCatalogService supplierCatalogService;

    @Inject
    public SupplierResource(SupplierCatalogService supplierCatalogService) {
        this.supplierCatalogService = supplierCatalogService;
    }

    @GET
    @RunOnVirtualThread
    @Operation(summary = "List suppliers")
    public List<SupplierResponse> list() {
        return supplierCatalogService.list();
    }

    @GET
    @Path("{id}")
    @RunOnVirtualThread
    @Operation(summary = "Get supplier by id (cached)")
    public SupplierResponse getById(@PathParam("id") String id) {
        return supplierCatalogService.getById(id);
    }
}
