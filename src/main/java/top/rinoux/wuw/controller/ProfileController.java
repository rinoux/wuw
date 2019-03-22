package top.rinoux.wuw.controller;

import top.rinoux.profile.ProfileHelper;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by rinoux on 2019-02-19.
 */
public class ProfileController implements Initializable {
    public static Stage currentInstance;
    public TextField profileName;
    public Button saveButton;


    public static Stage getCurrentInstance() {
        return currentInstance;
    }

    public static void setCurrentInstance(Stage currentInstance) {
        ProfileController.currentInstance = currentInstance;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        saveButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {

                String name = profileName.getText();
                if (name != null) {
                    ProfileHelper.saveProfile(name);
                }

                getCurrentInstance().close();

            }
        });


        saveButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {

            }
        });
    }
}
