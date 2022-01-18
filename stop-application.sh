!#/bin/bash

echo "Stopping application"
docker-compose down
./gradlew clean