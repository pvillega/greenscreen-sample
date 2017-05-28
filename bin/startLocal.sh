#!/usr/bin/env bash
set -e # fail fast
dir="$( cd "$( dirname "$0" )" && pwd )"
cd ${dir}/..

# See https://devcenter.heroku.com/articles/heroku-local
echo "Requires an updated stage package"
sbt stage

echo "Env variables loaded are:"
cat .env

echo "Using Heroku local to run the application on port 8080. See https://devcenter.heroku.com/articles/heroku-local"
heroku local -p 8080