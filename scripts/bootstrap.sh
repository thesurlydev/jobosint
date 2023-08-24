#!/bin/bash

set -e

source ./config

echo "Creating data path: ${DATA_PATH}"
mkdir -p ${DATA_PATH}

echo "Starting container: ${CONTAINER_NAME}"
docker run --name ${CONTAINER_NAME} \
  -e POSTGRES_USER=${DB_USER} \
  -e POSTGRES_PASSWORD=${DB_PASS} \
  -v ${DATA_PATH}:/var/lib/postgresql/data \
  -p ${DB_PORT}:5432 \
  -d postgres


sleep 3


CMD1="CREATE DATABASE ${DB_NAME} WITH TEMPLATE = template0 ENCODING = '${DB_ENCODING}' LOCALE_PROVIDER = libc LOCALE = '${DB_LOCALE}'"
echo "Running command: ${CMD1}"
PGPASSWORD=${DB_PASS} psql -h ${DB_HOST} -p ${DB_PORT} -U ${DB_USER} --no-password -c "${CMD1}"

CMD2="ALTER DATABASE ${DB_NAME} OWNER TO ${DB_USER};"
echo "Running command: ${CMD2}"
PGPASSWORD=${DB_PASS} psql -h ${DB_HOST} -p ${DB_PORT} -U ${DB_USER} --no-password -c "${CMD2}"


DB_STRING="postgres://${DB_USER}:${DB_PASS}@localhost:${DB_PORT}/${DB_NAME}"

echo
echo "Container name: ${CONTAINER_NAME}"
echo "Connect string: ${DB_STRING}"
echo
