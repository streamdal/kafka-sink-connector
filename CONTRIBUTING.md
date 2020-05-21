# Contributing

Help us make this connector better!

## Getting Started

* Fork this repo
* Clone the project and bootstrap all you need to get going
  > `make setup`
* Build the artifact
  > `make`
* Run the built JAR within a contained kafka connect setup in docker
  > `docker-compose up`

### Running Locally

The simplest way to run the connector locally is with [docker-compose](https://docs.docker.com/compose/).

The compose file is configured to start up a container with all the needed services to test the connector
along with a comprehensive dashboard UI located at http://localhost:3030. The connector is added via a volume mount
to `/collectors` which is included in the `pluginPath` for kafka on the container host.

In order to run the service, execute:

```shell
$ make && docker-compose up
```

#### Starting the connector via fast-data-dev

The connector itself is simply a set of JAR files containing instructions on how
to pull records off of topics and what to do with them plus any dependencies
needed to perform these tasks. Kafka is able to discover connector plugins
on start as long as the JARs are in a directory within the configured `pluginPath`.

Our docker-compose file should automatically add the built JAR from our `target` dir
into the `pluginPath` of our container so it should be detected on startup.

Once it is detected it must be configured. On base Kafka we would do this through a
request to the Kafka-Connect REST interface. Since our dev container ships with a UI
we can configure it that way:

1. Navigate to the UI which by default should be running on http://localhost:3030
2. Click 'Enter' under the Kafka Connect UI box
3. Setup a New connector
![Setup a New Connector](assets/new_connector.png)
4. Choose 'BatchSinkConnector' from the list of options
![Choose BatchSinkConnector](assets/choose_collector.png)
5. Provide necessary configuration
![Configure Connector](assets/confirm_connector.png)
6. Create and allow it to rebalance tasks

The connector can be viewed from the Connector UI and from there it is possible to see if the
task launched successfully or if it hit some error on initialization. 


### Submitting Changes

* Create a branch off master using a self-descriptive branch name
* Make incremental commits for logical units
* Run tests for the connector with `make test` and add any relevant tests for changes made
* Use clear commit messages ex: ```(#205) Show good commit message with GH issue number.```
* Push your changes to your branch in your fork of the repository
* Submit a pull request against the `batchcorp` org

The team meets weekly and triage incoming issues/fixes when applicable.
