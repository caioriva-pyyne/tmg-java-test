# TMG Java Test

This is a Spring-Boot application created by Caio Riva as an assigment from TMG.

## Requirements
This project was created using [JDK 19](https://jdk.java.net/19/) and 
[Maven 3.8.1](https://maven.apache.org/docs/3.8.1/release-notes.html), so make sure to use them as well when running 
the application.

## Design Choices
The following class diagram describes the main classes that compose the application:
![project-uml-class-diagram.png](project-uml-class-diagram.png)

### Response Standard
There are 5 endpoints in total (inside the controller package): 
* GET endpoints return 200 and produce serialized JSON objects based on 
[StandardResponse](src/main/java/com/example/tmgjavatest/model/dto/response/StandardResponse.java) class;
* POST, PUT and DELETE endpoints return 204 with no response body;
* When request validation errors happen, the endpoints return 400 and produce a serialized JSON based 
on `ErrorResponse`record inside [GlobalExceptionHandler](src/main/java/com/example/tmgjavatest/exception/handling/GlobalExceptionHandler.java)
* Retrieving data from an empty stack or accessing a nonexistent key in the map returns 404 and produces a 
serialized JSON based on `ErrorResponse` record inside [GlobalExceptionHandler](src/main/java/com/example/tmgjavatest/exception/handling/GlobalExceptionHandler.java).

### Data Structure Services
Both services that store the in memory data are [Spring Beans](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans-definition).
They are both `Service` beans and by default `Spring IoC Container` manages all beans as Singleton instances unless a 
different prototype is specified. This guarantees that all threads (e.g. when different requests happen at the same time)
access the same in memory data structure.

#### The StackService
For the sake of actually implementing the FILO logic, no collection from JDK Collections API was used. However, in a 
real case scenario, it would be better to use a thread-safe collection that supports FILO operations like [ConcurrentLinkedDeque](https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/ConcurrentLinkedDeque.html).

For this test, the implementation chosen was a doubly linked list with synchronized methods to achieve thread-safety in 
the push and pop operations.

#### The TTLMapService
For the sake of avoiding using third-party libraries or services, no caching library or library with cacheable data 
structures was used. However, in a real case scenario, it would be better to use them instead of manually running a 
background thread to check and remove expired entries.

For this test, the implementation chosen was having two [ConcurrentHashMap](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ConcurrentHashMap.html) 
instance variables, one for the data and another for expiration that would be checked and possibly updated on every 
get action or every 900ms seconds by a background thread (900ms was an arbitrary chosen number that is smaller than 
the minimal time to live that can be specified). 

## Installing Dependencies
Because this is a Java application that uses maven as the build and dependency management tool, to install the projects
dependencies, execute the following commend on the [root directory](.):
```
mvn install
```
You can also run the following command on the [root directory](.) if you want to remove obsolete compiled classes 
(e.g. renamed classes that are no longer the same in `target/classes`) before installing the dependencies:
```
mvn clean install
```

## Tests
The project supports two different types of tests: unit and integration tests. The project uses 
[maven-surefire-plugin](https://maven.apache.org/surefire/maven-surefire-plugin/),
[maven-failsafe-plugin](https://maven.apache.org/surefire/maven-failsafe-plugin/) and
[JUnit 5](https://junit.org/junit5/) tag annotations to run certain test suites for different maven lifecycle phases.

### Unit Tests
To run all unit tests, execute the following commend on the [root directory](.):
```
mvn test
```

### Integration Tests
To run all integration tests (including unit tests), execute the following commend on the [root directory](.):
```
mvn integration-test
```

To only run the integration tests, execute the following commend on the [root directory](.):
```
mvn failsafe:integration-test
```

## Suggestions
### Using Postman
If you want to test the application using [Postman](https://www.postman.com/downloads/), a [Postman collection](tmg-java-test.postman_collection.json)
file is included in this project.