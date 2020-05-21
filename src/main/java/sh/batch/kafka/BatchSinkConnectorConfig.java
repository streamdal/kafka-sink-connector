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

import java.util.Map;

public class BatchSinkConnectorConfig extends AbstractConfig {
    public static final String TOKEN = "batch.token";
    private static final String TOKEN_DOC = "Collection key to be used when authenticating against the batch.sh cloud collector";

    // 'host:port' of the batch.sh collector service (default: kafka-sink-collector.batch.sh:9000)
    public static final String COLLECTOR_ADDRESS = "batch.collector";

    // default: false; if true the grpc collector client will use a plaintext connection
    public static final String DISABLE_COLLECTOR_TLS = "batch.collector.disable.tls";

    // 'proto://host:port' of the batch.sh connector metrics service (default: https://connector-metrics.batch.sh:8787)
    public static final String METRICS_ADDRESS = "batch.metrics";

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

                .defineInternal(DISABLE_COLLECTOR_TLS,
                        ConfigDef.Type.BOOLEAN,
                        false,
                        ConfigDef.Importance.LOW);
    }
}
