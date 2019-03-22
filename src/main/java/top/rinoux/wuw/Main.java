package top.rinoux.wuw;

import top.rinoux.config.SettingsManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

/**
 * 应用启动
 *
 * @author vito
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // 读取配置
        SettingsManager.getInstance().load();

        primaryStage.setTitle("wuw by X-Engine");
        FXMLLoader loader = new FXMLLoader();
        URL location = getClass().getResource("/view/main.fxml");
        loader.setLocation(location);
        Parent parent = loader.load();
        primaryStage.setScene(new Scene(parent));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
