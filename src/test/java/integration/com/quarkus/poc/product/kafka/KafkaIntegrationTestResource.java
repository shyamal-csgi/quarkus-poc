package integration.com.quarkus.poc.product.kafka;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import java.util.HashMap;
import java.util.Map;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Testcontainers Kafka for integration tests (real smallrye-kafka connector).
 */
public class KafkaIntegrationTestResource implements QuarkusTestResourceLifecycleManager {

    private KafkaContainer kafka;

    @Override
    public Map<String, String> start() {
        kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.6.1"));
        kafka.start();
        Map<String, String> props = new HashMap<>();
        props.put("kafka.bootstrap.servers", kafka.getBootstrapServers());
        props.put("mp.messaging.outgoing.product-events.connector", "smallrye-kafka");
        props.put("mp.messaging.incoming.stock-events.connector", "smallrye-kafka");
        props.put("mp.messaging.outgoing.product-events.bootstrap.servers", kafka.getBootstrapServers());
        props.put("mp.messaging.incoming.stock-events.bootstrap.servers", kafka.getBootstrapServers());
        return props;
    }

    @Override
    public void stop() {
        if (kafka != null) {
            kafka.stop();
        }
    }
}
