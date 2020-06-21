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


import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BatchSinkConnectorTest {

    @Test
    void testStart() {
        BatchSinkConnector sc = new BatchSinkConnector();
        HashMap<String, String> inputs = new HashMap<>();

        inputs.put(BatchSinkConnectorConfig.TOKEN, "foobar");

        sc.start(inputs);

        assertEquals( "foobar",
                sc.configProperties.get(BatchSinkConnectorConfig.TOKEN),
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
