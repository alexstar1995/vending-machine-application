FROM adoptopenjdk:11-jre-hotspot
COPY build/libs/vendingmachine-0.0.1-SNAPSHOT.jar .
ENTRYPOINT ["java", "-jar", "vendingmachine-0.0.1-SNAPSHOT.jar"]