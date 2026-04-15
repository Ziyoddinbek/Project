package com.parkinglot.ui.tabs;

import com.parkinglot.model.ParkingLot;
import com.parkinglot.model.ParkingTicket;
import com.parkinglot.service.DataStore;
import com.parkinglot.ui.Styles;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class VehiclesTab {

    private TableView<ParkingTicket> table;
    private ObservableList<ParkingTicket> data;

    public Tab build() {
        Tab tab = new Tab("Vehicles");
        tab.setClosable(false);

        VBox root = new VBox(16);
        root.setPadding(new Insets(20));
        root.setStyle(Styles.mainBackground());

        Label title = new Label("CURRENTLY PARKED VEHICLES");
        title.setStyle(Styles.sectionTitle());

        table = new TableView<>();
        table.setStyle(Styles.tableView());
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS);

        TableColumn<ParkingTicket, String> colPlate   = col("License Plate", t -> t.getVehicle().getLicenseNumber());
        TableColumn<ParkingTicket, String> colType    = col("Vehicle Type",  t -> t.getVehicle().getType().name());
        TableColumn<ParkingTicket, String> colFloor   = col("Floor",         t -> t.getFloorName());
        TableColumn<ParkingTicket, String> colSpot    = col("Spot",          t -> t.getParkingSpot().getNumber());
        TableColumn<ParkingTicket, String> colSpotType= col("Spot Type",     t -> t.getParkingSpot().getType().name());
        TableColumn<ParkingTicket, String> colTicket  = col("Ticket #",      t -> t.getTicketNumber());
        TableColumn<ParkingTicket, String> colTime    = col("Time Parked",   t -> formatDuration(t.getIssuedAt()));
        TableColumn<ParkingTicket, String> colIssued  = col("Since",         t -> t.getIssuedAtFormatted());

        table.getColumns().addAll(colPlate, colType, colFloor, colSpot, colSpotType, colTicket, colTime, colIssued);

        data = FXCollections.observableArrayList();
        table.setItems(data);
        loadData();

        root.getChildren().addAll(title, table);

        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);
        scroll.setFitToHeight(true);
        scroll.setStyle("-fx-background-color:" + Styles.BG_MAIN + "; -fx-background:" + Styles.BG_MAIN + ";");
        tab.setContent(scroll);
        return tab;
    }

    private void loadData() {
        ParkingLot lot = DataStore.getInstance().getParkingLot();
        List<ParkingTicket> active = lot.getActiveTickets().values().stream()
                .filter(ParkingTicket::isActive)
                .toList();
        data.setAll(active);
    }

    public void refresh() {
        loadData();
    }

    private String formatDuration(LocalDateTime issuedAt) {
        Duration d = Duration.between(issuedAt, LocalDateTime.now());
        long h = d.toHours();
        long m = d.toMinutesPart();
        return h + "h " + m + "m";
    }

    private TableColumn<ParkingTicket, String> col(String title,
            java.util.function.Function<ParkingTicket, String> extractor) {
        TableColumn<ParkingTicket, String> c = new TableColumn<>(title);
        c.setCellValueFactory(d -> new SimpleStringProperty(extractor.apply(d.getValue())));
        return c;
    }
}
