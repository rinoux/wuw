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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GitProject)) return false;

        GitProject that = (GitProject) o;

        if (isPub() != that.isPub()) return false;
        if (!getId().equals(that.getId())) return false;
        if (getKey() != null ? !getKey().equals(that.getKey()) : that.getKey() != null) return false;
        if (!getName().equals(that.getName())) return false;
        if (getHref() != null ? !getHref().equals(that.getHref()) : that.getHref() != null) return false;
        if (getDescription() != null ? !getDescription().equals(that.getDescription()) : that.getDescription() != null)
            return false;
        return getType() == that.getType();
    }

    @Override
    public int hashCode() {
        int result = getId().hashCode();
        result = 31 * result + (getKey() != null ? getKey().hashCode() : 0);
        result = 31 * result + getName().hashCode();
        result = 31 * result + (getHref() != null ? getHref().hashCode() : 0);
        result = 31 * result + (getDescription() != null ? getDescription().hashCode() : 0);
        result = 31 * result + (isPub() ? 1 : 0);
        result = 31 * result + (getType() != null ? getType().hashCode() : 0);
        return result;
    }
}
