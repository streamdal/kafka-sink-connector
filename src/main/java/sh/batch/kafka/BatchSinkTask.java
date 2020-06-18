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
import sh.batch.services.AddKafkaSinkRecordRequest;
import sh.batch.services.AddKafkaSinkRecordResponse;
import sh.batch.services.KafkaSinkCollectorGrpc;

import java.util.Collection;
import java.util.Map;

public class BatchSinkTask extends SinkTask {
    private static final Logger log = LoggerFactory.getLogger(BatchSinkTask.class);

    private ManagedChannel channel;
    private KafkaSinkCollectorGrpc.KafkaSinkCollectorBlockingStub blockingStub;

    public BatchSinkTask() {
    }

    @Override
    public String version() {
        return new BatchSinkConnector().version();
    }

    @Override
    public void start(Map<String, String> params) {
        String collectionToken = params.get(BatchSinkConnectorConfig.TOKEN_CONFIG);
        String batchCollector = params.get(BatchSinkConnectorConfig.COLLECTOR_ADDRESS_CONFIG);

        // create the channel for our grpc connection
        // put the client collection key into the request metadata
        Metadata metadata = new Metadata();
        Metadata.Key<String> tokenMetadataKey = Metadata.Key.of("batch.token", Metadata.ASCII_STRING_MARSHALLER);
        metadata.put(tokenMetadataKey, collectionToken);

        channel = ManagedChannelBuilder.forTarget(batchCollector)
                .intercept(MetadataUtils.newAttachHeadersInterceptor(metadata))
                .build();

        blockingStub = KafkaSinkCollectorGrpc.newBlockingStub(channel);
    }

    @Override
    public void put(Collection<SinkRecord> records) {

        AddKafkaSinkRecordRequest.Builder arr = AddKafkaSinkRecordRequest.newBuilder();

        // the AddRecords RPC takes an array of KafkaSinkRecords so we have to
        // iterate through the ones we get and convert to our proto
        for (SinkRecord record : records) {
            Kafka.KafkaSinkRecord.Builder ksr = Kafka.KafkaSinkRecord.newBuilder()
                    .setValue(ByteString.copyFrom((byte[])record.value()))
                    .setTopic(record.topic())
                    .setPartition(record.kafkaPartition())
                    .setOffset(record.kafkaOffset())
                    .setTimestamp(record.timestamp());

            if (record.key() != null) {
                ksr.setKey(ByteString.copyFrom((byte[])record.key()));
            }

            arr.addRecords(ksr.build());
        }

        // execute the RPC with the compiled list of records
        try {
            AddKafkaSinkRecordResponse resp = blockingStub.addRecord(arr.build());
        } catch (StatusRuntimeException e) {
            log.warn("RPC failed: {}", e.getStatus());
        }

    }

    @Override
    public void stop() {
        channel.shutdown();
    }
}
