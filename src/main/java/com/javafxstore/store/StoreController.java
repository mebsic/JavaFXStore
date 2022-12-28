package com.javafxstore.store;

import com.javafxstore.product.Product;
import com.javafxstore.utils.LoggerUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.regex.Pattern;

/**
 * StoreController Class
 */
public class StoreController implements Initializable {

    /**
     * This class holds all functionality for the store
     * and will act as the class to contain the controller.
     * */
    private static final String productFile = "products.csv", tempFile = "temp_products.csv", ordersFile = "orders.csv";
    private static final LoggerUtils l = new LoggerUtils();
    List<Double> selectedProducts = new ArrayList<>();
    private double prodPrice = 0.00, prodTotal = 0.00;
    private static boolean isItemInCart = false;
    private static final double taxes = 0.13;

    private final Pattern pricePattern = Pattern.compile("^\\d{1,3}(?:[.,]\\d{3})*(?:[.,]\\d{2})$");
    private final Pattern quantityPattern = Pattern.compile("^\\d+$");
    private final Pattern idPattern = Pattern.compile("^\\d{10}$");

    @FXML
    public TableView<Product> productView = new TableView<>();

    @FXML
    public TableView<Product> cartView = new TableView<>();

    @FXML
    public Button add, clear, checkout;

    @FXML
    public Label subtotal, total;

    @FXML
    public TableColumn<Product, String> name, cartName;

    @FXML
    public TableColumn<Product, Integer> quantity, cartQty;

    @FXML
    public TableColumn<Product, Double> price, cartPrice;

    @FXML
    public ObservableList<Product> productObservableList = FXCollections.observableArrayList();

    @FXML
    public ObservableList<Product> selectedItems;

    @FXML
    public TableView<Product> tableView = new TableView<>();

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

    private void getProducts() {

        /**
         * This method gets the products from the products.csv file.
         * Uses the buffered reader to get characters line by line.
         * */

        String fieldDelimiter = ",";
        BufferedReader br;

        try {
            br = new BufferedReader(new FileReader(productFile));
            String line;

            while ((line = br.readLine()) != null) {
                String[] fields = line.split(fieldDelimiter, -1);
                Product products = new Product(Long.parseLong(fields[0]), fields[1], Integer.parseInt(fields[2]), Double.parseDouble(fields[3]), fields[4]);
                productObservableList.add(products);
            }
            br.close();
        } catch (IOException ex) {
            l.log("Error: " + ex, Level.SEVERE);
        }
    }

    @FXML
    private void addToCart() {
        /**
         * This method adds the selected product(s) to the cart
         * */
        Alert noProducts = new Alert(Alert.AlertType.ERROR, "There are no products available", ButtonType.OK),
                itemNotSelected = new Alert(Alert.AlertType.ERROR, "Please add a product to the cart", ButtonType.OK);

        if (productView.getItems().isEmpty()) {
            noProducts.show();
        } else if (productView.getSelectionModel().getSelectedItem() == null) {
            itemNotSelected.show();
        } else {
            isItemInCart = true;
            refreshCart();
            cartView.getItems().addAll(productView.getSelectionModel().getSelectedItems());
        }
    }

    @FXML
    private void clearCart() {
        /**
         * This method clears the cart of
         * all added products to it.
         * */
        isItemInCart = false;
        refreshCart();
        updateTable();
    }

    @FXML
    private void completeCheckout() {
        /**
         * This method completes the checkout if the
         * cart is not empty.
         * */

        if (!cartView.getItems().isEmpty()) {
            isItemInCart = false;
            refreshCart();
            checkout();
            updateTable();
        }
    }

    // TODO change products.csv quantity by product ID after order placed
    private void checkout() {

        /**
         * This method completes the checkout and writes
         * the final confirmed order to the orders.csv file
         * */
        selectedItems = cartView.getItems();
        long orderID = (long) Math.floor(Math.random() * 9_000_000_000L) + 1_000_000_000L;
        String name;
        double price;
        BufferedWriter bw;

        try {
            bw = new BufferedWriter(new FileWriter(ordersFile, true));
            for (Product row : selectedItems) {
                name = row.getName();
                price = row.getPrice();
                String line = orderID + "," + name + "," + price + "\n";
                bw.write(line);
            }

            bw.close();
            updateTable();
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, "Your order (ID " + orderID + ") has been placed", ButtonType.OK);
            confirmation.show();
            l.log("New order (ID " + orderID + ") has been placed", Level.INFO);

        } catch (IOException ex) {
            l.log("Error: " + ex, Level.SEVERE);
        }
    }

    private void refreshCart() {

        /**
         * This method refreshes the cart and sets the
         * availability of the checkout section to false if there
         * is nothing added to the cart. When a product gets selected
         * it is then added to the cart and formulates the price.
         * It also refreshes the functionality of the clear and checkout
         * buttons once an item is added.
         * */
        selectedItems = productView.getSelectionModel().getSelectedItems();

        if (!isItemInCart) {
            prodTotal = 0.00;
            prodPrice = 0.00;
            for (Product row : selectedItems) {
                selectedProducts.remove(row.getPrice());
            }
            subtotal.setText(String.format("$%.2f", prodPrice));
            total.setText(String.format("$%.2f", prodTotal));
            clear.setDisable(true);
            checkout.setDisable(true);
            add.setDisable(false);
            productView.setDisable(false);
            cartView.setDisable(true);
        } else {
            for (Product row : selectedItems) {
                selectedProducts.add(row.getPrice());
            }
            for (double prod : selectedProducts) {
                prodPrice += prod;
                prodTotal += prod * taxes + prod;
            }
            subtotal.setText(String.format("$%.2f", prodPrice));
            total.setText(String.format("$%.2f", prodTotal));
            productView.setDisable(true);
            cartView.setDisable(false);
            clear.setDisable(false);
            checkout.setDisable(false);
            add.setDisable(true);
        }
    }

    private void updateTable() {
        /**
         * This method updates the table with all the new
         * data from the selected functions.
         * */
        productView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        cartView.setSelectionModel(null);
        productView.getItems().clear();
        cartView.getItems().clear();
        getProducts();
        setProductTableView();
        setCartTableView();
        productView.setItems(productObservableList);

    }

    private void setProductTableView() {
        /**
         * This method sets the cell values of the table,
         * these being name, quantity and price.
         * */
        name.setCellValueFactory(new PropertyValueFactory<>("Name"));
        quantity.setCellValueFactory(new PropertyValueFactory<>("Quantity"));
        price.setCellValueFactory(new PropertyValueFactory<>("Price"));
    }

    private void setCartTableView() {
        /**
         * This method sets the cell values of the cart,
         * these being name, quantity and price.
         * */
        cartName.setCellValueFactory(new PropertyValueFactory<>("Name"));
        cartQty.setCellValueFactory(new PropertyValueFactory<>("Quantity"));
        cartPrice.setCellValueFactory(new PropertyValueFactory<>("Price"));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        /**
         * This method initializes the item in the cart
         * to false and updates and refreshes.
         * */
        isItemInCart = false;
        updateTable();
        refreshCart();
    }
}
