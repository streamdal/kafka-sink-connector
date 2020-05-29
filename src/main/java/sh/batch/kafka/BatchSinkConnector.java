package sh.batch.kafka;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.utils.AppInfoParser;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.sink.SinkConnector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BatchSinkConnector extends SinkConnector {

    protected BatchSinkConnectorConfig connectorConfig;

    @Override
    public void start(Map<String, String> props) {
        this.connectorConfig = new BatchSinkConnectorConfig(props);
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
            config.put(BatchSinkConnectorConfig.LICENSE_CONFIG, connectorConfig.getString(BatchSinkConnectorConfig.LICENSE_CONFIG));
            config.put(BatchSinkConnectorConfig.BATCH_COLLECTOR_CONFIG, connectorConfig.getString(BatchSinkConnectorConfig.BATCH_COLLECTOR_CONFIG));

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
        return BatchSinkConnectorConfig.configDef();
    }

    @Override
    public String version() {
        return AppInfoParser.getVersion();
    }
}
