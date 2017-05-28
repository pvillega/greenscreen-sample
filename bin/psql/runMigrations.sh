#!/usr/bin/env bash
dir="$( cd "$( dirname "$0" )" && pwd )"
cd ${dir}/../..

# See https://flywaydb.org/documentation/sbt/
# See https://jdbc.postgresql.org/documentation/head/connect.html
JDBC_DATABASE_URL="jdbc:postgresql://localhost:5432/greenscreen?user=postgres&password=passw0rd"

echo "Running Flyway migrations on local postgres database. See https://flywaydb.org/documentation/sbt/"
sbt -Dflyway.url=$JDBC_DATABASE_URL flywayMigrate
