#Vending Machine

This application serves a backend API that implements a vending machine

###Running the Application on Unix/Linux
1. Install Docker and have it up and running
2. Go into the main application folder using your terminal. The build scrips also runs unit and integration tests (MVC tests)
```
    chmod +x *.sh  
   ./build-application.sh  
   ./start-application.sh
```
3. To restart a running application and delete the database please run
```
./restart-application.sh
```
4. Import the postman collection from Vending-Machine.postman_collection.json file
5. Use Http Basic Authorization in Postman by passing the username and password used when you signed up into the application
###Running the Application on Windows
1. Install Docker and have it up and running
2. Run the following command to start the application
```
    docker-compose up
```
3. Run the following command to stop the application
```
    docker-compose down
```

###Application Improvements
1. Full unit, integration and mvc tests coverage.
2. A second round of refactoring and slight improvements
3. Implement better logic for coin change. Right now the vending machines that it had an infinite number of coins. A better implementation would be to keep track of how many coins of each type exist and return change based on that.
4. Add OpenAPI schema file
5. Deploy the application in Kubernetes via a CI/CD pipeline
6. Create multiple environments
7. Keep the database credentials in a secured sealed secret