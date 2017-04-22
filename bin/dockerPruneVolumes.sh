#!/usr/bin/env bash
dir="$( cd "$( dirname "$0" )" && pwd )"
cd ${dir}/..

# See https://docs.docker.com/engine/tutorials/dockervolumes/#remove-volumes
docker volume prune
