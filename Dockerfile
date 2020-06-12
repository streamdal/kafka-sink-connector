FROM debezium/connect-base:1.1

COPY jar/*jar kafka/connect/batch-sink-connector/
