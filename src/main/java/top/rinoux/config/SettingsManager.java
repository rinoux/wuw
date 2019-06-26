package top.rinoux.config;

import top.rinoux.util.GeneralUtils;
import org.json.JSONObject;
import top.rinoux.log.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * setting manager
 *
 * account settings and fetching history
 *
 * @author vito
 * @date 2018/5/1
 */
public class SettingsManager {

    private String gitType ="bitbucket";
    private String endpoint = "https://cloud.finedevelop.com";
    private String restVersion = "1.0";
    private String username;
    private String password;
    private String protocol = "http";

    /**
     * auto login
     */
    private boolean autoLogin = true;

    /**
     * 常用配置
     */
    private String project;
    private String branch = "feature/10.0";
    private String output;

    private List<AccountListener> listeners = new ArrayList<>();


    private static final SettingsManager INSTANCE = new SettingsManager();

    private SettingsManager() {
    }

    public static SettingsManager getInstance() {
        return INSTANCE;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        if (endpoint != null) {
            this.endpoint = endpoint;
        }
    }

    public String getRestVersion() {
        return restVersion;
    }

    public void setRestVersion(String restVersion) {
        if (restVersion != null) {
            this.restVersion = restVersion;
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        if (username != null) {
            this.username = username;
        }
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if (password != null) {
            this.password = password;
        }
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        if (protocol != null) {
            this.protocol = protocol;
        }
    }

    public String getGitType() {
        return gitType;
    }

    public void setGitType(String gitType) {
        this.gitType = gitType;
    }

    public boolean isAutoLogin() {
        return autoLogin;
    }

    public void setAutoLogin(boolean autoLogin) {
        this.autoLogin = autoLogin;
    }

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

    /**
     * save profile
     */
    public void save() {
        try {
            File tmpFolder = new File(Constants.TMP_DIR);
            if (!tmpFolder.exists()) {
                tmpFolder.mkdir();
            }
            File targetsFile = new File(tmpFolder, Constants.CONFIG);
            if (!targetsFile.exists()) {
                targetsFile.createNewFile();
            }

            JSONObject object = new JSONObject();
            if (autoLogin) {
                object.put("connection", new JSONObject()
                        .put("gitType", gitType)
                        .put("endpoint", endpoint)
                        .put("username", username)
                        .put("password", Base64.getEncoder().encodeToString(password.getBytes()))
                        .put("protocol", protocol)
                        .put("restVersion", restVersion));
            }
            object.put("target", new JSONObject()
                    .put("project", project)
                    .put("branch", branch)
                    .put("output", output));

            FileWriter fileWriter = new FileWriter(targetsFile);
            fileWriter.write(object.toString());
            fileWriter.close();
        } catch (Exception e) {
            LoggerFactory.getLogger().error(e.getMessage(), e);
        }
    }

    /**
     * load profile
     */
    public void load() {
        try {
            File settingsFile = new File(Constants.TMP_DIR, Constants.CONFIG);
            if (settingsFile.exists()) {
                StringBuilder sb = new StringBuilder();
                BufferedReader br = new BufferedReader(new FileReader(settingsFile));
                String line;
                if ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                JSONObject settings = new JSONObject(sb.toString());
                if (settings.has("connection")) {
                    JSONObject connection = settings.getJSONObject("connection");
                    setGitType(connection.optString("gitType", "bitbucket"));
                    setEndpoint(connection.getString("endpoint"));
                    setUsername(connection.getString("username"));
                    setPassword(new String(Base64.getDecoder().decode(connection.getString("password"))));
                    setProtocol(connection.getString("protocol"));
                    setRestVersion(connection.getString("restVersion"));
                }
                if (settings.has("target")) {
                    JSONObject target = settings.getJSONObject("target");
                    if (target.has("branch")) {
                        setBranch(target.getString("branch"));
                    }
                    if (target.has("output")) {
                        setOutput(target.getString("output"));
                    }
                    if (target.has("project")) {
                        setProject(target.getString("project"));
                    }
                }
            }
        } catch (Throwable e) {
            LoggerFactory.getLogger().error(e.getMessage(), e);
        }
    }

    /**
     * check account existence
     *
     * @return existence
     */
    public boolean checkAccountExist() {
        return GeneralUtils.isNotEmpty(username)
                && GeneralUtils.isNotEmpty(password)
                && GeneralUtils.isNotEmpty(endpoint);
    }

    /**
     * 添加账户改变监听
     */
    public void addAccountListener(AccountListener accountListener) {
        listeners.add(accountListener);
    }

    /**
     * 触发账户改变监听
     */
    public void fireAccountListener() {
        for (AccountListener accountListener : listeners) {
            try {
                accountListener.accountChange();
            } catch (Throwable e) {
                LoggerFactory.getLogger().error(e.getMessage(), e);
            }
        }
    }
}
