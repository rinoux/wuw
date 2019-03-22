package top.rinoux.profile;

import java.io.Serializable;
import java.util.List;

/**
 * Created by rinoux on 2019-02-15.
 */
public class Profile implements Serializable {
    private String name;
    private String projectName;
    private List<RepositoryDescription> descriptions;


    public Profile(String name, String projectName, List<RepositoryDescription> descriptions) {
        this.name = name;
        this.projectName = projectName;
        this.descriptions = descriptions;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public List<RepositoryDescription> getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(List<RepositoryDescription> descriptions) {
        this.descriptions = descriptions;
    }

    public static class RepositoryDescription {

        String repoName;
        String repoBranch;


        public RepositoryDescription(String repoName, String repoBranch) {
            this.repoName = repoName;
            this.repoBranch = repoBranch;
        }

        public String getRepoName() {
            return repoName;
        }

        public void setRepoName(String repoName) {
            this.repoName = repoName;
        }

        public String getRepoBranch() {
            return repoBranch;
        }

        public void setRepoBranch(String repoBranch) {
            this.repoBranch = repoBranch;
        }
    }
}
