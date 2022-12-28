package com.javafxstore.product;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Product Class
 * Getters and setters used in ProductsController module
 * @version 0.1
 */
public class Product {

    public SimpleLongProperty productID;
    public SimpleStringProperty name;
    public SimpleIntegerProperty quantity;
    public SimpleDoubleProperty price;
    public SimpleStringProperty category;

    /**
     * Default constructor
     */
    public Product() {
        this(0L, "", 0, 0.0, "");
    }

    /**
     * Overloaded constructor
     * @param productID used for product ID
     * @param name used for product name
     * @param quantity used for product quantity
     * @param price used for product price
     * @param category used for product category
     */
    public Product(long productID, String name, int quantity, double price, String category) {
        this.productID = new SimpleLongProperty(productID);
        this.name = new SimpleStringProperty(name);
        this.quantity = new SimpleIntegerProperty(quantity);
        this.price = new SimpleDoubleProperty(price);
        this.category = new SimpleStringProperty(category);
    }

    /**
     * Getters
     * @return product ID
     */
    public long getProductID() {
        return productID.get();
    }

    /**
     * @return product name
     */
    public String getName() {
        return name.get();
    }

    /**
     * @return product quantity
     */
    public int getQuantity() {
        return quantity.get();
    }

    /**
     * @return product price
     */
    public double getPrice() {
        return price.get();
    }

    /**
     * @return product category
     */
    public String getCategory() {
        return category.get();
    }

    /**
     * Setters
     * @param productID used for product ID
     */
    public void setProductID(long productID) {
        this.productID.set(productID);
    }

    /**
     * @param category used for product category
     */
    public void setCategory(String category) {
        this.category.set(category);
    }

    /**
     * @param price used for product price
     */
    public void setPrice(double price) {
        this.price.set(price);
    }

    /**
     * @param quantity used for product quantity
     */
    public void setQuantity(int quantity) {
        this.quantity.set(quantity);
    }

    /**
     * @param name used for product name
     */
    public void setName(String name) {
        this.name.set(name);
    }
}
