# Batch.sh Kafka Sink Connector

The [Kafka sink connector](https://docs.confluent.io/current/connect/devguide.html#sink-tasks) funnels records sent over specified topics to the batch.sh collector service.

[Batch](https://batch.sh) offers the ability to tee any events produced within a Kafka cluster up to a remote collector that can optionally analyze and schemify those events.
These events can then be queried and replayed at target destinations.

[![](https://mermaid.ink/img/eyJjb2RlIjoiZ3JhcGggTFJcblx0QVtQcm9kdWNlcl0gLS0-fHNlbmQgbWVzc2FnZXwgQntLYWZrYSBCcm9rZXJzfVxuXHRCIC0tPiBEW2ZhOmZhLXVzZXIgQ29uc3VtZXIgMV1cblx0QiAtLT4gRVtmYTpmYS11c2VyIENvbnN1bWVyIDJdXG5cdEIgLS0-IEZbZmE6ZmEtdXNlciBDb25zdW1lciAzXVxuICBCIC0tPiBDKGJhdGNoLnNoIGthZmthIHNpbmsgY29ubmVjdG9yKVxuICBDIC0tPiB8bWVzc2FnZXMgb3ZlciB0b3BpY3MgY29uZmlndXJlZCBmb3IgYmF0Y2guc2h8R1soYmF0Y2guc2gga2Fma2EgY29sbGVjdG9yKV1cblx0XHRcdFx0XHQiLCJtZXJtYWlkIjp7InRoZW1lIjoiZGVmYXVsdCJ9LCJ1cGRhdGVFZGl0b3IiOmZhbHNlfQ)](https://mermaid-js.github.io/mermaid-live-editor/#/edit/eyJjb2RlIjoiZ3JhcGggTFJcblx0QVtQcm9kdWNlcl0gLS0-fHNlbmQgbWVzc2FnZXwgQntLYWZrYSBCcm9rZXJzfVxuXHRCIC0tPiBEW2ZhOmZhLXVzZXIgQ29uc3VtZXIgMV1cblx0QiAtLT4gRVtmYTpmYS11c2VyIENvbnN1bWVyIDJdXG5cdEIgLS0-IEZbZmE6ZmEtdXNlciBDb25zdW1lciAzXVxuICBCIC0tPiBDKGJhdGNoLnNoIGthZmthIHNpbmsgY29ubmVjdG9yKVxuICBDIC0tPiB8bWVzc2FnZXMgb3ZlciB0b3BpY3MgY29uZmlndXJlZCBmb3IgYmF0Y2guc2h8R1soYmF0Y2guc2gga2Fma2EgY29sbGVjdG9yKV1cblx0XHRcdFx0XHQiLCJtZXJtYWlkIjp7InRoZW1lIjoiZGVmYXVsdCJ9LCJ1cGRhdGVFZGl0b3IiOmZhbHNlfQ)

# Installation

## Prerequisites

* [Apache Kafka](https://kafka.apache.org/downloads)
  * including [Kafka Connect](https://docs.confluent.io/current/connect/index.html)
  
  OR
  
* [Confluent Platform](https://docs.confluent.io/current/platform.html#cp-platform)


### Kafka Versions
* Internally tested with Kafka 2.2.0 and 2.2.1 (on MSK)
  * newer versions of Kafka up through 2.5.x should work, older versions are not tested
* Built for use with Kafka Connect API 2.5.x
  * should be compatible with Connect API 2.0.x - 2.5.0

## Getting the Connector

### Confluent

1. Download the connector ZIP archive from the official [releases](https://github.com/batchcorp/kafka-sink-connector/releases/)
2. Follow confluent-platform's guide on installing custom connectors using that file
    * https://docs.confluent.io/current/connect/userguide.html#connect-installing-plugins

### Open Source Apache Kafka

1. Download the connector JAR file from the official [releases](https://github.com/batchcorp/kafka-sink-connector/releases/)
2. Copy the connector JAR file to the `plugin.path` on your Connect worker nodes; this path is specified in your
[worker properties](https://docs.confluent.io/current/connect/userguide.html#connect-configuring-workers) (see: https://docs.confluent.io/current/connect/userguide.html#installing-kconnect-plugins)

## Configuring the Connector

Kafka Connect offers two modes of operation: distributed and standalone. These modes each have their own configuration values that
can be passed along with the specific values needed by the connected. See: https://docs.confluent.io/current/connect/userguide.html#running-workers

### Distributed Mode

Distributed mode is configured through the connect REST API.

Example:

```bash
curl -X POST \
    KAFKA_CONNECT_REST_API_HOST:8083/connectors \
    -H 'Content-Type: application/json' \
    -H 'Accept: application/json' \
    -d '{"name": "FooBarBatchSh", "config": {
    "connector.class": "sh.batch.kafka.BatchSinkConnector",
    "tasks.max": "5",
    "topics": "topic1,topic2",
    "key.converter": "org.apache.kafka.connect.converters.ByteArrayConverter",
    "value.converter": "org.apache.kafka.connect.converters.ByteArrayConverter",
    "batch.token": "2494b6fd-74dc-45a3-9c7c-fc0d81e9591c",
}}'

``` 

### Standalone Mode

Standalone mode is configured through a properties file that is passed in as an argument to the kafka connect CLI

Example:

Create this file somewhere on your host, for example to `/config/batchsh-connector.properties`

```properties
name=FooBarBatchSh
connector.class=sh.batch.kafka.BatchSinkConnector
batch.token=2494b6fd-74dc-45a3-9c7c-fc0d81e9591c
tasks.max=5
topics=topic1,topic2
key.converter=org.apache.kafka.connect.converters.ByteArrayConverter
value.converter=org.apache.kafka.connect.converters.ByteArrayConverter
```

Then use the Kafka connect CLI tools to start the connector:

`bin/connect-standalone /path/to/worker.properties /config/batchsh-connector.properties`

### Configuration Values

#### Required

---
**name**

Unique name for the connector. Attempting to register again with the same name will fail.

---
**connector.class**

`sh.batch.kafka.BatchSinkConnector`
___
**batch.token**

The ID of the batch.sh collection that will be the destination for the events. The collection ID allows the collector service
to map the data to a known schema, model it, and store it.

> *NOTE:* Do not share this UUID around. It is used to uniquely identify specific collections and should be treated like a secret.
> All production uses of this connector should use secure transport wherever possible.

---
**topics**

A comma-separated list of topics to use as input for this connector.

> `topics` or `topics.regex` may be specified, but not both.
---
**topics.regex**

Regular expression giving topics to consume. Under the hood, the regex is compiled to a java.util.regex.Pattern. Only one of topics or topics.regex should be specified.

> `topics` or `topics.regex` may be specified, but not both.
---
**key.converter** AND **value.converter**

`org.apache.kafka.connect.converters.ByteArrayConverter`

> *NOTE:* The batch.sh sink connector was designed from the start as a pass-through connector. If the kafka-connect runtime tries to do
> its own data conversion then the resulting byte array sent to the collector is no longer the untainted data sent originally by the
> producer. No matter what data type your records are (Avro, Protobuf, etc) ALWAYS choose the ByteArray converter and let the collector
> resolve the data with its own schemas. 
___

#### Optional

---
**tasks.max**

Maximum number of tasks to use for this connector. Tasks act as consumer threads for Connect workers so tune this setting
to be in line with a typical consumer group for the configured topic(s).

---
**errors.retry.timeout**

The maximum duration in milliseconds that a failed operation will be reattempted. The default is 0, which means no retries will be attempted. 
Use -1 for infinite retries. See [Error Handling](#Error-Handling) section below.
___
