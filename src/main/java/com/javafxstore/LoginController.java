package com.javafxstore;

import com.javafxstore.store.Store;
import com.javafxstore.user.User;
import com.javafxstore.utils.LoggerUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;

/**
 * LoginController Class
 * @version 0.2
 */
public class LoginController implements Initializable {

    private static final LoggerUtils l = new LoggerUtils();

    @FXML
    private Button login;

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    @FXML
    private ComboBox<String> roleDropDown;

    @FXML
    private Label loginError;

    /**
     * This function is called when the stage starts, it sets the values for the comboBox
     * @param location - This is the location of the scene
     * @param resources - This is the resourceBundle of the scene
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        if (roleDropDown.getItems().isEmpty()) {
            roleDropDown.setValue("Choose...");
            roleDropDown.getItems().add("user");
            roleDropDown.getItems().add("admin");
        }
    }

    /**
     * This function checks to see if the user's username and password are correct. This function is called once the
     * user clicks on the login button, and will check the user input field and compare them to the ones in our database.
     * depending on the comboBox selected either the admin or user screen will pop up after login.
     */
    @FXML
    protected void loginButtonClicked() {
        boolean loggedIn = false;

        for (User user : OnlineStoreApplication.users) {
            if (user.ValidateLogin(String.valueOf(String.valueOf(username.getText())), String.valueOf(password.getText()), String.valueOf(roleDropDown.getValue()))) {
                loggedIn = true;
            }
        }

        if (loggedIn) {
            if (roleDropDown.getValue().equalsIgnoreCase("admin")) {
                l.log("Admin login", Level.INFO);
                OnlineStoreApplication.createStage("Admin Dashboard", "admin.fxml");
                Stage stage = (Stage) login.getScene().getWindow();
                stage.close();
            }
            if (roleDropDown.getValue().equalsIgnoreCase("user")) {
                l.log("User login", Level.INFO);
                new Store();
                Stage stage = (Stage) login.getScene().getWindow();
                stage.close();
            }
        } else {
            loginError.setText("Invalid login! Try again?");
            l.log("Login failed!", Level.WARNING);
        }
    }
}
