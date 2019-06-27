package top.rinoux.wuw.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import top.rinoux.code.CodeUpdateHelper;
import top.rinoux.config.SettingsManager;
import top.rinoux.exception.AccountErrorException;
import top.rinoux.git.arch.GitRepo;
import top.rinoux.log.LoggerFactory;
import top.rinoux.profile.Profile;
import top.rinoux.profile.ProfileHelper;
import top.rinoux.util.GeneralUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 * 主界面的控制类
 *
 * @author vito
 * @date 2018/4/29
 */
@SuppressWarnings("all")
public class MainController implements Initializable {

    public MenuBar menu;
    public ComboBox<String> providedProjects;
    public TextField outputValue;
    public Button selectPath;
    public Button selectAll;
    public Button selectInvert;
    public ListView<HBox> repoListView;
    public ComboBox<String> providedBranches;
    public TextField repoFilterText;
    public Button fetch;
    public TextArea logger;
    public ProgressBar bar;


    public Button saveProfile;
    public ComboBox<String> profiles;


    private int invertState = 0;
    Image invert1 = new Image("icons/all.png");
    Image invert2 = new Image("icons/invert.png");


    private boolean allSelected = true;
    Image asyes = new Image("icons/select_all.png");
    Image asno = new Image("icons/unselect_all.png");
    private Set<HBox> allItems = new HashSet<>();
    private Map<String, GitRepo> repositoryMap = new HashMap<>();

    private volatile boolean loadingReposCompleted = false;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        menu.setUseSystemMenuBar(true);


        // 设置默认值
        if (SettingsManager.getInstance().getOutput() != null) {
            outputValue.setText(SettingsManager.getInstance().getOutput());
        }
        if (SettingsManager.getInstance().getBranch() != null) {
            providedBranches.setValue(SettingsManager.getInstance().getBranch());
        }

        providedProjects.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                bindInfo(loadBranchesService);
                loadBranchesService.restart();
            }
        });

        selectPath.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DirectoryChooser directoryChooser = new DirectoryChooser();
                File file = directoryChooser.showDialog(selectPath.getScene().getWindow());
                if (file != null) {
                    outputValue.setText(file.getAbsolutePath());
                }
            }
        });

        fetch.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                logger.setVisible(true);
                fetch.setDisable(true);
                String project = providedProjects.getValue();
                String branch = providedBranches.getValue();
                String output = outputValue.getText();
                if (GeneralUtils.isEmpty(project)) {
                    bindInfo(pullService);
                    pullService.restart();
                    return;
                }
                if (GeneralUtils.isEmpty(output)) {
                    logger.setText("target directory can not be null！");
                    fetch.setDisable(false);
                    return;
                }
                if (repoListView.getItems().size() == 0) {
                    logger.setText("No repo available！");
                    fetch.setDisable(false);
                    return;
                }
                bindInfo(cloneService);
                cloneService.restart();


                SettingsManager.getInstance().setProject(project);
                SettingsManager.getInstance().setBranch(branch);
                SettingsManager.getInstance().setOutput(output);
                SettingsManager.getInstance().save();
            }
        });

        selectAll.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ImageView imageView = (ImageView) selectAll.getChildrenUnmodifiable().get(0);
                if (!allSelected) {
                    imageView.setImage(asyes);
                }
                int size = repoListView.getItems().size();
                for (int i = 0; i < size; i++) {
                    HBox g = repoListView.getItems().get(i);
                    CheckBox s = (CheckBox) g.getChildren().get(3);
                    s.setSelected(true);
                }

                allSelected = true;
            }
        });

        selectInvert.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ImageView imageView = (ImageView) selectInvert.getChildrenUnmodifiable().get(0);
                if (invertState == 0) {
                    imageView.setImage(invert1);
                    invertState = 1;
                } else {
                    imageView.setImage(invert2);
                    invertState = 0;
                }

                int size = repoListView.getItems().size();
                for (int i = 0; i < size; i++) {
                    HBox g = repoListView.getItems().get(i);
                    CheckBox s = (CheckBox) g.getChildren().get(3);
                    boolean selected = s.isSelected();
                    s.setSelected(!selected);
                }

                ImageView iv = (ImageView) selectAll.getChildrenUnmodifiable().get(0);
                if (allSelected) {
                    iv.setImage(asno);
                    allSelected = false;
                } else {
                    iv.setImage(asyes);
                    allSelected = true;
                }
            }
        });

        providedBranches.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                String preferBranch = providedBranches.getValue();
                for (HBox item : repoListView.getItems()) {
                    ComboBox<String> branchBox = (ComboBox<String>) item.getChildren().get(2);
                    for (String branchBoxItem : branchBox.getItems()) {
                        if (branchBoxItem.equals(preferBranch)) {
                            branchBox.setValue(preferBranch);
                        }
                    }
                }

                sortRepoList(repoListView);
                repoListView.refresh();
            }
        });


        repoFilterText.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                String preferRepo = repoFilterText.getText().toLowerCase();
                repoListView.getItems().clear();
                for (HBox item : allItems) {
                    Label repo = (Label) item.getChildren().get(1);
                    if (repo.getText().toLowerCase().contains(preferRepo)) {
                        repoListView.getItems().add(item);
                    }
                }

                sortRepoList(repoListView);
                repoListView.refresh();
            }
        });

        saveProfile.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Stage stage = showSaveProfileDialog();
                ProfileController.setCurrentInstance(stage);
                List<Profile.RepositoryDescription> descriptions = new ArrayList<>();
                for (HBox item : repoListView.getItems()) {
                    String repoName = ((Label) item.getChildren().get(1)).getText();
                    String branchName = ((ComboBox) item.getChildren().get(2)).getValue().toString();
                    boolean selected = ((CheckBox) item.getChildren().get(3)).isSelected();
                    if (selected) {
                        descriptions.add(new Profile.RepositoryDescription(repoName, branchName));
                    }
                }


                String prjName = providedProjects.getSelectionModel().getSelectedItem();
                ProfileHelper.setCurrentDescriptions(prjName, descriptions);

            }
        });

        profiles.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                profiles.getItems().clear();
                profiles.getItems().addAll(ProfileHelper.getProfileNames());
            }
        });

        profiles.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String name) {
                if (GeneralUtils.isNotEmpty(name)) {
                    Profile profile = ProfileHelper.readProfile(name);
                    repoListView.getItems().clear();

                    providedProjects.setValue(profile.getProjectName());

                    try {
                        while (true) {
                            Thread.sleep(1000);
                            if (loadingReposCompleted) {
                                filterReposByProfile(profile);
                                break;
                            }
                        }
                    } catch (InterruptedException e) {
                        LoggerFactory.getLogger().error(e.getMessage(), e);
                    }
                }
                repoFilterText.clear();
            }
        });

        // 账户监听
        SettingsManager.getInstance().addAccountListener(() -> {
            bindInfo(loadProjectsService);
            loadProjectsService.restart();
        });

        if (SettingsManager.getInstance().checkAccountExist()) {
            bindInfo(loadProjectsService);
            loadProjectsService.restart();
        } else {
            showLoginDialog();
        }

    }


    /**
     * 过滤
     */
    private void filterReposByProfile() {
        String profileName = profiles.getSelectionModel().getSelectedItem();
        if (profileName != null) {
            Profile profile = ProfileHelper.readProfile(profileName);
            if (profile != null) {
                filterReposByProfile(profile);
            }
        }
    }


    /**
     * 过滤
     *
     * @param profile
     */
    private void filterReposByProfile(Profile profile) {
        repoListView.getItems().clear();
        List<Profile.RepositoryDescription> descriptions = profile.getDescriptions();

        for (HBox item : allItems) {
            String repoName = ((Label) item.getChildren().get(1)).getText();
            for (Profile.RepositoryDescription description : descriptions) {
                if (description.getRepoName().equals(repoName)) {
                    ((ComboBox) item.getChildren().get(2)).setValue(description.getRepoBranch());
                    ((CheckBox) item.getChildren().get(3)).setSelected(true);
                    repoListView.getItems().add(item);
                }
            }
        }

        sortRepoList(repoListView);
        repoListView.refresh();
    }

    @FXML
    public void changeAccount(ActionEvent actionEvent) {
        showLoginDialog();
    }


    /**
     * 加载选定的project
     */
    private Service<String[]> loadProjectsService = new Service<String[]>() {
        @Override
        protected Task<String[]> createTask() {
            return new Task<String[]>() {
                @Override
                protected void succeeded() {
                    try {
                        String[] projects = get();
                        unbindInfo();
                        Set<String> prjSet = new HashSet<>();
                        Collections.addAll(prjSet, projects);
                        providedProjects.setEditable(true);
                        providedProjects.getItems().clear();
                        providedProjects.getItems().addAll(prjSet);

                        if (GeneralUtils.isNotEmpty(SettingsManager.getInstance().getProject())
                                && prjSet.contains(SettingsManager.getInstance().getProject())) {
                            providedProjects.setValue(SettingsManager.getInstance().getProject());
                        } else {
                            providedProjects.setValue(CodeUpdateHelper.getCurrent().getDefaultProject());
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        LoggerFactory.getLogger().error(e.getMessage(), e);
                    } catch (Exception e) {
                        updateMessage(e.getMessage());
                    }

                }

                @Override
                protected String[] call() throws IOException, ClassNotFoundException {
                    updateProgress(0, 1);
                    return getProjects();
                }
            };
        }
    };

    /**
     * 绑定进度条和日志
     */
    private void bindInfo(Worker worker) {
        bar.setVisible(true);
        bar.progressProperty().bind(worker.progressProperty());
        logger.textProperty().bind(worker.messageProperty());
    }


    /**
     * 过滤结果排序
     *
     * @param listView
     */
    private void sortRepoList(ListView<HBox> listView) {
        listView.getItems().sort(new Comparator<HBox>() {
            @Override
            public int compare(HBox o1, HBox o2) {
                String repoName1 = ((Label) o1.getChildren().get(1)).getText();
                String repoName2 = ((Label) o2.getChildren().get(1)).getText();


                return compareStrings(repoName1, repoName2);
            }
        });
    }


    private int compareStrings(String s1, String s2) {
        char c1 = s1.charAt(0);
        char c2 = s2.charAt(0);

        if (c1 == c2) {
            if (s1.length() == 1) {
                return -1;
            }

            if (s2.length() == 1) {
                return 1;
            }
            return compareStrings(s1.substring(1, s1.length()), s2.substring(1, s2.length()));
        } else return c1 - c2;
    }

    /**
     * 解绑进度条和日志
     */
    private void unbindInfo() {
        bar.progressProperty().unbind();
        logger.textProperty().unbind();
        bar.setVisible(false);
    }


    /**
     * 获取repo和分支信息并塞到列表
     */
    private Service<Map<String, String[]>> loadBranchesService = new Service<Map<String, String[]>>() {
        @Override
        protected Task<Map<String, String[]>> createTask() {
            return new Task<Map<String, String[]>>() {
                @Override
                protected void succeeded() {
                    try {
                        loadingReposCompleted = false;
                        Map<String, String[]> branches = get();

                        repoListView.getItems().clear();
                        allItems.clear();


                        Set<String> branchSet = new HashSet<>();

                        for (Map.Entry<String, String[]> branch : branches.entrySet()) {
                            HBox item = createItemBox(branch.getKey(), branch.getValue());
                            ComboBox<String> branchBox = (ComboBox<String>) item.getChildren().get(2);
                            ((CheckBox) item.getChildren().get(3)).setSelected(false);
                            String[] repoBranches = branch.getValue();
                            Collections.addAll(branchSet, repoBranches);
                            if (repoBranches.length > 0) {
                                branchBox.setValue(repoBranches[0]);
                            }
                            allItems.add(item);
                            repoListView.getItems().add(item);
                        }
                        sortRepoList(repoListView);
                        providedBranches.getItems().clear();
                        providedBranches.getItems().addAll(branchSet);
                        updateMessage("List repo done!");
                        unbindInfo();
                        fetch.setDisable(false);
                    } catch (InterruptedException | ExecutionException e) {
                        LoggerFactory.getLogger().error(e.getMessage(), e);
                    } catch (IOException e) {
                        LoggerFactory.getLogger().error(e.getMessage(), e);
                    } finally {
                        loadingReposCompleted = true;
                        filterReposByProfile();
                    }

                }

                @Override
                protected Map<String, String[]> call() throws IOException {
                    Map<String, String[]> bran = new LinkedHashMap<>(repositoryMap.size());
                    try {

                        saveProfile.setDisable(true);

                        int count = 0;
                        String project = providedProjects.getValue();
                        repositoryMap.clear();
                        for (GitRepo repo : CodeUpdateHelper.getCurrent().getRepos(project)) {
                            repositoryMap.put(repo.getRepoName(), repo);
                        }


                        for (Map.Entry<String, GitRepo> entry : repositoryMap.entrySet()) {
                            String name = entry.getKey();
                            GitRepo repository = entry.getValue();
                            bran.put(name, CodeUpdateHelper.getCurrent().getBranchNames(project, name));
                            updateMessage("Scanning repo: " + name + " ...");
                            updateProgress(count, repositoryMap.size());
                            count++;
                        }

                        saveProfile.setDisable(false);

                    } catch (Exception e) {
                        updateMessage(e.getMessage());
                    }

                    return bran;
                }
            };
        }
    };


    /**
     * clone代码
     */
    private Service<Void> cloneService = new Service<Void>() {
        @Override
        protected Task<Void> createTask() {
            return new AuthenticatedTask<Void>() {

                @Override
                protected void succeeded() {
                    fetch.setDisable(false);
                    unbindInfo();
                }

                @Override
                protected Void call() throws Exception {
                    String project = providedProjects.getValue();
                    String output = outputValue.getText();

                    int size = repoListView.getItems().size();

                    List<HBox> checkedItems = new ArrayList<>();
                    for (int i = 0; i < size; i++) {
                        HBox g = repoListView.getItems().get(i);
                        CheckBox s = (CheckBox) g.getChildren().get(3);
                        if (s.isSelected()) {
                            checkedItems.add(g);
                        }
                    }
                    int checkedSize = checkedItems.size();
                    List<Exception> exceptions = new LinkedList<>();
                    for (int i = 0; i < checkedSize; i++) {
                        HBox g = checkedItems.get(i);
                        String repoName = ((Label) g.getChildren().get(1)).getText();
                        String branchName = ((ComboBox) g.getChildren().get(2)).getValue().toString();
                        if (repositoryMap.containsKey(repoName)) {
                            String href = repositoryMap.get(repoName).getHttpHref();
                            String path = GeneralUtils.pathJoin(output, repoName);
                            updateMessage("Fethcing code of " + repoName + "...");

                            try {
                                CodeUpdateHelper.getCurrent().cloneOrPullRepo(href, branchName, new File(path));
                            } catch (Exception e) {
                                exceptions.add(e);
                            }
                            updateProgress(i + 1, checkedSize);
                        } else {
                            break;
                        }

                    }

                    if (exceptions.isEmpty()) {
                        updateMessage("Code fetch done!");
                    } else {
                        StringBuilder sb = new StringBuilder();
                        sb.append("Code fetch done with error:\n");
                        for (Exception exception : exceptions) {
                            sb.append(exception.getMessage()).append("\n");
                        }

                        updateMessage(sb.toString());
                    }

                    return null;
                }
            };
        }
    };

    /**
     * pull代码
     */
    private Service<Void> pullService = new Service<Void>() {
        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {

                @Override
                protected void succeeded() {
                    fetch.setDisable(false);
                    bar.setVisible(false);
                    bar.progressProperty().unbind();
                    logger.textProperty().unbind();
                }

                @Override
                protected Void call() throws IOException {
                    String branch = providedBranches.getValue();
                    String output = outputValue.getText();
                    bar.setProgress(0);
                    bar.setVisible(true);
                    File[] fs = new File(output).listFiles();
                    if (fs == null) {
                        return null;
                    }
                    try {
                        for (int i = 0; i < fs.length; i++) {
                            final File repo = fs[i];
                            updateMessage("Update code of " + repo.getName() + "...");
                            CodeUpdateHelper.getCurrent().update(repo, branch);
                            updateProgress(i + 1, fs.length);
                        }
                    } catch (Exception e) {
                        updateMessage(e.getMessage());
                    }
                    updateMessage("Done!");
                    return null;
                }
            };
        }
    };

    /**
     * 登录对话框
     */
    public static void showLoginDialog() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainController.class.getResource("/view/login.fxml"));
            Parent parent = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Login");
            stage.setScene(new Scene(parent));
            stage.setResizable(false);
            stage.setAlwaysOnTop(true);
            stage.show();
        } catch (IOException e) {
            LoggerFactory.getLogger().error(e.getMessage(), e);
        }
    }

    /**
     * 保存描述文件的对话框
     *
     * @return
     */
    public static Stage showSaveProfileDialog() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainController.class.getResource("/view/profile_save_dialog.fxml"));
            Parent parent = loader.load();

            Stage stage = new Stage();
            stage.setTitle("新建仓库组");
            stage.setScene(new Scene(parent));
            stage.setResizable(false);
            stage.setAlwaysOnTop(true);
            stage.show();

            return stage;
        } catch (IOException e) {
            LoggerFactory.getLogger().error(e.getMessage(), e);
        }

        return null;
    }


    /**
     * 获取project
     *
     * @return
     */
    private String[] getProjects() {
        List<String> prjs = new ArrayList<>();
        try {
            String[] projects = CodeUpdateHelper.getCurrent().getProjects();
            if (projects.length > 0) {
                Collections.addAll(prjs, projects);
                prjs.sort(new Comparator<String>() {
                    @Override
                    public int compare(String o1, String o2) {
                        return o1.compareTo(o2);
                    }
                });

                prjs.add(0, CodeUpdateHelper.getCurrent().getDefaultProject());
            }
        } catch (Exception e) {
            logger.setText(e.getMessage());
        }

        return prjs.toArray(new String[0]);
    }


    /**
     * 创建repo行
     *
     * @param repoName
     * @param branches
     * @return
     * @throws IOException
     */
    private HBox createItemBox(String repoName, String... branches) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MainController.class.getResource("/view/repo_item.fxml"));
        HBox hBox = loader.load();
        Label name = (Label) hBox.getChildren().get(1);
        ComboBox<String> branchBox = (ComboBox<String>) hBox.getChildren().get(2);
        CheckBox selected = (CheckBox) hBox.getChildren().get(3);

        name.setText(repoName);
        branchBox.getItems().addAll(branches);
        selected.setSelected(true);
        return hBox;
    }

    private abstract class AuthenticatedTask<T> extends Task<T> {

        @Override
        protected void failed() {

            if (getException() instanceof AccountErrorException) {
                showLoginDialog();
            }
        }
    }
}
