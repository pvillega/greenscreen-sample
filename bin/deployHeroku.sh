#!/usr/bin/env bash
set -e # fail fast
dir="$( cd "$( dirname "$0" )" && pwd )"
cd ${dir}/..

echo "Deploying application to Heroku via sbt-heroku (https://github.com/heroku/sbt-heroku)."
echo "Requires Heroku CLI, being logged in, and a Heroku repository in our git remote (direct upload to Heroku instead of Github integration)"

sbt stage deployHeroku