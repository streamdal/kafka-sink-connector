package sh.batch.kafka;

import io.grpc.ManagedChannel;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.stub.StreamObserver;
import io.grpc.testing.GrpcCleanupRule;
import io.grpc.util.MutableHandlerRegistry;
import org.apache.kafka.common.record.TimestampType;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.sink.SinkRecord;
import org.junit.Rule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sh.batch.services.AddKafkaSinkRecordRequest;
import sh.batch.services.AddKafkaSinkRecordResponse;
import sh.batch.services.KafkaSinkCollectorGrpc;
import sh.batch.services.KafkaSinkCollectorGrpc.KafkaSinkCollectorImplBase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;


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
        params.put("key.converter", "org.apache.kafka.connect.converters.ByteArrayConverter");
        params.put("value.converter", "org.apache.kafka.connect.converters.ByteArrayConverter");

        // Generate a unique in-process server name.
        String serverName = InProcessServerBuilder.generateName();

        // Create a server, add service, start, and register for automatic graceful shutdown.
        grpcCleanup.register(InProcessServerBuilder.forName(serverName)
                .fallbackHandlerRegistry(serviceRegistry).directExecutor().build().start());

        ManagedChannel channel = grpcCleanup.register(
                InProcessChannelBuilder.forName(serverName).directExecutor().build());

        task = new BatchSinkTask();

        task.channel = channel;
        task.blockingStub = KafkaSinkCollectorGrpc.newBlockingStub(channel);

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
    }

    @Test
    void shouldPut() {
        final AtomicReference<Integer> numRecords = new AtomicReference<>(0);

        // mock happy path
        KafkaSinkCollectorImplBase addRecordImpl = new KafkaSinkCollectorImplBase() {
            @Override
            public void addRecord(AddKafkaSinkRecordRequest request,
                                  StreamObserver<AddKafkaSinkRecordResponse> responseObserver) {

                numRecords.set(request.getRecordsCount());
                responseObserver.onNext(AddKafkaSinkRecordResponse.newBuilder().setNumRecordsProcessed(request.getRecordsCount()).build());
                responseObserver.onCompleted();
            }
        };
        serviceRegistry.addService(addRecordImpl);

        assertDoesNotThrow(() -> task.put(records));
        assertEquals(records.size(), numRecords.get());
    }

//    @Test
//    void shouldCatchGrpcRuntimeErr() {
//        final AtomicReference<Integer> numRecords = new AtomicReference<>(0);
//        final StatusRuntimeException fakeError = new StatusRuntimeException(Status.DATA_LOSS);
//
//        // mock an error happening
//        KafkaSinkCollectorImplBase addRecordImpl = new KafkaSinkCollectorImplBase() {
//            @Override
//            public void addRecord(AddKafkaSinkRecordRequest request,
//                                  StreamObserver<AddKafkaSinkRecordResponse> responseObserver) {
//
//                numRecords.set(request.getRecordsCount());
//                responseObserver.onError(fakeError);
//            }
//        };
//        serviceRegistry.addService(addRecordImpl);
//
//        assertDoesNotThrow(() -> task.put(records));
//
//        // all we currently do is log so this one we just have to trust the coverage on for now
//        assertEquals(records.size(), numRecords.get());
//    }
}
