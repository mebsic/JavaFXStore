package com.javafxstore.product;

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
 * ManageProducts Class
 * @version 0.1
 */
public class ManageProducts extends Application {

    public static Stage stage = new Stage();
    private static final LoggerUtils l = new LoggerUtils();

    /**
     * Default constructor
     */
    public ManageProducts() {
        start(stage);
    }

    /**
     * Parses fxml file to load UI, set title, and not resizable properties
     * Event handler to close stage and launch admin page when window is closed
     * Implemented using error handling
     * @param stage used for main layout
     */
    @Override
    public void start(Stage stage) {
        Store.stage = stage;

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/javafxstore/products.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            stage.setResizable(false);
            stage.setTitle("Manage Products");
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
