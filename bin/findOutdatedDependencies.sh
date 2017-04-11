#!/usr/bin/env bash
dir="$( cd "$( dirname "$0" )" && pwd )"
cd ${dir}/..

echo "Using Sbt-Updates plugin (See https://github.com/rtimush/sbt-updates) to find outdated dependencies"
sbt dependencyUpdates