#!/usr/bin/env bash
dir="$( cd "$( dirname "$0" )" && pwd )"
cd ${dir}/..

echo "Checking vulnerabilities in our dependencies using sbt-dependency-check (see https://github.com/albuch/sbt-dependency-check)."

sbt dependencyCheckAggregate