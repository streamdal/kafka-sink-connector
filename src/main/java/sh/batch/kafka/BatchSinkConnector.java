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

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigException;
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
            // Right away override the key and value converters since our connector should always serve
            // as a pass through up the collector service where data conversion is handled
            props.put("key.converter", "ByteArrayConverter");
            props.put("value.converter", "ByteArrayConverter");

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
        return BatchSinkConnector.class.getPackage().getImplementationVersion();
    }
}
