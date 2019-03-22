package top.rinoux.git.tool;

import top.rinoux.log.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Created by rinoux on 2018/4/11.
 */
public class RequestUtils {
    /**
     * request the url
     *
     * @param URLStr url string
     * @return result
     */
    public static String request(String userName, String password, final String URLStr) {

        StringBuilder sb = new StringBuilder();
        try {
            byte[] auth = Base64.getEncoder().encode(userName.concat(":").concat(password).getBytes());
            //use basic auth
            String encode = new String(auth);
            URL url = new URL(URLStr);
            URLConnection connection = url.openConnection();

            HttpURLConnection httpURLConnection = (HttpURLConnection) connection;
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("Accept-Charset", "utf-8");
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.setRequestProperty("Authorization", "Basic " + encode);

            InputStream is = httpURLConnection.getInputStream();

            InputStreamReader isReader = new InputStreamReader(is, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(isReader);
            String line = null;

            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            LoggerFactory.getLogger().error(e.getMessage(), e);
            return null;
        }
        return sb.toString();
    }
}
