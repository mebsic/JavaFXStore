module com.javafxstore {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;
    opens com.javafxstore to javafx.fxml;
    exports com.javafxstore;
    exports com.javafxstore.category;
    opens com.javafxstore.category to javafx.fxml;
    exports com.javafxstore.product;
    opens com.javafxstore.product to javafx.fxml;
    exports com.javafxstore.user;
    opens com.javafxstore.user to javafx.fxml;
    exports com.javafxstore.admin;
    opens com.javafxstore.admin to javafx.fxml;
    exports com.javafxstore.store;
    opens com.javafxstore.store to javafx.fxml;
}
