package top.rinoux.git;

import top.rinoux.GeneralUtils;
import top.rinoux.git.service.BitBucketContentService;
import top.rinoux.git.service.GogsContentService;

/**
 * Created by rinoux on 2018/11/7.
 */
public class ServiceManager {


    public static GitContentService createService(String gitServerType, String restEndpoint, String username, String password) throws Exception {
        if (gitServerType.equalsIgnoreCase("bitbucket")) {
            return new BitBucketContentService(restEndpoint, username, password);
        } else if (gitServerType.equalsIgnoreCase("gogs")) {
            restEndpoint = GeneralUtils.pathJoin(restEndpoint, "api", "v1");
            return new GogsContentService(restEndpoint, username, password);
        }


        throw new Exception("Unsupported Git type...");
    }


    public static String[] getTypeSupport() {
        return new String[]{
                "bitbucket",
                "gogs",
        };
    }
}
