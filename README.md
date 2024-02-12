# GitHubFetchingApp
 
## Features
This app was made with Java version 21 and Spring Boot 3.2.2.

It provides the information of the GitHub repositories of a particular user.
Specifically, it returns the names and owner id's of each repository of such user, as well as the name of each branch together with the sha of the last commit.

## Required Program Versions

Oracle Java 21 JDK [Available here](https://www.oracle.com/java/technologies/downloads/#java21)

## Instructions
Clone the repository

Start the backend server
```
 mvn spring-boot:run
```
The app provides a GET service, available at:
http://localhost:8080/repodata/{username}

Insert {username} as the name of the GitHub user you want to test.

As an option, you can include your own GitHub access token to avoid the App's 404 errors from Github blocking further requests due to reaching internal API rate limit.
You can enable the option in the FetchRepositoriesData class, following the relevant comments in code.