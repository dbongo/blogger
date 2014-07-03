#!/usr/bin/env bash

set -e

cd "$( dirname "${BASH_SOURCE[0]}" )"
ROOT=$( pwd )

cd $ROOT/backend
sbt stage
docker build -t examplebackend .

cd $ROOT/frontend
docker build -t examplefrontend .