package sh.batch.kafka;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

public class BatchSinkConnectorConfig extends AbstractConfig {
    public static final String LICENSE_CONFIG = "batch.license";
    public static final String BATCH_COLLECTOR_CONFIG = "batch.collector";

    public BatchSinkConnectorConfig(Map originals) {
        super(configDef(), originals);
    }

    protected static ConfigDef configDef() {
        return new ConfigDef()
                .define(LICENSE_CONFIG,
                        ConfigDef.Type.STRING,
                        null,
                        new ConfigDef.NonEmptyString(),
                        ConfigDef.Importance.HIGH,
                        "License key to be used when authenticating against batch cloud collector")
                .define(BATCH_COLLECTOR_CONFIG,
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
                        "'host:port' of the batch.sh collector service (default: grpc-collector.batch.sh:8080)");
    }
}
