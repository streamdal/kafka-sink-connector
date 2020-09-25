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

import io.grpc.ManagedChannel;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.stub.StreamObserver;
import io.grpc.testing.GrpcCleanupRule;
import io.grpc.util.MutableHandlerRegistry;
import org.apache.kafka.common.record.TimestampType;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.sink.SinkRecord;
import org.junit.Rule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sh.batch.services.GRPCCollectorGrpc;
import sh.batch.services.KafkaSinkRecordRequest;
import sh.batch.services.KafkaSinkRecordResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

public class BatchSinkTaskTest {
    private final Map<String, String> params = new HashMap<>();
    private final Collection<SinkRecord> records = new ArrayList<>();
    private BatchSinkTask task;

    private final MutableHandlerRegistry serviceRegistry = new MutableHandlerRegistry();

    @Rule
    public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

    @BeforeEach
    public void beforeEach() throws IOException {
        // set up acceptable config
        params.put("batch.token", "foobar");
        params.put("batch.collector", "batch.sh:9000");
        params.put("task_id", "BatchSinkConnector-0");

        // Generate a unique in-process server name.
        String serverName = InProcessServerBuilder.generateName();

        // Create a server, add service, start, and register for automatic graceful shutdown.
        grpcCleanup.register(InProcessServerBuilder.forName(serverName)
                .fallbackHandlerRegistry(serviceRegistry).directExecutor().build().start());

        ManagedChannel channel = grpcCleanup.register(
                InProcessChannelBuilder.forName(serverName).directExecutor().build());

        task = new BatchSinkTask();

        task.channel = channel;
        task.blockingStub = GRPCCollectorGrpc.newBlockingStub(channel);

        SinkRecord fooRecord = new SinkRecord("foo-topic-1",
                1,
                Schema.BYTES_SCHEMA,
                "fookey".getBytes(),
                Schema.BYTES_SCHEMA,
                "foovalue".getBytes(),
                0,
                0L,
                TimestampType.CREATE_TIME);

        SinkRecord barRecord = new SinkRecord("bar-topic-1",
                1,
                Schema.BYTES_SCHEMA,
                "barkey".getBytes(),
                Schema.BYTES_SCHEMA,
                "barvalue".getBytes(),
                0);

        records.add(fooRecord);
        records.add(barRecord);
    }

    @AfterEach
    public void afterEach() {
        params.clear();
        task.stop();
        BatchSinkMetricsReporter.taskErrors.clear();
    }

    @Test
    void shouldPut() {
        final AtomicReference<Integer> numRecords = new AtomicReference<>(0);

        // mock happy path
        GRPCCollectorGrpc.GRPCCollectorImplBase addRecordImpl = new GRPCCollectorGrpc.GRPCCollectorImplBase() {
            @Override
            public void addKafkaRecord(KafkaSinkRecordRequest request,
                                       StreamObserver<KafkaSinkRecordResponse> responseObserver) {

                numRecords.set(request.getRecordsCount());
                responseObserver.onNext(KafkaSinkRecordResponse.newBuilder().setNumRecordsProcessed(request.getRecordsCount()).build());
                responseObserver.onCompleted();
            }
        };
        serviceRegistry.addService(addRecordImpl);

        assertDoesNotThrow(() -> task.put(records));
        assertEquals(records.size(), numRecords.get());
    }

    @Test
    void shouldCatchGrpcRuntimeErr() {
        final AtomicReference<Integer> numRecords = new AtomicReference<>(0);
        final StatusRuntimeException fakeError = new StatusRuntimeException(Status.DATA_LOSS);

        // mock an error happening
        GRPCCollectorGrpc.GRPCCollectorImplBase addRecordImpl = new GRPCCollectorGrpc.GRPCCollectorImplBase() {
            @Override
            public void addKafkaRecord(KafkaSinkRecordRequest request,
                                  StreamObserver<KafkaSinkRecordResponse> responseObserver) {

                numRecords.set(request.getRecordsCount());
                responseObserver.onError(fakeError);
            }
        };
        serviceRegistry.addService(addRecordImpl);

        assertTrue(BatchSinkMetricsReporter.taskErrors.isEmpty());
        assertThrows(ConnectException.class, () -> task.put(records));
        assertFalse(BatchSinkMetricsReporter.taskErrors.isEmpty());
        assertEquals(String.format("RPC failed: %s", fakeError.getStatus()), BatchSinkMetricsReporter.taskErrors.values().iterator().next());
    }
}
