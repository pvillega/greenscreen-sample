#!/usr/bin/env bash
set -e # fail fast
dir="$( cd "$( dirname "$0" )" && pwd )"
cd ${dir}/../..

# See http://concourse.ci/vagrant.html
echo "> Upgrades Concourse Vagrant machine..."
vagrant box update --box concourse/lite # gets the newest Vagrant box
vagrant destroy                         # remove the old Vagrant box
vagrant up                              # re-create the machine with the newer box
