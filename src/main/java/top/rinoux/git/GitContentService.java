package top.rinoux.git;

import top.rinoux.git.arch.GitBranch;
import top.rinoux.git.arch.GitProject;
import top.rinoux.git.arch.GitRepo;
import javafx.scene.image.Image;

/**
 * Created by rinoux on 2018/11/7.
 */
public interface GitContentService {


    /**
     * get repos under project
     *
     * @param projectName prj name
     * @return repos
     */
    GitRepo[] getRepos(String projectName) throws Exception;

    /**
     * get branches of repo
     *
     * @param projectName project name of repo
     * @param repoName    repo name
     * @return branches
     */
    GitBranch[] getBranches(String projectName, String repoName) throws Exception;


    /**
     * get all projects
     *
     * @return
     */
    GitProject[] getProjects() throws Exception;


    /**
     * validate username&password
     *
     * @return valid
     */
    boolean validate();


    /**
     * load avatar....
     *
     * @return image object
     */
    String loadAvatar();




    String getDefaultProject();
}
