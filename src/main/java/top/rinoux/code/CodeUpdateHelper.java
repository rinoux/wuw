package top.rinoux.code;

import top.rinoux.util.GeneralUtils;
import top.rinoux.config.Constants;
import top.rinoux.config.SettingsManager;
import top.rinoux.exception.AccountErrorException;
import top.rinoux.git.GitContentService;
import top.rinoux.git.ServiceManager;
import top.rinoux.git.arch.GitBranch;
import top.rinoux.git.arch.GitProject;
import top.rinoux.git.arch.GitRepo;
import top.rinoux.git.tool.GITTools;
import top.rinoux.log.LoggerFactory;
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

    private static CodeUpdateHelper current = null;

    public static CodeUpdateHelper getCurrent() throws Exception {
        if (current == null) {
            CodeUpdateHelper helper = null;
            try {
                helper = CodeUpdateHelper.newBuilder()
                        .setGitType(SettingsManager.getInstance().getGitType())
                        .setEndPoint(SettingsManager.getInstance().getEndpoint())
                        .setUsername(SettingsManager.getInstance().getUsername())
                        .setPassword(SettingsManager.getInstance().getPassword())
                        .build();
                if (helper.validate()) {
                    current = helper;
                } else {
                    throw new AccountErrorException("Error git account setting!");
                }
            } catch (Exception e) {
                throw e;
            }

        }
        return current;
    }


    public static void setCurrent(CodeUpdateHelper current) {
        CodeUpdateHelper.current = current;
    }

    public File[] cloneOrPullProjects(String... projectNames) throws Exception {
        File[] allUpdateDirs = new File[0];
        Set<File> all = new HashSet<File>();
        for (String projectName : projectNames) {
            GitRepo[] repos = service.getRepos(projectName);
            for (GitRepo repo : repos) {
                File repoRoot = new File("fine-git-repos", projectName);
                File[] files = updateLocalRepo(repoRoot, repo);
                allUpdateDirs = GeneralUtils.addAll(allUpdateDirs, files);
            }
            LoggerFactory.getLogger().info("All projects help " + projectName + " were updated!");
        }
        Collections.addAll(all, allUpdateDirs);
        return all.toArray(new File[all.size()]);
    }


    public File[] cloneOrPullProjects(File dir, String... projectNames) throws Exception {
        File[] allUpdateDirs = new File[0];
        Set<File> all = new HashSet<File>();

        for (String projectName : projectNames) {
            GitRepo[] repos = service.getRepos(projectName);
            for (GitRepo repo : repos) {
                File repoRoot = new File(dir, projectName);
                File[] files = updateLocalRepo(repoRoot, repo);
                allUpdateDirs = GeneralUtils.addAll(allUpdateDirs, files);
            }
            LoggerFactory.getLogger().info("All projects help " + projectName + " were updated!");
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
    public File[] cloneOrPullRepos(File dir, String projectName, String branch, String... repos) throws Exception {
        Set<File> localDirs = new HashSet<File>();
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
        return localDirs.toArray(new File[localDirs.size()]);
    }


    public File cloneOrPullRepo(File dir, String projectName, String repoName, String branch) throws Exception {
        GitRepo[] repos = getRepos(projectName);
        for (GitRepo repo : repos) {
            if (repo.getRepoName().equals(repoName)) {
                return cloneOrPullRepo(repo.getHttpHref(), branch, dir);
            }
        }

        throw new NullPointerException(repoName + " help " + projectName + " does not exist!");
    }


    public File cloneOrPullRepo(String gitHref, String branch, File dir) throws Exception {
        return gitTools.cloneOrPull(gitHref, branch, dir);
    }


    public void update(File repoDir, String branch) throws Exception {
        gitTools.pull(repoDir.getName(), branch, repoDir);
    }


    public String[] getProjects() throws Exception {
        GitProject[] projects = service.getProjects();
        String[] projectNames = new String[projects.length];
        for (int i = 0; i < projects.length; i++) {
            projectNames[i] = projects[i].getName();
        }
        return projectNames;
    }

    public GitRepo[] getRepos(String projectName) throws Exception {
        return service.getRepos(projectName);
    }

    public String[] getBranchNames(String projectName, String repoName) throws Exception {
        GitBranch[] branches = service.getBranches(projectName, repoName);
        String[] branchNames = new String[branches.length];
        for (int i = 0; i < branches.length; i++) {
            branchNames[i] = branches[i].getDisplayId();
        }

        return branchNames;
    }


    public String getAvatar() {
        return this.service.loadAvatar();
    }



    public boolean validate() {
        return this.service.validate();
    }


    public String getDefaultProject() {
        return this.service.getDefaultProject();
    }

    private File[] updateLocalRepo(File dir, GitRepo repo) throws Exception {
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
                throw new AccountErrorException("endpoint should not be empty!");
            }

            if (GeneralUtils.isEmpty(helper.username)) {
                throw new AccountErrorException("username should not be empty!");
            }

            if (GeneralUtils.isEmpty(helper.password)) {
                throw new AccountErrorException("password should not be empty!");
            }

            String restUrl = GeneralUtils.pathJoin(helper.endpoint, Constants.REST_API, helper.restVersion);
            helper.service = ServiceManager.createService(helper.gitType, restUrl, helper.username, helper.password);
            helper.gitTools = new GITTools(new UsernamePasswordCredentialsProvider(helper.username, helper.password));
            return helper;
        }

    }
}
