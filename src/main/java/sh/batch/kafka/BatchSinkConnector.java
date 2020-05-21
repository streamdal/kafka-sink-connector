package sh.batch.kafka;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigDef.Importance;
import org.apache.kafka.common.config.ConfigDef.Type;
import org.apache.kafka.common.utils.AppInfoParser;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.sink.SinkConnector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BatchSinkConnector extends SinkConnector {
    public static final String LICENSE_CONFIG = "license";
    public static final String COLLECTOR_URL_CONFIG = "collector";
    private static final ConfigDef CONFIG_DEF = new ConfigDef()
            .define(LICENSE_CONFIG, Type.STRING, null, Importance.HIGH, "License key to be used when authenticating against batch cloud collector")
            .define(COLLECTOR_URL_CONFIG, Type.STRING, "https://collector.batch.sh", Importance.HIGH, "URL of the batch.sh collector service");

    private String licenseKey;
    private String collectorURL;

    @Override
    public void start(Map<String, String> map) {
        AbstractConfig parsedConfig = new AbstractConfig(CONFIG_DEF, map);

        licenseKey = parsedConfig.getString(LICENSE_CONFIG);
        collectorURL = parsedConfig.getString(COLLECTOR_URL_CONFIG);
    }

    @Override
    public Class<? extends Task> taskClass() {
        return BatchSinkTask.class;
    }

    @Override
    public List<Map<String, String>> taskConfigs(int maxTasks) {
        ArrayList<Map<String, String>> configs = new ArrayList<>();
        for (int i = 0; i < maxTasks; i++) {
            Map<String, String> config = new HashMap<>();

            // create input param maps to pass along to tasks
            config.put(LICENSE_CONFIG, licenseKey);
            config.put(COLLECTOR_URL_CONFIG, collectorURL);

            configs.add(config);
        }
        return configs;
    }

    @Override
    public void stop() {
        // no-op
    }

    @Override
    public ConfigDef config() {
        return CONFIG_DEF;
    }

    @Override
    public String version() {
        return AppInfoParser.getVersion();
    }
}
