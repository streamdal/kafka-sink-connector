package sh.batch.kafka;

/*-
 * #%L
 * sink-connector
 * %%
 * Copyright (C) 2020 Batch.sh
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import org.apache.kafka.connect.errors.ConnectException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class BatchSinkConnectorTest {

    private Map<String, String> inputs = new HashMap<>();
    private BatchSinkConnector sc;

    @BeforeEach
    void beforeEach() {
        sc = new BatchSinkConnector();
        inputs.put("batch.token", "foobar");
    }

    @AfterEach
    void afterEach() {
        inputs.clear();
    }

    @Test
    void testStart() {
        sc.start(inputs);

        assertEquals( "foobar",
                sc.configProperties.get(BatchSinkConnectorConfig.TOKEN),
                "input and configured token should be the same");
    }

    @Test
    void shouldNotStartIfConfigInvalid() {
        inputs.clear();

        Exception ex = assertThrows(ConnectException.class, () -> {
            sc.start(inputs);
        });

        assertTrue(ex.getMessage().contains("Couldn't start BatchSinkConnector due to configuration error"));
    }

    @Test
    void taskClass() {
        assertSame(BatchSinkTask.class, sc.taskClass());
    }

    @Test
    void taskConfigs() {
        int numTasks = 5;

        // start the sink collector with some inputs
        // it should output a map with those inputs as configs for each task workers * numTasks
        inputs.put("foo", "bar");
        sc.start(inputs);

        List<Map<String,String>> tconfs = sc.taskConfigs(numTasks);

        assertEquals(numTasks, tconfs.size());
        assertEquals("bar", tconfs.get(0).get("foo"));

        // all task configs should have the key and value converters set by us
        for (Map<String, String> config : tconfs) {
            assertEquals("org.apache.kafka.connect.converters.ByteArrayConverter", config.get("key.converter"));
            assertEquals("org.apache.kafka.connect.converters.ByteArrayConverter", config.get("value.converter"));
        }
    }
}
