package top.rinoux.wuw.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import top.rinoux.code.CodeUpdateHelper;
import top.rinoux.config.SettingsManager;
import top.rinoux.git.ServiceManager;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import top.rinoux.git.service.GitHubContentService;
import top.rinoux.log.LoggerFactory;
import top.rinoux.util.GeneralUtils;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * 登录界面控制器
 *
 * @author vito
 * @date 2018/4/29
 */
public class LoginController implements Initializable {

    public ComboBox<String> gitType;
    public TextField hostUrlValue;
    public TextField usernameValue;
    public PasswordField passwordValue;
    public CheckBox autoLogin;
    public Button loginBtn;
    public ImageView avatar;
    public Label message;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 初始化一些默认配置
        String username = SettingsManager.getInstance().getUsername();
        String host = SettingsManager.getInstance().getEndpoint();
        String pwd = SettingsManager.getInstance().getPassword();
        String git = SettingsManager.getInstance().getGitType();
        if (GeneralUtils.isNotEmpty(username)) {
            usernameValue.setText(username);
        }
        if (GeneralUtils.isNotEmpty(host)) {
            hostUrlValue.setText(host);
        }
        passwordValue.setText(pwd);

        gitType.setValue(git);
        gitType.getItems().setAll(ServiceManager.getTypeSupport());
        gitType.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                loginBtn.setDisable(false);
            }
        });
        gitType.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                if (t1.equalsIgnoreCase("github")) {
                    hostUrlValue.setText(GitHubContentService.GITHUB_ENDPOINT);
                } else if (t1.equalsIgnoreCase("bitbucket")) {
                    hostUrlValue.setText("https://cloud.finedevelop.com");
                }
            }
        });

        loadDefaultAvatar();

        usernameValue.focusedProperty().addListener((observable, oldValue, newValue) -> {
            clearStatus();
        });

        passwordValue.focusedProperty().addListener((observable, oldValue, newValue) -> clearStatus());

        loginBtn.setOnAction(event -> {
            loginBtn.setDisable(true);
            SettingsManager settings = SettingsManager.getInstance();
            settings.setGitType(gitType.getValue());
            settings.setEndpoint(hostUrlValue.getText().trim());
            settings.setUsername(usernameValue.getText().trim());
            settings.setPassword(passwordValue.getText().trim());
            settings.setAutoLogin(autoLogin.isSelected());
            settings.setProject("");
            validateService.restart();
            MainController.showLoginDialog();
        });


    }

    private void reloadAvatar() {
        try {
            String imageUrl = CodeUpdateHelper.getCurrent().getAvatar();
            SettingsManager.getInstance().setAvatarUrl(imageUrl);
            avatar.setImage(new Image(imageUrl, true));
        } catch (Exception e) {
            failedStatus(e.getMessage());
        }
    }

    private void loadDefaultAvatar() {
        avatar.setImage(new Image(SettingsManager.getInstance().getAvatarUrl(), true));
    }

    private Service<Boolean> validateService = new Service<Boolean>() {

        @Override
        protected Task<Boolean> createTask() {
            return new Task<Boolean>() {
                @Override
                protected void succeeded() {
                    try {
                        boolean pass = get();
                        if (pass) {
                            reloadAvatar();
                            SettingsManager.getInstance().fireAccountListener();
                            successStatus("Login success!");
                            Stage window = (Stage) loginBtn.getScene().getWindow();
                            window.close();
                            if (autoLogin.isSelected()) {
                                SettingsManager.getInstance().save();
                            }
                        } else {
                            failedStatus("Invalid username or password");
                        }

                        loginBtn.setDisable(false);
                    } catch (Exception e) {
                        LoggerFactory.getLogger().error(e.getMessage(), e);
                        failedStatus(e.getMessage());
                    }
                }


                @Override
                protected Boolean call() {
                    try {
                        CodeUpdateHelper helper = CodeUpdateHelper.newBuilder()
                                .setGitType(SettingsManager.getInstance().getGitType())
                                .setEndPoint(SettingsManager.getInstance().getEndpoint())
                                .setUsername(SettingsManager.getInstance().getUsername())
                                .setPassword(SettingsManager.getInstance().getPassword())
                                .build();
                        if (helper.validate()) {
                            CodeUpdateHelper.setCurrent(helper);
                            return true;
                        }

                    } catch (Exception e) {
                        LoggerFactory.getLogger().error(e.getMessage(), e);

                    }
                    return false;
                }
            };
        }
    };

    private void failedStatus(String log) {
        message.setVisible(true);
        message.setTextFill(Color.ORANGE);
        message.setText(log);
    }

    private void successStatus(String log) {
        message.setVisible(true);
        message.setTextFill(Color.GREEN);
        message.setText(log);
    }

    private void clearStatus() {
        if (message.isVisible()) {
            message.setVisible(false);
            message.setText("");
        }
    }
}
