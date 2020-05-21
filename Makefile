VERSION ?= $(shell git rev-parse --short HEAD)
SERVICE=kafka-sink-connector

DOCKER_IMAGE=connect

# allows to specify which tests to be run (ex: TEST_PATTERN=FooTest make test)
TEST_PATTERN ?= .

OUTPUT_DIR ?= .

# allow passing -ldflags, etc for release builds
BUILD_ARGS ?=

.PHONY: build
build: ## build the artifact
	@mvn clean package $(BUILD_ARGS)

.PHONY: test
test: ## run the unit tests
	@mvn clean test jacoco:report

.PHONY: setup
setup: ## get setup
	@printf ">> bootstrapping tools...\n"
	@./script/bootstrap
	@mvn clean install
	$(call display_check)

.PHONY: help
help: ## display this help message
	@awk 'BEGIN {FS = ":.*?## "} /^[a-zA-Z_\/-]+:.*?## / {printf "\033[34m%-30s\033[0m %s\n", $$1, $$2}' $(MAKEFILE_LIST) | \
		sort | \
	grep -v '#'

.PHONY: docker/build
docker/build: ## build sandbox docker image
	docker build -t docker.pkg.github.com/batchcorp/$(SERVICE)/$(DOCKER_IMAGE):$(VERSION) \
	-t docker.pkg.github.com/batchcorp/$(SERVICE)/$(DOCKER_IMAGE):latest \
	-f ./Dockerfile .

.PHONY: docker/push
docker/push: ## publish sandbox docker image
	docker push docker.pkg.github.com/batchcorp/$(SERVICE)/$(DOCKER_IMAGE):$(VERSION) && \
	docker push docker.pkg.github.com/batchcorp/$(SERVICE)/$(DOCKER_IMAGE):latest

.DEFAULT_GOAL := build
default: build

define display_check
	@printf " \033[32m✔︎\033[0m\n"
endef
