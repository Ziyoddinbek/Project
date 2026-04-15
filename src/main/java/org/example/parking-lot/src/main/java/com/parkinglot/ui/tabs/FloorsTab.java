package com.parkinglot.ui.tabs;

import com.parkinglot.model.ParkingFloor;
import com.parkinglot.model.ParkingLot;
import com.parkinglot.model.ParkingSpot;
import com.parkinglot.model.enums.ParkingSpotType;
import com.parkinglot.service.DataStore;
import com.parkinglot.ui.Styles;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;

public class FloorsTab {

    private ListView<String> floorList;
    private VBox spotGrid;
    private Label floorDetailTitle;
    private Runnable refreshCallback;

    public Tab build(Runnable refreshCallback) {
        this.refreshCallback = refreshCallback;
        Tab tab = new Tab("Floors");
        tab.setClosable(false);

        SplitPane split = new SplitPane();
        split.setStyle("-fx-background-color:" + Styles.BG_MAIN + ";");
        split.setDividerPositions(0.28);

        split.getItems().addAll(buildFloorList(), buildSpotDetail());
        tab.setContent(split);
        return tab;
    }

    private VBox buildFloorList() {
        VBox box = new VBox(12);
        box.setPadding(new Insets(16));
        box.setStyle(Styles.mainBackground());

        Label title = new Label("FLOORS");
        title.setStyle(Styles.sectionTitle());

        floorList = new ListView<>();
        floorList.setStyle("-fx-background-color:" + Styles.BG_CARD + "; -fx-text-fill:" + Styles.TEXT +
                "; -fx-border-color:" + Styles.BORDER + "; -fx-background-radius:8;");
        floorList.setPrefHeight(300);
        refreshFloorList();

        floorList.getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) -> {
            if (newVal != null) showFloorSpots(newVal);
        });

        // Add Floor button
        Button addFloorBtn = new Button("+ Add Floor");
        addFloorBtn.setStyle(Styles.accentButton());
        addFloorBtn.setMaxWidth(Double.MAX_VALUE);
        addFloorBtn.setOnMouseEntered(e -> addFloorBtn.setStyle(Styles.button(Styles.ACCENT_HOVER, "white")));
        addFloorBtn.setOnMouseExited(e -> addFloorBtn.setStyle(Styles.accentButton()));
        addFloorBtn.setOnAction(e -> showAddFloorDialog());

        Button removeFloorBtn = new Button("Remove Floor");
        removeFloorBtn.setStyle(Styles.dangerButton());
        removeFloorBtn.setMaxWidth(Double.MAX_VALUE);
        removeFloorBtn.setOnMouseEntered(e -> removeFloorBtn.setStyle(Styles.button("#b91c1c", "white")));
        removeFloorBtn.setOnMouseExited(e -> removeFloorBtn.setStyle(Styles.dangerButton()));
        removeFloorBtn.setOnAction(e -> {
            String selected = floorList.getSelectionModel().getSelectedItem();
            if (selected == null) { showAlert("Select a floor first."); return; }
            ParkingLot lot = DataStore.getInstance().getParkingLot();
            boolean removed = lot.removeParkingFloor(selected);
            if (!removed) {
                showAlert("Cannot remove floor: it has occupied spots or doesn't exist.");
            } else {
                refreshFloorList();
                spotGrid.getChildren().clear();
                floorDetailTitle.setText("Select a floor");
                refreshCallback.run();
            }
        });

        VBox.setVgrow(floorList, Priority.ALWAYS);
        box.getChildren().addAll(title, floorList, addFloorBtn, removeFloorBtn);
        return box;
    }

    private VBox buildSpotDetail() {
        VBox box = new VBox(16);
        box.setPadding(new Insets(16));
        box.setStyle(Styles.mainBackground());

        floorDetailTitle = new Label("Select a floor to view spots");
        floorDetailTitle.setFont(Font.font("System", FontWeight.BOLD, 16));
        floorDetailTitle.setStyle("-fx-text-fill:" + Styles.TEXT + ";");

        // Legend
        HBox legend = new HBox(16);
        legend.setAlignment(Pos.CENTER_LEFT);
        legend.getChildren().addAll(
                legendItem("Free",        Styles.SPOT_FREE,        Styles.SPOT_FREE_TEXT),
                legendItem("Occupied",    Styles.SPOT_OCCUPIED,    Styles.SPOT_OCCUPIED_TEXT),
                legendItem("Electric",    Styles.SPOT_ELECTRIC,    Styles.SPOT_ELECTRIC_TEXT),
                legendItem("Handicapped", Styles.SPOT_HANDICAPPED, Styles.SPOT_HANDICAPPED_TEXT)
        );

        spotGrid = new VBox(12);

        ScrollPane scroll = new ScrollPane(spotGrid);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color:" + Styles.BG_MAIN + "; -fx-background:" + Styles.BG_MAIN + ";");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        box.getChildren().addAll(floorDetailTitle, legend, scroll);
        return box;
    }

    private HBox legendItem(String text, String bg, String fg) {
        Label box = new Label("  ");
        box.setStyle("-fx-background-color:" + bg + "; -fx-background-radius:4; -fx-min-width:20; -fx-min-height:20;");
        Label lbl = new Label(text);
        lbl.setStyle("-fx-text-fill:" + fg + "; -fx-font-size:12px;");
        HBox h = new HBox(6, box, lbl);
        h.setAlignment(Pos.CENTER_LEFT);
        return h;
    }

    private void showFloorSpots(String floorName) {
        ParkingLot lot = DataStore.getInstance().getParkingLot();
        ParkingFloor floor = lot.getFloors().stream()
                .filter(f -> f.getName().equals(floorName))
                .findFirst().orElse(null);
        if (floor == null) return;

        floorDetailTitle.setText(floorName + "  —  " + floor.getAvailableCount() + "/" + floor.getTotalCapacity() + " available");
        spotGrid.getChildren().clear();

        for (ParkingSpotType type : ParkingSpotType.values()) {
            List<ParkingSpot> spots = floor.getAllSpots().stream()
                    .filter(s -> s.getType() == type).toList();
            if (spots.isEmpty()) continue;

            Label typeLabel = new Label(type.name() + " SPOTS");
            typeLabel.setStyle(Styles.sectionTitle());

            FlowPane flow = new FlowPane(8, 8);
            flow.setPrefWrapLength(700);

            for (ParkingSpot spot : spots) {
                VBox cell = buildSpotCell(spot);
                flow.getChildren().add(cell);
            }

            spotGrid.getChildren().addAll(typeLabel, flow);
        }
    }

    private VBox buildSpotCell(ParkingSpot spot) {
        boolean free = spot.getIsFree();
        String bg, fg;

        if (!free) {
            bg = Styles.SPOT_OCCUPIED;
            fg = Styles.SPOT_OCCUPIED_TEXT;
        } else {
            bg = switch (spot.getType()) {
                case ELECTRIC    -> Styles.SPOT_ELECTRIC;
                case HANDICAPPED -> Styles.SPOT_HANDICAPPED;
                default          -> Styles.SPOT_FREE;
            };
            fg = switch (spot.getType()) {
                case ELECTRIC    -> Styles.SPOT_ELECTRIC_TEXT;
                case HANDICAPPED -> Styles.SPOT_HANDICAPPED_TEXT;
                default          -> Styles.SPOT_FREE_TEXT;
            };
        }

        Label numLabel = new Label(spot.getNumber());
        numLabel.setStyle("-fx-text-fill:" + Styles.TEXT + "; -fx-font-weight:bold; -fx-font-size:12px;");

        Label statusLabel = new Label(free ? "FREE" : "OCCUPIED");
        statusLabel.setStyle("-fx-text-fill:" + fg + "; -fx-font-size:10px; -fx-font-weight:bold;");

        VBox cell = new VBox(4, numLabel, statusLabel);
        cell.setPadding(new Insets(10));
        cell.setAlignment(Pos.CENTER);
        cell.setPrefWidth(90);
        cell.setPrefHeight(70);
        cell.setStyle("-fx-background-color:" + bg + "; -fx-background-radius:8;");

        if (!free && spot.getVehicle() != null) {
            Label plateLabel = new Label(spot.getVehicle().getLicenseNumber());
            plateLabel.setStyle("-fx-text-fill:" + Styles.TEXT_MUTED + "; -fx-font-size:9px;");
            cell.getChildren().add(plateLabel);
        }

        // Tooltip
        Tooltip tip = new Tooltip(spot.getType().name() + " | " + spot.getNumber() +
                (free ? " | FREE" : " | " + (spot.getVehicle() != null ? spot.getVehicle().getLicenseNumber() : "OCCUPIED")));
        Tooltip.install(cell, tip);

        return cell;
    }

    public void refreshFloorList() {
        ParkingLot lot = DataStore.getInstance().getParkingLot();
        floorList.setItems(FXCollections.observableArrayList(
                lot.getFloors().stream().map(ParkingFloor::getName).toList()
        ));
    }

    public void refresh() {
        refreshFloorList();
        String selected = floorList.getSelectionModel().getSelectedItem();
        if (selected != null) showFloorSpots(selected);
    }

    private void showAddFloorDialog() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Add Parking Floor");
        dialog.setHeaderText(null);

        DialogPane dp = dialog.getDialogPane();
        dp.setStyle(Styles.mainBackground());

        TextField nameField = new TextField();
        nameField.setPromptText("Floor name (e.g. Floor-4)");
        nameField.setStyle(Styles.input());

        VBox content = new VBox(12, styledLabel("Floor Name:"), nameField);
        content.setPadding(new Insets(16));
        dp.setContent(content);

        ButtonType addBtn = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dp.getButtonTypes().addAll(addBtn, ButtonType.CANCEL);

        styleDialogButtons(dp);

        dialog.setResultConverter(bt -> bt == addBtn ? nameField.getText().trim() : null);
        dialog.showAndWait().ifPresent(name -> {
            if (name.isEmpty()) { showAlert("Floor name cannot be empty."); return; }
            ParkingLot lot = DataStore.getInstance().getParkingLot();
            boolean exists = lot.getFloors().stream().anyMatch(f -> f.getName().equals(name));
            if (exists) { showAlert("A floor with that name already exists."); return; }
            lot.addParkingFloor(new ParkingFloor(name));
            refreshFloorList();
            refreshCallback.run();
        });
    }

    private Label styledLabel(String text) {
        Label l = new Label(text);
        l.setStyle(Styles.labelMuted());
        return l;
    }

    private void styleDialogButtons(DialogPane dp) {
        dp.lookupButton(ButtonType.CANCEL).setStyle(Styles.button(Styles.BG_CARD, Styles.TEXT));
    }

    private void showAlert(String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK);
        a.setHeaderText(null);
        a.getDialogPane().setStyle(Styles.mainBackground());
        a.showAndWait();
    }
}
