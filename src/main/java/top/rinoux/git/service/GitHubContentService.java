package top.rinoux.git.service;

import top.rinoux.git.GitContentService;
import top.rinoux.git.arch.GitBranch;
import top.rinoux.git.arch.GitProject;
import top.rinoux.git.arch.GitRepo;
import javafx.scene.image.Image;

/**
 * Created by rinoux on 2019-03-20.
 */
public class GitHubContentService implements GitContentService {
    @Override
    public GitRepo[] getRepos(String projectName) throws Exception {
        return new GitRepo[0];
    }

    @Override
    public GitBranch[] getBranches(String projectName, String repoName) throws Exception {
        return new GitBranch[0];
    }

    @Override
    public GitProject[] getProjects() throws Exception {
        return new GitProject[0];
    }

    @Override
    public boolean validate() {
        return false;
    }

    @Override
    public Image loadAvatar() {
        return null;
    }
}
