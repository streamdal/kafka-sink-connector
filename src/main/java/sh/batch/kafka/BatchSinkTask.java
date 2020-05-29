package sh.batch.kafka;

import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Metadata;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.MetadataUtils;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sh.batch.events.records.Kafka;
import sh.batch.services.AddRecordsRequest;
import sh.batch.services.GRPCCollectorGrpc;
import sh.batch.services.GRPCCollectorGrpc.GRPCCollectorBlockingStub;

import java.util.Collection;
import java.util.Map;

public class BatchSinkTask extends SinkTask {
    private static final Logger log = LoggerFactory.getLogger(BatchSinkTask.class);

    private ManagedChannel channel;
    private GRPCCollectorBlockingStub blockingStub;

    public BatchSinkTask() {
    }

    @Override
    public String version() {
        return new BatchSinkConnector().version();
    }

    @Override
    public void start(Map<String, String> params) {
        String licenseKey = params.get(BatchSinkConnectorConfig.LICENSE_CONFIG);
        String batchCollector = params.get(BatchSinkConnectorConfig.BATCH_COLLECTOR_CONFIG);

        // create the channel for our grpc connection
        // put the client key into the request metadata
        Metadata metadata = new Metadata();
        Metadata.Key<String> licenseMetadataKey = Metadata.Key.of("batch.license", Metadata.ASCII_STRING_MARSHALLER);
        metadata.put(licenseMetadataKey, licenseKey);

        channel = ManagedChannelBuilder.forTarget(batchCollector)
                .intercept(MetadataUtils.newAttachHeadersInterceptor(metadata))
                .usePlaintext()
                .build();

        blockingStub = GRPCCollectorGrpc.newBlockingStub(channel);
    }

    @Override
    public void put(Collection<SinkRecord> records) {

        AddRecordsRequest.Builder arr = AddRecordsRequest.newBuilder();

        // the AddRecords RPC takes an array of KafkaSinkRecords so we have to
        // iterate through the ones we get and convert to our proto
        for (SinkRecord record : records) {

            Kafka.KafkaSinkRecord ksr = Kafka.KafkaSinkRecord.newBuilder()
                    .setKey(ByteString.copyFrom((byte[])record.key()))
                    .setValue(ByteString.copyFrom((byte[])record.value()))
                    .setTopic(record.topic())
                    .setPartition(record.kafkaPartition())
                    .setOffset(record.kafkaOffset())
                    .setTimestamp(record.timestamp())
                    .build();

            arr.addRecords(ksr);
        }

        // execute the RPC with the compiled list of records
        try {
            blockingStub.addRecords(arr.build());
        } catch (StatusRuntimeException e) {
            log.warn("RPC failed: {}", e.getStatus());
        }

    }

    @Override
    public void stop() {
        channel.shutdown();
    }
}
