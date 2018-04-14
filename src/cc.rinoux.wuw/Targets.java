package cc.rinoux.wuw;
import java.io.Serializable;

/**
 * Created by rinoux on 2018/4/13.
 */
public class Targets implements Serializable {

    private String project;
    private String branch;
    private String output;
    private String reposStr;

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }


    public String getReposStr() {
        return reposStr;
    }

    public void setReposStr(String reposStr) {
        this.reposStr = reposStr;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Targets{");
        sb.append("project='").append(project).append('\'');
        sb.append(", branch='").append(branch).append('\'');
        sb.append(", output='").append(output).append('\'');
        sb.append(", reposStr='").append(reposStr).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
