package unit.com.quarkus.poc.product.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.quarkus.poc.product.dto.ErrorResponse;
import com.quarkus.poc.product.exception.NotFoundExceptionMapper;
import com.quarkus.poc.product.exception.ProductNotFoundException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

class NotFoundExceptionMapperTest {

    @Test
    void toResponse_whenProductNotFound_shouldReturn404Body() {
        NotFoundExceptionMapper mapper = new NotFoundExceptionMapper();

        Response response = mapper.toResponse(new ProductNotFoundException("PRD-9"));

        assertThat(response.getStatus()).isEqualTo(404);
        ErrorResponse body = (ErrorResponse) response.getEntity();
        assertThat(body.code()).isEqualTo("404");
        assertThat(body.message()).isEqualTo("Entity not found");
        assertThat(body.reason()).contains("PRD-9");
    }
}
