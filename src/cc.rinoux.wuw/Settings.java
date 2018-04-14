package cc.rinoux.wuw;

import java.io.Serializable;

/**
 * Created by rinoux on 2018/4/13.
 */
public class Settings implements Serializable {

    private String endpoint;
    private String restVersion;
    private String username;
    private String password;
    private String protocol;


    public static Settings getDefault() {
        return new Settings();
    }
    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getRestVersion() {
        return restVersion;
    }

    public void setRestVersion(String restVersion) {
        this.restVersion = restVersion;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Settings{");
        sb.append("endpoint='").append(endpoint).append('\'');
        sb.append(", restVersion='").append(restVersion).append('\'');
        sb.append(", username='").append(username).append('\'');
        sb.append(", password='").append(password).append('\'');
        sb.append(", protocol='").append(protocol).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
