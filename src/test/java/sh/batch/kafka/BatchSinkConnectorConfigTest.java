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

import org.apache.kafka.common.config.ConfigException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BatchSinkConnectorConfigTest {

    private Map<String, String> props = new HashMap<>();
    private BatchSinkConnectorConfig config;

    @BeforeEach
    public void beforeEach() {
        props.put("batch.token", "foobar"); // bare minimum
    }

    @AfterEach
    public void afterEach() {
        props.clear();
        config = null;
    }

    @Test
    void shouldCreateConfigWithMinimalInput() {
        config = new BatchSinkConnectorConfig(props);
        assertEquals("foobar", config.getString("batch.token"));
    }

    @Test
    void shouldErrorWhenLicenseKeyIsEmpty() {
        props.clear();

        Exception ex = assertThrows(ConfigException.class, () -> {
            config = new BatchSinkConnectorConfig(props);
        });

        assertEquals("Missing required configuration \"batch.token\" which has no default value.", ex.getMessage());
    }
}
