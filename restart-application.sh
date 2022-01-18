!#/bin/bash

./stop-application.sh
rm -rf .data-volume
docker container prune
docker image prune
./start-application.sh