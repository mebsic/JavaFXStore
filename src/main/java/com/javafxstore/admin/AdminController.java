package com.javafxstore.admin;

import com.javafxstore.category.ManageCategories;
import com.javafxstore.product.ManageProducts;
import com.javafxstore.user.ManageUsers;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 * AdminController Class
 * Opens new stage based on action performed
 * @version 0.1
 */
public class AdminController {

    private static Stage stage = new Stage();

    @FXML
    public Button users;

    @FXML
    public Button products;

    @FXML
    public Button categories;

    /**
     * Display user management panel and close current stage
     */
    @FXML
    private void showUsers() {
        new ManageUsers();
        stage = (Stage) users.getScene().getWindow();
        stage.close();
    }

    /**
     * Display product management panel and close current stage
     */
    @FXML
    private void showProducts() {
        new ManageProducts();
        stage = (Stage) products.getScene().getWindow();
        stage.close();
    }

    /**
     * Display category management panel and close current stage
     */
    @FXML
    private void showCategories() {
        new ManageCategories();
        stage = (Stage) categories.getScene().getWindow();
        stage.close();
    }
}
