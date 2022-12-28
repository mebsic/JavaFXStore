package com.javafxstore.user;

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
 * UsersController Class
 * CRUD operations are used for managing the users.csv file
 * @version 0.1
 */
public class UsersController implements Initializable{

    public static final String userFile = "users.csv", tempFile = "temp_users.csv";
    private final Pattern namePattern = Pattern.compile("/^(.*?),/");
    private static final LoggerUtils l = new LoggerUtils();
    private static boolean isEditing = false;

    @FXML
    private TextField userName;

    @FXML
    private TextField userUser;

    @FXML
    private PasswordField userPass;

    @FXML
    private Button delete;

    @FXML
    private Button add;

    @FXML
    private Button edit;

    @FXML
    private Button cancel;

    @FXML
    private Button clear;

    @FXML
    private TableView<User> tableView;

    @FXML
    private TableColumn<User, String> id;

    @FXML
    private TableColumn<User, String> name;

    @FXML
    private TableColumn<User, String> pass;

    @FXML
    private TableColumn<User, Roles> role;

    @FXML
    private TextField userRole;

    @FXML
    private Label addUserError;

    @FXML
    private Button update;

    @FXML
    public ObservableList<User> userObservableList = FXCollections.observableArrayList();

    /**
     * This function reads the csv file in "userFile" and retrieves all the info
     * inside it to allow the admin to see all the users in the store. It checks through to see
     * if isEditing is true or false and starts to retrieve the data.
     */
    public void getUsers() {
        String fieldDelimiter = ",";
        BufferedReader br;

        try {
            br = new BufferedReader(new FileReader(userFile));
            String line;

            while ((line = br.readLine()) != null) {
                String[] fields = line.split(fieldDelimiter, -1);
                User users = new User(fields[0], fields[1], fields[2], (fields[3]));
                if (isEditing) {
                    users = tableView.getSelectionModel().getSelectedItem();
                    userName.setText(users.getName());
                    userUser.setText(users.getUsername());
                    userPass.setText(users.getPassword());
                    userRole.setText(String.valueOf(users.getRole()));
                } else {
                    userObservableList.add(users);
                }
            }
            br.close();
        } catch (IOException ex) {
            l.log("Error: " + ex, Level.SEVERE);
        }
    }

    /**
     * This function removes all the users in the csv file by clearing csv file data.
     * It checks to see if the table view is not empty, if it is not it will display a warning
     * prompting the admin to accept or decline and if accepted, it will result in a deletion of all users.
     */
    @FXML
    private void removeAllUsers() {
        Alert noUsers = new Alert(Alert.AlertType.ERROR, "There are no users available", ButtonType.OK),
                warning = new Alert(Alert.AlertType.WARNING, "This will delete all existing users!\nContinue?", ButtonType.YES, ButtonType.NO),
                confirmation = new Alert(Alert.AlertType.CONFIRMATION, "All users have been deleted", ButtonType.OK);
        clearInput();

        if (tableView.getItems().isEmpty()) {
            noUsers.show();
        } else {
            warning.showAndWait();
            if (warning.getResult() == ButtonType.YES) {
                try {
                    FileWriter fw = new FileWriter(userFile, false);
                    fw.write("");
                    fw.close();
                    tableView.getItems().clear();
                    confirmation.show();
                    l.log("All users have been deleted", Level.WARNING);
                } catch (IOException ex) {
                    l.log("Error: " + ex, Level.SEVERE);
                }
            }
        }
    }

    /**
     * This function deletes a single user by checking their name (or email address).
     * If the table view is not empty it will check to see if the email addresses are the same and if so,
     * a warning will pop up asking to confirm with the deletion to follow suit.
     */
    @FXML
    private void deleteUser() {
        Alert noUsers = new Alert(Alert.AlertType.ERROR, "There are no users available", ButtonType.OK),
                itemNotSelected = new Alert(Alert.AlertType.ERROR, "Please select a user to delete", ButtonType.OK),
                itemSelected = new Alert(Alert.AlertType.WARNING, "Are you sure you want to delete this user?", ButtonType.YES, ButtonType.CANCEL);
        clearInput();

        if (tableView.getItems().isEmpty()) {
            noUsers.show();
        } else if (tableView.getSelectionModel().getSelectedItem() == null) {
            itemNotSelected.show();
        } else {
            itemSelected.showAndWait();
            if (itemSelected.getResult() == ButtonType.YES) {
                removeFromFile(userFile, tempFile, String.valueOf(tableView.getSelectionModel().getSelectedItem().getName()));
                Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, "User (ID " + tableView.getSelectionModel().getSelectedItem().getName() + ") was deleted", ButtonType.OK);
                tableView.getItems().remove(tableView.getSelectionModel().getSelectedItem());
                confirmation.show();
                l.log("User (ID " + tableView.getSelectionModel().getSelectedItem().getName() + ") was deleted", Level.INFO);
            }
        }
    }

    /**
     * This function uses the path to locate the source file and create a
     * temp file for the data to be stored. With the remove term parameter
     * this function will delete a record by replacing the temp file with the real file
     * @param source - The normal file
     * @param temp - The temporary file
     * @param removeTerm - The item to remove
     */
    private void removeFromFile(String source, String temp, String removeTerm) {
        final Path src = Paths.get(source).toAbsolutePath();
        final Path tmp = src.resolveSibling(temp);
        String name, username, password, role;

        try {
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(temp, true)));
            Scanner s = new Scanner(new File(source));
            s.useDelimiter("[,\n]");

            while (s.hasNextLine()) {
                name = s.next();
                username = s.next();
                password = s.next();
                role = s.nextLine();
                if (!String.valueOf(name).equals(removeTerm)) {
                    pw.println(name + "," + username + "," + password + role); // here
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
     * This function adds a new user to the csv file/manager.
     * The function checks to see if the input is empty, if
     * not it will continue by calling the functions needed to add a new user
     */
    @FXML
    private void addUser() {

        if (userName.getText().isEmpty() && userUser.getText().isEmpty() && userPass.getText().isEmpty() && userRole.getText().isEmpty()) {
            addUserError.setText("User details cannot be blank!");
        } else if (userName.getText().isEmpty()) {
            addUserError.setText("User ID cannot be blank!");
        } else if (userUser.getText().isEmpty()) {
            addUserError.setText("Username cannot be blank!");
        } else if (userPass.getText().isEmpty()) {
            addUserError.setText("User password cannot be blank!");
        } else if (userRole.getText().isEmpty()) {
            addUserError.setText("User role cannot be blank!");
        } else if (!(userRole.getText().equals("user") || userRole.getText().equals("admin"))) {
            addUserError.setText("User must have a role of either user or admin");
        } else {
            writeToFile();
            clearInput();
        }
    }

    /**
     * This function writes a user to the csv file using input fields.
     */
    private void writeToFile() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, "User (ID " + name.getText() + ") was added", ButtonType.OK);
        String userID = userName.getText();
        String username = userUser.getText();
        String password = userPass.getText();
        String role = userRole.getText();
        BufferedWriter bw;

        try {
            bw = new BufferedWriter(new FileWriter(userFile, true));
            String line = userID + "," + username + "," + password + "," + role + "\n";
            bw.write(line);
            bw.close();
            updateTable();
            confirmation.show();
            l.log("User (ID " + userID + ") was added", Level.INFO);
        } catch (IOException ex) {
            l.log("Error: " + ex, Level.SEVERE);
        }
    }

    /**
     * This function edits a user in the csv file.
     * The function checks for the selected user and updates the state of the csv file.
     */
    @FXML
    private void editUser() {
        Alert noUsers = new Alert(Alert.AlertType.ERROR, "There are no users available", ButtonType.OK),
                itemNotSelected = new Alert(Alert.AlertType.ERROR, "Please select a user to edit", ButtonType.OK);
        clearInput();

        if (tableView.getItems().isEmpty()) {
            noUsers.show();
        } else if (tableView.getSelectionModel().getSelectedItem() == null) {
            itemNotSelected.show();
        } else {
            isEditing = true;
            updateScene();
            getUsers();
        }
    }

    /**
     * This function updates a user in the csv file by checking
     * to see if all the input texts are not empty.
     * If not then it will continue calling the needed functions to update a user.
     */
    @FXML
    private void updateUser() {
        Alert itemEdited = new Alert(Alert.AlertType.WARNING, "Are you sure you want to edit this user?", ButtonType.YES, ButtonType.CANCEL);

        if (userName.getText().isEmpty() && userUser.getText().isEmpty() && userPass.getText().isEmpty() && userRole.getText().isEmpty()) {
            addUserError.setText("User details cannot be blank!");
        } else if (userName.getText().isEmpty()) {
            addUserError.setText("User ID cannot be blank!");
        } else if (userUser.getText().isEmpty()) {
            addUserError.setText("Username cannot be blank!");
        } else if (userPass.getText().isEmpty()) {
            addUserError.setText("User password cannot be blank!");
        } else if (userRole.getText().isEmpty()) {
            addUserError.setText("User role cannot be blank!");
        } else if (!(userRole.getText().equals("user") || userRole.getText().equals("admin"))) {
            addUserError.setText("User must have a role of either user or admin");
        } else {
            itemEdited.showAndWait();
            if (itemEdited.getResult() == ButtonType.YES) {
                isEditing = false;
                writeEditToFile(userFile, tempFile, String.valueOf(tableView.getSelectionModel().getSelectedItem().getName()), userName.getText(), userUser.getText(), userPass.getText(), userRole.getText());
                Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, "User (ID " + userUser.getText() + ") was modified", ButtonType.OK);
                confirmation.show();
                updateScene();
                updateTable();
            }
        }
    }

    /**
     * This function updates the csv file with the parameters it receives.
     * The function checks the csv file currently being used, and compares it with the temporary one.
     * @param source - The source file csv
     * @param temp - The temp file csv
     * @param editTerm - The way to check the user
     * @param newUserID - The new user ID
     * @param newUser - The new username
     * @param newPass - The new password
     * @param newRole - The New Role
     */
    private void writeEditToFile(String source, String temp, String editTerm, String newUserID, String newUser, String newPass, String newRole) {
        Path src = Paths.get(source).toAbsolutePath();
        Path tmp = src.resolveSibling(temp);
        String name, username, password, role;

        try {
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(temp, true)));
            Scanner s = new Scanner(new File(source));
            s.useDelimiter("[,\n]");

            while (s.hasNextLine()) {
                name = s.next();
                username = s.next();
                password = s.next();
                role = s.nextLine();
                if (String.valueOf(name).equals(editTerm)) {
                    pw.println(newUserID + "," + newUser + "," + newPass + "," + newRole);
                } else {
                    pw.println(name + "," + username + "," + password + role);
                }
            }

            s.close();
            pw.flush();
            pw.close();
            Files.move(tmp, src, StandardCopyOption.REPLACE_EXISTING);
            l.log("User (ID " + tableView.getSelectionModel().getSelectedItem().getName() + ") was modified", Level.INFO);
        } catch (IOException ex) {
            l.log("Error: " + ex, Level.SEVERE);
        }
    }

    /**
     * This function cancels the edit and updates the scene to the default version
     */
    @FXML
    private void cancelEdit() {
        isEditing = false;
        updateScene();
    }

    /**
     * This function updates the scene. It will set different buttons to
     * visible or invisible and restart the tableview.
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
     * This function clears the input in all the text-fields by using .clear() .
     */
    private void clearInput() {
        addUserError.setText("");
        userName.clear();
        userUser.clear();
        userPass.clear();
        userRole.clear();
    }

    /**
     * This function updates the tableview by calling the getUsers function and the setTableView. This can be called restarting the tableView
     */
    private void updateTable() {
        tableView.getItems().clear();
        getUsers();
        setTableView();
        tableView.setItems(userObservableList);
    }

    /**
     * This function sets the table view to the users: name(id/email), username, password and role.
     */
    private void setTableView() {
        id.setCellValueFactory(new PropertyValueFactory<>("Name"));
        name.setCellValueFactory(new PropertyValueFactory<>("Username"));
        pass.setCellValueFactory(new PropertyValueFactory<>("Password"));
        role.setCellValueFactory(new PropertyValueFactory<>("Role"));
    }

    /**
     * This function is run on render. It updates the table and sets the visibility to false.
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        updateTable();
        update.setVisible(false);
        cancel.setVisible(false);
    }
}
