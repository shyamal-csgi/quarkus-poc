package component.com.quarkus.poc.product.db;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import io.smallrye.reactive.messaging.memory.InMemoryConnector;
import java.util.HashMap;
import java.util.Map;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Starts two Postgres containers and wires Quarkus named datasources.
 */
public class PostgresTestResource implements QuarkusTestResourceLifecycleManager {

    private static final DockerImageName IMAGE = DockerImageName.parse("postgres:16-alpine");

    private PostgreSQLContainer<?> productsDb;
    private PostgreSQLContainer<?> suppliersDb;

    @Override
    public Map<String, String> start() {
        productsDb = new PostgreSQLContainer<>(IMAGE)
                .withDatabaseName("qp_products")
                .withUsername("postgres")
                .withPassword("postgres");
        suppliersDb = new PostgreSQLContainer<>(IMAGE)
                .withDatabaseName("qp_suppliers")
                .withUsername("postgres")
                .withPassword("postgres");
        productsDb.start();
        suppliersDb.start();

        Map<String, String> props = new HashMap<>();
        props.put("quarkus.datasource.products.jdbc.url", productsDb.getJdbcUrl());
        props.put("quarkus.datasource.products.username", productsDb.getUsername());
        props.put("quarkus.datasource.products.password", productsDb.getPassword());
        props.put("quarkus.datasource.suppliers.jdbc.url", suppliersDb.getJdbcUrl());
        props.put("quarkus.datasource.suppliers.username", suppliersDb.getUsername());
        props.put("quarkus.datasource.suppliers.password", suppliersDb.getPassword());
        props.put("quarkus.flyway.products.migrate-at-start", "true");
        props.put("quarkus.flyway.suppliers.migrate-at-start", "true");
        props.put("quarkus.kafka.devservices.enabled", "false");
        props.put("quarkus.datasource.devservices.enabled", "false");
        props.putAll(InMemoryConnector.switchIncomingChannelsToInMemory("stock-events"));
        props.putAll(InMemoryConnector.switchOutgoingChannelsToInMemory("product-events"));
        return props;
    }

    @Override
    public void stop() {
        InMemoryConnector.clear();
        if (productsDb != null) {
            productsDb.stop();
        }
        if (suppliersDb != null) {
            suppliersDb.stop();
        }
    }
}
