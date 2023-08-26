#!/usr/bin/env bash

set -e

pushd scripts
  ./bootstrap.sh
popd

flyway migrate -configFiles=database/flyway.conf

echo "Done!"