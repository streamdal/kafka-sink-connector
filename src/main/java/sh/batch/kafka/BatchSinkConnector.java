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

import org.apache.http.impl.client.HttpClients;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigException;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.sink.SinkConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.MalformedObjectNameException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BatchSinkConnector extends SinkConnector {
    private static final Logger log = LoggerFactory.getLogger(BatchSinkConnector.class);
    protected Map<String, String> configProperties;

    protected static final String NAME = "name";
    protected String connectorName;

    protected ScheduledExecutorService executor;

    @Override
    public void start(Map<String, String> props) {
        configProperties = props;

        try {
            new BatchSinkConnectorConfig(configProperties);
        } catch (ConfigException e) {
            throw new ConnectException("Couldn't start BatchSinkConnector due to configuration error",e);
        }

        connectorName = props.getOrDefault(NAME, "");
        if (connectorName.isEmpty()) {
            throw new ConfigException("Connector 'name' cannot be blank");
        }

        configProperties.putIfAbsent(NAME, connectorName);

        configProperties.put(BatchSinkConnectorConfig.COLLECTOR_ADDRESS, "kafka-sink-collector.batch.sh:9000");
        configProperties.put(BatchSinkConnectorConfig.METRICS_ADDRESS, "https://connector-metrics.batch.sh:8787");

        // health check / metrics reporter
        executor = Executors.newScheduledThreadPool(1);
        int initialDelay = 3;
        int period = 120;

        BatchSinkMetricsReporter reporter;
        try {
            reporter = new BatchSinkMetricsReporter(configProperties, HttpClients.createDefault());
        } catch (MalformedObjectNameException e) {
            throw new ConnectException("Could not start BatchSinkConnector due to error with metrics reporter");
        }

        executor.scheduleAtFixedRate(reporter, initialDelay, period, TimeUnit.SECONDS);
    }

    @Override
    public Class<? extends Task> taskClass() {
        return BatchSinkTask.class;
    }

    @Override
    public List<Map<String, String>> taskConfigs(int maxTasks) {
        List<Map<String, String>> taskConfigs = new ArrayList<>();

        for (int i = 0; i < maxTasks; i++) {
            Map<String, String> taskProps = new HashMap<>(configProperties);

            taskProps.put("task_id", connectorName + "-task-" + i);
            taskConfigs.add(taskProps);
        }
        return taskConfigs;
    }

    @Override
    public void stop() {
        try {
            log.info("shutting down health and metrics reporter");
            executor.shutdown();
            executor.awaitTermination(5, TimeUnit.SECONDS);
        }
        catch (InterruptedException e) {
            log.warn("metrics reporter interrupted");
        }
        finally {
            if (!executor.isTerminated()) {
                log.warn("canceling non-finished report");
            }
            executor.shutdownNow();
            log.info("metrics reporter shutdown finished");
        }
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
