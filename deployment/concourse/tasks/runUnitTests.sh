#!/bin/sh

set -e # fail fast
set -x # print commands

cd greenscreen-source
sbt clean test