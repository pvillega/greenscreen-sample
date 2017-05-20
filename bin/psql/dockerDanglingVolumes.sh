#!/usr/bin/env bash
set -e # fail fast
dir="$( cd "$( dirname "$0" )" && pwd )"
cd ${dir}/../..

# See https://docs.docker.com/engine/tutorials/dockervolumes/#creating-and-mounting-a-data-volume-container
echo "Find volumes we are not using in any container and can be removed via:"
echo "   $ docker volume rm <volume name>"
docker volume ls -f dangling=true
