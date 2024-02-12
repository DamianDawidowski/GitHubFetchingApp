package application.githubfetch.resources;

import application.githubfetch.errors.ErrorResponse;
import application.githubfetch.errors.WebClientResponseException;
import application.githubfetch.models.AllRepositoriesData;
import application.githubfetch.models.BranchData;
import application.githubfetch.models.RepositoryData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/repodata")
public class FetchRepoData {
//    Obtain and define your own API KEY to improve app functionality, otherwise analysis of users with large
//    repository pools will lead to error 403 from github timeout.
    @Value("${github_API_KEY}")
    private String githubKey;

    @RequestMapping("/{ownerName}")
    public AllRepositoriesData getAllRepositoriesData(@PathVariable("ownerName") String ownerName) {

        AllRepositoriesData allRepositoriesData = new AllRepositoriesData();

        int pageNumber = 1;

        boolean repeatFetch = true;

        while (repeatFetch) {
            ObjectMapper objectMapper = new ObjectMapper();

            String urlOfUserRepos = "https://api.github.com/users/" + ownerName + "/repos?page=" + pageNumber;

            WebClient.Builder builder = WebClient.builder();

            String repoData ="";

            repoData = builder
                .build()
                .get()
                .uri(urlOfUserRepos)
                .headers(httpHeaders -> {
                    httpHeaders.add("Accept", "application/json");
//                    enable the header for github API key if you have one
                    httpHeaders.setBearerAuth(githubKey);
                })
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> response.bodyToMono(String.class) // error body as String or other class
                    .flatMap(error -> {
                        Map<String, Object> errorDataList = new LinkedHashMap<>();

                        try {
                        errorDataList = objectMapper.readValue(error, new TypeReference<Map<String, Object>>() {
                        });

                        } catch (JsonProcessingException e) {
                          throw new RuntimeException(e);
                        }

                        String errorMessage = (String) errorDataList.get("message");
                        int errorCode = response.statusCode().value();

                        throw new WebClientResponseException(errorMessage, errorCode);
                    })
                )
                .bodyToMono(String.class)
                .block();

            List<Map<String, Object>> listOfRepos = new ArrayList<>();

            try {
                listOfRepos = objectMapper.readValue(repoData, new TypeReference<>() {
                });

            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            for (Map<String, Object> repo : listOfRepos) {
                String ownerLogin = "";

                String repositoryName = "";

                ArrayList<BranchData> allBranchesData = new ArrayList<>();

//                a logical check if a particular repository is a fork, the particular loop iteration will break if positive
                if ((boolean) repo.get("fork")) {
                    continue;
                }

                repositoryName = (String) repo.get("name");

                if (repo.get("owner") instanceof LinkedHashMap) {
                    Map<String, Object> ownerData = (Map<String, Object>) repo.get("owner");

                    ownerLogin = (String) ownerData.get("login");

                    String urlForRepoBranches = "https://api.github.com/repos/" + ownerName + "/" + repositoryName + "/branches";

                    String branchesData = builder
                        .build()
                        .get()
                        .uri(urlForRepoBranches)
                        .headers(httpHeaders -> {
                            httpHeaders.add("Accept", "application/json");
//                              enable the header for github API key if you have one
                            httpHeaders.setBearerAuth(githubKey);
                        })
                        .retrieve()
                        .onStatus(HttpStatusCode::isError, response -> response.bodyToMono(String.class)
                            .flatMap(error -> {
                                Map<String, Object> errorDataList = new LinkedHashMap<>();

                                try {
                                    errorDataList = objectMapper.readValue(error, new TypeReference<Map<String, Object>>() {
                                    });
                                } catch (JsonProcessingException e) {
                                    throw new RuntimeException(e);
                                }

                                String errorMessage = (String) errorDataList.get("message");

                                throw new WebClientResponseException(errorMessage, response.statusCode().value());
                            })

                        )
                        .bodyToMono(String.class)
                        .block();

                    List<Map<String, Object>> listOfBranches = new ArrayList<>();

                    try {
                        listOfBranches = objectMapper.readValue(branchesData, new TypeReference<List<Map<String, Object>>>() {
                        });
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }

                    for (Map<String, Object> branchGitHubData : listOfBranches) {

                        String branchName = (String) branchGitHubData.get("name");

                        Map<String, Object> commitData = (Map<String, Object>) branchGitHubData.get("commit");
                        String branchLastCommitSha = (String) commitData.get("sha");

                        BranchData branchData = new BranchData(branchName, branchLastCommitSha);
                        allBranchesData.add(branchData);
                    }
                }

                allRepositoriesData.addRepositoryData(new RepositoryData(repositoryName, ownerLogin, allBranchesData));
            }
//           the test below checks if github did send full 30 repositories data, in which case a new fetch with
//           incremented page number will be executed
            if (listOfRepos.size() == 30) {
                pageNumber++;
            } else {
                repeatFetch = false;
            }
        }

        return allRepositoriesData;
    }

    @ExceptionHandler({WebClientResponseException.class})
    public ResponseEntity<ErrorResponse> notFound(WebClientResponseException ex){

//        this assumes the 404 status from perspective of this server, however the error response can also deliver the status
//        from GitHub api instead, just put ex.getCode() as the first argument in new ErrorResponse call below
        return new ResponseEntity<ErrorResponse>(
            new ErrorResponse(404, ex.getMessage()), HttpStatus.NOT_FOUND);
    }
}