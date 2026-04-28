package com.parkinglot.ui.tabs;

import com.parkinglot.model.ParkingFloor;
import com.parkinglot.model.ParkingLot;
import com.parkinglot.model.ParkingSpot;
import com.parkinglot.model.accounts.Admin;
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

        VBox.setVgrow(floorList, Priority.ALWAYS);
        box.getChildren().addAll(title, floorList);

        // Add/Remove Floor buttons — Admin only
        boolean isAdmin = DataStore.getInstance().getLoggedInUser() instanceof Admin;
        if (isAdmin) {
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

            box.getChildren().addAll(addFloorBtn, removeFloorBtn);
        }
        return box;
    }

    private VBox buildSpotDetail() {
        VBox box = new VBox(16);
        box.setPadding(new Insets(20));
        box.setStyle(Styles.mainBackground());

        // ── Header ────────────────────────────────────────────────────────────
        floorDetailTitle = new Label("Select a floor to view spots");
        floorDetailTitle.setFont(Font.font("System", FontWeight.BOLD, 18));
        floorDetailTitle.setStyle("-fx-text-fill:" + Styles.TEXT + ";");

        // ── Legend ────────────────────────────────────────────────────────────
        Label legendTitle = new Label("SPOT TYPES");
        legendTitle.setStyle(Styles.sectionTitle());

        HBox legend = new HBox(8);
        legend.setAlignment(Pos.CENTER_LEFT);
        legend.getChildren().addAll(
                legendPill("C",  "Compact",     Styles.SUCCESS,              "#0a3d1c"),
                legendPill("L",  "Large",        "#60a5fa",                   "#0a1f3d"),
                legendPill("MC", "Motorcycle",   "#c084fc",                   "#2d1a4a"),
                legendPill("EV", "Electric",     Styles.SPOT_ELECTRIC_TEXT,   Styles.SPOT_ELECTRIC),
                legendPill("HC", "Handicapped",  Styles.SPOT_HANDICAPPED_TEXT,Styles.SPOT_HANDICAPPED),
                legendPill("X",  "Occupied",     Styles.SPOT_OCCUPIED_TEXT,   Styles.SPOT_OCCUPIED)
        );

        spotGrid = new VBox(20);

        ScrollPane scroll = new ScrollPane(spotGrid);
        scroll.setFitToWidth(true);
        scroll.setPadding(new Insets(4, 0, 4, 0));
        scroll.setStyle("-fx-background-color:" + Styles.BG_MAIN + "; -fx-background:" + Styles.BG_MAIN + ";");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        box.getChildren().addAll(floorDetailTitle, legendTitle, legend,
                new Separator(), scroll);
        return box;
    }

    /** A pill-shaped legend item with badge + label */
    private HBox legendPill(String badge, String label, String fg, String bg) {
        Label badgeLbl = new Label(badge);
        badgeLbl.setStyle(
                "-fx-text-fill:white; -fx-font-size:10px; -fx-font-weight:bold;" +
                " -fx-background-color:" + fg + "; -fx-background-radius:4;" +
                " -fx-padding:2 6 2 6; -fx-min-width:24;"
        );
        Label nameLbl = new Label(label);
        nameLbl.setStyle("-fx-text-fill:" + fg + "; -fx-font-size:11px;");

        HBox pill = new HBox(5, badgeLbl, nameLbl);
        pill.setAlignment(Pos.CENTER_LEFT);
        pill.setPadding(new Insets(5, 10, 5, 10));
        pill.setStyle("-fx-background-color:" + bg + "; -fx-background-radius:20;" +
                " -fx-border-color:" + fg + "; -fx-border-radius:20; -fx-border-width:1;");
        return pill;
    }

    private void showFloorSpots(String floorName) {
        ParkingLot lot = DataStore.getInstance().getParkingLot();
        ParkingFloor floor = lot.getFloors().stream()
                .filter(f -> f.getName().equals(floorName))
                .findFirst().orElse(null);
        if (floor == null) return;

        int avail = floor.getAvailableCount();
        int total = floor.getTotalCapacity();
        int used  = total - avail;

        floorDetailTitle.setText(floorName);
        spotGrid.getChildren().clear();

        // ── Floor summary bar ─────────────────────────────────────────────────
        HBox summaryBar = new HBox(20);
        summaryBar.setPadding(new Insets(14, 18, 14, 18));
        summaryBar.setStyle("-fx-background-color:" + Styles.BG_CARD +
                "; -fx-background-radius:10;" +
                " -fx-border-color:" + Styles.BORDER + "; -fx-border-radius:10; -fx-border-width:1;");
        summaryBar.setAlignment(Pos.CENTER_LEFT);

        summaryBar.getChildren().addAll(
                summaryItem("Total Spots",  String.valueOf(total), Styles.TEXT),
                summaryDivider(),
                summaryItem("Available",    String.valueOf(avail), Styles.SUCCESS),
                summaryDivider(),
                summaryItem("Occupied",     String.valueOf(used),  Styles.ERROR),
                summaryDivider(),
                summaryItem("Utilization",  String.format("%.0f%%", total > 0 ? (used * 100.0 / total) : 0),
                        used > total * 0.8 ? Styles.ERROR : Styles.WARNING)
        );

        // Progress bar
        ProgressBar pb = new ProgressBar(total > 0 ? (double) used / total : 0);
        pb.setMaxWidth(Double.MAX_VALUE);
        pb.setPrefHeight(6);
        String pbColor = floor.isFull() ? Styles.ERROR : (used > total * 0.8 ? Styles.WARNING : Styles.SUCCESS);
        pb.setStyle("-fx-accent:" + pbColor + ";");

        VBox summaryBox = new VBox(8, summaryBar, pb);
        spotGrid.getChildren().add(summaryBox);

        // ── Spots by type ─────────────────────────────────────────────────────
        for (ParkingSpotType type : ParkingSpotType.values()) {
            List<ParkingSpot> spots = floor.getAllSpots().stream()
                    .filter(s -> s.getType() == type).toList();
            if (spots.isEmpty()) continue;

            long freeCount = spots.stream().filter(ParkingSpot::getIsFree).count();
            String typeColor = typeColor(type);
            String typeBadge = typeBadge(type);
            String typeName  = typeName(type);

            // Section header
            HBox sectionHeader = new HBox(10);
            sectionHeader.setAlignment(Pos.CENTER_LEFT);
            sectionHeader.setPadding(new Insets(6, 0, 2, 0));

            Label badgeLbl = new Label(typeBadge);
            badgeLbl.setStyle(
                    "-fx-text-fill:white; -fx-font-size:11px; -fx-font-weight:bold;" +
                    " -fx-background-color:" + typeColor + "; -fx-background-radius:5;" +
                    " -fx-padding:3 8 3 8;"
            );
            Label nameLbl = new Label(typeName + " SPOTS");
            nameLbl.setStyle("-fx-text-fill:" + typeColor + "; -fx-font-size:12px; -fx-font-weight:bold;");

            Label countLbl = new Label(freeCount + "/" + spots.size() + " free");
            countLbl.setStyle("-fx-text-fill:" + Styles.TEXT_MUTED + "; -fx-font-size:11px;");

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            sectionHeader.getChildren().addAll(badgeLbl, nameLbl, spacer, countLbl);

            FlowPane flow = new FlowPane(10, 10);
            flow.setPrefWrapLength(900);
            for (ParkingSpot spot : spots) {
                flow.getChildren().add(buildSpotCell(spot, typeColor, typeBadge));
            }

            VBox section = new VBox(8, sectionHeader, flow);
            section.setPadding(new Insets(12, 14, 12, 14));
            section.setStyle("-fx-background-color:" + Styles.BG_CARD +
                    "; -fx-background-radius:10;" +
                    " -fx-border-color:" + typeColor + "44" +
                    "; -fx-border-radius:10; -fx-border-width:1;");

            spotGrid.getChildren().add(section);
        }
    }

    private VBox buildSpotCell(ParkingSpot spot, String typeColor, String typeBadge) {
        boolean free = spot.getIsFree();

        String bg = free ? Styles.BG_MAIN : Styles.SPOT_OCCUPIED;
        String borderColor = free ? typeColor : Styles.SPOT_OCCUPIED_TEXT;
        String statusColor = free ? Styles.SUCCESS : Styles.ERROR;

        // Top row: badge + spot number
        Label badgeLbl = new Label(typeBadge);
        badgeLbl.setStyle(
                "-fx-text-fill:white; -fx-font-size:9px; -fx-font-weight:bold;" +
                " -fx-background-color:" + (free ? typeColor : Styles.SPOT_OCCUPIED_TEXT) + ";" +
                " -fx-background-radius:3; -fx-padding:1 5 1 5;"
        );

        Label numLabel = new Label(spot.getNumber());
        numLabel.setFont(Font.font("System", FontWeight.BOLD, 13));
        numLabel.setStyle("-fx-text-fill:" + Styles.TEXT + ";");

        // Status dot + text
        Label dotLabel = new Label("  ");
        dotLabel.setStyle(
                "-fx-background-color:" + statusColor + ";" +
                " -fx-background-radius:50; -fx-min-width:8; -fx-min-height:8;" +
                " -fx-max-width:8; -fx-max-height:8;"
        );
        Label statusLabel = new Label(free ? "FREE" : "OCCUPIED");
        statusLabel.setStyle("-fx-text-fill:" + statusColor + "; -fx-font-size:9px; -fx-font-weight:bold;");

        HBox statusRow = new HBox(4, dotLabel, statusLabel);
        statusRow.setAlignment(Pos.CENTER_LEFT);

        VBox cell = new VBox(5, badgeLbl, numLabel, statusRow);
        cell.setPadding(new Insets(10, 12, 10, 12));
        cell.setAlignment(Pos.TOP_LEFT);
        cell.setPrefWidth(105);
        cell.setMinHeight(85);
        cell.setStyle(
                "-fx-background-color:" + bg + ";" +
                " -fx-background-radius:10;" +
                " -fx-border-color:" + borderColor + ";" +
                " -fx-border-radius:10; -fx-border-width:1.5;" +
                " -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 4, 0, 0, 2);"
        );

        // Vehicle info if occupied
        if (!free && spot.getVehicle() != null) {
            Label plateLabel = new Label(spot.getVehicle().getLicenseNumber());
            plateLabel.setStyle("-fx-text-fill:" + Styles.TEXT_MUTED + "; -fx-font-size:9px;");
            Label brandLabel = new Label(spot.getVehicle().getBrand());
            brandLabel.setStyle("-fx-text-fill:" + Styles.TEXT_MUTED + "; -fx-font-size:9px;");
            cell.getChildren().addAll(plateLabel, brandLabel);
        }

        // Hover effect
        cell.setOnMouseEntered(e -> cell.setStyle(
                "-fx-background-color:" + (free ? Styles.BG_CARD : "#5a1010") + ";" +
                " -fx-background-radius:10;" +
                " -fx-border-color:" + borderColor + ";" +
                " -fx-border-radius:10; -fx-border-width:2;" +
                " -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 8, 0, 0, 3);" +
                " -fx-cursor:hand;"
        ));
        cell.setOnMouseExited(e -> cell.setStyle(
                "-fx-background-color:" + bg + ";" +
                " -fx-background-radius:10;" +
                " -fx-border-color:" + borderColor + ";" +
                " -fx-border-radius:10; -fx-border-width:1.5;" +
                " -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 4, 0, 0, 2);"
        ));

        Tooltip tip = new Tooltip(
                typeName(spot.getType()) + "  |  " + spot.getNumber() + "\n" +
                (free ? "Status: FREE"
                      : "Status: OCCUPIED\n" +
                        "Plate:  " + spot.getVehicle().getLicenseNumber() + "\n" +
                        "Brand:  " + spot.getVehicle().getBrand())
        );
        tip.setStyle("-fx-font-size:12px;");
        Tooltip.install(cell, tip);

        return cell;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private VBox summaryItem(String label, String value, String color) {
        Label valLbl = new Label(value);
        valLbl.setFont(Font.font("System", FontWeight.BOLD, 20));
        valLbl.setStyle("-fx-text-fill:" + color + ";");
        Label lblLbl = new Label(label);
        lblLbl.setStyle(Styles.labelMuted());
        VBox v = new VBox(2, valLbl, lblLbl);
        v.setAlignment(Pos.CENTER_LEFT);
        return v;
    }

    private Label summaryDivider() {
        Label l = new Label("|");
        l.setStyle("-fx-text-fill:" + Styles.BORDER + "; -fx-font-size:20px;");
        return l;
    }

    private String typeColor(ParkingSpotType type) {
        return switch (type) {
            case COMPACT     -> Styles.SUCCESS;
            case LARGE       -> "#60a5fa";
            case MOTORCYCLE  -> "#c084fc";
            case ELECTRIC    -> Styles.SPOT_ELECTRIC_TEXT;
            case HANDICAPPED -> Styles.SPOT_HANDICAPPED_TEXT;
        };
    }

    private String typeBadge(ParkingSpotType type) {
        return switch (type) {
            case COMPACT     -> "C";
            case LARGE       -> "L";
            case MOTORCYCLE  -> "MC";
            case ELECTRIC    -> "EV";
            case HANDICAPPED -> "HC";
        };
    }

    private String typeName(ParkingSpotType type) {
        return switch (type) {
            case COMPACT     -> "Compact";
            case LARGE       -> "Large";
            case MOTORCYCLE  -> "Motorcycle";
            case ELECTRIC    -> "Electric";
            case HANDICAPPED -> "Handicapped";
        };
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
