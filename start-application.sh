!#/bin.bash

echo "Starting application"
gradle clean test integrationTest build
docker-compose up