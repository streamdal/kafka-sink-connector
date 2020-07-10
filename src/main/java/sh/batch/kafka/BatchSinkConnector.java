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

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigException;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.sink.SinkConnector;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.*;
import java.lang.management.ManagementFactory;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;

public class BatchSinkConnector extends SinkConnector {
    private static final Logger log = LoggerFactory.getLogger(BatchSinkConnector.class);
    protected Map<String, String> configProperties;

    protected ScheduledExecutorService executor;

    // CAS map used by the task runners to update the health check thread on their errors
    protected static final ConcurrentMap<String, String> taskErrors = new ConcurrentHashMap<>();

    final String CONNECTOR_MBEANS = "kafka.connect:type=*,connector=BatchSinkConnector,*";
    private ObjectName MBEAN_FILTER;

    @Override
    public void start(Map<String, String> props) {
        try {
            // Right away override the key and value converters since our connector should always serve
            // as a passthrough to the collector service where data conversion is handled
            props.put("key.converter", "org.apache.kafka.connect.converters.ByteArrayConverter");
            props.put("value.converter", "org.apache.kafka.connect.converters.ByteArrayConverter");

            configProperties = props;
            new BatchSinkConnectorConfig(props);
        } catch (ConfigException e) {
            throw new ConnectException("Couldn't start BatchSinkConnector due to configuration error",e);
        }

        // health check / metrics reporter
        executor = Executors.newScheduledThreadPool(1);
        int initialDelay = 3;
        int period = 120;

        try {
            MBEAN_FILTER = new ObjectName(CONNECTOR_MBEANS);
        } catch (MalformedObjectNameException e) {
            throw new ConnectException("Could not start BatchSinkConnector due to error with metrics reporter");
        }

        Runnable reporter = this::reportMetrics;

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
            // set task ID for error reporting
            taskProps.put("task_id", "BatchSinkConnector-" + i);
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

    private void reportMetrics() {
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

        JSONObject metrics = new JSONObject();
        JSONObject stats = new JSONObject();

        Set<ObjectInstance> instances = mbs.queryMBeans(MBEAN_FILTER, null);

        for (ObjectInstance instance : instances) {
            MBeanInfo info;

            try {
                info = mbs.getMBeanInfo(instance.getObjectName());
            } catch (Throwable e) {
                log.error("error getting mbean", e);
                continue;
            }

            MBeanAttributeInfo[] attrs = info.getAttributes();

            JSONArray beanAttrs = new JSONArray();

            for (MBeanAttributeInfo attr : attrs) {
                JSONObject a = new JSONObject();

                // Some MBean attribute values the JSON encoder does not like such as NaN
                // For now just quietly omit any values that are not valid JSON
                // In general all other errors are logged but dropped so our thread loop stays alive
                try {
                    Object attrObj = mbs.getAttribute(instance.getObjectName(), attr.getName());
                    a.put("value", attrObj);
                } catch (JSONException e) {
                    continue;
                } catch (Throwable e) {
                    log.error("error adding metric value", e);
                    continue;
                }

                a.put("name", attr.getName());
                a.put("description", attr.getDescription());

                beanAttrs.put(a);
            }

            stats.put(instance.getObjectName().toString(), beanAttrs);
        }

        metrics.put("timestamp", Instant.now().getEpochSecond());
        metrics.put("stats", stats);

        // set errors to a snapshot view of our concurrent map at this point in time then immediately clear the map
        // so new entries can begin populating for the next metrics reporter run
        metrics.put("errors", Collections.unmodifiableMap(taskErrors));
        taskErrors.clear();

        // DEBUG
        // log.info(metrics.toString(1));


        // send off heartbeat with metrics data
        HttpPut put = new HttpPut("https://connector-metrics.dev.batch.sh:8787/v1/heartbeat");
        put.addHeader("X-Batch-Collection-ID", configProperties.get(BatchSinkConnectorConfig.TOKEN));

        try {
            put.setEntity(new StringEntity(metrics.toString()));
        } catch (Throwable e) {
            log.error("error setting JSON heartbeat payload", e);
        }

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(put)) {

            if (response.getStatusLine().getStatusCode() != 200) {
                log.error("error while pushing heartbeat: {}", EntityUtils.toString(response.getEntity()));
            }
        } catch (Throwable e) {
            log.error("error pushing heartbeat", e);
        }

    }

    public static void putTaskError(String taskID, String error) {
        taskErrors.put(String.format("%s-%d", taskID, Instant.now().getEpochSecond()), error);
    }
}
