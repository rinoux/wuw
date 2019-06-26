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
    private static Stage current;
    public TextField profileName;
    public Button saveButton;


    static void setCurrent(Stage currentInstance) {
        current = currentInstance;
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
                current.close();
            }
        });
    }
}
