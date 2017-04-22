#!/usr/bin/env bash
dir="$( cd "$( dirname "$0" )" && pwd )"
cd ${dir}/..

# This script creates the necessary docker images and volumes so we can run postgres locally, via docker
# See https://hub.docker.com/_/postgres/
# See https://ryaneschinger.com/blog/dockerized-postgresql-development-environment/
VERSION=9.6.2

# Create a data volume container for our docker images, so the data cna be persisted even if we destroy/update postgres containers
# See https://docs.docker.com/engine/tutorials/dockervolumes/#creating-and-mounting-a-data-volume-container
# See https://hub.docker.com/_/busybox/
echo "Setting up docker container volume for postgres ${VERSION}"
if [[ "$(docker container ls -a -q --filter name=postgres${VERSION}-data 2> /dev/null)" == "" ]]; then
  docker create -v /var/lib/postgresql/data --name postgres${VERSION}-data postgres:${VERSION} /bin/true
else
  echo "A container volume exists with that name. Run 'docker container ls -a' to see a complete list of containers."
fi

# Create docker image using volume
echo "Starting docker image postgres ${VERSION} on port 5432 with user 'postgres' and password 'passw0rd'"
docker run --name local-postgres${VERSION} -p 5432:5432 -e POSTGRES_PASSWORD=passw0rd -d --volumes-from postgres${VERSION}-data postgres:${VERSION}
