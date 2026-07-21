package blackbox.com.quarkus.poc.product.wiremock;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import java.util.Map;

/**
 * Starts an embedded WireMock for enrichment API stubs during blackbox tests.
 */
public class WireMockTestResource implements QuarkusTestResourceLifecycleManager {

    private WireMockServer server;

    @Override
    public Map<String, String> start() {
        server = new WireMockServer(WireMockConfiguration.options().dynamicPort());
        server.start();
        server.stubFor(com.github.tomakehurst.wiremock.client.WireMock.get(
                        com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching(
                                "/api/v1/enrichment/students/.*"))
                .willReturn(com.github.tomakehurst.wiremock.client.WireMock.aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                  "studentId": "dynamic",
                                  "note": "Enriched by WireMock",
                                  "source": "wiremock"
                                }
                                """)));
        return Map.of("quarkus.rest-client.enrichment-api.url", "http://localhost:" + server.port());
    }

    @Override
    public void stop() {
        if (server != null) {
            server.stop();
        }
    }
}
