package top.rinoux.git.service;

import org.json.JSONArray;
import org.json.JSONObject;
import top.rinoux.config.Constants;
import top.rinoux.git.GitContentService;
import top.rinoux.git.arch.GitBranch;
import top.rinoux.git.arch.GitProject;
import top.rinoux.git.arch.GitRepo;
import javafx.scene.image.Image;
import top.rinoux.git.tool.RequestUtils;
import top.rinoux.log.LoggerFactory;
import top.rinoux.util.GeneralUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by rinoux on 2019-03-20.
 */
public class GitHubContentService implements GitContentService {

    public static final String GITHUB_ENDPOINT = "https://api.github.com/";
    //private static final String GITHUB_ENDPOINT = "https://api.github.com/graphql";

    private GitContentService service;

    private String username;

    private String password;

    public GitHubContentService(String username, String password) {
        this.username = username;
        this.password = password;
        service = new GogsContentService(GITHUB_ENDPOINT, username, password);
    }

    @Override
    public GitRepo[] getRepos(String projectName) throws Exception {
        String url = GeneralUtils.pathJoin(GITHUB_ENDPOINT, "user", Constants.SLUG_REPOS);
        String result = RequestUtils.request(username, password, url);

        List<GitRepo> repositories = new ArrayList<>();
        if (GeneralUtils.isNotEmpty(result)) {
            JSONArray ja = new JSONArray(result);
            if (ja.length() > 0) {
                for (Object o : ja) {
                    JSONObject ro = (JSONObject) o;
                    JSONObject owner = ro.getJSONObject("owner");
                    String username = owner.getString("login");
                    if (username.equals(projectName)) {
                        GitRepo repository = new GitRepo();

                        repository.setProjectName(username);
                        repository.setRepoName(ro.getString("name"));
                        repository.setId(String.valueOf(ro.getInt("id")));
                        repository.setPub(!ro.getBoolean("private"));
                        repository.setForkable(ro.getBoolean("fork"));
                        repository.setSshHref(ro.getString("ssh_url"));
                        repository.setHttpHref(ro.getString("clone_url"));

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
        String url = GeneralUtils.pathJoin(GITHUB_ENDPOINT, Constants.SLUG_REPOS, projectName, repoName, Constants.SLUG_BRANCHES);
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
    public GitProject[] getProjects() throws Exception {
        Set<GitProject> projects = new HashSet<>();
        String url = GeneralUtils.pathJoin(GITHUB_ENDPOINT, "user", Constants.SLUG_REPOS);
        String result = RequestUtils.request(username, password, url);

        if (GeneralUtils.isNotEmpty(result)) {
            JSONArray ja = new JSONArray(result);
            if (ja.length() > 0) {
                for (Object o : ja) {
                    JSONObject ro = (JSONObject) o;
                    JSONObject owner = ro.getJSONObject("owner");
                    String username = owner.getString("login");
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
        try {
            String url = GeneralUtils.pathJoin(GITHUB_ENDPOINT, Constants.USERS, username);
            String rs = RequestUtils.request(username, password, url);
            return GeneralUtils.isNotEmpty(rs) && !new JSONObject(rs).has("errors");
        } catch (Exception e) {
            LoggerFactory.getLogger().error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public String loadAvatar() {
        String url = GeneralUtils.pathJoin(GITHUB_ENDPOINT, Constants.USERS, username);
        String rs = RequestUtils.request(username, password, url);

        if (GeneralUtils.isNotEmpty(rs)) {
            JSONObject jo = new JSONObject(rs);

            String avatarUrl = jo.getString("avatar_url");
            if (GeneralUtils.isNotEmpty(avatarUrl)) {
                return avatarUrl;
            }

        }

        return null;
    }

    @Override
    public String getDefaultProject() {
        return username;
    }
}
