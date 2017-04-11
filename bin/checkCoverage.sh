#!/usr/bin/env bash
dir="$( cd "$( dirname "$0" )" && pwd )"
cd ${dir}/..

echo "Checking build test coverage using sbt-scoverage (see https://github.com/scoverage/sbt-scoverage). "

sbt clean coverage test coverageReport
sbt coverageAggregate
