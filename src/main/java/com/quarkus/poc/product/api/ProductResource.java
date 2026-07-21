package com.quarkus.poc.product.api;

import com.quarkus.poc.product.dto.ProductProfileResponse;
import com.quarkus.poc.product.dto.ProductRequest;
import com.quarkus.poc.product.dto.ProductResponse;
import com.quarkus.poc.product.service.ProductProfileService;
import com.quarkus.poc.product.service.ProductService;
import io.smallrye.common.annotation.RunOnVirtualThread;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * Product REST API: sync CRUD on virtual threads + async profile.
 */
@Path("/api/v1/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Products")
public class ProductResource {

    private final ProductService productService;
    private final ProductProfileService productProfileService;

    @Inject
    public ProductResource(ProductService productService, ProductProfileService productProfileService) {
        this.productService = productService;
        this.productProfileService = productProfileService;
    }

    @GET
    @RunOnVirtualThread
    @Operation(summary = "List products")
    public List<ProductResponse> list() {
        return productService.list();
    }

    @GET
    @Path("{id}")
    @RunOnVirtualThread
    @Operation(summary = "Get product by id")
    public ProductResponse getById(@PathParam("id") String id) {
        return productService.getById(id);
    }

    @POST
    @RunOnVirtualThread
    @Operation(summary = "Create product")
    public Response create(@Valid ProductRequest request) {
        ProductResponse created = productService.create(request);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @GET
    @Path("{id}/profile")
    @Operation(summary = "Get async product profile (DB + supplier + enrichment)")
    public Uni<ProductProfileResponse> getProfile(@PathParam("id") String id) {
        return productProfileService.getProfile(id);
    }
}
