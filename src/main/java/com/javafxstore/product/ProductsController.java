package com.javafxstore.product;

import com.javafxstore.category.Category;
import com.javafxstore.utils.LoggerUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.regex.Pattern;

/**
 * CategoriesController Class
 * Perform CRUD operations for managing products.csv file
 * @version 0.1
 */
public class ProductsController implements Initializable {

    private static final String productFile = "products.csv", tempFile = "temp_products.csv", categoriesFile = "categories.csv";
    private final Pattern pricePattern = Pattern.compile("^\\d{1,3}(?:[.,]\\d{3})*(?:[.,]\\d{2})$");
    private final Pattern quantityPattern = Pattern.compile("^\\d+$");
    private final Pattern idPattern = Pattern.compile("^\\d{10}$");
    private static final LoggerUtils l = new LoggerUtils();
    private static boolean isEditing = false;

    @FXML
    public Button clear;

    @FXML
    public Button delete;

    @FXML
    public Button add;

    @FXML
    public Button edit;

    @FXML
    public Button update;

    @FXML
    public Button cancel;

    @FXML
    public TextField product_ID;

    @FXML
    public TextField productQuantity;

    @FXML
    public TextField productName;

    @FXML
    public TextField productPrice;

    @FXML
    public ComboBox<String> productCategory;

    @FXML
    public Label addProductError;

    @FXML
    public TableView<Product> tableView = new TableView<>();

    @FXML
    public TableColumn<Product, Long> productID;

    @FXML
    public TableColumn<Product, String> name;

    @FXML
    public TableColumn<Product, Integer> quantity;

    @FXML
    public TableColumn<Product, Double> price;

    @FXML
    public TableColumn<Product, String> category;

    @FXML
    public ObservableList<Product> productObservableList = FXCollections.observableArrayList();

    /**
     * Retrieve products from csv file using delimiter
     * BufferedReader uses character-by-character input stream
     * If product in editing state, get table selection and populate input fields
     * Otherwise, add products to observable collection
     * Implemented using error handling
     */
    public void getProducts() {
        String fieldDelimiter = ",";
        BufferedReader br;

        try {
            br = new BufferedReader(new FileReader(productFile));
            String line;

            while ((line = br.readLine()) != null) {
                String[] fields = line.split(fieldDelimiter, -1);
                Product products = new Product(Long.parseLong(fields[0]), fields[1], Integer.parseInt(fields[2]), Double.parseDouble(fields[3]), fields[4]);
                if (isEditing) {
                    products = tableView.getSelectionModel().getSelectedItem();
                    product_ID.setText(String.valueOf(products.getProductID()));
                    productName.setText(products.getName());
                    productQuantity.setText(String.valueOf(products.getQuantity()));
                    productPrice.setText(String.valueOf(products.getPrice()));
                    productCategory.setValue(products.getCategory());
                } else {
                    productObservableList.add(products);
                }
            }
            br.close();
        } catch (IOException ex) {
            l.log("Error: " + ex, Level.SEVERE);
        }
    }

    /**
     * Retrieve categories from csv file using delimiter
     * Add categories to combo box selection list
     * Implemented using error handling
     */
    private void getCategories() {
        String fieldDelimiter = ",";
        BufferedReader br;

        try {
            br = new BufferedReader(new FileReader(categoriesFile));
            String line;

            while ((line = br.readLine()) != null) {
                String[] fields = line.split(fieldDelimiter, -1);
                Category categories = new Category(Long.parseLong(fields[0]), fields[1], fields[2]);
                productCategory.getItems().add(categories.getName() + " (" + categories.getType() + ")");
            }
            br.close();
        } catch (IOException ex) {
            l.log("Error: " + ex, Level.SEVERE);
        }
    }

    /**
     * Removes all products by clearing csv file data
     * When table view is empty, show error
     * Otherwise, display warning and prompt confirmation to delete
     * Implemented using error handling
     */
    @FXML
    private void removeAllProducts() {
        Alert noProducts = new Alert(Alert.AlertType.ERROR, "There are no products available", ButtonType.OK),
                warning = new Alert(Alert.AlertType.WARNING, "This will delete all existing products!\nContinue?", ButtonType.YES, ButtonType.NO),
                confirmation = new Alert(Alert.AlertType.CONFIRMATION, "All products have been deleted", ButtonType.OK);
        clearInput();

        if (tableView.getItems().isEmpty()) {
            noProducts.show();
        } else {
            warning.showAndWait();
            if (warning.getResult() == ButtonType.YES) {
                try {
                    FileWriter fw = new FileWriter(productFile, false);
                    fw.write("");
                    fw.close();
                    tableView.getItems().clear();
                    confirmation.show();
                    l.log("All products have been deleted", Level.WARNING);
                } catch (IOException ex) {
                    l.log("Error: " + ex, Level.SEVERE);
                }
            }
        }
    }

    /**
     * Deletes individual product in csv file by product ID
     * When table view is empty, show error
     * Otherwise, check for selected record and prompt confirmation to delete
     */
    @FXML
    private void deleteProduct() {
        Alert noProducts = new Alert(Alert.AlertType.ERROR, "There are no products available", ButtonType.OK),
                itemNotSelected = new Alert(Alert.AlertType.ERROR, "Please select a product to delete", ButtonType.OK),
                itemSelected = new Alert(Alert.AlertType.WARNING, "Are you sure you want to delete this product?", ButtonType.YES, ButtonType.CANCEL);
        clearInput();

        if (tableView.getItems().isEmpty()) {
            noProducts.show();
        } else if (tableView.getSelectionModel().getSelectedItem() == null) {
            itemNotSelected.show();
        } else {
            itemSelected.showAndWait();
            if (itemSelected.getResult() == ButtonType.YES) {
                removeFromFile(productFile, tempFile, String.valueOf(tableView.getSelectionModel().getSelectedItem().getProductID()));
                Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, "Product (ID " + tableView.getSelectionModel().getSelectedItem().getProductID() + ") was deleted", ButtonType.OK);
                tableView.getItems().remove(tableView.getSelectionModel().getSelectedItem());
                confirmation.show();
                l.log("Product (ID " + tableView.getSelectionModel().getSelectedItem().getProductID() + ") was deleted", Level.INFO);
            }
        }
    }

    /**
     * Path used to locate source file and create temporary file
     * Using PrintWriter for writing data to csv file with Scanner object
     * If remove term not specified, maintain file data
     * Otherwise, perform deleting record by replacing temporary file with source
     * Implemented using error handling
     * @param source main destination
     * @param temp temporary file
     * @param removeTerm used to delete record
     */
    private void removeFromFile(String source, String temp, String removeTerm) {
        Path src = Paths.get(source).toAbsolutePath();
        Path tmp = src.resolveSibling(temp);
        long id;
        int quantity;
        double price;
        String name, category;

        try {
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(temp, true)));
            Scanner s = new Scanner(new File(source));
            s.useDelimiter("[,\n]");

            while (s.hasNextLine()) {
                id = s.nextLong();
                name = s.next();
                quantity = s.nextInt();
                price = s.nextDouble();
                category = s.nextLine();
                if (!String.valueOf(id).equals(removeTerm)) {
                    pw.println(id + "," + name + "," + quantity + "," + price + category);
                }
            }

            s.close();
            pw.flush();
            pw.close();
            Files.move(tmp, src, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            l.log("Error: " + ex, Level.SEVERE);
        }
    }

    /**
     * Creates new product by ID, name, quantity, price, and category properties
     * When correct information entered, append new data and clear input field
     * Implemented using input validation
     */
    @FXML
    private void addProduct() {

        if (product_ID.getText().isEmpty() && productName.getText().isEmpty() && productCategory.getValue() == null && productQuantity.getText().isEmpty() && productPrice.getText().isEmpty()) {
            addProductError.setText("Product details cannot be blank!");
        } else if (product_ID.getText().isEmpty()) {
            addProductError.setText("Product ID cannot be blank!");
        } else if (productName.getText().isEmpty()) {
            addProductError.setText("Product name cannot be blank!");
        } else if (productQuantity.getText().isEmpty()) {
            addProductError.setText("Product quantity cannot be blank!");
        } else if (productPrice.getText().isEmpty()) {
            addProductError.setText("Product price cannot be blank!");
        } else if (productCategory.getValue() == null) {
            addProductError.setText("Product category cannot be blank!");
        } else if (!product_ID.getText().matches(String.valueOf(idPattern))) {
            addProductError.setText("Product ID must be 10 digits!");
        } else if (!productQuantity.getText().matches(String.valueOf(quantityPattern))) {
            addProductError.setText("Product quantity must be a number!");
        } else if (!productPrice.getText().matches(String.valueOf(pricePattern))) {
            addProductError.setText("Product price must be a decimal number!");
        } else {
            writeToFile();
            clearInput();
        }
    }

    /**
     * Writes new product created to csv file using input fields
     * Implemented using error handling
     */
    private void writeToFile() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, "Product (ID " + product_ID.getText() + ") was added", ButtonType.OK);
        String category = productCategory.getValue();
        String id = product_ID.getText();
        String name = productName.getText();
        String quantity = productQuantity.getText();
        String price = productPrice.getText();
        BufferedWriter bw;

        try {
            bw = new BufferedWriter(new FileWriter(productFile, true));
            String line = id + "," + name + "," + quantity + "," + price + "," + category + "\n";
            bw.write(line);
            bw.close();
            updateTable();
            confirmation.show();
            l.log("Product (ID " + id + ") was added", Level.INFO);
        } catch (IOException ex) {
            l.log("Error: " + ex, Level.SEVERE);
        }
    }

    /**
     * Edits an individual product in csv file by product ID
     * When table view is empty, show error
     * Otherwise, check for selected record and update editing state
     */
    @FXML
    private void editProduct() {
        Alert noProducts = new Alert(Alert.AlertType.ERROR, "There are no products available", ButtonType.OK),
                itemNotSelected = new Alert(Alert.AlertType.ERROR, "Please select a product to edit", ButtonType.OK);
        clearInput();

        if (tableView.getItems().isEmpty()) {
            noProducts.show();
        } else if (tableView.getSelectionModel().getSelectedItem() == null) {
            itemNotSelected.show();
        } else {
            isEditing = true;
            updateScene();
            getProducts();
        }
    }

    /**
     * Updates existing product while in editing state
     * When invalid information entered, show error
     * Otherwise, prompt confirmation to modify product and write edit to csv file
     * Implemented using input validation
     */
    @FXML
    private void updateProduct() {
        Alert itemEdited = new Alert(Alert.AlertType.WARNING, "Are you sure you want to edit this product?", ButtonType.YES, ButtonType.CANCEL);

        if (product_ID.getText().isEmpty() && productName.getText().isEmpty() && productCategory.getValue() == null && productQuantity.getText().isEmpty() && productPrice.getText().isEmpty()) {
            addProductError.setText("Product details cannot be blank!");
        } else if (product_ID.getText().isEmpty()) {
            addProductError.setText("Product ID cannot be blank!");
        } else if (productName.getText().isEmpty()) {
            addProductError.setText("Product name cannot be blank!");
        } else if (productQuantity.getText().isEmpty()) {
            addProductError.setText("Product quantity cannot be blank!");
        } else if (productPrice.getText().isEmpty()) {
            addProductError.setText("Product price cannot be blank!");
        } else if (productCategory.getValue() == null) {
            addProductError.setText("Product category cannot be blank!");
        } else if (!product_ID.getText().matches(String.valueOf(idPattern))) {
            addProductError.setText("Product ID must be 10 digits!");
        } else if (!productQuantity.getText().matches(String.valueOf(quantityPattern))) {
            addProductError.setText("Product quantity must be a number!");
        } else if (!productPrice.getText().matches(String.valueOf(pricePattern))) {
            addProductError.setText("Product price must be a decimal number!");
        } else {
            itemEdited.showAndWait();
            if (itemEdited.getResult() == ButtonType.YES) {
                isEditing = false;
                writeEditToFile(productFile, tempFile, String.valueOf(tableView.getSelectionModel().getSelectedItem().getProductID()), product_ID.getText(), productName.getText(), productQuantity.getText(), productPrice.getText(), productCategory.getValue());
                Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, "Product (ID " + product_ID.getText() + ") was modified", ButtonType.OK);
                confirmation.show();
                updateScene();
                updateTable();
            }
        }
    }

    /**
     * Path used to locate source file and create temporary file
     * Using PrintWriter for writing data to csv file with Scanner object
     * If edit term not specified, maintain file data
     * Otherwise, perform editing record by replacing temporary file with source
     * Implemented using error handling
     * @param source main destination
     * @param temp temporary file
     * @param editTerm used to edit record
     * @param newProdID new product ID
     * @param newName new product name
     * @param newQty new product quantity
     * @param newPrice new product price
     * @param newCategory new product category
     */
    private void writeEditToFile(String source, String temp, String editTerm, String newProdID, String newName, String newQty, String newPrice, String newCategory) {
        Path src = Paths.get(source).toAbsolutePath();
        Path tmp = src.resolveSibling(temp);
        long id;
        int quantity;
        double price;
        String name, category;

        try {
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(temp, true)));
            Scanner s = new Scanner(new File(source));
            s.useDelimiter("[,\n]");

            while (s.hasNextLine()) {
                id = s.nextLong();
                name = s.next();
                quantity = s.nextInt();
                price = s.nextDouble();
                category = s.nextLine();
                if (String.valueOf(id).equals(editTerm)) {
                    pw.println(newProdID + "," + newName + "," + newQty + "," + newPrice + "," + newCategory);
                } else {
                    pw.println(id + "," + name + "," + quantity + "," + price + category);
                }
            }

            s.close();
            pw.flush();
            pw.close();
            Files.move(tmp, src, StandardCopyOption.REPLACE_EXISTING);
            l.log("Product (ID " + tableView.getSelectionModel().getSelectedItem().getProductID() + ") was modified", Level.INFO);
        } catch (IOException ex) {
            l.log("Error: " + ex, Level.SEVERE);
        }
    }

    /**
     * Cancels editing product state by updating scene
     */
    @FXML
    private void cancelEdit() {
        isEditing = false;
        updateScene();
    }

    /**
     * When not editing existing product, show all buttons and enable table view
     * Otherwise, hide all buttons and disable table view
     */
    private void updateScene() {

        if (!isEditing) {
            add.setVisible(true);
            edit.setDisable(false);
            clear.setVisible(true);
            delete.setVisible(true);
            update.setVisible(false);
            cancel.setVisible(false);
            tableView.setDisable(false);
            clearInput();
        } else {
            add.setVisible(false);
            edit.setDisable(true);
            clear.setVisible(false);
            delete.setVisible(false);
            update.setVisible(true);
            cancel.setVisible(true);
            tableView.setDisable(true);
        }
    }

    /**
     * Clears input fields and resets error message
     */
    private void clearInput() {
        addProductError.setText("");
        product_ID.clear();
        productName.clear();
        productQuantity.clear();
        productPrice.clear();
    }

    /**
     * Updates table view by clearing existing items, retrieving changes, and setting items
     */
    private void updateTable() {
        tableView.getItems().clear();
        getProducts();
        getCategories();
        setTableView();
        tableView.setItems(productObservableList);
    }

    /**
     * Sets table view using field names
     */
    private void setTableView() {
        productID.setCellValueFactory(new PropertyValueFactory<>("productID"));
        name.setCellValueFactory(new PropertyValueFactory<>("Name"));
        quantity.setCellValueFactory(new PropertyValueFactory<>("Quantity"));
        price.setCellValueFactory(new PropertyValueFactory<>("Price"));
        category.setCellValueFactory(new PropertyValueFactory<>("Category"));
    }

    /**
     * When stage is rendered, display table view
     * Temporarily hide update and cancel buttons used in editing state
     * @param location unused
     * @param resources unused
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        updateTable();
        update.setVisible(false);
        cancel.setVisible(false);
    }
}
