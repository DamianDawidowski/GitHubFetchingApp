package application.githubfetch.models;

import java.util.ArrayList;
import java.util.List;

public class RepositoryData {
    String repositoryName;
    String ownerLogin;
    ArrayList<BranchData> allBranchesData;

    public RepositoryData(String repositoryName, String ownerLogin, ArrayList<BranchData> allBranchesData) {
        this.repositoryName = repositoryName;
        this.ownerLogin = ownerLogin;
        this.allBranchesData = allBranchesData;
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public void setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
    }

    public String getOwnerLogin() {
        return ownerLogin;
    }

    public void setOwnerLogin(String ownerLogin) {
        this.ownerLogin = ownerLogin;
    }

    public List<BranchData> getAllBranchesData() {
        return allBranchesData;
    }

    public void setAllBranchesData(ArrayList<BranchData> allBranchesData) {
        this.allBranchesData = allBranchesData;
    }

    public void addBranchData(BranchData branchData) {
        allBranchesData.add(branchData);
    }

}
