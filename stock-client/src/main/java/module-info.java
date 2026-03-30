module com.stock.stockclient {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;


    opens com.stock.stockclient.model to com.google.gson;
    opens com.stock.stockclient.service to com.google.gson;
    opens com.stock.stockclient to javafx.fxml;

    exports com.stock.stockclient;
    exports com.stock.stockclient.view;
    exports com.stock.stockclient.model;
    exports com.stock.stockclient.service;
}