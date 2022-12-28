package com.javafxstore;

import com.javafxstore.user.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.ArrayList;

/**
 * OnlineStoreApplication Class
 * @version 0.1
 */
public class OnlineStoreApplication extends Application {

    public static ArrayList<User> users = new ArrayList<>();
    public static Scene currentScene;

    @Override
    public void start(Stage stage) throws IOException {
        users.add(new User("admin@example.com","admin","admin", "admin"));
        users.add(new User("test@example.com","test","test", "user"));

        FXMLLoader fxmlLoader = new FXMLLoader(OnlineStoreApplication.class.getResource("login.fxml"));
        currentScene = new Scene(fxmlLoader.load());
        stage.setTitle("Login");
        stage.setResizable(false);
        stage.setScene(currentScene);
        stage.show();
    }

    /**
     * Parse the fxml file as resource and generate new stage
     * Contains modifications to title, resizable, modality, and scene properties
     * Implemented using error handling
     * @param title used for setting window title
     * @param location used for getting filepath
     */
    public static void createStage(String title, String location) {

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(OnlineStoreApplication.class.getResource(location));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Main function used to launch application
     * @param args unused
     */
    public static void main(String[] args) {
        launch();
    }
}
