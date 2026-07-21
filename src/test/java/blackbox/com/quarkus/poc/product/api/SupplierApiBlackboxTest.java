package blackbox.com.quarkus.poc.product.api;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

import com.atlassian.oai.validator.restassured.OpenApiValidationFilter;
import component.com.quarkus.poc.product.db.PostgresTestResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

@QuarkusTest
@QuarkusTestResource(PostgresTestResource.class)
class SupplierApiBlackboxTest {

    private static final OpenApiValidationFilter OPENAPI =
            new OpenApiValidationFilter("api/openapi.yaml");

    @Test
    void getById_whenSeeded_shouldReturn200() {
        given()
                .filter(OPENAPI)
                .pathParam("id", "SUP-001")
                .when()
                .get("/api/v1/suppliers/{id}")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", is("SUP-001"))
                .body("name", is("Acme Supplies"));
    }

    @Test
    void getById_whenMissing_shouldReturn404() {
        given()
                .filter(OPENAPI)
                .pathParam("id", "SUP-NONE")
                .when()
                .get("/api/v1/suppliers/{id}")
                .then()
                .statusCode(404)
                .body("code", is("404"));
    }
}
