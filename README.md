# TMG Java Test

This is a Spring-Boot application created by Caio Riva as an assigment from TMG.

## Requirements
This project was created using [JDK 19](https://jdk.java.net/19/) and 
[Maven 3.8.1](https://maven.apache.org/docs/3.8.1/release-notes.html), so make sure to use them as well.

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