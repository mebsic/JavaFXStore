package com.javafxstore.category;

import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Category Class
 * Getters and setters used in CategoriesController module
 * @version 0.1
 */
public class Category {

    public SimpleLongProperty categoryID;
    public SimpleStringProperty name;
    public SimpleStringProperty type;

    /**
     * Default constructor
     */
    public Category() {
        this(0L, "", "");
    }

    /**
     * Overloaded constructor
     * @param categoryID used for category ID
     * @param name used for category name
     * @param type used for category type
     */
    public Category(long categoryID, String name, String type) {
        this.categoryID = new SimpleLongProperty(categoryID);
        this.name = new SimpleStringProperty(name);
        this.type = new SimpleStringProperty(type);
    }

    /**
     * Getters
     * @return categoryID
     */
    public long getId() {
        return categoryID.get();
    }

    /**
     * @return category name
     */
    public String getName() {
        return name.get();
    }

    /**
     * @return category type
     */
    public String getType() {
        return type.get();
    }

    /**
     * Setters
     * @param type used for category type
     */
    public void setType(String type) {
        this.type.set(type);
    }

    /**
     * @param id used for category ID
     */
    public void setId(long id) {
        this.categoryID.set(id);
    }

    /**
     * @param name used for category name
     */
    public void setName(String name) {
        this.name.set(name);
    }
}
