#!/bin/bash

CURRENT_DIR=$(pwd)

cd ./api/target

export JAVA_HOME="/usr/lib/jvm/openjdk-11-oracle"

echo $JAVA_HOME

echo "JAVA_OPTS=-Dspring.profiles.active=ekalosha" > api-core.conf

JAVA_HOME="/usr/lib/jvm/openjdk-11-oracle" ./api-core.jar start > __api-core.log &

cd ${CURRENT_DIR}
