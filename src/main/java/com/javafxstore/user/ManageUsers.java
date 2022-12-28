package com.javafxstore.user;

import com.javafxstore.OnlineStoreApplication;
import com.javafxstore.store.Store;
import com.javafxstore.utils.LoggerUtils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.logging.Level;

/**
 * ManageUsers Class
 * @version 0.1
 */
public class ManageUsers extends Application {

    public static Stage stage = new Stage();
    private static final LoggerUtils l = new LoggerUtils();

    /**
     * This is the default constructor
     */
    public ManageUsers() {
        start(stage);
    }

    /**
     * This start function is responsible for loading up a new stage for the admins of the store.
     * It loads up a scene which is called users.fxml in the scenes folder as to allow the admin to work on the store.
     * @param stage - This is the stage that will be shown onscreen
     */
    @Override
    public void start(Stage stage) {
        Store.stage = stage;

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/javafxstore/users.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            stage.setResizable(false);
            stage.setTitle("Manage Users");
            stage.setScene(scene);
            stage.setOnCloseRequest(event -> {
                stage.close();
                OnlineStoreApplication.createStage("Admin Dashboard", "/com/javafxstore/admin.fxml");
            });
            stage.show();
        } catch (IOException ex) {
            l.log("Error: " + ex, Level.SEVERE);
        }
    }
}
