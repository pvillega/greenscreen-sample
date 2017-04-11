#!/usr/bin/env bash
dir="$( cd "$( dirname "$0" )" && pwd )"
cd ${dir}/..

echo "Using Sbt-dependency-graph plugin (See https://github.com/jrudolph/sbt-dependency-graph) to display graph of dependencies"
sbt dependencyBrowseGraph