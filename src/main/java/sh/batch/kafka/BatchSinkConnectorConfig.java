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

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

public class BatchSinkConnectorConfig extends AbstractConfig {
    public static final String TOKEN = "batch.token";
    private static final String TOKEN_DOC = "Collection key to be used when authenticating against the batch.sh cloud collector";

    public static final String COLLECTOR_ADDRESS = "batch.collector";
    private static final String COLLECT_ADDRESS_DOC = "'host:port' of the batch.sh collector service (default: kafka-sink-collector.batch.sh:9000)";

    public BatchSinkConnectorConfig(Map originals) {
        super(configDef(), originals);
    }

    public static ConfigDef configDef() {
        return new ConfigDef()
                .define(TOKEN,
                        ConfigDef.Type.STRING,
                        ConfigDef.NO_DEFAULT_VALUE,
                        new ConfigDef.NonEmptyString(),
                        ConfigDef.Importance.HIGH,
                        TOKEN_DOC)

                .define(COLLECTOR_ADDRESS,
                        ConfigDef.Type.STRING,
                        "kafka-sink-collector.dev.batch.sh:9000",
                        new CollectorURIValidator(),
                        ConfigDef.Importance.HIGH,
                        COLLECT_ADDRESS_DOC);
    }

    private static class CollectorURIValidator implements ConfigDef.Validator {
        @Override
        public void ensureValid(String key, Object value) {
            String s = (String) value;
            try {
                if (!s.contains("://")) {
                    s = "my://" + s;
                }

                URI uri = new URI(s.trim());
                if (uri.getHost() == null || uri.getPort() == -1) {
                    throw new ConfigException(key, value,
                            "collector address must be valid URI with host and port");
                }
            } catch (URISyntaxException e) {
                throw new ConfigException(key, value, e.getMessage());
            }
        }

        @Override
        public String toString() {
            return "valid URI with host and port";
        }
    }
}
