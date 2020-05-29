package sh.batch.kafka;


import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BatchSinkConnectorTest {

    @Test
    void testStart() {
        BatchSinkConnector sc = new BatchSinkConnector();
        HashMap<String, String> inputs = new HashMap<>();

        inputs.put(BatchSinkConnectorConfig.LICENSE_CONFIG, "foobar");

        sc.start(inputs);

        assertEquals( "foobar",
                sc.connectorConfig.getString(BatchSinkConnectorConfig.LICENSE_CONFIG),
                "input and configured license should be the same");
        assertEquals("grpc-collector.batch.sh:8080",
                sc.connectorConfig.getString(BatchSinkConnectorConfig.BATCH_COLLECTOR_CONFIG),
                "input and configured collectorURL should be the same");
    }

    @Test
    void taskClass() {
    }

    @Test
    void taskConfigs() {
    }

    @Test
    void stop() {
    }

    @Test
    void config() {
    }

    @Test
    void version() {
    }
}