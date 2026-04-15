module com.parkinglot {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;


    exports com.parkinglot;
    exports com.parkinglot.model;
    exports com.parkinglot.model.enums;
    exports com.parkinglot.model.spots;
    exports com.parkinglot.model.vehicles;
    exports com.parkinglot.model.accounts;
    exports com.parkinglot.model.payments;
    exports com.parkinglot.service;
    exports com.parkinglot.ui;
    exports com.parkinglot.ui.tabs;
}
