package top.rinoux;

import top.rinoux.config.Constants;
import top.rinoux.config.SettingsManager;
import top.rinoux.git.GitContentService;
import top.rinoux.git.ServiceManager;
import top.rinoux.git.arch.GitBranch;
import top.rinoux.git.arch.GitProject;
import top.rinoux.git.arch.GitRepo;
import top.rinoux.git.tool.GITTools;
import top.rinoux.log.LoggerFactory;
import javafx.scene.image.Image;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 构建此helper用于下代码
 * <p>
 * <p>
 * 如
 * public static void main(String[] args) {
 * CodeUpdateHelper helper = new CodeUpdateHelper.Builder()
 * .setGitType("bitbucket")
 * .setEndPoint("https://cloud.finedevelop.com/")
 * .setUsername("git")
 * .setPassword("FineReport2012")
 * .build();
 * <p>
 * <p>
 * //获取所有project
 * String[] prjNames = helper.getProjects();
 * <p>
 * <p>
 * //获取PG下的所有repo
 * GitRepo[] repositories = helper.getRepos("PG");
 * for (GitRepo repository : repositories) {
 * repository.getHttpHref();
 * }
 * }
 * <p>
 * Created by rinoux on 2018/4/11.
 */
@SuppressWarnings("all")
public class CodeUpdateHelper {
    private String gitType;
    private String endpoint;
    private String restVersion = "1.0";
    private String username;
    private String password;
    private String protocol = "http";

    private GitContentService service;
    private GITTools gitTools;

    private static CodeUpdateHelper INSTANCE = null;


    public static CodeUpdateHelper getInstance() {
        if (INSTANCE == null) {
            CodeUpdateHelper helper = null;
            try {
                helper = CodeUpdateHelper.newBuilder()
                        .setGitType(SettingsManager.getInstance().getGitType())
                        .setEndPoint(SettingsManager.getInstance().getEndpoint())
                        .setUsername(SettingsManager.getInstance().getUsername())
                        .setPassword(SettingsManager.getInstance().getPassword())
                        .build();
                if (helper.validate()) {
                    INSTANCE = helper;
                } else {
                    throw new Exception("Wrong git setting!");
                }
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }

        }
        return INSTANCE;
    }


    public File[] cloneOrPullProjects(String... projectNames) {
        File[] allUpdateDirs = new File[0];
        Set<File> all = new HashSet<File>();
        try {
            for (String projectName : projectNames) {
                GitRepo[] repos = service.getRepos(projectName);
                for (GitRepo repo : repos) {
                    File repoRoot = new File("fine-git-repos", projectName);
                    File[] files = updateLocalRepo(repoRoot, repo);
                    allUpdateDirs = GeneralUtils.addAll(allUpdateDirs, files);
                }
                LoggerFactory.getLogger().info("All projects help " + projectName + " were updated!");
            }
        } catch (Exception e) {
            LoggerFactory.getLogger().error(e.getMessage(), e);
        }

        Collections.addAll(all, allUpdateDirs);
        return all.toArray(new File[all.size()]);
    }


    public File[] cloneOrPullProjects(File dir, String... projectNames) {
        File[] allUpdateDirs = new File[0];
        Set<File> all = new HashSet<File>();
        try {
            for (String projectName : projectNames) {
                GitRepo[] repos = service.getRepos(projectName);
                for (GitRepo repo : repos) {
                    File repoRoot = new File(dir, projectName);
                    File[] files = updateLocalRepo(repoRoot, repo);
                    allUpdateDirs = GeneralUtils.addAll(allUpdateDirs, files);
                }
                LoggerFactory.getLogger().info("All projects help " + projectName + " were updated!");
            }
        } catch (Exception e) {
            LoggerFactory.getLogger().error(e.getMessage(), e);
        }


        Collections.addAll(all, allUpdateDirs);
        return all.toArray(new File[all.size()]);
    }


    /**
     * 将某个project下的一些repo， clone/pull到dir目录
     *
     * @param dir
     * @param projectName
     * @param repos
     * @return 更新的代码所在路径集合
     */
    public File[] cloneOrPullRepos(File dir, String projectName, String branch, String... repos) {
        Set<File> localDirs = new HashSet<File>();
        try {
            List<GitRepo> repositories = new ArrayList<>();
            Collections.addAll(repositories, service.getRepos(projectName));

            for (String repo : repos) {
                GitRepo currentRepo = null;
                for (GitRepo repository : repositories) {
                    if (repository.getRepoName().equals(repo)) {
                        currentRepo = repository;
                    }
                }
                GitBranch[] branches = service.getBranches(projectName, repo);
                for (GitBranch gitBranch : branches) {
                    File repoFile = new File(dir, gitBranch.getRepoName());

                    localDirs.add(cloneOrPullRepo(currentRepo.getHttpHref(), gitBranch.getDisplayId(), repoFile));
                }

            }

            LoggerFactory.getLogger().info("project " + projectName + " were updated!");
        } catch (Exception e) {
            LoggerFactory.getLogger().error(e.getMessage(), e);
        }
        return localDirs.toArray(new File[localDirs.size()]);
    }


    public File cloneOrPullRepo(File dir, String projectName, String repoName, String branch) {
        GitRepo[] repos = getRepos(projectName);
        for (GitRepo repo : repos) {
            if (repo.getRepoName().equals(repoName)) {
                return cloneOrPullRepo(repo.getHttpHref(), branch, dir);
            }
        }

        throw new NullPointerException(repoName + " help " + projectName + " does not exist!");
    }


    public File cloneOrPullRepo(String gitHref, String branch, File dir) {
        return gitTools.cloneOrPull(gitHref, branch, dir);
    }


    public void update(File repoDir, String branch) {
        gitTools.pull(repoDir.getName(), branch, repoDir);
    }


    public String[] getProjects() {
        try {
            GitProject[] projects = service.getProjects();
            String[] projectNames = new String[projects.length];
            for (int i = 0; i < projects.length; i++) {
                projectNames[i] = projects[i].getName();
            }
            return projectNames;
        } catch (Exception e) {
            LoggerFactory.getLogger().error(e.getMessage(), e);
            return new String[0];
        }
    }

    public GitRepo[] getRepos(String projectName) {
        try {
            return service.getRepos(projectName);
        } catch (Exception e) {
            LoggerFactory.getLogger().error(e.getMessage(), e);
            return new GitRepo[0];
        }
    }

    public String[] getBranchNames(String projectName, String repoName) {
        try {
            GitBranch[] branches = service.getBranches(projectName, repoName);
            String[] branchNames = new String[branches.length];
            for (int i = 0; i < branches.length; i++) {
                branchNames[i] = branches[i].getDisplayId();
            }

            return branchNames;
        } catch (Exception e) {
            LoggerFactory.getLogger().error(e.getMessage(), e);
            return new String[0];
        }
    }


    public Image getAvatar() {
        return this.service.loadAvatar();
    }


    public Image getAvatar(String gitType, String host, String username) {
        try {
            return ServiceManager.createService(gitType, host, username, null).loadAvatar();
        } catch (Exception e) {
            return null;
        }
    }


    public boolean validate() {
        return this.service.validate();
    }

    private File[] updateLocalRepo(File dir, GitRepo repo) {
        try {
            String repoName = repo.getRepoName();
            String gitHref = repo.getHttpHref();
            GitBranch[] branches = service.getBranches(repo.getProjectName(), repo.getRepoName());

            if (GeneralUtils.isNotEmpty(gitHref)) {
                if (branches != null && branches.length > 0) {
                    Set<File> localDirs = new HashSet<File>();
                    for (GitBranch branch : branches) {
                        //dir-release-10.0
                        String repoBranchPath = GeneralUtils.pathJoin(dir.getPath(), repo.getRepoName(), branch.getDisplayId().replace('/', '-'));
                        //File repo = new File(dir, repo.getRepoName().concat("-").concat(branch.replace('/', '-')));
                        File repoFile = new File(repoBranchPath);
                        localDirs.add(cloneOrPullRepo(gitHref, branch.getDisplayId(), repoFile));
                    }
                    return localDirs.toArray(new File[localDirs.size()]);
                }
            }


            LoggerFactory.getLogger().info("No repo url found!");
        } catch (Exception e) {
            LoggerFactory.getLogger().error(e.getMessage(), e);
        }
        return new File[0];
    }


    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        CodeUpdateHelper helper = new CodeUpdateHelper();


        public Builder setEndPoint(String endPoint) {
            helper.endpoint = endPoint;
            return this;
        }

        public Builder setRestVersion(String version) {
            helper.restVersion = version;
            return this;
        }

        public Builder setProtocol(String protocol) {
            helper.protocol = protocol;
            return this;
        }

        public Builder setUsername(String username) {
            helper.username = username;
            return this;
        }

        public Builder setPassword(String password) {
            helper.password = password;
            return this;
        }

        public Builder setGitType(String type) {
            helper.gitType = type;
            return this;
        }

        public CodeUpdateHelper build() throws Exception {

            if (GeneralUtils.isEmpty(helper.endpoint)) {
                throw new NullPointerException("endpoint should not be empty!");
            }

            if (GeneralUtils.isEmpty(helper.username)) {
                throw new NullPointerException("username should not be empty!");
            }

            if (GeneralUtils.isEmpty(helper.password)) {
                throw new NullPointerException("password should not be empty!");
            }

            String restUrl = GeneralUtils.pathJoin(helper.endpoint, Constants.REST_API, helper.restVersion);
            helper.service = ServiceManager.createService(helper.gitType, restUrl, helper.username, helper.password);
            helper.gitTools = new GITTools(new UsernamePasswordCredentialsProvider(helper.username, helper.password));
            return helper;
        }

    }
}
