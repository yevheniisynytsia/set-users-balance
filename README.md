It is a Spring Boot application written in Java and built with Gradle.

The application is designed to update user balances in a database. It uses two different strategies to accomplish this:

## Main Branch - Batch Read-Update Strategy

The `main` branch contains a basic solution that uses a batch read-update strategy. This strategy reads and updates user balances in batches, which can be configured in the `application.yml` file under the `user.balanceUpdate.batchSize` property.

This strategy uses Spring Data JPA and Hibernate to interact with the database.

## Experimental Branch - Multithreading Strategy

The `experimental` branch contains a raw solution that uses multithreading to update user balances. This strategy is currently slower than the batch read-update strategy in the `main` branch.

## Running the Application

To run the application, you need to have Docker installed on your machine.
Steps to run the application:
1. Clone the repository.
2. Run the Docker engine.
3. Build the jar file:`./gradlew build`
4. Build the Docker image: `docker-compose build.`
5. Run the Docker container: `docker-compose up`
6. Access the application at `http://localhost:8088/users/balance`

By default, there will be 20 users inserted in the database. You can change this number in the `users.csv` file located in the `src/main/resources` directory.
To run some tests you may use the next curl from Postman: 
`curl --location --request PUT 'http://localhost:8088/users/balance' \
--header 'Content-Type: application/json' \
--data '{"11":22,"12":24,"13":26,"14":28,"15":30,"16":32,"17":34,"18":36,"19":38,"1":2,"2":4,"3":6,"4":8,"5":10,"6":12,"7":14,"8":16,"9":18,"20":40,"10":20}'`
There are files with 500k records in it, you may use them to test the application as well.

## Possible Improvements

I've started looking into using multithreading to solve the task, but for now it is not optimal.
Using Executor and CompletableFuture to run the threads in parallel may be a good improvement, as for me.

I think there is also a possibility to update records in some kind of better batches. For now, I'm updating records one by one, but it may be better to update them in batches of some size.

Finally, it would be great to rewrite it to Kotlin, but it needs investing in time.
