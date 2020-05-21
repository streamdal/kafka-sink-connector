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
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicStatusLine;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.management.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.apache.http.HttpVersion.HTTP_1_1;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@ExtendWith(MockitoExtension.class)
class BatchSinkMetricsReporterTest {

    private final Map<String, String> inputs = new HashMap<>();
    BatchSinkMetricsReporter reporter;

    @Mock CloseableHttpClient client;

    @BeforeEach
    void beforeEach() throws MalformedObjectNameException {
        inputs.put("batch.token", "foobar");
        inputs.put("batch.metrics", "https://metrics.url");
        reporter = new BatchSinkMetricsReporter(inputs, client);
    }

    @AfterEach
    void afterEach() {
        inputs.clear();
        BatchSinkMetricsReporter.taskErrors.clear();
    }

    @Test
    void shouldReportMetrics(@Mock MBeanServer mbs, @Mock ObjectInstance instance, @Mock MBeanInfo mBeanInfo, @Mock MBeanAttributeInfo attr, @Mock CloseableHttpResponse response)
            throws IOException, IntrospectionException, InstanceNotFoundException, ReflectionException, AttributeNotFoundException, MBeanException, MalformedObjectNameException {
        reporter.mbs = mbs;

        ObjectName objName = new ObjectName("kafka.connect:type=connector-metrics,connector=BatchSinkConnector");

        Set<ObjectInstance> instances = new HashSet<>();
        instances.add(instance);

        MBeanAttributeInfo[] attrs = new MBeanAttributeInfo[1];
        attrs[0] = attr;

        // on a successful run this gets cleared
        BatchSinkMetricsReporter.taskErrors.put("foo", "bar");

        when(mbs.queryMBeans(any(ObjectName.class), eq(null))).thenReturn(instances);
        when(instance.getObjectName()).thenReturn(objName);
        when(mbs.getMBeanInfo(objName)).thenReturn(mBeanInfo);
        when(mBeanInfo.getAttributes()).thenReturn(attrs);
        when(attr.getName()).thenReturn("bar");
        when(mbs.getAttribute(objName, "bar")).thenReturn(1);
        when(client.execute(any(HttpUriRequest.class))).thenReturn(response);

        assertDoesNotThrow(reporter::run);

        verify(mbs, times(1)).queryMBeans(any(ObjectName.class), eq(null));
        verify(instance, atLeast(2)).getObjectName();
        verify(mbs, times(1)).getMBeanInfo(objName);
        verify(mbs, times(1)).getAttribute(objName, "bar");
        verify(client, times(1)).execute(any(HttpPut.class));
        verify(response, times(0)).getEntity(); // we only grab the response body on error

        assertTrue(BatchSinkMetricsReporter.taskErrors.isEmpty());
    }

    @Test
    void shouldFailWhenNoMBeans(@Mock MBeanServer mbs, @Mock ObjectInstance instance)
            throws IntrospectionException, InstanceNotFoundException, ReflectionException, AttributeNotFoundException, MBeanException, MalformedObjectNameException, IOException {
        reporter.mbs = mbs;

        ObjectName objName = new ObjectName("kafka.connect:type=connector-metrics,connector=BatchSinkConnector");

        Set<ObjectInstance> instances = new HashSet<>();
        instances.add(instance);

        BatchSinkMetricsReporter.taskErrors.put("foo", "bar");

        when(mbs.queryMBeans(any(ObjectName.class), eq(null))).thenReturn(instances);
        when(instance.getObjectName()).thenReturn(objName);
        when(mbs.getMBeanInfo(objName)).thenThrow(InstanceNotFoundException.class);

        assertDoesNotThrow(reporter::run);

        // unfortunately due to the lack of side effects caused by the Runnable the easiest way to test something happened
        // is verifying the correct number of invocations of various methods
        verify(mbs, times(1)).queryMBeans(any(ObjectName.class), eq(null));
        verify(mbs, times(1)).getMBeanInfo(objName);
        verify(mbs, times(0)).getAttribute(objName, "bar");
        verify(client, times(0)).execute(any(HttpPut.class));

        // failure cases should not clear the task errors map so we can hopefully send them in the next iteration
        assertFalse(BatchSinkMetricsReporter.taskErrors.isEmpty());
    }

    @Test
    void shouldFailWhenNoMBeanAttributes(@Mock MBeanServer mbs, @Mock ObjectInstance instance, @Mock MBeanInfo mBeanInfo, @Mock MBeanAttributeInfo attr, @Mock CloseableHttpResponse response)
            throws IOException, IntrospectionException, InstanceNotFoundException, ReflectionException, AttributeNotFoundException, MBeanException, MalformedObjectNameException {
        reporter.mbs = mbs;

        ObjectName objName = new ObjectName("kafka.connect:type=connector-metrics,connector=BatchSinkConnector");

        Set<ObjectInstance> instances = new HashSet<>();
        instances.add(instance);

        MBeanAttributeInfo[] attrs = new MBeanAttributeInfo[1];
        attrs[0] = attr;

        BatchSinkMetricsReporter.taskErrors.put("foo", "bar");

        when(mbs.queryMBeans(any(ObjectName.class), eq(null))).thenReturn(instances);
        when(instance.getObjectName()).thenReturn(objName);
        when(mbs.getMBeanInfo(objName)).thenReturn(mBeanInfo);
        when(mBeanInfo.getAttributes()).thenReturn(attrs);
        when(attr.getName()).thenReturn("bar");
        when(mbs.getAttribute(objName, "bar")).thenThrow(MBeanException.class);

        assertDoesNotThrow(reporter::run);

        verify(mbs, times(1)).queryMBeans(any(ObjectName.class), eq(null));
        verify(instance, atLeast(2)).getObjectName();
        verify(mbs, times(1)).getMBeanInfo(objName);
        verify(mbs, times(1)).getAttribute(objName, "bar");
        verify(client, times(0)).execute(any(HttpPut.class));

        assertFalse(BatchSinkMetricsReporter.taskErrors.isEmpty());
    }

    @Test
    void shouldFailWhenHttpRequestFails(@Mock MBeanServer mbs, @Mock ObjectInstance instance, @Mock MBeanInfo mBeanInfo, @Mock MBeanAttributeInfo attr, @Mock CloseableHttpResponse response)
            throws IOException, IntrospectionException, InstanceNotFoundException, ReflectionException, AttributeNotFoundException, MBeanException, MalformedObjectNameException {
        reporter.mbs = mbs;

        ObjectName objName = new ObjectName("kafka.connect:type=connector-metrics,connector=BatchSinkConnector");

        Set<ObjectInstance> instances = new HashSet<>();
        instances.add(instance);

        MBeanAttributeInfo[] attrs = new MBeanAttributeInfo[1];
        attrs[0] = attr;

        BatchSinkMetricsReporter.taskErrors.put("foo", "bar");

        when(mbs.queryMBeans(any(ObjectName.class), eq(null))).thenReturn(instances);
        when(instance.getObjectName()).thenReturn(objName);
        when(mbs.getMBeanInfo(objName)).thenReturn(mBeanInfo);
        when(mBeanInfo.getAttributes()).thenReturn(attrs);
        when(attr.getName()).thenReturn("bar");
        when(mbs.getAttribute(objName, "bar")).thenReturn(1);
        when(client.execute(any(HttpUriRequest.class))).thenReturn(response);
        when(response.getStatusLine()).thenReturn(new BasicStatusLine(HTTP_1_1, 500, "error"));

        assertDoesNotThrow(reporter::run);

        verify(mbs, times(1)).queryMBeans(any(ObjectName.class), eq(null));
        verify(instance, atLeast(2)).getObjectName();
        verify(mbs, times(1)).getMBeanInfo(objName);
        verify(mbs, times(1)).getAttribute(objName, "bar");
        verify(client, times(1)).execute(any(HttpPut.class));
        verify(response, times(1)).getEntity(); // we only grab the response body on error

        assertTrue(BatchSinkMetricsReporter.taskErrors.isEmpty());
    }
}

