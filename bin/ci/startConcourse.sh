#!/usr/bin/env bash
dir="$( cd "$( dirname "$0" )" && pwd )"
cd ${dir}/../..

echo "> Initialises the Vagrant file for Concourse"
vagrant init concourse/lite

echo "> Starts Concourse..."
vagrant up
