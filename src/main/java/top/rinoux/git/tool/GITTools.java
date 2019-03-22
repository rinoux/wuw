package top.rinoux.git.tool;

import top.rinoux.GeneralUtils;
import top.rinoux.log.LoggerFactory;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.File;

/**
 * Created by rinoux on 2018/4/11.
 */
public class GITTools {

    private UsernamePasswordCredentialsProvider credentials;
    private String localRepoDir;


    public GITTools(UsernamePasswordCredentialsProvider credentials) {
        this.credentials = credentials;
        this.localRepoDir = "fine-git-repos";
    }

    public GITTools(UsernamePasswordCredentialsProvider credentials, String localRepoDir) {
        this.credentials = credentials;
        this.localRepoDir = localRepoDir;
    }

    /**
     * clone  to default dir
     *
     * @param repoName repo name
     * @param gitHref  git href
     * @param branch   branch
     */
    public void clone(String repoName, String gitHref, String branch) {
        File target = getGitDir(repoName, branch);
        clone(gitHref, branch, target);
    }

    /**
     * clone
     *
     * @param gitHref git href
     * @param branch  branch
     * @param dir     local dir
     */
    public void clone(String gitHref, String branch, File dir) {

        String repoName = getRepoName(gitHref);
        try {
            CloneCommand cloneCommand = Git.cloneRepository();
            LoggerFactory.getLogger().info("Cloning " + repoName + "(branch:" + branch + ") to " + dir.getAbsolutePath() + "...");
            cloneCommand
                    .setURI(gitHref)//远程git地址
                    .setBranch(branch)//分支
                    .setDirectory(dir)//本地路径
                    .setCredentialsProvider(this.credentials)//验证
                    .call();

        } catch (Exception e) {
            LoggerFactory.getLogger().error("Clone " + gitHref + "(branch:" + branch + ") counter error! \n" + e.getMessage(), e);
        }

    }


    /**
     * pull代码
     *
     * @param repoName repo名称
     * @param branch   分支
     * @param dir      本地目录
     */
    public void pull(String repoName, String branch, File dir) {
        try {
            Git git = new Git(new FileRepository(new File(dir, ".git")));
            LoggerFactory.getLogger().info("Pulling " + repoName + "(branch:" + branch + ")...");
            git.pull()
                    .setRemoteBranchName(branch)
                    .setCredentialsProvider(this.credentials)
                    .call();

        } catch (Exception e) {
            LoggerFactory.getLogger().error("Pull " + repoName + "(branch:" + branch + ") counter error! \n" + e.getMessage(), e);
        }

    }

    /**
     * pull
     *
     * @param repoName repo
     * @param dir      local dir
     */
    public void pull(String repoName, File dir) {
        try {
            Git git = new Git(new FileRepository(dir));
            LoggerFactory.getLogger().info("Pulling " + repoName + "...");
            git.pull()
                    .setCredentialsProvider(this.credentials)
                    .call();

        } catch (Exception e) {
            LoggerFactory.getLogger().error("Pull " + repoName + " counter error! \n" + e.getMessage(), e);

        }
    }


    public File cloneOrPull(String gitHref, String branch, File dir) {
        String repoName = getRepoName(gitHref);
        if (GeneralUtils.isNotEmpty(gitHref)) {
            if (GeneralUtils.isEmpty(branch)) {
                if (dir.exists()) {
                    pull(repoName, dir);
                } else {
                    clone(gitHref, branch, dir);
                }

            } else {
                if (dir.exists()) {
                    pull(repoName, branch, dir);
                } else {
                    clone(gitHref, branch, dir);
                }
            }
            return dir;
        }
        return null;
    }


    private File getGitDir(String repoName, String branch) {
        if (GeneralUtils.isNotEmpty(branch)) {
            branch = branch.replace("/", "-");
            repoName = repoName.concat("(").concat(branch).concat(")");
            return new File(localRepoDir, repoName);
        } else {
            return getGitDir(repoName);
        }
    }


    private File getGitDir(String repoName) {
        return new File(localRepoDir, repoName);
    }


    private static String getRepoName(String gitHref) {
        int idx = gitHref.lastIndexOf("/");
        if (idx > -1) {
            String tmp = gitHref.substring(idx + 1);
            return tmp.substring(0, tmp.indexOf(".git"));
        }

        return gitHref;
    }

}
