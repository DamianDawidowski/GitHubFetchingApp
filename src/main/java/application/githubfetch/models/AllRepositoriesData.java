package application.githubfetch.models;

import java.util.ArrayList;

public class AllRepositoriesData {
    ArrayList<RepositoryData> AllRepositoriesData;

    public AllRepositoriesData() {
        AllRepositoriesData = new ArrayList<RepositoryData>();
    }

    public AllRepositoriesData(ArrayList<RepositoryData> allReposData) {
        AllRepositoriesData = AllRepositoriesData;
    }

    public ArrayList<RepositoryData> getAllReposData() {
        return AllRepositoriesData;
    }

    public void setAllRepositoriesData(ArrayList<RepositoryData> allReposData) {
        AllRepositoriesData = AllRepositoriesData;
    }

    public void addRepositoryData(RepositoryData repoData) {
        AllRepositoriesData.add(repoData);
    }

}
