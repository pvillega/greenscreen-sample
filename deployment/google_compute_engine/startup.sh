#!/usr/bin/env bash

set -e
set -v

# Talk to the metadata server to get the project id
PROJECTID=$(curl -s "http://metadata.google.internal/computeMetadata/v1/project/project-id" -H "Metadata-Flavor: Google")
BUCKET=$(curl -s "http://metadata.google.internal/computeMetadata/v1/instance/attributes/BUCKET" -H "Metadata-Flavor: Google")

echo "Project ID: ${PROJECTID}  Bucket: ${BUCKET}"

echo "Install Java 8 via apt"
apt-get update
apt-get install -y openjdk-8-jdk unzip

echo "Make Java8 the default"
update-alternatives --set java /usr/lib/jvm/java-8-openjdk-amd64/jre/bin/java


echo "Install Stackdriver agents"
# Some agents don't support latest Ubuntu yet...
#curl -O https://repo.stackdriver.com/stack-install.sh
#chmod +x ./stack-install.sh
#bash stack-install.sh --write-gcm

curl -sSO https://dl.google.com/cloudagents/install-logging-agent.sh
chmod +x ./install-logging-agent.sh
bash install-logging-agent.sh

echo "Copy and unpack application"
cd /tmp
gsutil cp "gs://${BUCKET}/gce/"** .
unzip greenscreen-c7ef5a96cf1f672aa90716c8bae0291605470fd2-SNAPSHOT.zip
cd greenscreen-c7ef5a96cf1f672aa90716c8bae0291605470fd2-SNAPSHOT

echo "Set env variables"
export KEYSTORE_PATH=/tmp/selfsigned.jks
export JDBC_DATABASE_URL=jdbc:postgresql://35.184.111.217:5432/postgres
export JDBC_DATABASE_USERNAME=postgres
export JDBC_DATABASE_PASSWORD=passw0rd

echo "Launch app"
exec bash bin/greenscreen -Dconfig.resource=application.prod.conf


