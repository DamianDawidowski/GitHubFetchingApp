# GitHubFetchingApp
 
## Features
This app returns the information of the GitHub repositories of a particular user.
Specifically, it returns the names and owner id's of each repository of such user, as well as the name of each branch together with the sha of the last commit


## Instructions
Clone the repository

Start the backend server
```
 mvn spring-boot:run
```
The app provides a GET service, available at:
http://localhost:8080/repodata/{username}

Insert {username} as the name of the GitHub user you want to test