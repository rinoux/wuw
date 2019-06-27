package top.rinoux.git.service;


import top.rinoux.util.GeneralUtils;
import top.rinoux.config.Constants;
import top.rinoux.git.GitContentService;
import top.rinoux.git.arch.GitBranch;
import top.rinoux.git.arch.GitProject;
import top.rinoux.git.arch.GitRepo;
import top.rinoux.git.tool.RequestUtils;
import javafx.scene.image.Image;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * public static void main(String[] args) {
 * GogsContentService service= new GogsContentService("http://git.fanruan.com:3000/api/v1/", "test", "123456");
 * for (GitRepo test : service.getRepos("test")) {
 * System.out.println(test.getHttpHref());
 * System.out.println(test.getSshHref());
 * }
 * }
 * <p>
 * <p>
 * Created by rinoux on 2018/11/7.
 */
public class GogsContentService implements GitContentService {
    //http://git.fanruan.com:3000/api/v1/
    private String restEndPoint;
    private String username;
    private String password;


    public GogsContentService(String restEndPoint, String username, String password) {
        this.restEndPoint = restEndPoint;
        this.username = username;
        this.password = password;
    }

    @Override
    public GitRepo[] getRepos(String projectName) throws Exception {

        String url = GeneralUtils.pathJoin(restEndPoint, "user", Constants.SLUG_REPOS);
        String result = RequestUtils.request(username, password, url);

        List<GitRepo> repositories = new ArrayList<>();
        if (GeneralUtils.isNotEmpty(result)) {
            JSONArray ja = new JSONArray(result);
            if (ja.length() > 0) {
                for (Object o : ja) {
                    JSONObject ro = (JSONObject) o;
                    JSONObject owner = ro.getJSONObject("owner");
                    String username = owner.getString("username");
                    if (username.equals(projectName)) {
                        GitRepo repository = new GitRepo();

                        repository.setProjectName(username);
                        repository.setRepoName(ro.getString("name"));
                        repository.setId(ro.getString("id"));
                        repository.setPub(!ro.getBoolean("private"));
                        repository.setForkable(ro.getBoolean("fork"));
                        repository.setSshHref(ro.getString("ssh_url"));
                        repository.setHttpHref(ro.getString("clone_url"));
                        // TODO: 2018/11/7

                        repositories.add(repository);
                    }
                }
            }
        } else {
            throw new Exception("No result for request of " + url);
        }


        return repositories.toArray(new GitRepo[0]);
    }

    @Override
    public GitBranch[] getBranches(String projectName, String repoName) throws Exception {
        String url = GeneralUtils.pathJoin(restEndPoint, Constants.SLUG_REPOS, projectName, repoName, Constants.SLUG_BRANCHES);
        String result = RequestUtils.request(username, password, url);

        List<GitBranch> branches = new ArrayList<>();
        if (GeneralUtils.isNotEmpty(result)) {
            JSONArray ja = new JSONArray(result);


            if (ja.length() > 0) {
                for (Object o : ja) {
                    JSONObject bo = (JSONObject) o;
                    GitBranch branch = new GitBranch();
                    branch.setId(bo.getString("name"));
                    branch.setDisplayId(bo.getString("name"));
                    branch.setRepoName(repoName);


                    branches.add(branch);

                }
            }
        } else {
            throw new Exception("No result for request of " + url);
        }


        return branches.toArray(new GitBranch[0]);
    }

    @Override
    public GitProject[] getProjects() {

        Set<GitProject> projects = new HashSet<>();
        String url = GeneralUtils.pathJoin(restEndPoint, "user", Constants.SLUG_REPOS);
        String result = RequestUtils.request(username, password, url);

        if (GeneralUtils.isNotEmpty(result)) {
            JSONArray ja = new JSONArray(result);
            if (ja.length() > 0) {
                for (Object o : ja) {
                    JSONObject ro = (JSONObject) o;
                    JSONObject owner = ro.getJSONObject("owner");
                    String username = owner.getString("username");
                    int id = owner.getInt("id");
                    GitProject project = new GitProject();
                    project.setName(username);
                    project.setId(String.valueOf(id));
                    project.setKey(owner.getString("login"));

                    projects.add(project);
                }
            }
        }

        return projects.toArray(new GitProject[0]);
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
