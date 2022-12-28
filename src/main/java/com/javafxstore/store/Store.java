package com.javafxstore.store;

import com.javafxstore.OnlineStoreApplication;
import com.javafxstore.utils.LoggerUtils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.logging.Level;
/**
 * Store Class
 */
public class Store extends Application {

    public static Stage stage = new Stage();
    private static final LoggerUtils l = new LoggerUtils();

    /**
     * Starts the stage
     * */
    public Store() {
        start(stage);
    }

    @Override
    public void start(Stage stage) {

        /**
         * This method loads the javafx window and fxml files,
         * it also starts the application selecting the correct
         * fxml files to load for the scene.
         * */
        Store.stage = stage;

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/javafxstore/store.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            stage.setResizable(false);
            stage.setTitle("JavaFX Store");
            stage.setScene(scene);
            stage.setOnCloseRequest(event -> {
                stage.close();
                OnlineStoreApplication.createStage("Login", "/com/javafxstore/login.fxml");
            });
            stage.show();
        } catch (IOException ex) {
            l.log("Error: " + ex, Level.SEVERE);
        }
    }
}
