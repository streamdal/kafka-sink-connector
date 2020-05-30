package sh.batch.kafka;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigException;
import org.apache.kafka.common.utils.AppInfoParser;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.sink.SinkConnector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BatchSinkConnector extends SinkConnector {
    protected Map<String, String> configProperties;

    @Override
    public void start(Map<String, String> props) {
        try {
            configProperties = props;
            new BatchSinkConnectorConfig(props);
        } catch (ConfigException e) {
            throw new ConnectException("Couldn't start BatchSinkConnector due to configuration error",e);
        }
    }

    @Override
    public Class<? extends Task> taskClass() {
        return BatchSinkTask.class;
    }

    @Override
    public List<Map<String, String>> taskConfigs(int maxTasks) {
        List<Map<String, String>> taskConfigs = new ArrayList<>();
        Map<String, String> taskProps = new HashMap<>(configProperties);

        taskProps.putAll(configProperties);
        for (int i = 0; i < maxTasks; i++) {
            taskConfigs.add(taskProps);
        }
        return taskConfigs;
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
