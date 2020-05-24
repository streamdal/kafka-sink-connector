package sh.batch.kafka;


import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.util.HashMap;

class BatchSinkConnectorTest {

    @Test
    void testStart() throws NoSuchFieldException, IllegalAccessException {
        BatchSinkConnector sc = new BatchSinkConnector();
        HashMap<String, String> inputs = new HashMap<>();

        inputs.put(BatchSinkConnector.LICENSE_CONFIG, "foobar");

        sc.start(inputs);

        Field license = sc.getClass().getDeclaredField("licenseKey");
        Field collectorURL = sc.getClass().getDeclaredField("collectorURL");

        license.setAccessible(true);
        collectorURL.setAccessible(true);

        String licenseStr = license.get(sc).toString();
        String collectorURLStr = collectorURL.get(sc).toString();

        assertEquals( "foobar", licenseStr, "input and configured license should be the same");
        assertEquals("https://collector.batch.sh", collectorURLStr, "input and configured collectorURL should be the same");
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