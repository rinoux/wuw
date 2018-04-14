package cc.rinoux.wuw;

import com.fr.CodeUpdateHelper;
import com.fr.Utils;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Main extends Application {
    private CodeUpdateHelper helper;

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("wuw by rinoux");
        Group root = new Group();
        Scene scene  = new Scene(root);
        root.getChildren().add(getSettingsPane(primaryStage));
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    private GridPane getSettingsPane(Stage stage) throws Exception {
        GridPane basicSettings = new GridPane();
        basicSettings.setPadding(new Insets(5));
        basicSettings.setHgap(5);
        basicSettings.setVgap(10);

        ColumnConstraints keyCC = new ColumnConstraints(80);
        ColumnConstraints valueCC = new ColumnConstraints(250);
        ColumnConstraints gap = new ColumnConstraints(30);
        ColumnConstraints prjCC = new ColumnConstraints(60);
        ColumnConstraints prjVCC = new ColumnConstraints(200);
        ColumnConstraints branchCC = new ColumnConstraints(60);
        ColumnConstraints branchVCC = new ColumnConstraints(100);
        basicSettings.getColumnConstraints().addAll(keyCC, valueCC, gap, prjCC, prjVCC, branchCC, branchVCC);

        Settings settings = getSavedSettings();
        if (settings == null) {
            settings = Settings.getDefault();
        }
        Label endpoint = new Label("endpoint:");
        Label username = new Label("username:");
        Label password = new Label("password:");
        Label restVersion = new Label("rest version:");
        Label protocol = new Label("protocol:");

        TextField endpointValue = new TextField(settings.getEndpoint());
        TextField restVersionValue = new TextField(settings.getRestVersion());
        TextField usernameValue = new TextField(settings.getUsername());
        PasswordField passwordValue = new PasswordField();
        passwordValue.setText(settings.getPassword());
        ChoiceBox<String> protocolValue = new ChoiceBox<>();
        protocolValue.getItems().add("http");
        protocolValue.getItems().add("ssh");
        protocolValue.setValue(settings.getProtocol());

        basicSettings.add(endpoint, 0, 0);
        basicSettings.add(endpointValue, 1, 0);
        basicSettings.add(restVersion, 0, 1);
        basicSettings.add(restVersionValue, 1, 1);
        basicSettings.add(username, 0, 2);
        basicSettings.add(usernameValue, 1, 2);
        basicSettings.add(password, 0, 3);
        basicSettings.add(passwordValue, 1, 3);
        basicSettings.add(protocol, 0, 4);
        basicSettings.add(protocolValue, 1, 4);


        Label project = new Label("project:");
        Label branch = new Label("branch:");
        Label output = new Label("output:");
        Label repos = new Label("Repos write here, separated by ',' !");
        Label completed = new Label("completed!");
        Button selectPath = new Button("select path");
        DirectoryChooser directoryChooser = new DirectoryChooser();


        Targets targets = getLastTargets();
        TextField projectValue = new TextField(targets.getProject());
        TextField branchValue = new TextField(targets.getBranch());
        TextField outputValue = new TextField(targets.getOutput());
        TextArea repoNames = new TextArea(targets.getReposStr());

        basicSettings.add(project, 3, 0);
        basicSettings.add(projectValue, 4, 0);
        basicSettings.add(branch, 5, 0);
        basicSettings.add(branchValue, 6, 0);
        basicSettings.add(output, 3, 1);
        basicSettings.add(outputValue, 4, 1, 2, 1);
        basicSettings.add(selectPath, 6, 1);
        basicSettings.add(repos, 3, 2, 4, 1);
        basicSettings.add(repoNames, 3, 3, 4, 3);

        selectPath.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                File file = directoryChooser.showDialog(stage);
                if (file != null) {
                    outputValue.setText(file.getAbsolutePath());
                }
            }
        });

        Button saveBtn = new Button();
        saveBtn.setText("Save settings");
        saveBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String endpoint = endpointValue.getText();
                String restVersion = restVersionValue.getText();
                String username = usernameValue.getText();
                String password = passwordValue.getText();
                String protocol = protocolValue.getValue();

                Settings settings = new Settings();
                settings.setEndpoint(endpoint);
                settings.setRestVersion(restVersion);
                settings.setUsername(username);
                settings.setPassword(password);
                settings.setProtocol(protocol);
                persistSettings(settings);
            }
        });

        basicSettings.add(saveBtn, 1, 5);


        Button pullOrClone = new Button("Fetch them!");
        pullOrClone.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                completed.setText("fetching...");
                completed.setVisible(true);
                String project = projectValue.getText();
                String branch = branchValue.getText();
                String reposStr = repoNames.getText();
                String output = outputValue.getText();
                reposStr = reposStr.replace("\n", "").trim();
                reposStr = reposStr.replace(" ", "").trim();
                String[] repos = reposStr.split(",");
                for (int i = 0; i < repos.length; i++) {
                    repos[i] = repos[i].trim();
                }
                if (Utils.isEmpty(project)) {
                    System.out.println("project should not be null!");
                    return;
                }

                if (Utils.isEmpty(branch)) {
                    System.out.println("branch should not be null!");
                    return;
                }

                if (Utils.isEmpty(reposStr)) {
                    System.out.println("repos should not be null!");
                    return;
                }

                if (Utils.isEmpty(output)) {
                    System.out.println("output should not be null!");
                    return;
                }

                Targets tar = new Targets();
                tar.setProject(project);
                tar.setBranch(branch);
                tar.setOutput(output);
                tar.setReposStr(reposStr);
                persistLastTargets(tar);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        pullOrClone.setDisable(true);
                        helper.cloneOrPullRepos(new File(output), project, branch, repos);
                        pullOrClone.setDisable(false);
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                completed.setText("completed!");
                            }
                        });

                    }
                }).start();
            }
        });

        completed.setVisible(false);
        basicSettings.add(completed, 3, 6, 2, 1);
        basicSettings.add(pullOrClone, 5, 6, 2, 1);




        return basicSettings;
    }

    private Targets getLastTargets() throws Exception {
        File targetsFile = new File("last_targets");
        if (targetsFile.exists()) {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(targetsFile));
            Targets targets = (Targets) ois.readObject();
            String repoStr = targets.getReposStr();
            repoStr = repoStr.replace(",", ",\n");
            targets.setReposStr(repoStr);
            return targets;
        }

        return new Targets();
    }

    private void persistLastTargets(Targets targets) {
        try {
            File targetsFile = new File("last_targets");
            if (!targetsFile.exists()) {
                targetsFile.createNewFile();
            }

            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(targetsFile));
            oos.writeObject(targets);
            oos.flush();
            oos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void persistSettings(Settings settings) {
        try {
            helper = new CodeUpdateHelper.Builder()
                    .setEndPoint(settings.getEndpoint())
                    .setRestVersion(settings.getRestVersion())
                    .setUsername(settings.getUsername())
                    .setPassword(settings.getPassword())
                    .setProtocol(settings.getProtocol())
                    .build();

            File settingsFile = new File("settings");
            if (!settingsFile.exists()) {
                if (settingsFile.createNewFile()) {
                    ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(settingsFile));
                    oos.writeObject(settings);
                    oos.flush();
                    oos.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private Settings getSavedSettings() throws Exception {
        File settingsFile = new File("settings");
        if (settingsFile.exists()) {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(settingsFile));
            Settings settings = (Settings) ois.readObject();
            helper = new CodeUpdateHelper.Builder()
                    .setEndPoint(settings.getEndpoint())
                    .setRestVersion(settings.getRestVersion())
                    .setUsername(settings.getUsername())
                    .setPassword(settings.getPassword())
                    .setProtocol(settings.getProtocol())
                    .build();

            return settings;
        }

        return null;
    }




    public static void main(String[] args) {
        launch(args);
    }
}
