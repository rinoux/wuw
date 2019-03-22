package top.rinoux.git.arch;

/**
 * Created by rinoux on 2018/11/7.
 */
public class GitProject {

    private String id;
    private String key;
    private String name;
    private String href;
    private String description;
    private boolean pub;
    private GitRepo.Type type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isPub() {
        return pub;
    }

    public void setPub(boolean pub) {
        this.pub = pub;
    }

    public GitRepo.Type getType() {
        return type;
    }

    public void setType(GitRepo.Type type) {
        this.type = type;
    }
}
