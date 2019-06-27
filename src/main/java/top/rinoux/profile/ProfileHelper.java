package top.rinoux.profile;

import org.json.JSONArray;
import org.json.JSONObject;
import top.rinoux.util.GeneralUtils;
import top.rinoux.git.tool.Filter;
import top.rinoux.log.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rinoux on 2019-02-15.
 */
public class ProfileHelper {

    private static String prjName = null;
    private static List<Profile.RepositoryDescription> currentDescriptions = new ArrayList<>();


    private static final String PERSIST_DIR = GeneralUtils.pathJoin(System.getProperty("user.home"), ".wuw/profiles");


    static {
        assert new File(PERSIST_DIR).mkdirs();
    }

    public static List<Profile.RepositoryDescription> getCurrentDescriptions() {
        return currentDescriptions;
    }

    public static void setCurrentDescriptions(String prjName, List<Profile.RepositoryDescription> currentDescriptions) {
        ProfileHelper.prjName = prjName;
        ProfileHelper.currentDescriptions = currentDescriptions;
    }

    private static void saveProfile(Profile profile) {

        profileToFile(profile);
    }

    public static void saveProfile(String name) {
        if (currentDescriptions.size() > 0) {
            saveProfile(new Profile(name, prjName, currentDescriptions));
        }

    }


    public static String[] getProfileNames() {
        return GeneralUtils.list(PERSIST_DIR, new Filter<String>() {
            @Override
            public boolean accept(String s) {
                return !s.equals(".DS_Store");
            }
        });
    }

    public static Profile[] readProfiles() {
        File[] files = new File(PERSIST_DIR).listFiles();
        if (files != null && files.length > 0) {
            Profile[] profiles = new Profile[files.length];
            for (int i = 0; i < files.length; i++) {
                profiles[i] = FileToProfile(files[i]);
            }

            return profiles;
        }


        return null;
    }


    public static Profile readProfile(String name) {
        File file = new File(PERSIST_DIR, name);

        return FileToProfile(file);
    }


    public static void clearAllProfiles() {
        GeneralUtils.delete(PERSIST_DIR);
    }


    public static void removeProfile(String name) {
        File[] files = new File(PERSIST_DIR).listFiles();
        assert files != null;
        for (File file : files) {
            assert !file.getName().equals(name) || file.delete();
        }
    }


    private static void profileToFile(Profile profile) {
        File dest = new File(PERSIST_DIR, profile.getName());

        try {
            JSONObject jo = new JSONObject();
            jo.put("projectName", profile.getProjectName());
            JSONArray ja = new JSONArray();
            for (Profile.RepositoryDescription description : profile.getDescriptions()) {
                JSONObject deso = new JSONObject();

                deso.put("repoName", description.getRepoName());
                deso.put("repoBranch", description.getRepoBranch());
                ja.put(deso);
            }
            jo.put("descriptions", ja);
            assert dest.exists() || dest.createNewFile();
            FileWriter fw = new FileWriter(dest);
            fw.write(jo.toString());
            fw.flush();
            fw.close();
        } catch (IOException e) {
            LoggerFactory.getLogger().error(e.getMessage(), e);
        }

    }

    private static Profile FileToProfile(File file) {
        try {
            FileReader fr = new FileReader(file);
            char[] buffer = new char[1024];
            StringBuilder sb = new StringBuilder();
            while (fr.read(buffer) != -1) {
                sb.append(new String(buffer));
            }

            fr.close();

            JSONObject jo = new JSONObject(sb.toString().trim());
            String projName = jo.getString("projectName");
            JSONArray ja = jo.getJSONArray("descriptions");

            List<Profile.RepositoryDescription> descriptions = new ArrayList<>();
            for (Object o : ja) {

                JSONObject deso = (JSONObject) o;
                Profile.RepositoryDescription description = new Profile.RepositoryDescription(
                        deso.getString("repoName"),
                        deso.getString("repoBranch")
                );

                descriptions.add(description);
            }

            return new Profile(file.getName(), projName, descriptions);
        } catch (IOException e) {
            LoggerFactory.getLogger().error(e.getMessage(), e);
        }

        return null;
    }

}
