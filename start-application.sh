!#/bin/bash

echo "Starting application"
./gradlew clean build -x test -x integrationTest && docker-compose up