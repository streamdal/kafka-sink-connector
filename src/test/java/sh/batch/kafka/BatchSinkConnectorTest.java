package sh.batch.kafka;


import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BatchSinkConnectorTest {

    @Test
    void testStart() {
        BatchSinkConnector sc = new BatchSinkConnector();
        HashMap<String, String> inputs = new HashMap<>();

        inputs.put(BatchSinkConnectorConfig.TOKEN_CONFIG, "foobar");

        sc.start(inputs);

        assertEquals( "foobar",
                sc.configProperties.get(BatchSinkConnectorConfig.TOKEN_CONFIG),
                "input and configured token should be the same");
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