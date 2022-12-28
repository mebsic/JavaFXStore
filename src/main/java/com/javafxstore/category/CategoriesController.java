package com.javafxstore.category;

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
 * Perform CRUD operations for managing categories.csv file
 * @version 0.1
 */
public class CategoriesController implements Initializable {

    private static final String categoriesFile = "categories.csv", tempFile = "temp_categories.csv";
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
    public TextField category_ID;

    @FXML
    public TextField categoryName;

    @FXML
    public TextField categoryType;

    @FXML
    public Label addCategoryError;

    @FXML
    public TableView<Category> tableView = new TableView<>();

    @FXML
    public TableColumn<Category, Long> categoryID;

    @FXML
    public TableColumn<Category, String> name;

    @FXML
    public TableColumn<Category, String> type;

    @FXML
    public ObservableList<Category> categoryObservableList = FXCollections.observableArrayList();

    /**
     * Retrieve categories from csv file using delimiter
     * If category in editing state, get table selection and populate input fields
     * Otherwise, add categories to observable collection
     * Implemented using error handling
     */
    public void getCategories() {
        String fieldDelimiter = ",";
        BufferedReader br;

        try {
            br = new BufferedReader(new FileReader(categoriesFile));
            String line;

            while ((line = br.readLine()) != null) {
                String[] fields = line.split(fieldDelimiter, -1);
                Category categories = new Category(Long.parseLong(fields[0]), fields[1], fields[2]);
                if (isEditing) {
                    categories = tableView.getSelectionModel().getSelectedItem();
                    category_ID.setText(String.valueOf(categories.getId()));
                    categoryName.setText(categories.getName());
                    categoryType.setText(categories.getType());
                } else {
                    categoryObservableList.add(categories);
                }
            }
            br.close();
        } catch (IOException ex) {
            l.log("Error: " + ex, Level.SEVERE);
        }
    }

    /**
     * Removes all categories by clearing csv file data
     * When table view is empty, show error
     * Otherwise, display warning and prompt confirmation to delete
     * Implemented using error handling
     */
    @FXML
    private void removeAllCategories() {
        Alert noCategories = new Alert(Alert.AlertType.ERROR, "There are no categories available", ButtonType.OK),
                warning = new Alert(Alert.AlertType.WARNING, "This will delete all existing categories!\nContinue?", ButtonType.YES, ButtonType.NO),
                confirmation = new Alert(Alert.AlertType.CONFIRMATION, "All categories have been deleted", ButtonType.OK);
        clearInput();

        if (tableView.getItems().isEmpty()) {
            noCategories.show();
        } else {
            warning.showAndWait();
            if (warning.getResult() == ButtonType.YES) {
                try {
                    FileWriter fw = new FileWriter(categoriesFile, false);
                    fw.write("");
                    fw.close();
                    tableView.getItems().clear();
                    confirmation.show();
                    l.log("All categories have been deleted", Level.WARNING);
                } catch (IOException ex) {
                    l.log("Error: " + ex, Level.SEVERE);
                }
            }
        }
    }

    /**
     * Deletes individual record in csv file by category ID
     * When table view is empty, show error
     * Otherwise, check for selected record and prompt confirmation to delete
     */
    @FXML
    private void deleteCategory() {
        Alert noCategories = new Alert(Alert.AlertType.ERROR, "There are no categories available", ButtonType.OK),
                itemNotSelected = new Alert(Alert.AlertType.ERROR, "Please select a category to delete", ButtonType.OK),
                itemSelected = new Alert(Alert.AlertType.WARNING, "Are you sure you want to delete this category?", ButtonType.YES, ButtonType.CANCEL);
        clearInput();

        if (tableView.getItems().isEmpty()) {
            noCategories.show();
        } else if (tableView.getSelectionModel().getSelectedItem() == null) {
            itemNotSelected.show();
        } else {
            itemSelected.showAndWait();
            if (itemSelected.getResult() == ButtonType.YES) {
                removeFromFile(categoriesFile, tempFile, String.valueOf(tableView.getSelectionModel().getSelectedItem().getId()));
                Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, "Category (ID " + tableView.getSelectionModel().getSelectedItem().getId() + ") was deleted", ButtonType.OK);
                tableView.getItems().remove(tableView.getSelectionModel().getSelectedItem());
                confirmation.show();
                l.log("Category (ID " + tableView.getSelectionModel().getSelectedItem().getId() + ") was deleted", Level.INFO);
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
        String name, type;

        try {
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(temp, true)));
            Scanner s = new Scanner(new File(source));
            s.useDelimiter("[,\n]");

            while (s.hasNextLine()) {
                id = s.nextLong();
                name = s.next();
                type = s.nextLine();
                if (!String.valueOf(id).equals(removeTerm)) {
                    pw.println(id + "," + name + type);
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
     * Creates new category by ID, name, and type properties
     * When correct information entered, append new data and clear input field
     * Implemented using input validation
     */
    @FXML
    private void addCategory() {

        if (category_ID.getText().isEmpty() && categoryName.getText().isEmpty() && categoryType.getText().isEmpty()) {
            addCategoryError.setText("Category details cannot be blank!");
        } else if (category_ID.getText().isEmpty()) {
            addCategoryError.setText("Category ID cannot be blank!");
        } else if (categoryName.getText().isEmpty()) {
            addCategoryError.setText("Category name cannot be blank!");
        } else if (categoryType.getText().isEmpty()) {
            addCategoryError.setText("Category type cannot be blank!");
        } else if (!category_ID.getText().matches(String.valueOf(idPattern))) {
            addCategoryError.setText("Category ID must be 10 digits!");
        } else {
            writeToFile();
            clearInput();
        }
    }

    /**
     * Writes new category created to csv file using input fields
     * Implemented using error handling
     */
    private void writeToFile() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, "Category (ID " + category_ID.getText() + ") was added", ButtonType.OK);
        String id = category_ID.getText();
        String name = categoryName.getText();
        String type = categoryType.getText();
        BufferedWriter bw;

        try {
            bw = new BufferedWriter(new FileWriter(categoriesFile, true));
            String line = id + "," + name + "," + type + "\n";
            bw.write(line);
            bw.close();
            updateTable();
            confirmation.show();
            l.log("Category (ID " + id + ") was added", Level.INFO);
        } catch (IOException ex) {
            l.log("Error: " + ex, Level.SEVERE);
        }
    }

    /**
     * Edits an individual category in csv file by category ID
     * When table view is empty, show error
     * Otherwise, check for selected record and update editing state
     */
    @FXML
    private void editCategory() {
        Alert noCategories = new Alert(Alert.AlertType.ERROR, "There are no categories available", ButtonType.OK),
                itemNotSelected = new Alert(Alert.AlertType.ERROR, "Please select a category to edit", ButtonType.OK);
        clearInput();

        if (tableView.getItems().isEmpty()) {
            noCategories.show();
        } else if (tableView.getSelectionModel().getSelectedItem() == null) {
            itemNotSelected.show();
        } else {
            isEditing = true;
            updateScene();
            getCategories();
        }
    }

    /**
     * Updates existing category while in editing state
     * When invalid information entered, show error
     * Otherwise, prompt confirmation to modify category and write edit to csv file
     * Implemented using input validation
     */
    @FXML
    private void updateCategory() {
        Alert itemEdited = new Alert(Alert.AlertType.WARNING, "Are you sure you want to edit this category?", ButtonType.YES, ButtonType.CANCEL);

        if (category_ID.getText().isEmpty() && categoryName.getText().isEmpty() && categoryType.getText().isEmpty()) {
            addCategoryError.setText("Category details cannot be blank!");
        } else if (category_ID.getText().isEmpty()) {
            addCategoryError.setText("Category ID cannot be blank!");
        } else if (categoryName.getText().isEmpty()) {
            addCategoryError.setText("Category name cannot be blank!");
        } else if (categoryType.getText().isEmpty()) {
            addCategoryError.setText("Category type cannot be blank!");
        } else if (!category_ID.getText().matches(String.valueOf(idPattern))) {
            addCategoryError.setText("Category ID must be 10 digits!");
        } else {
            itemEdited.showAndWait();
            if (itemEdited.getResult() == ButtonType.YES) {
                isEditing = false;
                writeEditToFile(categoriesFile, tempFile, String.valueOf(tableView.getSelectionModel().getSelectedItem().getId()), category_ID.getText(), categoryName.getText(),categoryType.getText());
                Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, "Category (ID " + category_ID.getText() + ") was modified", ButtonType.OK);
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
     * @param newCatID new category ID
     * @param newName new category name
     * @param newType new category type
     */
    private void writeEditToFile(String source, String temp, String editTerm, String newCatID, String newName, String newType) {
        Path src = Paths.get(source).toAbsolutePath();
        Path tmp = src.resolveSibling(temp);
        long id;
        String name, type;

        try {
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(temp, true)));
            Scanner s = new Scanner(new File(source));
            s.useDelimiter("[,\n]");

            while (s.hasNextLine()) {
                id = s.nextLong();
                name = s.next();
                type = s.nextLine();
                if (String.valueOf(id).equals(editTerm)) {
                    pw.println(newCatID + "," + newName + "," + newType);
                } else {
                    pw.println(id + "," + name + type);
                }
            }

            s.close();
            pw.flush();
            pw.close();
            Files.move(tmp, src, StandardCopyOption.REPLACE_EXISTING);
            l.log("Category (ID " + tableView.getSelectionModel().getSelectedItem().getId() + ") was modified", Level.INFO);
        } catch (IOException ex) {
            l.log("Error: " + ex, Level.SEVERE);
        }
    }

    /**
     * Cancels editing category state by updating scene
     */
    @FXML
    private void cancelEdit() {
        isEditing = false;
        updateScene();
    }

    /**
     * When not editing existing category, show all buttons and enable table view
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
        addCategoryError.setText("");
        category_ID.clear();
        categoryName.clear();
        categoryType.clear();
    }

    /**
     * Updates table view by clearing existing items, retrieving changes, and setting items
     */
    private void updateTable() {
        tableView.getItems().clear();
        getCategories();
        setTableView();
        tableView.setItems(categoryObservableList);
    }

    /**
     * Sets table view using field names
     */
    private void setTableView() {
        categoryID.setCellValueFactory(new PropertyValueFactory<>("id"));
        name.setCellValueFactory(new PropertyValueFactory<>("Name"));
        type.setCellValueFactory(new PropertyValueFactory<>("Type"));
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
