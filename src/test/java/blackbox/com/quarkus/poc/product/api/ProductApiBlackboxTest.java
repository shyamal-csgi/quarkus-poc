package blackbox.com.quarkus.poc.product.api;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;

import blackbox.com.quarkus.poc.product.wiremock.WireMockTestResource;
import com.atlassian.oai.validator.restassured.OpenApiValidationFilter;
import component.com.quarkus.poc.product.db.PostgresTestResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

@QuarkusTest
@QuarkusTestResource(PostgresTestResource.class)
@QuarkusTestResource(WireMockTestResource.class)
class ProductApiBlackboxTest {

    private static final OpenApiValidationFilter OPENAPI =
            new OpenApiValidationFilter("api/openapi.yaml");

    @Test
    void getById_whenMissing_shouldReturn404MatchingOpenApi() {
        given()
                .filter(OPENAPI)
                .pathParam("id", "NONEXISTENT")
                .when()
                .get("/api/v1/products/{id}")
                .then()
                .statusCode(404)
                .contentType(ContentType.JSON)
                .body("code", is("404"))
                .body("message", containsString("Entity not found"))
                .body("reason", containsString("Product not found"));
    }

    @Test
    void createAndGet_whenValid_shouldReturn201Then200() {
        String id = given()
                .filter(OPENAPI)
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "name": "Blackbox Widget",
                          "sku": "SKU-BB-01",
                          "supplierId": "SUP-001"
                        }
                        """)
                .when()
                .post("/api/v1/products")
                .then()
                .statusCode(201)
                .body("name", is("Blackbox Widget"))
                .body("id", startsWith("PRD-"))
                .extract()
                .path("id");

        given()
                .filter(OPENAPI)
                .pathParam("id", id)
                .when()
                .get("/api/v1/products/{id}")
                .then()
                .statusCode(200)
                .body("id", is(id))
                .body("sku", is("SKU-BB-01"));
    }

    @Test
    void getProfile_whenProductExists_shouldCombineSupplierAndEnrichment() {
        String id = given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "name": "Profile Part",
                          "sku": "SKU-PROF-01",
                          "supplierId": "SUP-001"
                        }
                        """)
                .when()
                .post("/api/v1/products")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        given()
                .filter(OPENAPI)
                .pathParam("id", id)
                .when()
                .get("/api/v1/products/{id}/profile")
                .then()
                .statusCode(200)
                .body("supplierName", is("Acme Supplies"))
                .body("supplierCity", is("Sydney"))
                .body("enrichmentNote", containsString("WireMock"));
    }
}
