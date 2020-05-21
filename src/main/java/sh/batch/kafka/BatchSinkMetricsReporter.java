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
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.*;
import java.lang.management.ManagementFactory;
import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class BatchSinkMetricsReporter implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(BatchSinkMetricsReporter.class);

    // CAS map used by the task runners to update the health check thread on their errors
    protected static final ConcurrentMap<String, String> taskErrors = new ConcurrentHashMap<>();

    final String CONNECTOR_MBEANS = "kafka.connect:type=*,connector=%s,*";
    private final ObjectName MBEAN_FILTER;

    private final CloseableHttpClient httpClient;
    private final Map<String, String> configProperties;

    protected MBeanServer mbs;

    public BatchSinkMetricsReporter(Map<String, String> params, CloseableHttpClient client) throws MalformedObjectNameException {
        MBEAN_FILTER = new ObjectName(String.format(CONNECTOR_MBEANS, params.get(BatchSinkConnector.NAME)));

        this.httpClient = client;
        this.configProperties = params;

        this.mbs = ManagementFactory.getPlatformMBeanServer();
    }

    public void run() {
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

            JSONObject beanAttrs = new JSONObject();

            for (MBeanAttributeInfo attr : attrs) {
                // Some MBean attribute values the JSON encoder does not like such as NaN
                // For now just quietly omit any values that are not valid JSON
                // In general all other errors are logged but dropped so our thread loop stays alive
                try {
                    Object attrObj = mbs.getAttribute(instance.getObjectName(), attr.getName());
                    beanAttrs.put(attr.getName(), attrObj);
                } catch (JSONException e) {
                    log.debug("invalid value for json serialization", e);
                } catch (Throwable e) {
                    log.error("error adding metric value", e);
                }
            }

            if (!beanAttrs.isEmpty()) {
                stats.put(instance.getObjectName().toString(), beanAttrs);
            }
        }

        if (stats.isEmpty()) {
            log.error("no metrics gathered, skipping sending empty stats payload to metrics collector");
            return;
        }

        metrics.put("timestamp", Instant.now().getEpochSecond());
        metrics.put("stats", stats);

        // set errors as a snapshot of taskErrors then clear it for the next heartbeat
        metrics.put("errors", Collections.unmodifiableMap(taskErrors));
        taskErrors.clear();

        HttpPut put = new HttpPut(String.format("%s/v1/heartbeat", configProperties.get(BatchSinkConnectorConfig.METRICS_ADDRESS)));
        put.addHeader("Batch-Token", configProperties.get(BatchSinkConnectorConfig.TOKEN));

        try {
            put.setEntity(new StringEntity(metrics.toString()));
        } catch (Throwable e) {
            log.error("error setting JSON heartbeat payload", e);
            return;
        }

        try (CloseableHttpResponse response = httpClient.execute(put)) {
            if (response.getStatusLine().getStatusCode() != 200) {
                log.error("error while pushing heartbeat: {}", EntityUtils.toString(response.getEntity()));
            }
        } catch (Throwable e) {
            log.error("error pushing heartbeat", e);
        }
    }

    public static void reportError(String taskID, String error) {
        taskErrors.put(String.format("%s-%d", taskID, Instant.now().getEpochSecond()), error);
    }
}
