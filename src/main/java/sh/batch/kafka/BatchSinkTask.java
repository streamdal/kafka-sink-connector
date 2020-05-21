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

import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Metadata;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.MetadataUtils;
import org.apache.kafka.connect.errors.RetriableException;
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

    protected ManagedChannel channel;
    protected KafkaSinkCollectorGrpc.KafkaSinkCollectorBlockingStub blockingStub;
    protected String taskID;

    @Override
    public String version() {
        return new BatchSinkConnector().version();
    }

    @Override
    public void start(Map<String, String> params) {
        String collectionToken = params.get(BatchSinkConnectorConfig.TOKEN);
        String batchCollector = params.get(BatchSinkConnectorConfig.COLLECTOR_ADDRESS);
        taskID = params.get("task_id");
        boolean disableTLS = Boolean.parseBoolean(params.get(BatchSinkConnectorConfig.DISABLE_COLLECTOR_TLS));

        // create the channel for our grpc connection
        // put the client collection key into the request metadata
        Metadata metadata = new Metadata();
        Metadata.Key<String> tokenMetadataKey = Metadata.Key.of("batch.token", Metadata.ASCII_STRING_MARSHALLER);
        metadata.put(tokenMetadataKey, collectionToken);

        ManagedChannelBuilder<?> channelBuilder = ManagedChannelBuilder.forTarget(batchCollector)
                .intercept(MetadataUtils.newAttachHeadersInterceptor(metadata));

        // for local dev
        if (disableTLS) {
            channelBuilder.usePlaintext();
        }

        channel = channelBuilder.build();
        blockingStub = KafkaSinkCollectorGrpc.newBlockingStub(channel);
    }

    @Override
    public void put(Collection<SinkRecord> records) {
        if (records.isEmpty()) {
            return;
        }

        final SinkRecord first = records.iterator().next();
        final int recordsCount = records.size();
        log.debug(
                "Received {} records. First record kafka coordinates:({}-{}-{}). Writing them to the "
                        + "database...",
                recordsCount, first.topic(), first.kafkaPartition(), first.kafkaOffset()
        );

        AddKafkaSinkRecordRequest.Builder arr = AddKafkaSinkRecordRequest.newBuilder();

        for (SinkRecord record : records) {
            Kafka.KafkaSinkRecord.Builder ksr = Kafka.KafkaSinkRecord.newBuilder()
                    .setValue(ByteString.copyFrom((byte[])record.value()))
                    .setTopic(record.topic())
                    .setPartition(record.kafkaPartition())
                    .setOffset(record.kafkaOffset());

            if (record.timestamp() != null) {
                ksr.setTimestamp(record.timestamp());
            }

            if (record.key() != null) {
                ksr.setKey(ByteString.copyFrom((byte[])record.key()));
            }

            arr.addRecords(ksr.build());
        }

        try {
            AddKafkaSinkRecordResponse resp = blockingStub.addRecord(arr.build());
            log.info("{} records processed", resp.getNumRecordsProcessed());
        } catch (StatusRuntimeException e) {
            String errMessage = String.format("RPC failed: %s", e.getStatus());
            handleRPCErrors(records.size(), e, errMessage);
        } catch (Exception e) {
            String errMessage = e.getMessage();
            handleRPCErrors(records.size(), e, errMessage);
        }
    }

    private void handleRPCErrors(int numRecords, Throwable e, String errMessage) {
        log.warn(
                "Write of {} records failed",
                numRecords,
                e
        );

        log.error(errMessage);
        BatchSinkMetricsReporter.reportError(taskID, errMessage);

        throw new RetriableException(e);
    }

    @Override
    public void stop() {
        if (!channel.isShutdown()) {
            channel.shutdown();
        }
    }
}
