package top.rinoux.git.arch;


/**
 * Created by rinoux on 2018/11/7.
 */
public class GitRepo {

    private String projectName;
    private String repoName;
    private String id;
    private String slug;
    private State state;
    private boolean pub;
    private boolean forkable;
    private String scmId;
    private String httpHref;
    private String sshHref;

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getRepoName() {
        return repoName;
    }

    public void setRepoName(String repoName) {
        this.repoName = repoName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public boolean isPub() {
        return pub;
    }

    public void setPub(boolean pub) {
        this.pub = pub;
    }

    public boolean isForkable() {
        return forkable;
    }

    public void setForkable(boolean forkable) {
        this.forkable = forkable;
    }

    public String getScmId() {
        return scmId;
    }

    public void setScmId(String scmId) {
        this.scmId = scmId;
    }

    public String getHttpHref() {
        return httpHref;
    }

    public void setHttpHref(String httpHref) {
        this.httpHref = httpHref;
    }

    public String getSshHref() {
        return sshHref;
    }

    public void setSshHref(String sshHref) {
        this.sshHref = sshHref;
    }


    /**
     * state
     *
     * not used yet
     */
    public enum State {
        AVAILABLE;
    }


    /**
     * type
     *
     * not used yet
     */
    public enum Type {
        PERSONAL,
        BRANCH,
        NORMAL;
    }
}
