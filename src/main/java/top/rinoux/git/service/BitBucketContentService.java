package top.rinoux.git.service;

import top.rinoux.GeneralUtils;
import top.rinoux.config.Constants;
import top.rinoux.git.GitContentService;
import top.rinoux.git.arch.GitBranch;
import top.rinoux.git.arch.GitProject;
import top.rinoux.git.arch.GitRepo;
import top.rinoux.git.tool.RequestUtils;
import javafx.scene.image.Image;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import top.rinoux.log.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by rinoux on 2018/11/7.
 */
public class BitBucketContentService implements GitContentService {
    private static final int LIMIT = 200;

    private String restEndPoint;
    private String username;
    private String password;

    public BitBucketContentService(String restEndPoint, String username, String password) {
        this.restEndPoint = restEndPoint;
        this.username = username;
        this.password = password;
    }

    @Override
    public GitRepo[] getRepos(String projectName) throws Exception {
        String url = GeneralUtils.pathJoin(
                restEndPoint,
                Constants.SLUG_PROJECTS,
                projectName,
                Constants.SLUG_REPOS,
                "?limit=" + LIMIT);
        String rs = RequestUtils.request(username, password, url);
        Set<GitRepo> repositories = new HashSet<>();
        if (GeneralUtils.isNotEmpty(rs)) {
            JSONObject jo = new JSONObject(rs);
            JSONArray reposJA = jo.getJSONArray("values");
            if (reposJA != null) {
                for (int i = 0; i < reposJA.length(); i++) {
                    JSONObject repoJO = reposJA.getJSONObject(i);
                    GitRepo repo = new GitRepo();
                    repo.setProjectName(projectName);
                    repo.setRepoName(repoJO.getString("name"));
                    repo.setId(String.valueOf(repoJO.getInt("id")));
                    repo.setForkable(repoJO.getBoolean("forkable"));
                    repo.setSlug(repoJO.getString("slug"));
                    repo.setState(GitRepo.State.valueOf(repoJO.getString("state")));
                    repo.setPub(repoJO.getBoolean("public"));
                    repo.setScmId(repoJO.getString("scmId"));

                    JSONObject links = repoJO.getJSONObject("links");
                    JSONArray clone = links.getJSONArray("clone");
                    for (Object o : clone) {
                        JSONObject ho = (JSONObject) o;
                        if (ho.getString("name").equals("http")) {
                            repo.setHttpHref(ho.getString("href"));
                        }

                        if (ho.getString("name").equals("ssh")) {
                            repo.setSshHref(ho.getString("href"));
                        }

                    }
                    repositories.add(repo);
                }
            }
        }

        return repositories.toArray(new GitRepo[0]);
    }

    @Override
    public GitBranch[] getBranches(String projectName, String repoName) throws Exception {
        String url = GeneralUtils.pathJoin(
                restEndPoint,
                Constants.SLUG_PROJECTS,
                projectName,
                Constants.SLUG_REPOS,
                repoName,
                Constants.SLUG_BRANCHES,
                "?limit=" + LIMIT);
        String rs = RequestUtils.request(username, password, url);
        Set<GitBranch> branches = new HashSet<>();
        if (GeneralUtils.isNotEmpty(rs)) {
            JSONObject jo = new JSONObject(rs);
            JSONArray branchesJA = jo.getJSONArray("values");
            for (int i = 0; i < branchesJA.length(); i++) {
                JSONObject bo = branchesJA.getJSONObject(i);
                GitBranch branch = new GitBranch();
                branch.setProjectName(projectName);
                branch.setRepoName(repoName);
                branch.setId(bo.getString("id"));
                branch.setDefault(bo.getBoolean("isDefault"));
                branch.setDisplayId(bo.getString("displayId"));
                branch.setLatestCommit(bo.getString("latestCommit"));
                branch.setLatestChangeset(bo.getString("latestChangeset"));

                branches.add(branch);
            }
        }

        return branches.toArray(new GitBranch[0]);
    }

    @Override
    public GitProject[] getProjects() throws Exception {
        String url = GeneralUtils.pathJoin(
                restEndPoint,
                Constants.SLUG_PROJECTS,
                "?limit=" + LIMIT);
        String rs = RequestUtils.request(username, password, url);
        Set<GitProject> projects = new HashSet<>();
        if (GeneralUtils.isNotEmpty(rs)) {
            JSONObject jo = new JSONObject(rs);
            JSONArray prjValues = jo.getJSONArray("values");
            for (Object prjValue : prjValues) {
                JSONObject po = (JSONObject) prjValue;
                GitProject project = new GitProject();
                project.setName(po.getString("key"));
                project.setId(String.valueOf(po.getInt("id")));
                project.setDescription(po.optString("description"));
                project.setPub(po.getBoolean("public"));
                project.setType(GitRepo.Type.valueOf(po.optString("type")));
                JSONObject links = po.getJSONObject("links");
                JSONArray self = links.getJSONArray("self");

                if (self.length() > 0) {
                    project.setHref(self.getJSONObject(0).getString("href"));
                }

                projects.add(project);
            }
        }


        return projects.toArray(new GitProject[0]);
    }


    /**
     * validate account
     *
     * @return valid
     */
    public boolean validate() {
        try {
            String url = GeneralUtils.pathJoin(restEndPoint, Constants.USERS, username);
            String rs = RequestUtils.request(username, password, url);
            return GeneralUtils.isNotEmpty(rs) && !new JSONObject(rs).has("errors");
        } catch (JSONException e) {
            LoggerFactory.getLogger().error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public Image loadAvatar() {
        String s = GeneralUtils.pathJoin(restEndPoint, Constants.USERS, username, Constants.AVATAR);
        return new Image(s, true);
    }
}
