package sh.batch.kafka;

import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;

public class BatchSinkTask extends SinkTask {
    private static final Logger log = LoggerFactory.getLogger(BatchSinkTask.class);

    private String licenseKey;
    private String collectorURL;

    public BatchSinkTask() {
    }

    @Override
    public String version() {
        return new BatchSinkConnector().version();
    }

    @Override
    public void start(Map<String, String> params) {
        licenseKey = params.get(BatchSinkConnector.LICENSE_CONFIG);
        collectorURL = params.get(BatchSinkConnector.COLLECTOR_URL_CONFIG);

        // TODO: ^^^ Validate these actually exist and are legit ^^^
    }

    @Override
    public void put(Collection<SinkRecord> records) {
        for (SinkRecord record : records) {
            log.trace("Writing record to batch collector ({}) with license key {}: {}", collectorURL, licenseKey, record.value());
            System.out.printf("Writing record to batch collector (%s) with license key %s: %s", collectorURL, licenseKey, record.value());
            // TODO: This is where the meat of the connector will live
            //       Besides logging this is where we translate the records into
            //       whatever format we decide and make the request to the collector service.
        }
    }

    @Override
    public void stop() {
        // TODO: Tear down any clients that need flushed/closed
    }
}
