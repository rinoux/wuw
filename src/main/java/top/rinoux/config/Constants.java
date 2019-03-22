package top.rinoux.config;

import top.rinoux.GeneralUtils;


/**
 * some constants
 *
 * @author rinoux
 * @date 2018/4/11
 */
public class Constants {

    private static String userHome;

    static {
        userHome = System.getProperty("user.home");
        if (userHome == null) {
            userHome = System.getProperty("userHome");
        }
    }

    public static final String TMP_DIR = GeneralUtils.pathJoin(userHome, ".WUW");
    public static final String SETTINGS_FILE_NAME = "wuw_settings";
    public static final String TARGETS_FILE_NAME = "wuw_last_targets";
    public static final String CONFIG = "config.json";


    public static final String REST_API = "rest/api";
    public static final String REST_API_VERSION = "1.0";
    public static final String SLUG_PROJECTS = "projects";
    public static final String USERS = "users";
    public static final String AVATAR = "avatar.png";
    public static final String SLUG_REPOS = "repos";
    public static final String SLUG_BRANCHES = "branches";
}
