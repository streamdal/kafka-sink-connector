package sh.batch.kafka;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

public class BatchSinkConnectorConfig extends AbstractConfig {
    public static final String TOKEN_CONFIG = "batch.token";
    public static final String COLLECTOR_ADDRESS_CONFIG = "batch.collector";

    public BatchSinkConnectorConfig(Map originals) {
        super(configDef(), originals);
    }

    protected static ConfigDef configDef() {
        return new ConfigDef()
                .define(TOKEN_CONFIG,
                        ConfigDef.Type.STRING,
                        null,
                        new ConfigDef.NonEmptyString(),
                        ConfigDef.Importance.HIGH,
                        "Collection key to be used when authenticating against the batch.sh cloud collector")
                .define(COLLECTOR_ADDRESS_CONFIG,
                        ConfigDef.Type.STRING,
                        "kafka-sink-collector.dev.batch.sh:9000",
                        (name, value) -> {
                            String strVal = value.toString();
                            try {
                                new URI(strVal);
                            } catch (URISyntaxException e) {
                                throw new ConfigException(e.getMessage());

                            }
                        },
                        ConfigDef.Importance.HIGH,
                        "'host:port' of the batch.sh collector service (default: kafka-sink-collector.batch.sh:9000)");
    }
}
